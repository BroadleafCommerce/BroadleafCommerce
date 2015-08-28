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
package org.broadleafcommerce.openadmin.server.service;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.dao.GenericEntityDao;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelper;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelperImpl;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.openadmin.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.dto.BasicCollectionMetadata;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.CollectionMetadata;
import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.dto.GroupMetadata;
import org.broadleafcommerce.openadmin.dto.MapMetadata;
import org.broadleafcommerce.openadmin.dto.MapStructure;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.broadleafcommerce.openadmin.dto.TabMetadata;
import org.broadleafcommerce.openadmin.exception.EntityNotFoundException;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.server.factory.PersistencePackageFactory;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceResponse;
import org.broadleafcommerce.openadmin.server.service.persistence.module.BasicPersistenceModule;
import org.broadleafcommerce.openadmin.web.form.entity.DynamicEntityFormInfo;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.broadleafcommerce.openadmin.web.form.entity.Tab;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Andre Azzolini (apazzolini)
 */
@Service("blAdminEntityService")
public class AdminEntityServiceImpl implements AdminEntityService {

    protected static final Log LOG = LogFactory.getLog(AdminEntityServiceImpl.class);

    @Resource(name = "blDynamicEntityRemoteService")
    protected DynamicEntityService service;

    @Resource(name = "blPersistencePackageFactory")
    protected PersistencePackageFactory persistencePackageFactory;

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;
    
    @Resource(name = "blGenericEntityDao")
    protected GenericEntityDao genericEntityDao;

    protected DynamicDaoHelper dynamicDaoHelper = new DynamicDaoHelperImpl();

    @Override
    public PersistenceResponse getClassMetadata(PersistencePackageRequest request)
            throws ServiceException {
        PersistenceResponse response = inspect(request);
        return response;
    }

    @Override
    public PersistenceResponse getRecords(PersistencePackageRequest request) throws ServiceException {
        return fetch(request);
    }

    @Override
    public PersistenceResponse getRecord(PersistencePackageRequest request, String id, ClassMetadata cmd, boolean isCollectionRequest)
            throws ServiceException {
        String idProperty = getIdProperty(cmd);
        
        FilterAndSortCriteria fasc = new FilterAndSortCriteria(idProperty);
        fasc.setFilterValue(id);
        request.addFilterAndSortCriteria(fasc);
        
        PersistenceResponse response = fetch(request);
        Entity[] entities = response.getDynamicResultSet().getRecords();
        if (ArrayUtils.isEmpty(entities)) {
            throw new EntityNotFoundException();
        }

        return response;
    }

    @Override
    public PersistenceResponse addEntity(EntityForm entityForm, String[] customCriteria, List<SectionCrumb> sectionCrumb) throws ServiceException {
        PersistencePackageRequest ppr = getRequestForEntityForm(entityForm, customCriteria, sectionCrumb);
        // If the entity form has dynamic forms inside of it, we need to persist those as well.
        // They are typically done in their own custom persistence handlers, which will get triggered
        // based on the criteria specific in the PersistencePackage.
        for (Entry<String, EntityForm> entry : entityForm.getDynamicForms().entrySet()) {
            DynamicEntityFormInfo info = entityForm.getDynamicFormInfo(entry.getKey());

            if (info.getCustomCriteriaOverride() != null) {
                customCriteria = info.getCustomCriteriaOverride();
            } else {
                String propertyName = info.getPropertyName();
                String propertyValue;
                if (entityForm.getFields().containsKey(propertyName)) {
                    propertyValue = entityForm.getFields().get(propertyName).getValue();
                } else {
                    propertyValue = info.getPropertyValue();
                }
                customCriteria = new String[] {info.getCriteriaName(), entityForm.getId(), propertyName, propertyValue};
            }

            PersistencePackageRequest subRequest = getRequestForEntityForm(entry.getValue(), customCriteria, sectionCrumb);
            ppr.addSubRequest(info.getPropertyName(), subRequest);
        }
        return add(ppr);
    }

