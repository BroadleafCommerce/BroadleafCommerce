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
package org.broadleafcommerce.openadmin.server.service.handler;

import java.util.Map;

import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.module.RecordHelper;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;

/**
 * 
 * @author jfischer
 *
 */
public interface CustomPersistenceHandler {
	
	public Boolean canHandleInspect(String ceilingEntityFullyQualifiedClassname, String[] customCriteria);
	public Boolean canHandleFetch(String ceilingEntityFullyQualifiedClassname, String[] customCriteria);
	public Boolean canHandleAdd(String ceilingEntityFullyQualifiedClassname, String[] customCriteria);
	public Boolean canHandleRemove(String ceilingEntityFullyQualifiedClassname, String[] customCriteria);
	public Boolean canHandleUpdate(String ceilingEntityFullyQualifiedClassname, String[] customCriteria);
	
	public DynamicResultSet inspect(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, String[] customCriteria, Map<String, FieldMetadata> metadataOverrides, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException;

	public DynamicResultSet fetch(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, CriteriaTransferObject cto, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException;
	
	public Entity add(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException;
	
	public void remove(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException;
	
	public Entity update(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException;
	
}
