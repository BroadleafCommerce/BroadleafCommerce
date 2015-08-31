/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.core.catalog.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.search.domain.SearchFacet;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.springframework.stereotype.Component;
import java.util.Map;
import javax.annotation.Resource;

/**
 * @author Chad Harchar (charchar)
 */
@Component("blSearchFacetCustomPersistenceHandler")
public class SearchFacetCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(SearchFacetRangeCustomPersistenceHandler.class);

    @Resource(name = "blSearchFacetCustomPersistenceHandlerExtensionManager")
    protected SearchFacetCustomPersistenceHandlerExtensionManager extensionManager;

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        return SearchFacet.class.getName().equals(ceilingEntityFullyQualifiedClassname);
    }

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        return canHandleAdd(persistencePackage);
    }

    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(SearchFacet.class.getName(), persistencePerspective);
            Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
            SearchFacet adminInstance = (SearchFacet) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);

            return getEntity(persistencePackage, dynamicEntityDao, helper, entity, adminProperties, adminInstance);
        } catch (Exception e) {
            throw new ServiceException("Unable to perform update for entity: " + SearchFacet.class.getName(), e);
        }
    }

    protected Entity getEntity(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper, Entity entity, Map<String, FieldMetadata> adminProperties, SearchFacet adminInstance) throws ServiceException {
        adminInstance = (SearchFacet) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);
        adminInstance = dynamicEntityDao.merge(adminInstance);

        FieldType fieldType = FieldType.getInstance(adminInstance.getField().getFieldType());

        adminInstance.setFacetFieldType(fieldType.getType());

        if (extensionManager != null) {
            extensionManager.getProxy().addtoSearchableFields(persistencePackage, adminInstance);
        }

        if(adminInstance.getLabel() == null) {
            adminInstance.setLabel(entity.getPMap().get("field").getDisplayValue());
        }

        Entity adminEntity = helper.getRecord(adminProperties, adminInstance, null, null);

        return adminEntity;
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            SearchFacet adminInstance = (SearchFacet) Class.forName(entity.getType()[0]).newInstance();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(SearchFacet.class.getName(), persistencePerspective);
            return getEntity(persistencePackage, dynamicEntityDao, helper, entity, adminProperties, adminInstance);
        } catch (Exception e) {
            throw new ServiceException("Unable to perform add for entity: " + SearchFacet.class.getName(), e);
        }
    }

}