    @Override
    public PersistenceResponse updateEntity(EntityForm entityForm, String[] customCriteria, List<SectionCrumb> sectionCrumb) throws ServiceException {
        PersistencePackageRequest ppr = getRequestForEntityForm(entityForm, customCriteria, sectionCrumb);
        ppr.setRequestingEntityName(entityForm.getMainEntityName());
        // If the entity form has dynamic forms inside of it, we need to persist those as well.
        // They are typically done in their own custom persistence handlers, which will get triggered
        // based on the criteria specific in the PersistencePackage.
        for (Entry<String, EntityForm> entry : entityForm.getDynamicForms().entrySet()) {
            DynamicEntityFormInfo info = entityForm.getDynamicFormInfo(entry.getKey());

            if (info.getCustomCriteriaOverride() != null) {
                customCriteria = info.getCustomCriteriaOverride();
            } else {
                String propertyName = info.getPropertyName();
                String propertyValue = entityForm.getFields().get(propertyName).getValue();
                customCriteria = new String[] { info.getCriteriaName(), entityForm.getId(), propertyName, propertyValue };
            }

            PersistencePackageRequest subRequest = getRequestForEntityForm(entry.getValue(), customCriteria, sectionCrumb);
            subRequest.withSecurityCeilingEntityClassname(info.getSecurityCeilingClassName());
            ppr.addSubRequest(info.getPropertyName(), subRequest);
        }
        return update(ppr);
    }

    @Override
    public PersistenceResponse removeEntity(EntityForm entityForm, String[] customCriteria, List<SectionCrumb> sectionCrumb)
            throws ServiceException {
        PersistencePackageRequest ppr = getRequestForEntityForm(entityForm, customCriteria, sectionCrumb);
        return remove(ppr);
    }
    
    protected List<Property> getPropertiesFromEntityForm(EntityForm entityForm) {
        List<Property> properties = new ArrayList<Property>(entityForm.getFields().size());
        
        for (Entry<String, Field> entry : entityForm.getFields().entrySet()) {
            Property p = new Property();
            p.setName(entry.getKey());
            p.setValue(entry.getValue().getValue());
            p.setDisplayValue(entry.getValue().getDisplayValue());
            p.setIsDirty(entry.getValue().getIsDirty());
            properties.add(p);
        }
        
        return properties;
    }

    public PersistencePackageRequest getRequestForEntityForm(EntityForm entityForm, String[] customCriteria, List<SectionCrumb> sectionCrumbs) {
        // Ensure the ID property is on the form
        Field idField = entityForm.getFields().get(entityForm.getIdProperty());
        if (idField == null) {
            idField = new Field();
            idField.setName(entityForm.getIdProperty());
            idField.setValue(entityForm.getId());
            entityForm.getFields().put(entityForm.getIdProperty(), idField);
        } else {
            idField.setValue(entityForm.getId());
        }

        List<Property> propList = getPropertiesFromEntityForm(entityForm);
        Property[] properties = new Property[propList.size()];
        properties = propList.toArray(properties);

        Entity entity = new Entity();
        entity.setProperties(properties);
        String entityType = entityForm.getEntityType();
        if (StringUtils.isEmpty(entityType)) {
            entityType = entityForm.getCeilingEntityClassname();
        }
        entity.setType(new String[] { entityType });

        PersistencePackageRequest ppr = PersistencePackageRequest.standard()
                .withEntity(entity)
                .withCustomCriteria(customCriteria)
                .withCeilingEntityClassname(entityForm.getCeilingEntityClassname())
                .withSectionCrumbs(sectionCrumbs)
                .withRequestingEntityName(entityForm.getMainEntityName());
        return ppr;
    }

    @Override
    public PersistenceResponse getAdvancedCollectionRecord(ClassMetadata containingClassMetadata, Entity containingEntity,
            Property collectionProperty, String collectionItemId, List<SectionCrumb> sectionCrumbs, String alternateId)
            throws ServiceException {
        PersistencePackageRequest ppr = PersistencePackageRequest.fromMetadata(collectionProperty.getMetadata(), sectionCrumbs);

        FieldMetadata md = collectionProperty.getMetadata();
        String containingEntityId = getContextSpecificRelationshipId(containingClassMetadata, containingEntity,
                collectionProperty.getName());
        ppr.setSectionEntityField(collectionProperty.getName());

        PersistenceResponse response;

        if (md instanceof AdornedTargetCollectionMetadata) {
            FilterAndSortCriteria fasc = new FilterAndSortCriteria(ppr.getAdornedList().getCollectionFieldName());
            fasc.setFilterValue(containingEntityId);
            ppr.addFilterAndSortCriteria(fasc);

            fasc = new FilterAndSortCriteria(ppr.getAdornedList().getCollectionFieldName() + "Target");
            fasc.setFilterValue(collectionItemId);
            ppr.addFilterAndSortCriteria(fasc);

            if (!StringUtils.isEmpty(alternateId)) {
                fasc = new FilterAndSortCriteria(ppr.getAdornedList().getIdProperty());
                fasc.setFilterValue(alternateId);
                ppr.addFilterAndSortCriteria(fasc);
            }

            response = fetch(ppr);
            Entity[] entities = response.getDynamicResultSet().getRecords();
            if (ArrayUtils.isEmpty(entities)) {
                throw new EntityNotFoundException();
            }
        } else if (md instanceof MapMetadata) {
            MapMetadata mmd = (MapMetadata) md;
            FilterAndSortCriteria fasc = new FilterAndSortCriteria(ppr.getForeignKey().getManyToField());
            fasc.setFilterValue(containingEntityId);
            ppr.addFilterAndSortCriteria(fasc);

            response = fetch(ppr);
            Entity[] entities = response.getDynamicResultSet().getRecords();
            for (Entity e : entities) {
                String idProperty = getIdProperty(containingClassMetadata);
                if (mmd.isSimpleValue()) {
                    idProperty = "key";
                }
                Property p = e.getPMap().get(idProperty);
                if (p.getValue().equals(collectionItemId)) {
                    response.setEntity(e);
                    break;
                }
            }
        } else {
            throw new IllegalArgumentException(String.format("The specified field [%s] for class [%s] was not an " +
                    "advanced collection field.", collectionProperty.getName(), containingClassMetadata.getCeilingType()));
        }

        return response;
    }

