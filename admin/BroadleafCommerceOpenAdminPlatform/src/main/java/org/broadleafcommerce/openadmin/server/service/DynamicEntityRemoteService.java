/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.openadmin.server.service;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.operation.EntityOperationType;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityService;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.security.remote.AdminSecurityServiceRemote;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.TargetModeType;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jfischer
 */
@Service("blDynamicEntityRemoteService")
public class DynamicEntityRemoteService implements DynamicEntityService, ApplicationContextAware {

    public static final String DEFAULTPERSISTENCEMANAGERREF = "blPersistenceManager";
    private static final Log LOG = LogFactory.getLog(DynamicEntityRemoteService.class);

    @Resource(name = "blAdminSecurityRemoteService")
    protected AdminSecurityServiceRemote adminRemoteSecurityService;

    protected Map<String, FieldMetadata> metadataOverrides;
    protected String persistenceManagerRef = DEFAULTPERSISTENCEMANAGERREF;
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }

    public DynamicResultSet inspect(PersistencePackage persistencePackage, String[] metadataOverrideKeys, FieldMetadata[] metadataOverrideValues) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            //use any override provided by the presentation layer
            Map<String, FieldMetadata> metadataOverrides = null;
            if (metadataOverrideKeys != null) {
                metadataOverrides = new HashMap<String, FieldMetadata>();
                for (int j = 0; j < metadataOverrideKeys.length; j++) {
                    metadataOverrides.put(metadataOverrideKeys[j], metadataOverrideValues[j]);
                }
            }
            //if no presentation layer override are defined, use any defined via configuration on the server side
            if (metadataOverrides == null && this.metadataOverrides != null) {
                metadataOverrides = this.metadataOverrides;
            }

            PersistenceManager persistenceManager = null;
            try {
                SandBoxContext context = new SandBoxContext();
                context.setSandBoxName(persistencePackage.getSandBoxInfo().getSandBox());
                context.setSandBoxMode(SandBoxMode.IMMEDIATE_COMMIT);
                SandBoxContext.setSandBoxContext(context);

                persistenceManager = (PersistenceManager) applicationContext.getBean(persistenceManagerRef);
                persistenceManager.setTargetMode(TargetModeType.SANDBOX);
                return persistenceManager.inspect(persistencePackage, metadataOverrides);
            } finally {
                if (persistenceManager != null) {
                    persistenceManager.close();
                }
                SandBoxContext.setSandBoxContext(null);
            }
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
            throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
        }
    }

    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        SandBoxInfo sandBoxInfo = persistencePackage.getSandBoxInfo();
        adminRemoteSecurityService.securityCheck(ceilingEntityFullyQualifiedClassname, EntityOperationType.FETCH);

        PersistenceManager persistenceManager = null;
        try {
            SandBoxContext context = new SandBoxContext();
            context.setSandBoxName(sandBoxInfo.getSandBox());
            context.setSandBoxMode(persistencePackage.getSandBoxInfo().isCommitImmediately()?SandBoxMode.IMMEDIATE_COMMIT:SandBoxMode.SANDBOX_COMMIT);
            SandBoxContext.setSandBoxContext(context);

            persistenceManager = (PersistenceManager) applicationContext.getBean(persistenceManagerRef);
            persistenceManager.setTargetMode(TargetModeType.SANDBOX);
            return persistenceManager.fetch(persistencePackage, cto);
        } finally {
            try {
                if (persistenceManager != null) {
                    persistenceManager.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            SandBoxContext.setSandBoxContext(null);
        }
    }

    public Entity add(PersistencePackage persistencePackage) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        SandBoxInfo sandBoxInfo = persistencePackage.getSandBoxInfo();
        adminRemoteSecurityService.securityCheck(ceilingEntityFullyQualifiedClassname, EntityOperationType.ADD);

        PersistenceManager persistenceManager = null;
        try {
            SandBoxContext context = new SandBoxContext();
            context.setSandBoxName(sandBoxInfo.getSandBox());
            context.setSandBoxMode(persistencePackage.getSandBoxInfo().isCommitImmediately()?SandBoxMode.IMMEDIATE_COMMIT:SandBoxMode.SANDBOX_COMMIT);
            SandBoxContext.setSandBoxContext(context);

            persistenceManager = (PersistenceManager) applicationContext.getBean(persistenceManagerRef);
            persistenceManager.setTargetMode(TargetModeType.SANDBOX);
            return persistenceManager.add(persistencePackage);
        } finally {
            try {
                if (persistenceManager != null) {
                    persistenceManager.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            SandBoxContext.setSandBoxContext(null);
        }
    }

    public Entity update(PersistencePackage persistencePackage) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        SandBoxInfo sandBoxInfo = persistencePackage.getSandBoxInfo();
        for (Property p : entity.getProperties()) {
            if (p.getName().equals("ceilingEntityFullyQualifiedClassname")) {
                adminRemoteSecurityService.securityCheck(p.getValue(), EntityOperationType.UPDATE);
                break;
            }
        }

        PersistenceManager persistenceManager = null;
        try {
            SandBoxContext context = new SandBoxContext();
            context.setSandBoxName(sandBoxInfo.getSandBox());
            context.setSandBoxMode(persistencePackage.getSandBoxInfo().isCommitImmediately()?SandBoxMode.IMMEDIATE_COMMIT:SandBoxMode.SANDBOX_COMMIT);
            SandBoxContext.setSandBoxContext(context);

            persistenceManager = (PersistenceManager) applicationContext.getBean(persistenceManagerRef);
            persistenceManager.setTargetMode(TargetModeType.SANDBOX);
            return persistenceManager.update(persistencePackage);
        } finally {
            try {
                if (persistenceManager != null) {
                    persistenceManager.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            SandBoxContext.setSandBoxContext(null);
        }
    }

    public void remove(PersistencePackage persistencePackage) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        SandBoxInfo sandBoxInfo = persistencePackage.getSandBoxInfo();
        for (Property p : entity.getProperties()) {
            if (p.getName().equals("ceilingEntityFullyQualifiedClassname")) {
                adminRemoteSecurityService.securityCheck(p.getValue(), EntityOperationType.REMOVE);
                break;
            }
        }

        PersistenceManager persistenceManager = null;
        try {
            SandBoxContext context = new SandBoxContext();
            context.setSandBoxName(sandBoxInfo.getSandBox());
            context.setSandBoxMode(persistencePackage.getSandBoxInfo().isCommitImmediately()?SandBoxMode.IMMEDIATE_COMMIT:SandBoxMode.SANDBOX_COMMIT);
            SandBoxContext.setSandBoxContext(context);

            persistenceManager = (PersistenceManager) applicationContext.getBean(persistenceManagerRef);
            persistenceManager.setTargetMode(TargetModeType.SANDBOX);
            persistenceManager.remove(persistencePackage);
        } finally {
            try {
                if (persistenceManager != null) {
                    persistenceManager.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            SandBoxContext.setSandBoxContext(null);
        }
    }

    public Map<String, FieldMetadata> getMetadataOverrides() {
        return metadataOverrides;
    }

    public void setMetadataOverrides(Map<String, FieldMetadata> metadataOverrides) {
        this.metadataOverrides = metadataOverrides;
    }

    public String getPersistenceManagerRef() {
        return persistenceManagerRef;
    }

    public void setPersistenceManagerRef(String persistenceManagerRef) {
        this.persistenceManagerRef = persistenceManagerRef;
    }

}
