/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.openadmin.server.service;

import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.security.service.CleanStringException;
import org.broadleafcommerce.common.security.service.ExploitProtectionService;
import org.broadleafcommerce.common.util.StreamCapableTransactionalOperationAdapter;
import org.broadleafcommerce.common.util.StreamingTransactionCapableUtil;
import org.broadleafcommerce.openadmin.dto.BatchDynamicResultSet;
import org.broadleafcommerce.openadmin.dto.BatchPersistencePackage;
import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.Persistable;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManagerFactory;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceResponse;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceThreadManager;
import org.broadleafcommerce.openadmin.server.service.persistence.TargetModeType;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Resource;
/**
 * @author jfischer
 */
@Service("blDynamicEntityRemoteService")
public class DynamicEntityRemoteService implements DynamicEntityService {

    private static final Log LOG = LogFactory.getLog(DynamicEntityRemoteService.class);
    protected static final Map<BatchPersistencePackage, BatchDynamicResultSet> METADATA_CACHE = Collections.synchronizedMap(new LRUMap<BatchPersistencePackage, BatchDynamicResultSet>(1000));

    @Resource(name="blExploitProtectionService")
    protected ExploitProtectionService exploitProtectionService;

    @Resource(name="blPersistenceThreadManager")
    protected PersistenceThreadManager persistenceThreadManager;

    @Resource(name="blStreamingTransactionCapableUtil")
    protected StreamingTransactionCapableUtil transUtil;

    protected ServiceException recreateSpecificServiceException(ServiceException e, String message, Throwable cause) {
        try {
            ServiceException newException;
            if (cause == null) {
                Constructor constructor = e.getClass().getConstructor(String.class);
                newException = (ServiceException) constructor.newInstance(message);
            } else {
                Constructor constructor = e.getClass().getConstructor(String.class, Throwable.class);
                newException = (ServiceException) constructor.newInstance(message, cause);
            }

            return newException;
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
    }

    @Override
    public PersistenceResponse inspect(final PersistencePackage persistencePackage) throws ServiceException {
        return persistenceThreadManager.operation(TargetModeType.SANDBOX, new Persistable <PersistenceResponse, ServiceException>() {
            @Override
            public PersistenceResponse execute() throws ServiceException {
                String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
                try {
                    PersistenceManager persistenceManager = PersistenceManagerFactory.getPersistenceManager();
                    return persistenceManager.inspect(persistencePackage);
                } catch (ServiceException e) {
                    String message = exploitProtectionService.cleanString(e.getMessage());
                    throw recreateSpecificServiceException(e, message, e.getCause());
                } catch (Exception e) {
                    LOG.error("Problem inspecting results for " + ceilingEntityFullyQualifiedClassname, e);
                    throw new ServiceException(exploitProtectionService.cleanString("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname), e);
                }
            }
        });
    }

    @Override
    public PersistenceResponse fetch(final PersistencePackage persistencePackage, final CriteriaTransferObject cto) throws ServiceException {
        return persistenceThreadManager.operation(TargetModeType.SANDBOX, new Persistable<PersistenceResponse, ServiceException>() {
            @Override
            public PersistenceResponse execute() throws ServiceException {
                try {
                    PersistenceManager persistenceManager = PersistenceManagerFactory.getPersistenceManager();
                    return persistenceManager.fetch(persistencePackage, cto);
                } catch (ServiceException e) {
                    LOG.error("Problem fetching results for " + persistencePackage.getCeilingEntityFullyQualifiedClassname(), e);
                    String message = exploitProtectionService.cleanString(e.getMessage());
                    throw recreateSpecificServiceException(e, message, e.getCause());
                }
            }
        });
    }

    protected void cleanEntity(Entity entity) throws ServiceException {
        Property currentProperty = null;
        try {
            for (Property property : entity.getProperties()) {
                currentProperty = property;
                property.setRawValue(property.getValue());
                property.setValue(exploitProtectionService.cleanStringWithResults(property.getValue()));
                property.setUnHtmlEncodedValue(StringEscapeUtils.unescapeHtml(property.getValue()));
            }
        } catch (CleanStringException e) {
            StringBuilder sb = new StringBuilder();
            for (int j=0;j<e.getCleanResults().getNumberOfErrors();j++){
                sb.append("\n");
                sb.append(j+1);
                sb.append(") ");
                sb.append((String) e.getCleanResults().getErrorMessages().get(j));
                sb.append("\n");
            }
            sb.append("\nNote - Antisamy policy in effect. Set a new policy file to modify validation behavior/strictness.");
            entity.addValidationError(currentProperty.getName(), sb.toString());
        }
    }

    @Override
    public PersistenceResponse add(final PersistencePackage persistencePackage) throws ServiceException {
        final PersistenceResponse[] response = new PersistenceResponse[1];
        try {
            transUtil.runTransactionalOperation(new StreamCapableTransactionalOperationAdapter() {
                @Override
                public void execute() throws Throwable {
                    response[0] = nonTransactionalAdd(persistencePackage);
                }

                @Override
                public boolean shouldRetryOnTransactionLockAcquisitionFailure() {
                    return super.shouldRetryOnTransactionLockAcquisitionFailure();
                }
            }, RuntimeException.class);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof ServiceException) {
                throw (ServiceException) e.getCause();
            }
            throw e;
        }
        return response[0];
    }

    @Override
    public PersistenceResponse update(final PersistencePackage persistencePackage) throws ServiceException {
        final PersistenceResponse[] response = new PersistenceResponse[1];
        try {
            transUtil.runTransactionalOperation(new StreamCapableTransactionalOperationAdapter() {
                @Override
                public void execute() throws Throwable {
                    response[0] = nonTransactionalUpdate(persistencePackage);
                }

                @Override
                public boolean shouldRetryOnTransactionLockAcquisitionFailure() {
                    return super.shouldRetryOnTransactionLockAcquisitionFailure();
                }
            }, RuntimeException.class);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof ServiceException) {
                throw (ServiceException) e.getCause();
            }
            throw e;
        }
        return response[0];
    }

    @Override
    public PersistenceResponse remove(final PersistencePackage persistencePackage) throws ServiceException {
        final PersistenceResponse[] response = new PersistenceResponse[1];
        try {
            transUtil.runTransactionalOperation(new StreamCapableTransactionalOperationAdapter() {
                @Override
                public void execute() throws Throwable {
                    response[0] = nonTransactionalRemove(persistencePackage);
                }

                @Override
                public boolean shouldRetryOnTransactionLockAcquisitionFailure() {
                    return super.shouldRetryOnTransactionLockAcquisitionFailure();
                }
            }, RuntimeException.class);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof ServiceException) {
                throw (ServiceException) e.getCause();
            }
            throw e;
        }
        return response[0];
    }