    @Override
    public PersistenceResponse getRecordsForCollection(ClassMetadata containingClassMetadata, Entity containingEntity,
            Property collectionProperty, FilterAndSortCriteria[] fascs, Integer startIndex, Integer maxIndex, List<SectionCrumb> sectionCrumb)
            throws ServiceException {
        return getRecordsForCollection(containingClassMetadata, containingEntity, collectionProperty, fascs, startIndex, 
                maxIndex, null, sectionCrumb);
    }
    
    @Override
    public PersistenceResponse getRecordsForCollection(ClassMetadata containingClassMetadata, Entity containingEntity,
            Property collectionProperty, FilterAndSortCriteria[] fascs, Integer startIndex, Integer maxIndex,
            String idValueOverride, List<SectionCrumb> sectionCrumbs) throws ServiceException {
        
        PersistencePackageRequest ppr = PersistencePackageRequest.fromMetadata(collectionProperty.getMetadata(), sectionCrumbs)
                .withFilterAndSortCriteria(fascs)
                .withStartIndex(startIndex)
                .withMaxIndex(maxIndex);
        
        FilterAndSortCriteria fasc;

        FieldMetadata md = collectionProperty.getMetadata();
        String collectionCeilingClass = null;

        if (md instanceof BasicCollectionMetadata) {
            fasc = new FilterAndSortCriteria(ppr.getForeignKey().getManyToField());
            collectionCeilingClass = ((CollectionMetadata) md).getCollectionCeilingEntity();
        } else if (md instanceof AdornedTargetCollectionMetadata) {
            fasc = new FilterAndSortCriteria(ppr.getAdornedList().getCollectionFieldName());
            collectionCeilingClass = ((CollectionMetadata) md).getCollectionCeilingEntity();
        } else if (md instanceof MapMetadata) {
            fasc = new FilterAndSortCriteria(ppr.getForeignKey().getManyToField());
        } else {
            throw new IllegalArgumentException(String.format("The specified field [%s] for class [%s] was not a " +
                    "collection field.", collectionProperty.getName(), containingClassMetadata.getCeilingType()));
        }

        String id;
        if (idValueOverride == null) {
            id = getContextSpecificRelationshipId(containingClassMetadata, containingEntity, collectionProperty.getName());
        } else {
            id = idValueOverride;
        }
        fasc.setFilterValue(id);
        ppr.addFilterAndSortCriteria(fasc);

        if (collectionCeilingClass != null) {
            ppr.setCeilingEntityClassname(collectionCeilingClass);
        }
        ppr.setSectionEntityField(collectionProperty.getName());

        return fetch(ppr);
    }

    @Override
    public Map<String, DynamicResultSet> getRecordsForAllSubCollections(PersistencePackageRequest ppr, Entity containingEntity, Integer startIndex, Integer maxIndex, List<SectionCrumb> sectionCrumb)
            throws ServiceException {
        Map<String, DynamicResultSet> map = new HashMap<String, DynamicResultSet>();

        PersistenceResponse response = getClassMetadata(ppr);
        ClassMetadata cmd = response.getDynamicResultSet().getClassMetaData();
        for (Property p : cmd.getProperties()) {
            if (ArrayUtils.contains(p.getMetadata().getAvailableToTypes(), containingEntity.getType()[0])
                    && p.getMetadata() instanceof CollectionMetadata) {
                PersistenceResponse response2 = getRecordsForCollection(cmd, containingEntity, p, null, startIndex, maxIndex, sectionCrumb);
                map.put(p.getName(), response2.getDynamicResultSet());
            }
        }

        return map;
    }

