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
package org.broadleafcommerce.openadmin.server.service.module;

import java.util.List;
import java.util.Map;

import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.dto.OperationType;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandler;
import org.broadleafcommerce.openadmin.server.service.remote.DynamicEntityRemoteService;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;

/**
 * 
 * @author jfischer
 *
 */
public interface RemoteServiceModule {

	public boolean isCompatible(OperationType operationType);
	
	public Entity add(String ceilingEntityFullyQualifiedClassname, Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException;
	
	public void updateMergedProperties(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties, Map<String, FieldMetadata> metadataOverrides) throws ServiceException;
	
	public void setCustomPersistenceHandlers(List<CustomPersistenceHandler> customPersistenceHandlers);
	
	public void extractProperties(Map<MergedPropertyType, Map<String, FieldMetadata>> mergedProperties, List<Property> properties) throws NumberFormatException;
	
	public Entity update(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException;
	
	public void remove(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException;
	
	public DynamicResultSet fetch(String ceilingEntityFullyQualifiedClassname, CriteriaTransferObject cto, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException;
	
	public void setDynamicEntityRemoteService(DynamicEntityRemoteService dynamicEntityRemoteService);
	
}
