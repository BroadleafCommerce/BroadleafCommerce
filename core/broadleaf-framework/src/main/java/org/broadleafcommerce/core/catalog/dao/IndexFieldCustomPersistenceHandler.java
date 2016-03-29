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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.persistence.ArchiveStatus;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.util.BLCCollectionUtils;
import org.broadleafcommerce.common.util.TypedTransformer;
import org.broadleafcommerce.common.util.dao.QueryUtils;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXrefImpl;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.search.domain.IndexField;
import org.broadleafcommerce.core.search.domain.IndexFieldImpl;
import org.broadleafcommerce.core.search.domain.IndexFieldType;
import org.broadleafcommerce.core.search.domain.IndexFieldTypeImpl;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.dto.ForeignKey;
import org.broadleafcommerce.openadmin.dto.OperationTypes;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FieldPath;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FieldPathBuilder;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FilterMapping;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.Restriction;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate.PredicateProvider;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.ListUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author Chad Harchar (charchar)
 */
@Component("blIndexFieldCustomPersistenceHandler")
public class IndexFieldCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(IndexFieldCustomPersistenceHandler.class);

    @Resource(name = "blIndexFieldCustomPersistenceHandlerExtensionManager")
    protected IndexFieldCustomPersistenceHandlerExtensionManager extensionManager;

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        return IndexField.class.getName().equals(ceilingEntityFullyQualifiedClassname);
    }

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        return canHandleAdd(persistencePackage);
    }

    @Override
    public Boolean canHandleRemove(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();

        if (IndexField.class.getName().equalsIgnoreCase(ceilingEntityFullyQualifiedClassname) ||
                IndexFieldTypeImpl.class.getName().equalsIgnoreCase(ceilingEntityFullyQualifiedClassname)) {
            return true;
        }
        return super.canHandleRemove(persistencePackage);
    }

    @Override
    public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        return canHandleAdd(persistencePackage);
    }

    @Override
    public void remove(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(IndexField.class.getName(), persistencePerspective);
            Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
            Serializable instance = dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]),primaryKey);
            if (instance instanceof Status) {
                ((Status)instance).setArchived('Y');
                dynamicEntityDao.merge(instance);
                return;
            }

        } catch (Exception ex) {
            throw new ServiceException("Unable to perform remove for entity: " + entity.getType()[0], ex);

        }
        super.remove(persistencePackage, dynamicEntityDao, helper);
    }

    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(IndexField.class.getName(), persistencePerspective);
            Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
            IndexField adminInstance = (IndexField) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);

            return getEntity(persistencePackage, dynamicEntityDao, helper, entity, adminProperties, adminInstance);
        } catch (Exception e) {
            throw new ServiceException("Unable to perform update for entity: " + IndexField.class.getName(), e);
        }
    }

    protected Entity getEntity(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper, Entity entity, Map<String, FieldMetadata> adminProperties, IndexField adminInstance) throws ServiceException {
        adminInstance = (IndexField) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);
        adminInstance = dynamicEntityDao.merge(adminInstance);

        ExtensionResultStatusType result = ExtensionResultStatusType.NOT_HANDLED;
        if (extensionManager != null) {
             result = extensionManager.getProxy().addtoSearchableFields(persistencePackage, adminInstance);
        }

        if (result.equals(ExtensionResultStatusType.NOT_HANDLED)) {
            // If there is no searchable field types then we need to add a default as String
            if (ListUtils.isEmpty(adminInstance.getFieldTypes())) {
                IndexFieldType indexFieldType = new IndexFieldTypeImpl();
                indexFieldType.setFieldType(FieldType.TEXT);
                indexFieldType.setIndexField(adminInstance);
                adminInstance.getFieldTypes().add(indexFieldType);
                adminInstance = dynamicEntityDao.merge(adminInstance);
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
            IndexField adminInstance = (IndexField) Class.forName(entity.getType()[0]).newInstance();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(IndexField.class.getName(), persistencePerspective);
            return getEntity(persistencePackage, dynamicEntityDao, helper, entity, adminProperties, adminInstance);
        } catch (Exception e) {
            throw new ServiceException("Unable to perform add for entity: " + IndexField.class.getName(), e);
        }
    }

    @Override
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto, DynamicEntityDao
            dynamicEntityDao, RecordHelper helper) throws ServiceException {

        FilterAndSortCriteria fieldFsc = cto.getCriteriaMap().get("field");
        FilterAndSortCriteria searchableFsc = cto.getCriteriaMap().get("searchable");
        if (fieldFsc != null) {
            List<String> filterValues = fieldFsc.getFilterValues();
            boolean didFilter = false;
            cto.getCriteriaMap().remove("field");

            CriteriaBuilder builder = dynamicEntityDao.getStandardEntityManager().getCriteriaBuilder();
            CriteriaQuery<IndexField> criteria = builder.createQuery(IndexField.class);
            Root<IndexFieldImpl> root = criteria.from(IndexFieldImpl.class);
            criteria.select(root);

            // Check if we are searching for specific field names
            List<Predicate> restrictions = new ArrayList<Predicate>();
            if (CollectionUtils.isNotEmpty(filterValues)) {
                restrictions.add(builder.like(root.get("field").<String>get("friendlyName"), "%" + filterValues.get(0) + "%"));
                didFilter = true;
            }

            // We only want unarchived records
            QueryUtils.notArchived(builder, restrictions, root, "archiveStatus");

            // Check if this filter value has a sort direction associated with it
            Order order = null;
            if (fieldFsc.getSortDirection() != null) {
                if (fieldFsc.getSortAscending()) {
                    order = builder.asc(root.get("field").get("friendlyName"));
                } else {
                    order = builder.desc(root.get("field").get("friendlyName"));
                }
                criteria.orderBy(order);
            }
            // Make sure the user isn't sorting on searchable
            else if (searchableFsc != null && searchableFsc.getSortDirection() != null) {
                if (searchableFsc.getSortAscending()) {
                    order = builder.asc(root.get("searchable"));
                } else {
                    order = builder.desc(root.get("searchable"));
                }
                criteria.orderBy(order);
            }

            criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));

            TypedQuery<IndexField> query = dynamicEntityDao.getStandardEntityManager().createQuery(criteria);
            List<IndexField> indexFields = query.getResultList();

            List<Entity> entityList = new ArrayList<Entity>();
            for (IndexField indexField : indexFields) {
                // Create the new Entity DTO
                Entity entity = new Entity();
                Property[] properties = new Property[3];

                // Set the entities id
                Property idProperty = new Property();
                idProperty.setName("id");
                idProperty.setValue(indexField.getId().toString());

                // Set the field id
                Property fieldProperty = new Property();
                fieldProperty.setName("field");
                fieldProperty.setValue(indexField.getField().getId().toString());
                fieldProperty.setDisplayValue(indexField.getField().getFriendlyName());

                // Set the searchable property
                Property searchableProperty = new Property();
                searchableProperty.setName("searchable");
                searchableProperty.setValue(indexField.getSearchable().toString());

                // Add the properties to the Entity
                properties[0] = idProperty;
                properties[1] = fieldProperty;
                properties[2] = searchableProperty;
                entity.setProperties(properties);

                // Store the fully created entity
                entityList.add(entity);
            }

            // We need to get the total number of records available because the entityList returned may not be the full set.
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> originalProps = helper.getSimpleMergedProperties(IndexField.class.getName(), persistencePerspective);
            List<FilterMapping> filterMappings = helper.getFilterMappings(persistencePerspective, cto, IndexField.class.getName(), originalProps);
            int totalRecords = helper.getTotalRecords(persistencePackage.getCeilingEntityFullyQualifiedClassname(), filterMappings);

            // Create a Dynamic Result Set from the entity list created above.
            DynamicResultSet resultSet = new DynamicResultSet(entityList.toArray(new Entity[entityList.size()]), (didFilter ? entityList.size() : totalRecords));
            return resultSet;
        }

        DynamicResultSet resultSet = helper.getCompatibleModule(OperationType.BASIC).fetch(persistencePackage, cto);
        return resultSet;
    }

    protected PersistencePackage createPersistencePackage(IndexField searchField, FieldType fieldType) {
        PersistencePackage pp = new PersistencePackage();
        pp.setCeilingEntityFullyQualifiedClassname(IndexFieldTypeImpl.class.getName());
        pp.setSecurityCeilingEntityFullyQualifiedClassname(IndexFieldTypeImpl.class.getName());
        pp.setSectionEntityField("fieldTypes");

        PersistencePerspective perspective = new PersistencePerspective(new OperationTypes(OperationType.BASIC, OperationType.BASIC, OperationType.BASIC,
                OperationType.BASIC, OperationType.BASIC), new String[]{}, new ForeignKey[]{});
        ForeignKey foreignKey = new ForeignKey("indexField", IndexFieldImpl.class.getName());
        foreignKey.setOriginatingField(pp.getSectionEntityField());
        foreignKey.setDisplayValueProperty("name");
        perspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.FOREIGNKEY, foreignKey);
        pp.setPersistencePerspective(perspective);

        Entity entity = new Entity();
        entity.setType(new String[] { IndexFieldTypeImpl.class.getName() });
        List<Property> properties = new ArrayList<Property>();
        {
            Property prop = new Property();
            prop.setName("indexField");
            prop.setValue(String.valueOf(searchField.getId()));
            prop.setIsDirty(true);
            properties.add(prop);
        }
        {
            Property prop = new Property();
            prop.setName("fieldType");
            prop.setValue(fieldType.getType());
            prop.setIsDirty(true);
            properties.add(prop);
        }


        entity.setProperties(properties.toArray(new Property[properties.size()]));
        pp.setEntity(entity);
        pp.setRequestingEntityName(searchField.getField().getFriendlyName());
        SectionCrumb section = new SectionCrumb();
        section.setSectionIdentifier(IndexFieldImpl.class.getName());
        section.setSectionId(String.valueOf(searchField.getId()));
        pp.setSectionCrumbs(new SectionCrumb[] { section });

        return pp;
    }

}