    @Override
    public Map<String, DynamicResultSet> getRecordsForAllSubCollections(PersistencePackageRequest ppr, Entity containingEntity, List<SectionCrumb> sectionCrumb)
            throws ServiceException {

        return getRecordsForAllSubCollections(ppr, containingEntity, null, null, sectionCrumb);
    }

    @Override
    public Map<String, DynamicResultSet> getRecordsForSelectedTab(ClassMetadata cmd, Entity containingEntity, List<SectionCrumb> sectionCrumb,
            String currentTabName) throws ServiceException {
        Map<String, DynamicResultSet> map = new HashMap<String, DynamicResultSet>();
        for (Property p : cmd.getProperties()) {
            if (ArrayUtils.contains(p.getMetadata().getAvailableToTypes(), containingEntity.getType()[0])
                    && p.getMetadata() instanceof CollectionMetadata) {

                CollectionMetadata collectionMetadata = (CollectionMetadata) p.getMetadata();
                String tabName = collectionMetadata.getTab();
                int tabOrder = collectionMetadata.getTabOrder();
                updateTabInfo(collectionMetadata, cmd, tabName, tabOrder);

                if (collectionMetadata.getLazyFetch() != null && collectionMetadata.getLazyFetch()
                        && getTabNameDisplayValue(tabName).toUpperCase().startsWith(currentTabName.toUpperCase())) {
                    PersistenceResponse response2 = getRecordsForCollection(cmd, containingEntity, p, null, null, null, sectionCrumb);
                    map.put(p.getName(), response2.getDynamicResultSet());
                } else if (collectionMetadata.getLazyFetch() != null && !collectionMetadata.getLazyFetch()) {
                    PersistenceResponse response2 = getRecordsForCollection(cmd, containingEntity, p, null, null, null, sectionCrumb);
                    map.put(p.getName(), response2.getDynamicResultSet());
                } else {
                    DynamicResultSet drs = new DynamicResultSet();
                    Map<String, Tab> tabMap = new HashMap<String, Tab>();
                    Tab tab = new Tab();
                    tab.setTitle(tabName);
                    tab.setOrder(tabOrder);
                    tabMap.put(tab.getTitle(), tab);
                    drs.setUnselectedTabMetadata(tabMap);
                    drs.setTotalRecords(1);
                    drs.setStartIndex(0);
                    drs.setBatchId(1);
                    drs.setClassMetaData(null);
                    drs.setPageSize(1);
                    drs.setRecords(new Entity[0]);
                    map.put(p.getName(), drs);
                }
            }
        }

        return map;
    }

    protected void updateTabInfo(CollectionMetadata fmd, ClassMetadata cmd, String tabName, int tabOrder) {
        boolean tabInfoFound = false;
        Map<String, TabMetadata> tabMetadataMap = cmd.getTabAndGroupMetadata();
        for (String tabKey : tabMetadataMap.keySet()) {
            Map<String, GroupMetadata> groupMetadataMap = tabMetadataMap.get(tabKey).getGroupMetadata();
            for (String groupKey : groupMetadataMap.keySet()) {
                if (groupKey.equals(fmd.getGroup()) || groupMetadataMap.get(groupKey).getGroupName().equals(fmd.getGroup())) {
                    tabName = tabMetadataMap.get(tabKey).getTabName();
                    tabOrder = tabMetadataMap.get(tabKey).getTabOrder();
                    tabInfoFound = true;
                    break;
                }
            }
            if (tabInfoFound) {
                break;
            }
            if (tabKey.equals(tabName) || tabMetadataMap.get(tabKey).getTabName().equals(tabName)) {
                tabName = tabMetadataMap.get(tabKey).getTabName();
                tabOrder = tabMetadataMap.get(tabKey).getTabOrder();
            }
        }
    }

    protected String getTabNameDisplayValue(String tabName) {
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        if (context != null && context.getMessageSource() != null) {
            tabName = context.getMessageSource().getMessage(tabName, null, tabName, context.getJavaLocale());
        }
        return tabName;
    }
    
