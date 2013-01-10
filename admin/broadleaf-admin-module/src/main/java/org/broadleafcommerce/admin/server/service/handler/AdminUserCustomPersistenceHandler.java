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

package org.broadleafcommerce.admin.server.service.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUserImpl;
import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class AdminUserCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {
    
    private static final Log LOG = LogFactory.getLog(AdminUserCustomPersistenceHandler.class);
    
    @Resource(name="blAdminSecurityService")
    protected AdminSecurityService adminSecurityService;

    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        return persistencePackage.getCeilingEntityFullyQualifiedClassname() != null && persistencePackage.getCeilingEntityFullyQualifiedClassname().equals(AdminUserImpl.class.getName());
    }

    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        return persistencePackage.getCeilingEntityFullyQualifiedClassname() != null && persistencePackage.getCeilingEntityFullyQualifiedClassname().equals(AdminUserImpl.class.getName());
    }

    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
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

    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(AdminUser.class.getName(), persistencePerspective);
            Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
            AdminUser adminInstance = (AdminUser) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
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
}
