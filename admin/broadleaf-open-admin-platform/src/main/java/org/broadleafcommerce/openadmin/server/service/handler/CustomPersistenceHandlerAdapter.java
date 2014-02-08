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
package org.broadleafcommerce.openadmin.server.service.handler;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jeff Fischer
 */
public class CustomPersistenceHandlerAdapter implements CustomPersistenceHandler {

    @Override
    public Boolean canHandleInspect(PersistencePackage persistencePackage) {
        return false;
    }

    @Override
    public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        return false;
    }

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        return false;
    }

    @Override
    public Boolean canHandleRemove(PersistencePackage persistencePackage) {
        return false;
    }

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        return false;
    }

    @Override
    public DynamicResultSet inspect(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
        throw new ServiceException("Inspect not supported");
    }

    @Override
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        throw new ServiceException("Fetch not supported");
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        throw new ServiceException("Add not supported");
    }

    @Override
    public void remove(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
       throw new ServiceException("Remove not supported");
    }

    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        throw new ServiceException("Update not supported");
    }

    @Override
    public Boolean willHandleSecurity(PersistencePackage persistencePackage) {
        return false;
    }

    @Override
    public int getOrder() {
        return CustomPersistenceHandler.DEFAULT_ORDER;
    }
    
    /**
     * This is a helper method that can be invoked as a first step in a custom inspect phase
     */
    protected Map<String, FieldMetadata> getMetadata(PersistencePackage persistencePackage, InspectHelper helper) 
            throws ServiceException {
        String entityName = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        PersistencePerspective perspective = persistencePackage.getPersistencePerspective();
        return helper.getSimpleMergedProperties(entityName, perspective);
    }
    
    /**
     * This is a helper method that can be invoked as the last step in a custom inspect phase. It will assemble the
     * appropriate DynamicResultSet from the given parameters.
     */
    protected DynamicResultSet getResultSet(PersistencePackage persistencePackage, InspectHelper helper,
            Map<String, FieldMetadata> metadata) throws ServiceException {
        String entityName = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            if (helper instanceof PersistenceManager) {
                Class<?>[] entities = ((PersistenceManager) helper).getPolymorphicEntities(entityName);
                Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties = 
                        new HashMap<MergedPropertyType, Map<String, FieldMetadata>>();
                allMergedProperties.put(MergedPropertyType.PRIMARY, metadata);
                ClassMetadata mergedMetadata = helper.getMergedClassMetadata(entities, allMergedProperties);
                DynamicResultSet results = new DynamicResultSet(mergedMetadata);
                return results;
            }
        } catch (ClassNotFoundException e) {
            throw new ServiceException(e);
        }
        return new DynamicResultSet();
    }
}