    @Override
    public PersistenceResponse addSubCollectionEntity(EntityForm entityForm, ClassMetadata mainMetadata, Property field,
            Entity parentEntity, List<SectionCrumb> sectionCrumbs)
            throws ServiceException, ClassNotFoundException {
        // Assemble the properties from the entity form
        List<Property> properties = new ArrayList<Property>();
        for (Entry<String, Field> entry : entityForm.getFields().entrySet()) {
            Property p = new Property();
            p.setName(entry.getKey());
            p.setValue(entry.getValue().getValue());
            properties.add(p);
        }

        FieldMetadata md = field.getMetadata();

        PersistencePackageRequest ppr = PersistencePackageRequest.fromMetadata(md, sectionCrumbs)
                .withEntity(new Entity());

        if (md instanceof BasicCollectionMetadata) {
            BasicCollectionMetadata fmd = (BasicCollectionMetadata) md;
            ppr.getEntity().setType(new String[] { entityForm.getEntityType() });
            
            // If we're looking up an entity instead of trying to create one on the fly, let's make sure 
            // that we're not changing the target entity at all and only creating the association to the id
            if (fmd.getAddMethodType().equals(AddMethodType.LOOKUP) || 
                    fmd.getAddMethodType().equals(AddMethodType.LOOKUP_FOR_UPDATE)) {
                List<String> fieldsToRemove = new ArrayList<String>();
                
                String idProp = getIdProperty(mainMetadata);
                for (String key : entityForm.getFields().keySet()) {
                    if (!idProp.equals(key)) {
                        fieldsToRemove.add(key);
                    }
                }
                
                for (String key : fieldsToRemove) {
                    ListIterator<Property> li = properties.listIterator();
                    while (li.hasNext()) {
                        if (li.next().getName().equals(key)) {
                            li.remove();
                        }
                    }
                }
                
                ppr.setValidateUnsubmittedProperties(false);
            }

            if (fmd.getAddMethodType().equals(AddMethodType.LOOKUP_FOR_UPDATE)) {
                ppr.setUpdateLookupType(true);
            }

            Property fp = new Property();
            fp.setName(ppr.getForeignKey().getManyToField());
            fp.setValue(getContextSpecificRelationshipId(mainMetadata, parentEntity, field.getName()));
            properties.add(fp);
        } else if (md instanceof AdornedTargetCollectionMetadata) {
            ppr.getEntity().setType(new String[] { ppr.getAdornedList().getAdornedTargetEntityClassname() });
            
            String[] maintainedFields = ((AdornedTargetCollectionMetadata) md).getMaintainedAdornedTargetFields();
            if (maintainedFields == null || maintainedFields.length == 0) {
                ppr.setValidateUnsubmittedProperties(false);
            }
        } else if (md instanceof MapMetadata) {
            ppr.getEntity().setType(new String[] { entityForm.getEntityType() });
            
            Property p = new Property();
            p.setName("symbolicId");
            p.setValue(getContextSpecificRelationshipId(mainMetadata, parentEntity, field.getName()));
            properties.add(p);
        } else {
            throw new IllegalArgumentException(String.format("The specified field [%s] for class [%s] was" +
                    " not a collection field.", field.getName(), mainMetadata.getCeilingType()));
        }

        ppr.setCeilingEntityClassname(ppr.getEntity().getType()[0]);
        String sectionField = field.getName();
        if (sectionField.contains(".")) {
            sectionField = sectionField.substring(0, sectionField.lastIndexOf("."));
        }
        ppr.setSectionEntityField(sectionField);
        
        Property parentNameProp = parentEntity.getPMap().get(AdminMainEntity.MAIN_ENTITY_NAME_PROPERTY);
        if (parentNameProp != null) {
            ppr.setRequestingEntityName(parentNameProp.getValue());
        }

        Property[] propArr = new Property[properties.size()];
        properties.toArray(propArr);
        ppr.getEntity().setProperties(propArr);

        return add(ppr);
    }

    @Override
    public PersistenceResponse updateSubCollectionEntity(EntityForm entityForm, ClassMetadata mainMetadata, Property
            field, Entity parentEntity, String collectionItemId, List<SectionCrumb> sectionCrumb) throws ServiceException, ClassNotFoundException {
        return updateSubCollectionEntity(entityForm, mainMetadata, field, parentEntity, collectionItemId, null, sectionCrumb);
    }

