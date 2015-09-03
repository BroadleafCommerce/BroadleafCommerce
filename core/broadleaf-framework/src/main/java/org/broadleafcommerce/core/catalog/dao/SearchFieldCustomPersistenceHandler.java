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
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.core.search.domain.SearchField;
import org.broadleafcommerce.core.search.domain.SearchFieldImpl;
import org.broadleafcommerce.core.search.domain.SearchFieldTypeImpl;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.ForeignKey;
import org.broadleafcommerce.openadmin.dto.OperationTypes;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManagerFactory;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.ListUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * @author Chad Harchar (charchar)
 */
@Component("blSearchFieldCustomPersistenceHandler")
public class SearchFieldCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(SearchFacetRangeCustomPersistenceHandler.class);

    @Resource(name = "blSearchFieldCustomPersistenceHandlerExtensionManager")
    protected SearchFieldCustomPersistenceHandlerExtensionManager extensionManager;

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        return SearchField.class.getName().equals(ceilingEntityFullyQualifiedClassname);
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
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(SearchField.class.getName(), persistencePerspective);
            Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
            SearchField adminInstance = (SearchField) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);

            return getEntity(persistencePackage, dynamicEntityDao, helper, entity, adminProperties, adminInstance);
        } catch (Exception e) {
            throw new ServiceException("Unable to perform update for entity: " + SearchField.class.getName(), e);
        }
    }

    protected Entity getEntity(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper, Entity entity, Map<String, FieldMetadata> adminProperties, SearchField adminInstance) throws ServiceException {
        adminInstance = (SearchField) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);
        adminInstance = dynamicEntityDao.merge(adminInstance);

        ExtensionResultStatusType result = ExtensionResultStatusType.NOT_HANDLED;
        if (extensionManager != null) {
             result = extensionManager.getProxy().addtoSearchableFields(persistencePackage, adminInstance);
        }

        if (result.equals(ExtensionResultStatusType.NOT_HANDLED)) {
            // If there is no searchable field types then we need to add a default as String
            if (ListUtils.isEmpty(adminInstance.getSearchableFieldTypes())) {
                PersistencePackage pp = createPersistencePackage(adminInstance, FieldType.TEXT);

                PersistenceManager pm = PersistenceManagerFactory.getPersistenceManager();

                pm.add(pp);
            }
        }

        Entity adminEntity = helper.getRecord(adminProperties, adminInstance, null, null);

        return adminEntity;
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            SearchField adminInstance = (SearchField) Class.forName(entity.getType()[0]).newInstance();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(SearchField.class.getName(), persistencePerspective);
            return getEntity(persistencePackage, dynamicEntityDao, helper, entity, adminProperties, adminInstance);
        } catch (Exception e) {
            throw new ServiceException("Unable to perform add for entity: " + SearchField.class.getName(), e);
        }
    }


    protected PersistencePackage createPersistencePackage(SearchField searchField, FieldType fieldType) {
        PersistencePackage pp = new PersistencePackage();
        pp.setCeilingEntityFullyQualifiedClassname(SearchFieldTypeImpl.class.getName());
        pp.setSecurityCeilingEntityFullyQualifiedClassname(SearchFieldTypeImpl.class.getName());
        pp.setSectionEntityField("searchableFieldTypes");

        PersistencePerspective perspective = new PersistencePerspective(new OperationTypes(OperationType.BASIC, OperationType.BASIC, OperationType.BASIC,
                OperationType.BASIC, OperationType.BASIC), new String[]{}, new ForeignKey[]{});
        ForeignKey foreignKey = new ForeignKey("searchField", SearchFieldImpl.class.getName());
        foreignKey.setOriginatingField(pp.getSectionEntityField());
        foreignKey.setDisplayValueProperty("name");
        perspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.FOREIGNKEY, foreignKey);
        pp.setPersistencePerspective(perspective);

        Entity entity = new Entity();
        entity.setType(new String[] { SearchFieldTypeImpl.class.getName() });
        List<Property> properties = new ArrayList<Property>();
        {
            Property prop = new Property();
            prop.setName("searchField");
            prop.setValue(String.valueOf(searchField.getId()));
            prop.setIsDirty(true);
            properties.add(prop);
        }
        {
            Property prop = new Property();
            prop.setName("searchableFieldType");
            prop.setValue(fieldType.getType());
            prop.setIsDirty(true);
            properties.add(prop);
        }


        entity.setProperties(properties.toArray(new Property[properties.size()]));
        pp.setEntity(entity);
        pp.setRequestingEntityName(searchField.getField().getFriendlyName());
        SectionCrumb section = new SectionCrumb();
        section.setSectionIdentifier(SearchFieldImpl.class.getName());
        section.setSectionId(String.valueOf(searchField.getId()));
        pp.setSectionCrumbs(new SectionCrumb[] { section });

        return pp;
    }

}