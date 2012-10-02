/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.server.service;

import com.gwtincubator.security.exception.ApplicationSecurityException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.security.service.ExploitProtectionService;
import org.broadleafcommerce.openadmin.client.dto.BatchDynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.BatchPersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityService;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.TargetModeType;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author jfischer
 */
@Service("blDynamicEntityRemoteService")
@Transactional(value="blTransactionManager", rollbackFor = ServiceException.class)
public class DynamicEntityRemoteService implements DynamicEntityService, DynamicEntityRemote, ApplicationContextAware {

    public static final String DEFAULTPERSISTENCEMANAGERREF = "blPersistenceManager";
    private static final Log LOG = LogFactory.getLog(DynamicEntityRemoteService.class);

    protected String persistenceManagerRef = DEFAULTPERSISTENCEMANAGERREF;
    private ApplicationContext applicationContext;

    @Resource(name="blExploitProtectionService")
    protected ExploitProtectionService exploitProtectionService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public BatchDynamicResultSet batchInspect(BatchPersistencePackage batchPersistencePackage) throws ServiceException, ApplicationSecurityException {
        DynamicResultSet[] results = new DynamicResultSet[batchPersistencePackage.getPersistencePackages().length];
        for (int j=0;j<batchPersistencePackage.getPersistencePackages().length;j++){
            results[j] = inspect(batchPersistencePackage.getPersistencePackages()[j]);
        }
        BatchDynamicResultSet batchResults = new BatchDynamicResultSet();
        batchResults.setDynamicResultSets(results);

        return batchResults;
    }

    @Override
    public DynamicResultSet inspect(PersistencePackage persistencePackage) throws ServiceException {
        exploitProtectionService.compareToken(persistencePackage.getCsrfToken());

        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            PersistenceManager persistenceManager = (PersistenceManager) applicationContext.getBean(persistenceManagerRef);
            persistenceManager.setTargetMode(TargetModeType.SANDBOX);
            return persistenceManager.inspect(persistencePackage);
        } catch (ServiceException e) {
            String message = exploitProtectionService.cleanString(e.getMessage());
            if (e.getCause() == null) {
                throw new ServiceException(message);
            } else {
                throw new ServiceException(message, e.getCause());
            }
        } catch (Exception e) {
            LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
            throw new ServiceException(exploitProtectionService.cleanString("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname), e);
        }
    }

    @Override
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto) throws ServiceException {
        exploitProtectionService.compareToken(persistencePackage.getCsrfToken());

        com.anasoft.os.daofusion.cto.client.CriteriaTransferObject targetCto = translateCto(cto);

        try {
            PersistenceManager persistenceManager = (PersistenceManager) applicationContext.getBean(persistenceManagerRef);
            persistenceManager.setTargetMode(TargetModeType.SANDBOX);
            return persistenceManager.fetch(persistencePackage, targetCto);
        } catch (ServiceException e) {
            String message = exploitProtectionService.cleanString(e.getMessage());
            if (e.getCause() == null) {
                throw new ServiceException(message);
            } else {
                throw new ServiceException(message, e.getCause());
            }
        }
    }

    protected com.anasoft.os.daofusion.cto.client.CriteriaTransferObject translateCto(CriteriaTransferObject cto) {
        com.anasoft.os.daofusion.cto.client.CriteriaTransferObject targetCto = new com.anasoft.os.daofusion.cto.client.CriteriaTransferObject();
        targetCto.setFirstResult(cto.getFirstResult());
        targetCto.setMaxResults(cto.getMaxResults());
        for (String propertyId : cto.getPropertyIdSet()) {
            FilterAndSortCriteria criteria = cto.get(propertyId);
            com.anasoft.os.daofusion.cto.client.FilterAndSortCriteria targetCriteria = new com.anasoft.os.daofusion.cto.client.FilterAndSortCriteria(propertyId);
            targetCriteria.setFilterValues(criteria.getFilterValues());
            targetCriteria.setIgnoreCase(criteria.getIgnoreCase());
            targetCriteria.setSortAscending(criteria.getSortAscending());
            targetCto.add(targetCriteria);
        }
        return targetCto;
    }

    protected void cleanEntity(Entity entity) throws ServiceException {
        try {
            for (Property property : entity.getProperties()) {
                property.setRawValue(property.getValue());
                property.setValue(exploitProtectionService.cleanString(property.getValue()));
                property.setUnHtmlEncodedValue(StringEscapeUtils.unescapeHtml(property.getValue()));
            }
        } catch (Exception e) {
            LOG.error("Unable to clean the passed in entity values", e);
            throw new ServiceException("Unable to clean the passed in entity values", e);
        }
    }

    @Override
    public Entity add(PersistencePackage persistencePackage) throws ServiceException {
        exploitProtectionService.compareToken(persistencePackage.getCsrfToken());

        cleanEntity(persistencePackage.getEntity());
        try {
            PersistenceManager persistenceManager = (PersistenceManager) applicationContext.getBean(persistenceManagerRef);
            persistenceManager.setTargetMode(TargetModeType.SANDBOX);
            return persistenceManager.add(persistencePackage);
        } catch (ServiceException e) {
            String message = exploitProtectionService.cleanString(e.getMessage());
            if (e.getCause() == null) {
                throw new ServiceException(message);
            } else {
                throw new ServiceException(message, e.getCause());
            }
        }
    }

    @Override
    public Entity update(PersistencePackage persistencePackage) throws ServiceException {
        exploitProtectionService.compareToken(persistencePackage.getCsrfToken());

        cleanEntity(persistencePackage.getEntity());
        try {
            PersistenceManager persistenceManager = (PersistenceManager) applicationContext.getBean(persistenceManagerRef);
            persistenceManager.setTargetMode(TargetModeType.SANDBOX);
            return persistenceManager.update(persistencePackage);
        } catch (ServiceException e) {
            String message = exploitProtectionService.cleanString(e.getMessage());
            if (e.getCause() == null) {
                throw new ServiceException(message);
            } else {
                throw new ServiceException(message, e.getCause());
            }
        }
    }

    @Override
    public void remove(PersistencePackage persistencePackage) throws ServiceException {
        exploitProtectionService.compareToken(persistencePackage.getCsrfToken());

        try {
            PersistenceManager persistenceManager = (PersistenceManager) applicationContext.getBean(persistenceManagerRef);
            persistenceManager.setTargetMode(TargetModeType.SANDBOX);
            persistenceManager.remove(persistencePackage);
        } catch (ServiceException e) {
            String message = exploitProtectionService.cleanString(e.getMessage());
            if (e.getCause() == null) {
                throw new ServiceException(message);
            } else {
                throw new ServiceException(message, e.getCause());
            }
        }
    }

    @Override
    public String getPersistenceManagerRef() {
        return persistenceManagerRef;
    }

    @Override
    public void setPersistenceManagerRef(String persistenceManagerRef) {
        this.persistenceManagerRef = persistenceManagerRef;
    }
}
