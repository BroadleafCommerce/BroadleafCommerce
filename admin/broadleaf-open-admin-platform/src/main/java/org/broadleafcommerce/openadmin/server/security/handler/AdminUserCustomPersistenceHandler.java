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
package org.broadleafcommerce.openadmin.server.security.handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.remote.EntityOperationType;
import org.broadleafcommerce.openadmin.server.security.remote.SecurityVerifier;
import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.springframework.stereotype.Component;

import java.util.Map;

import javax.annotation.Resource;

/**
 * 
 * @author jfischer
 *
 */
@Component("blAdminUserCustomPersistenceHandler")
public class AdminUserCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {
    
    private static final Log LOG = LogFactory.getLog(AdminUserCustomPersistenceHandler.class);
    
    @Resource(name="blAdminSecurityService")
    protected AdminSecurityService adminSecurityService;

    @Resource(name="blAdminSecurityRemoteService")
    protected SecurityVerifier adminRemoteSecurityService;

    @Override
    public Boolean willHandleSecurity(PersistencePackage persistencePackage) {
        return true;
    }

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        try {
            return persistencePackage.getCeilingEntityFullyQualifiedClassname() != null && AdminUser.class.isAssignableFrom(Class.forName(persistencePackage.getCeilingEntityFullyQualifiedClassname()));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        return canHandleAdd(persistencePackage);
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        adminRemoteSecurityService.securityCheck(persistencePackage.getCeilingEntityFullyQualifiedClassname(), EntityOperationType.ADD);
        Entity entity  = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            AdminUser adminInstance = (AdminUser) Class.forName(entity.getType()[0]).newInstance();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(AdminUser.class.getName(), persistencePerspective);
            adminInstance = (AdminUser) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);
            adminInstance.setUnencodedPassword(adminInstance.getPassword());
            adminInstance.setPassword(null);

            adminInstance = adminSecurityService.saveAdminUser(adminInstance);

            Entity adminEntity = helper.getRecord(adminProperties, adminInstance, null, null);

            return adminEntity;
        } catch (Exception e) {
            throw new ServiceException("Unable to add entity for " + entity.getType()[0], e);
        }
    }

    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {       
        Entity entity = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(AdminUser.class.getName(), persistencePerspective);
            Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
            AdminUser adminInstance = (AdminUser) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
            dynamicEntityDao.detach(adminInstance);
            adminInstance = (AdminUser) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);
            if (entity.getPMap().get("password") != null && StringUtils.isNotEmpty(entity.getPMap().get("password").getValue())) {
                adminInstance.setUnencodedPassword(adminInstance.getPassword());
                adminInstance.setPassword(null);
            }
            
            // The current user can update their data, but they cannot update other user's data.
            if (! adminRemoteSecurityService.getPersistentAdminUser().getId().equals(adminInstance.getId())) {
                adminRemoteSecurityService.securityCheck(persistencePackage.getCeilingEntityFullyQualifiedClassname(), EntityOperationType.UPDATE);                
            }
            
            adminInstance = adminSecurityService.saveAdminUser(adminInstance);
            Entity adminEntity = helper.getRecord(adminProperties, adminInstance, null, null);

            return adminEntity;

        } catch (Exception e) {
            throw new ServiceException("Unable to update entity for " + entity.getType()[0], e);
        }
    }
}