    @Override
    public PersistenceResponse updateSubCollectionEntity(EntityForm entityForm, ClassMetadata mainMetadata, Property field,
            Entity parentEntity, String collectionItemId, String alternateId, List<SectionCrumb> sectionCrumbs)
            throws ServiceException, ClassNotFoundException {
        List<Property> properties = getPropertiesFromEntityForm(entityForm);

        FieldMetadata md = field.getMetadata();

        PersistencePackageRequest ppr = PersistencePackageRequest.fromMetadata(md, sectionCrumbs)
                .withEntity(new Entity());

        if (md instanceof BasicCollectionMetadata) {
            BasicCollectionMetadata fmd = (BasicCollectionMetadata) md;
            ppr.getEntity().setType(new String[] { fmd.getCollectionCeilingEntity() });

            Property fp = new Property();
            fp.setName(ppr.getForeignKey().getManyToField());
            fp.setValue(getContextSpecificRelationshipId(mainMetadata, parentEntity, field.getName()));
            if (!properties.contains(fp)) {
                properties.add(fp);
            }
        } else if (md instanceof AdornedTargetCollectionMetadata) {
            ppr.getEntity().setType(new String[] { ppr.getAdornedList().getAdornedTargetEntityClassname() });
            for (Property property : properties) {
                if (property.getName().equals(ppr.getAdornedList().getLinkedObjectPath() +
                                    "." + ppr.getAdornedList().getLinkedIdProperty())) {
                    break;
                }
            }

            if (!StringUtils.isEmpty(alternateId)) {
                Property p = new Property();
                p.setName(BasicPersistenceModule.ALTERNATE_ID_PROPERTY);
                p.setValue(alternateId);
                if (!properties.contains(p)) {
                    properties.add(p);
                }
            }
        } else if (md instanceof MapMetadata) {
            ppr.getEntity().setType(new String[] { entityForm.getEntityType() });
            
            Property p = new Property();
            p.setName("symbolicId");
            p.setValue(getContextSpecificRelationshipId(mainMetadata, parentEntity, field.getName()));
            properties.add(p);
        } else {
            throw new IllegalArgumentException(String.format("The specified field [%s] for class [%s] was" +
                    " not a collection field.", field.getName(), mainMetadata.getCeilingType()));
        }

        ppr.setCeilingEntityClassname(ppr.getEntity().getType()[0]);
        String sectionField = field.getName();
        if (sectionField.contains(".")) {
            sectionField = sectionField.substring(0, sectionField.lastIndexOf("."));
        }
        ppr.setSectionEntityField(sectionField);
        
        Property parentNameProp = parentEntity.getPMap().get(AdminMainEntity.MAIN_ENTITY_NAME_PROPERTY);
        if (parentNameProp != null) {
            ppr.setRequestingEntityName(parentNameProp.getValue());
        }

        Property p = new Property();
        p.setName(entityForm.getIdProperty());
        p.setValue(collectionItemId);
        if (!properties.contains(p)) {
            properties.add(p);
        }

        Property[] propArr = new Property[properties.size()];
        properties.toArray(propArr);
        ppr.getEntity().setProperties(propArr);

        return update(ppr);
    }

    @Override
    public PersistenceResponse removeSubCollectionEntity(ClassMetadata mainMetadata, Property field, Entity parentEntity,
                String itemId, String priorKey, List<SectionCrumb> sectionCrumbs) throws ServiceException {
        return removeSubCollectionEntity(mainMetadata, field, parentEntity, itemId, null, priorKey, sectionCrumbs);
    }

    @Override
    public PersistenceResponse removeSubCollectionEntity(ClassMetadata mainMetadata, Property field, Entity parentEntity,
            String itemId, String alternateId, String priorKey, List<SectionCrumb> sectionCrumbs) throws ServiceException {
        List<Property> properties = new ArrayList<Property>();

        Property p;
        String parentId = getContextSpecificRelationshipId(mainMetadata, parentEntity, field.getName());

        Entity entity = new Entity();
        PersistencePackageRequest ppr = PersistencePackageRequest.fromMetadata(field.getMetadata(), sectionCrumbs)
                .withEntity(entity);

        if (field.getMetadata() instanceof BasicCollectionMetadata) {
            BasicCollectionMetadata fmd = (BasicCollectionMetadata) field.getMetadata();

            p = new Property();
            p.setName("id");
            p.setValue(itemId);
            properties.add(p);

            p = new Property();
            p.setName(ppr.getForeignKey().getManyToField());
            p.setValue(parentId);
            properties.add(p);

            entity.setType(new String[] { fmd.getCollectionCeilingEntity() });
        } else if (field.getMetadata() instanceof AdornedTargetCollectionMetadata) {
            AdornedTargetList adornedList = ppr.getAdornedList();

            p = new Property();
            p.setName(adornedList.getLinkedObjectPath() + "." + adornedList.getLinkedIdProperty());
            p.setValue(parentId);
            properties.add(p);

            p = new Property();
            p.setName(adornedList.getTargetObjectPath() + "." + adornedList.getTargetIdProperty());
            p.setValue(itemId);
            properties.add(p);

            if (!StringUtils.isEmpty(alternateId)) {
                p = new Property();
                p.setName(BasicPersistenceModule.ALTERNATE_ID_PROPERTY);
                p.setValue(alternateId);
                properties.add(p);
            }

            entity.setType(new String[] { adornedList.getAdornedTargetEntityClassname() });
        } else if (field.getMetadata() instanceof MapMetadata) {
            MapMetadata fmd = (MapMetadata) field.getMetadata();

            p = new Property();
            p.setName("id");
            p.setValue(itemId);
            properties.add(p);

            p = new Property();
            p.setName("symbolicId");
            p.setValue(getContextSpecificRelationshipId(mainMetadata, parentEntity, field.getName()));
            properties.add(p);

            p = new Property();
            p.setName("priorKey");
            p.setValue(priorKey);
            properties.add(p);
            
            MapStructure mapStructure = ppr.getMapStructure();
            
            p = new Property();
            p.setName(mapStructure.getKeyPropertyName());
            p.setValue(itemId);
            properties.add(p);

            entity.setType(new String[] { fmd.getTargetClass() });
        }

        String sectionField = field.getName();
        if (sectionField.contains(".")) {
            sectionField = sectionField.substring(0, sectionField.lastIndexOf("."));
        }
        ppr.setSectionEntityField(sectionField);
        
        Property parentNameProp = parentEntity.getPMap().get(AdminMainEntity.MAIN_ENTITY_NAME_PROPERTY);
        if (parentNameProp != null) {
            ppr.setRequestingEntityName(parentNameProp.getValue());
        }

        Property[] propArr = new Property[properties.size()];
        properties.toArray(propArr);
        ppr.getEntity().setProperties(propArr);

        return remove(ppr);
    }

