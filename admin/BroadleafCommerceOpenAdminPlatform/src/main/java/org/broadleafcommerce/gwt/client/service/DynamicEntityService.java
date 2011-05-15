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
package org.broadleafcommerce.gwt.client.service;

import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.datasource.results.FieldMetadata;
import org.springframework.security.access.annotation.Secured;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.google.gwt.user.client.rpc.RemoteService;
import com.gwtincubator.security.exception.ApplicationSecurityException;

/**
 * 
 * @author jfischer
 *
 */
public interface DynamicEntityService extends RemoteService {
    
	@Secured("PERMISSION_DEFAULT")
	public DynamicResultSet inspect(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, String[] customCriteria, String[] metadataOverrideKeys, FieldMetadata[] metadataOverrideValues) throws ServiceException, ApplicationSecurityException;
	
	@Secured("PERMISSION_DEFAULT")
	public DynamicResultSet fetch(String ceilingEntityFullyQualifiedClassname, CriteriaTransferObject cto, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException, ApplicationSecurityException;
    
	@Secured("PERMISSION_DEFAULT")
	public Entity add(String ceilingEntityFullyQualifiedClassname, Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException, ApplicationSecurityException;
    
	@Secured("PERMISSION_DEFAULT")
    public Entity update(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException, ApplicationSecurityException;
    
	@Secured("PERMISSION_DEFAULT")
    public void remove(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException, ApplicationSecurityException;
    
}
