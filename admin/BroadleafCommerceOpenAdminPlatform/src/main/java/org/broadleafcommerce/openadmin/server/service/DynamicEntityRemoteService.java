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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.operation.EntityOperationType;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.client.dto.SandBoxInfo;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityService;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.security.remote.AdminSecurityServiceRemote;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.TargetModeType;
import org.springframework.stereotype.Service;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;

/**
 * 
 * @author jfischer
 *
 */
@Service("blDynamicEntityRemoteService")
public class DynamicEntityRemoteService implements DynamicEntityService {
	
	private static final Log LOG = LogFactory.getLog(DynamicEntityRemoteService.class);
	
	@Resource(name="blPersistenceManager")
	protected PersistenceManager persistenceManager;
	
	@Resource(name="blAdminSecurityRemoteService")
	protected AdminSecurityServiceRemote adminRemoteSecurityService;
	
	protected Map<String, FieldMetadata> metadataOverrides;
	
	public DynamicResultSet inspect(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, SandBoxInfo sandBoxInfo, String[] customCriteria, String[] metadataOverrideKeys, FieldMetadata[] metadataOverrideValues) throws ServiceException {
		try {
			//use any override provided by the presentation layer
			Map<String, FieldMetadata> metadataOverrides = null;
			if (metadataOverrideKeys != null) {
				metadataOverrides = new HashMap<String, FieldMetadata>();
				for (int j=0; j<metadataOverrideKeys.length; j++) {
					metadataOverrides.put(metadataOverrideKeys[j], metadataOverrideValues[j]);
				}
			}
			//if no presentation layer override are defined, use any defined via configuration on the server side
			if (metadataOverrides == null && this.metadataOverrides != null) {
				metadataOverrides = this.metadataOverrides;
			}
			
			try {
				SandBoxContext context = new SandBoxContext();
				context.setSandBoxName(sandBoxInfo.getSandBox());
				context.setSandBoxMode(SandBoxMode.ADMIN);
				SandBoxContext.setSandBoxContext(context);
				
				return persistenceManager.inspect(ceilingEntityFullyQualifiedClassname, persistencePerspective, customCriteria, metadataOverrides);
			} finally {
				SandBoxContext.setSandBoxContext(null);
			}
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
			throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
		}
	}

	public DynamicResultSet fetch(String ceilingEntityFullyQualifiedClassname, CriteriaTransferObject cto, PersistencePerspective persistencePerspective, SandBoxInfo sandBoxInfo, String[] customCriteria) throws ServiceException {
		adminRemoteSecurityService.securityCheck(ceilingEntityFullyQualifiedClassname, EntityOperationType.FETCH);
		
		try {
			SandBoxContext context = new SandBoxContext();
			context.setSandBoxName(sandBoxInfo.getSandBox());
			context.setSandBoxMode(SandBoxMode.ADMIN);
			SandBoxContext.setSandBoxContext(context);
			
			return persistenceManager.fetch(ceilingEntityFullyQualifiedClassname, cto, persistencePerspective, customCriteria);
		} finally {
			SandBoxContext.setSandBoxContext(null);
		}
	}

	public Entity add(String ceilingEntityFullyQualifiedClassname, Entity entity, PersistencePerspective persistencePerspective, SandBoxInfo sandBoxInfo, String[] customCriteria) throws ServiceException {
		adminRemoteSecurityService.securityCheck(ceilingEntityFullyQualifiedClassname, EntityOperationType.ADD);
		
		try {
			SandBoxContext context = new SandBoxContext();
			context.setSandBoxName(sandBoxInfo.getSandBox());
			context.setSandBoxMode(SandBoxMode.ADMIN);
			SandBoxContext.setSandBoxContext(context);
			
			return persistenceManager.add(ceilingEntityFullyQualifiedClassname, entity, persistencePerspective, customCriteria);
		} finally {
			SandBoxContext.setSandBoxContext(null);
		}
	}
	
	public Entity update(Entity entity, PersistencePerspective persistencePerspective, SandBoxInfo sandBoxInfo, String[] customCriteria) throws ServiceException {
		for (Property p : entity.getProperties()){
			if (p.getName().equals("ceilingEntityFullyQualifiedClassname")){
				adminRemoteSecurityService.securityCheck(p.getValue(), EntityOperationType.UPDATE);
				break;
			}
		}
		
		try {
			SandBoxContext context = new SandBoxContext();
			context.setSandBoxName(sandBoxInfo.getSandBox());
			context.setSandBoxMode(SandBoxMode.ADMIN);
			SandBoxContext.setSandBoxContext(context);
			
			return persistenceManager.update(entity, persistencePerspective, sandBoxInfo, customCriteria);
		} finally {
			SandBoxContext.setSandBoxContext(null);
		}
	}
	
	public void remove(Entity entity, PersistencePerspective persistencePerspective, SandBoxInfo sandBoxInfo, String[] customCriteria) throws ServiceException {
		for (Property p : entity.getProperties()){
			if (p.getName().equals("ceilingEntityFullyQualifiedClassname")){
				adminRemoteSecurityService.securityCheck(p.getValue(), EntityOperationType.REMOVE);
				break;
			}
		}
		
		try {
			SandBoxContext context = new SandBoxContext();
			context.setSandBoxName(sandBoxInfo.getSandBox());
			context.setSandBoxMode(SandBoxMode.ADMIN);
			SandBoxContext.setSandBoxContext(context);
			
			persistenceManager.remove(entity, persistencePerspective, customCriteria);
		} finally {
			SandBoxContext.setSandBoxContext(null);
		}
	}

	public Map<String, FieldMetadata> getMetadataOverrides() {
		return metadataOverrides;
	}

	public void setMetadataOverrides(Map<String, FieldMetadata> metadataOverrides) {
		this.metadataOverrides = metadataOverrides;
	}

	public PersistenceManager getPersistenceManager() {
		return persistenceManager;
	}

	public void setPersistenceManager(PersistenceManager persistenceManager) {
		persistenceManager.setTargetMode(TargetModeType.SANDBOX);
		this.persistenceManager = persistenceManager;
	}

}