    @Override
    public String getContextSpecificRelationshipId(ClassMetadata cmd, Entity entity, String propertyName) {
        String prefix;
        if (propertyName.contains(".")) {
            prefix = propertyName.substring(0, propertyName.lastIndexOf("."));
        } else {
            prefix = "";
        }
                
        if (prefix.equals("")) {
            return entity.findProperty("id").getValue();
        } else {
            //we need to check all the parts of the prefix. For example, the prefix could include an @Embedded class like
            //defaultSku.dimension. In this case, we want the id from the defaultSku property, since the @Embedded does
            //not have an id property - nor should it.
            String[] prefixParts = prefix.split("\\.");
            for (int j = 0; j < prefixParts.length; j++) {
                StringBuilder sb = new StringBuilder();
                for (int x = 0; x < prefixParts.length - j; x++) {
                    sb.append(prefixParts[x]);
                    if (x < prefixParts.length - j - 1) {
                        sb.append(".");
                    }
                }
                String tempPrefix = sb.toString();
                
                for (Property property : entity.getProperties()) {
                    if (property.getName().startsWith(tempPrefix)) {
                        if (cmd.getPMap().containsKey(property.getName())) {
                            BasicFieldMetadata md = (BasicFieldMetadata) cmd.getPMap().get(property.getName()).getMetadata();
                            if (md.getFieldType().equals(SupportedFieldType.ID)) {
                                return property.getValue();
                            }
                        }
                    }
                }
            }
        }
        if (!prefix.contains(".")) {
            //this may be an embedded class directly on the root entity (e.g. embeddablePriceList.restrictedPriceLists on OfferImpl)
            return entity.findProperty("id").getValue();
        }
        throw new RuntimeException("Unable to establish a relationship id");
    }
    
    @Override
    public String getIdProperty(ClassMetadata cmd) throws ServiceException {
        for (Property p : cmd.getProperties()) {
            if (p.getMetadata() instanceof BasicFieldMetadata) {
                BasicFieldMetadata fmd = (BasicFieldMetadata) p.getMetadata();
                //check for ID type and also make sure the field we're looking at is not a "ToOne" association
                if (SupportedFieldType.ID.equals(fmd.getFieldType()) && !p.getName().contains(".")) {
                    return p.getName();
                }
            }
        }
        
        throw new ServiceException("Could not determine ID field for " + cmd.getCeilingType());
    }

    @Override
    public PersistenceResponse add(PersistencePackageRequest request) throws ServiceException {
        return add(request, true);
    }
    