    @Override
    public PersistenceResponse nonTransactionalAdd(final PersistencePackage persistencePackage) throws ServiceException {
        return persistenceThreadManager.operation(TargetModeType.SANDBOX, new Persistable<PersistenceResponse, ServiceException>() {
            @Override
            public PersistenceResponse execute() throws ServiceException {
                cleanEntity(persistencePackage.getEntity());
                if (persistencePackage.getEntity().isValidationFailure()) {
                    return new PersistenceResponse().withEntity(persistencePackage.getEntity());
                }
                try {
                    PersistenceManager persistenceManager = PersistenceManagerFactory.getPersistenceManager();
                    return persistenceManager.add(persistencePackage);
                } catch (ServiceException e) {
                    //immediately throw validation exceptions without printing a stack trace
                    if (e instanceof ValidationException) {
                        throw e;
                    } else if (e.getCause() instanceof ValidationException) {
                        throw (ValidationException) e.getCause();
                    }
                    String message = exploitProtectionService.cleanString(e.getMessage());
                    LOG.error("Problem adding new " + persistencePackage.getCeilingEntityFullyQualifiedClassname(), e);
                    throw recreateSpecificServiceException(e, message, e.getCause());
                }
            }
        });
    }

    @Override
    public PersistenceResponse nonTransactionalUpdate(final PersistencePackage persistencePackage) throws ServiceException {
        return persistenceThreadManager.operation(TargetModeType.SANDBOX, new Persistable<PersistenceResponse, ServiceException>() {
            @Override
            public PersistenceResponse execute() throws ServiceException {
                cleanEntity(persistencePackage.getEntity());
                if (persistencePackage.getEntity().isValidationFailure()) {
                    return new PersistenceResponse().withEntity(persistencePackage.getEntity());
                }
                try {
                    PersistenceManager persistenceManager = PersistenceManagerFactory.getPersistenceManager();
                    return persistenceManager.update(persistencePackage);
                } catch (ServiceException e) {
                    //immediately throw validation exceptions without printing a stack trace
                    if (e instanceof ValidationException) {
                        throw e;
                    } else if (e.getCause() instanceof ValidationException) {
                        throw (ValidationException) e.getCause();
                    }
                    LOG.error("Problem updating " + persistencePackage.getCeilingEntityFullyQualifiedClassname(), e);
                    String message = exploitProtectionService.cleanString(e.getMessage());
                    throw recreateSpecificServiceException(e, message, e.getCause());
                }
            }
        });
    }

    @Override
    public PersistenceResponse nonTransactionalRemove(final PersistencePackage persistencePackage) throws ServiceException {
        return persistenceThreadManager.operation(TargetModeType.SANDBOX, new Persistable<PersistenceResponse, ServiceException>() {
            @Override
            public PersistenceResponse execute() throws ServiceException {
                try {
                    PersistenceManager persistenceManager = PersistenceManagerFactory.getPersistenceManager();
                    return persistenceManager.remove(persistencePackage);
                } catch (ServiceException e) {
                    //immediately throw validation exceptions without printing a stack trace
                    if (e instanceof ValidationException) {
                        throw e;
                    } else if (e.getCause() instanceof ValidationException) {
                        throw (ValidationException) e.getCause();
                    }
                    LOG.error("Problem removing " + persistencePackage.getCeilingEntityFullyQualifiedClassname(), e);
                    String message = exploitProtectionService.cleanString(e.getMessage());
                    throw recreateSpecificServiceException(e, message, e.getCause());
                }
            }
        });
    }
}
