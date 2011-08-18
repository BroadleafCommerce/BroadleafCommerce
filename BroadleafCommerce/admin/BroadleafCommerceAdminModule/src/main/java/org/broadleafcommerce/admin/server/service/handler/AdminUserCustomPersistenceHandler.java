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

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUserImpl;
import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandler;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;

/**
 * 
 * @author jfischer
 *
 */
public class AdminUserCustomPersistenceHandler implements CustomPersistenceHandler {
	
	private static final Log LOG = LogFactory.getLog(AdminUserCustomPersistenceHandler.class);
	
	@Resource(name="blAdminSecurityService")
	protected AdminSecurityService adminSecurityService;

	public Boolean canHandleFetch(PersistencePackage persistencePackage) {
		return false;
	}

	public Boolean canHandleAdd(PersistencePackage persistencePackage) {
		return persistencePackage.getCeilingEntityFullyQualifiedClassname().equals(AdminUserImpl.class.getName());
	}

	public Boolean canHandleRemove(PersistencePackage persistencePackage) {
		return false;
	}

	public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
		return persistencePackage.getCeilingEntityFullyQualifiedClassname().equals(AdminUserImpl.class.getName());
	}

	public Boolean canHandleInspect(PersistencePackage persistencePackage) {
		return false;
	}

	public DynamicResultSet inspect(PersistencePackage persistencePackage, Map<String, FieldMetadata> metadataOverrides, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
		throw new RuntimeException("custom inspect not supported");
	}

	public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		throw new RuntimeException("custom fetch not supported");
	}

	public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		Entity entity  = persistencePackage.getEntity();
		try {
			PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			AdminUser adminInstance = (AdminUser) Class.forName(entity.getType()[0]).newInstance();
			Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(AdminUser.class);
			Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(AdminUser.class.getName(), persistencePerspective, dynamicEntityDao, entityClasses);
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

	public void remove(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		throw new RuntimeException("custom remove not supported");
	}

	public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		Entity entity = persistencePackage.getEntity();
		try {
			PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(AdminUser.class);
			Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(AdminUser.class.getName(), persistencePerspective, dynamicEntityDao, entityClasses);
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