    @Override
    public PersistenceResponse add(PersistencePackageRequest request, boolean transactional) throws ServiceException {
        PersistencePackage pkg = persistencePackageFactory.create(request);
        try {
            if (request.isUpdateLookupType()) {
                if (pkg.getSectionCrumbs() != null && pkg.getSectionCrumbs().length > 0) {
                    SectionCrumb sc = pkg.getSectionCrumbs()[0];
                    if (StringUtils.isNotBlank(sc.getSectionIdentifier())) {
                        pkg.setSecurityCeilingEntityFullyQualifiedClassname(sc.getSectionIdentifier());
                    }
                }
                if (transactional) {
                    return service.update(pkg);
                } else {
                    return service.nonTransactionalUpdate(pkg);
                }
            } else {
                if (transactional) {
                    return service.add(pkg);
                } else {
                    return service.nonTransactionalAdd(pkg);
                }
            }
        } catch (ValidationException e) {
            ensureEntityMarkedAsValidationFailure(e, request);
            return new PersistenceResponse().withEntity(e.getEntity());
        }
    }
    
    @Override
    public PersistenceResponse update(PersistencePackageRequest request) throws ServiceException {
        return update(request, true);
    }
    
    @Override
    public PersistenceResponse update(PersistencePackageRequest request, boolean transactional) throws ServiceException {
        PersistencePackage pkg = persistencePackageFactory.create(request);
        try {
            if (transactional) {
                return service.update(pkg);
            } else {
                return service.nonTransactionalUpdate(pkg);
            }
        } catch (ValidationException e) {
            ensureEntityMarkedAsValidationFailure(e, request);
            return new PersistenceResponse().withEntity(e.getEntity());
        }
    }

    @Override
    public PersistenceResponse inspect(PersistencePackageRequest request)
            throws ServiceException {
        PersistencePackage pkg = persistencePackageFactory.create(request);
        return service.inspect(pkg);
    }

    @Override
    public PersistenceResponse remove(PersistencePackageRequest request)
            throws ServiceException {
        return remove(request, true);
    }

    @Override
    public PersistenceResponse remove(PersistencePackageRequest request, boolean transactional) throws ServiceException {
        PersistencePackage pkg = persistencePackageFactory.create(request);
        try {
            if (transactional) {
                return service.remove(pkg);
            } else {
                return service.nonTransactionalRemove(pkg);
            }
        } catch (ValidationException e) {
            ensureEntityMarkedAsValidationFailure(e, request);
            return new PersistenceResponse().withEntity(e.getEntity());
        }
    }
    
    /**
     * <p>
     * Should be invoked when a {@link ValidationException} is thrown to verify that the {@link Entity} contained within the
     * given <b>originalRequest</b> has a validationFailure = true
     * 
     * <p>
     * This will also check for a cause of {@link ConstraintViolationException} and add a gloal error to that.
     */
    protected void ensureEntityMarkedAsValidationFailure(ValidationException e, PersistencePackageRequest originalRequest) {
        if (e.containsCause(ConstraintViolationException.class)) {
            e.getEntity().addGlobalValidationError("constraintViolationError");
        } else if (!e.getEntity().isValidationFailure()) {
            e.getEntity().setValidationFailure(true);
            e.getEntity().addGlobalValidationError(e.getMessage());
        }
    }

    @Override
    public PersistenceResponse fetch(PersistencePackageRequest request)
            throws ServiceException {
        PersistencePackage pkg = persistencePackageFactory.create(request);

        CriteriaTransferObject cto = getDefaultCto();
        if (request.getFilterAndSortCriteria() != null) {
            cto.addAll(Arrays.asList(request.getFilterAndSortCriteria()));
        }
        
        if (request.getStartIndex() == null) {
            cto.setFirstResult(0);
        } else {
            cto.setFirstResult(request.getStartIndex());
        }
        
        if (request.getMaxIndex() != null) {
            int requestedMaxResults = request.getMaxIndex() - request.getStartIndex() + 1;
            if (requestedMaxResults >= 0 && requestedMaxResults < cto.getMaxResults()) {
                cto.setMaxResults(requestedMaxResults);
            }
        }
        
        return service.fetch(pkg, cto);
    }
    
    protected CriteriaTransferObject getDefaultCto() {
        CriteriaTransferObject cto = new CriteriaTransferObject();
        cto.setMaxResults(getDefaultMaxResults());
        return cto;
    }
    
    @Override
    public String getForeignEntityName(String owningClass, String id) {
        if (owningClass == null || id == null) {
            return null;
        }
        
        Class<?> clazz = genericEntityDao.getImplClass(owningClass);
        Object foreignEntity = genericEntityDao.readGenericEntity(clazz, id);

        if (foreignEntity instanceof AdminMainEntity) {
            return ((AdminMainEntity) foreignEntity).getMainEntityName();
        }
        
        return null;
    }
    
    protected int getDefaultMaxResults() {
        return BLCSystemProperty.resolveIntSystemProperty("admin.default.max.results", 50);
    }

}
