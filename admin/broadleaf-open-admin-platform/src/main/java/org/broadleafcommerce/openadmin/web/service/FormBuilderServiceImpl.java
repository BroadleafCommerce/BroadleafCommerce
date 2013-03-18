/*
 * Copyright 2008-2012 the original author or authors.
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

package org.broadleafcommerce.openadmin.web.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.client.dto.BasicCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.client.dto.CollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.MapMetadata;
import org.broadleafcommerce.openadmin.client.dto.MapStructure;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.security.service.AdminNavigationService;
import org.broadleafcommerce.openadmin.server.service.AdminEntityService;
import org.broadleafcommerce.openadmin.server.service.persistence.module.BasicPersistenceModule;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.component.ListGridRecord;
import org.broadleafcommerce.openadmin.web.form.entity.ComboField;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.springframework.stereotype.Service;

import com.gwtincubator.security.exception.ApplicationSecurityException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

/**
 * @author Andre Azzolini (apazzolini)
 */
@Service("blFormBuilderService")
public class FormBuilderServiceImpl implements FormBuilderService {

    @Resource(name = "blAdminEntityService")
    protected AdminEntityService adminEntityService;
    
    @Resource (name = "blAdminNavigationService")
    protected AdminNavigationService navigationService;

    @Override
    public ListGrid buildMainListGrid(Entity[] entities, ClassMetadata cmd, String sectionKey)
            throws ServiceException, ApplicationSecurityException {

        List<Field> headerFields = new ArrayList<Field>();
        ListGrid.Type type = ListGrid.Type.MAIN;

        for (Property p : cmd.getProperties()) {
            if (p.getMetadata() instanceof BasicFieldMetadata) {
                BasicFieldMetadata fmd = (BasicFieldMetadata) p.getMetadata();
                if (fmd.isProminent() != null && fmd.isProminent()) {
                    Field hf;
                    if (fmd.getFieldType().equals(SupportedFieldType.EXPLICIT_ENUMERATION)) {
                        hf = new ComboField();
                        ((ComboField) hf).setOptions(fmd.getEnumerationValues());
                    } else {
                        hf = new Field();
                    }
                    
                    hf.setName(p.getName());
                    hf.setFriendlyName(fmd.getFriendlyName());
                    
                    headerFields.add(hf);
                }
            }
        }

        return createListGrid(cmd.getCeilingType(), headerFields, type, entities, sectionKey);
    }

    @Override
    public ListGrid buildCollectionListGrid(String containingEntityId, Entity[] entities, Property field, String sectionKey)
            throws ServiceException, ApplicationSecurityException {
        FieldMetadata fmd = field.getMetadata();
        // Get the class metadata for this particular field
        PersistencePackageRequest ppr = PersistencePackageRequest.fromMetadata(fmd);
        ClassMetadata cmd = adminEntityService.getClassMetadata(ppr);

        List<Field> headerFields = new ArrayList<Field>();
        ListGrid.Type type = null;
        boolean editable = false;

        // Get the header fields for this list grid. Note that the header fields are different depending on the
        // kind of field this is.
        if (fmd instanceof BasicFieldMetadata) {
            for (Property p : cmd.getProperties()) {
                if (p.getMetadata() instanceof BasicFieldMetadata) {
                    BasicFieldMetadata md = (BasicFieldMetadata) p.getMetadata();
                    if (md.isProminent() != null && md.isProminent()) {
                        Field hf = new Field()
                                .withName(p.getName())
                                .withFriendlyName(fmd.getFriendlyName());
                        headerFields.add(hf);
                    }
                }
            }

            type = ListGrid.Type.TO_ONE;
        } else if (fmd instanceof BasicCollectionMetadata) {
            for (Property p : cmd.getProperties()) {
                if (p.getMetadata() instanceof BasicFieldMetadata) {
                    BasicFieldMetadata md = (BasicFieldMetadata) p.getMetadata();
                    if (md.isProminent() != null && md.isProminent()) {
                        Field hf = new Field()
                                .withName(p.getName())
                                .withFriendlyName(md.getFriendlyName());
                        headerFields.add(hf);
                    }
                }
            }

            type = ListGrid.Type.BASIC;
            
            if (((BasicCollectionMetadata) fmd).getAddMethodType().equals(AddMethodType.PERSIST)) {
                editable = true;
            }
        } else if (fmd instanceof AdornedTargetCollectionMetadata) {
            AdornedTargetCollectionMetadata atcmd = (AdornedTargetCollectionMetadata) fmd;

            for (String fieldName : atcmd.getGridVisibleFields()) {
                Property p = cmd.getPMap().get(fieldName);
                Field hf = new Field()
                        .withName(p.getName())
                        .withFriendlyName(p.getMetadata().getFriendlyName());
                headerFields.add(hf);
            }

            type = ListGrid.Type.ADORNED;

            if (((AdornedTargetCollectionMetadata) fmd).getMaintainedAdornedTargetFields().length > 0) {
                editable = true;
            }
        } else if (fmd instanceof MapMetadata) {
            MapMetadata mmd = (MapMetadata) fmd;

            Property p2 = cmd.getPMap().get("key");
            Field hf = new Field()
                    .withName(p2.getName())
                    .withFriendlyName(p2.getMetadata().getFriendlyName());
            headerFields.add(hf);

            for (Property p : cmd.getProperties()) {
                if (p.getMetadata() instanceof BasicFieldMetadata) {
                    BasicFieldMetadata md = (BasicFieldMetadata) p.getMetadata();
                    if (md.getTargetClass().equals(mmd.getValueClassName())) {
                        if (md.isProminent() != null && md.isProminent()) {
                            hf = new Field()
                                    .withName(p.getName())
                                    .withFriendlyName(md.getFriendlyName());
                            headerFields.add(hf);
                        }
                    }
                }
            }

            type = ListGrid.Type.MAP;
            editable = true;
        }

        ListGrid listGrid = createListGrid(cmd.getCeilingType(), headerFields, type, entities, sectionKey);
        listGrid.setSubCollectionFieldName(field.getName());
        listGrid.setFriendlyName(field.getMetadata().getFriendlyName());
        if (StringUtils.isEmpty(listGrid.getFriendlyName())) {
            listGrid.setFriendlyName(field.getName());
        }
        listGrid.setContainingEntityId(containingEntityId);
        listGrid.setEditable(editable);

        return listGrid;
    }

    protected ListGrid createListGrid(String className, List<Field> headerFields, ListGrid.Type type, Entity[] entities, 
            String sectionKey) {
        // Create the list grid and set some basic attributes
        ListGrid listGrid = new ListGrid();
        listGrid.setClassName(className);
        listGrid.setHeaderFields(headerFields);
        listGrid.setListGridType(type);
        listGrid.setSectionKey(sectionKey);
        
        AdminSection section = navigationService.findAdminSectionByClass(className);
        if (section != null) {
            listGrid.setExternalEntitySectionKey(section.getUrl());
        }

        // For each of the entities (rows) in the list grid, we need to build the associated
        // ListGridRecord and set the required fields on the record. These fields are the same ones
        // that are used for the header fields.
        for (Entity e : entities) {
            ListGridRecord record = new ListGridRecord();
            record.setListGrid(listGrid);
            
            if (e.findProperty("id") != null) {
                record.setId(e.findProperty("id").getValue());
            }

            for (Field headerField : headerFields) {
                Property p = e.findProperty(headerField.getName());
                if (p != null) {
                    Field recordField = new Field().withName(headerField.getName());
                    
                    if (headerField instanceof ComboField) {
                        recordField.setValue(((ComboField) headerField).getOption(p.getValue()));
                    } else {
                        recordField.setValue(p.getValue());
                    }
                    
                    record.getFields().add(recordField);
                }
            }

            listGrid.getRecords().add(record);
        }

        return listGrid;
    }

    protected void setEntityFormFields(EntityForm ef, List<Property> properties) {
        for (Property property : properties) {
            if (property.getMetadata() instanceof BasicFieldMetadata) {
                BasicFieldMetadata fmd = (BasicFieldMetadata) property.getMetadata();
                // Depending on visibility, field for the particular property is not created on the form
                if (!(VisibilityEnum.HIDDEN_ALL.equals(fmd.getVisibility())
                || VisibilityEnum.FORM_HIDDEN.equals(fmd.getVisibility()))) {
                	
                	String fieldType = fmd.getFieldType() == null ? null : fmd.getFieldType().toString();
                // Create the field and set some basic attributes
                Field f;
                if (fieldType.equals(SupportedFieldType.BROADLEAF_ENUMERATION.toString())) {
                    f = new ComboField();
                    ((ComboField) f).setOptions(fmd.getEnumerationValues());
                } else {
                    f = new Field();
                }

                f.withName(property.getName())
                        .withFieldType(fieldType)
                        .withOrder(fmd.getOrder())
                        .withFriendlyName(fmd.getFriendlyName())
                        .withForeignKeyDisplayValueProperty(fmd.getForeignKeyDisplayValueProperty());

                if (StringUtils.isBlank(f.getFriendlyName())) {
                    f.setFriendlyName(f.getName());
                }

                // Add the field to the appropriate FieldGroup
                ef.addField(f, fmd.getGroup(), fmd.getGroupOrder(), fmd.getTab(), fmd.getTabOrder());
                }
            }
        }
    }
    
    @Override
    public void removeNonApplicableFields(ClassMetadata cmd, EntityForm entityForm, String entityType) {
        for (Property p : cmd.getProperties()) {
            if (!ArrayUtils.contains(p.getMetadata().getAvailableToTypes(), entityType)) {
                entityForm.removeField(p.getName());
            }
        }
    }

    @Override
    public EntityForm buildEntityForm(ClassMetadata cmd) {
        EntityForm ef = new EntityForm();
        ef.setEntityType(cmd.getCeilingType());
        
        AdminSection section = navigationService.findAdminSectionByClass(cmd.getCeilingType());
        if (section != null) {
            ef.setSectionKey(section.getUrl());
        }

        setEntityFormFields(ef, Arrays.asList(cmd.getProperties()));

        return ef;
    }

    @Override
    public EntityForm buildEntityForm(ClassMetadata cmd, Entity entity) {
        // Get the empty form with appropriate fields
        EntityForm ef = buildEntityForm(cmd);

        ef.setId(entity.findProperty("id").getValue());
        ef.setEntityType(entity.getType()[0]);

        // Set the appropriate property values
        for (Property p : cmd.getProperties()) {
            if (p.getMetadata() instanceof BasicFieldMetadata) {
                Property entityProp = entity.findProperty(p.getName());

                if (entityProp == null) {
                    ef.removeField(p.getName());
                } else {
                    Field field = ef.findField(p.getName());
                    if (field != null) {
                        field.setValue(entityProp.getValue());
                        field.setDisplayValue(entityProp.getDisplayValue());
                    }
                }
            }
        }
        
        Property p = entity.findProperty(BasicPersistenceModule.MAIN_ENTITY_NAME_PROPERTY);
        if (p != null) {
            ef.setMainEntityName(p.getValue());
        }

        return ef;
    }

    @Override
    public EntityForm buildEntityForm(ClassMetadata cmd, Entity entity, Map<String, Entity[]> collectionRecords)
            throws ServiceException, ApplicationSecurityException {
        // Get the form with values for this entity
        EntityForm ef = buildEntityForm(cmd, entity);

        // Attach the sub-collection list grids
        for (Property p : cmd.getProperties()) {

            if (p.getMetadata() instanceof BasicFieldMetadata) {
                continue;
            }

            Entity[] subCollectionEntities = collectionRecords.get(p.getName());
            String containingEntityId = entity.getPMap().get("id").getValue();
            ListGrid listGrid = buildCollectionListGrid(containingEntityId, subCollectionEntities, p, ef.getSectionKey());
            listGrid.setListGridType(ListGrid.Type.INLINE);

            CollectionMetadata md = ((CollectionMetadata) p.getMetadata());
            ef.addListGrid(listGrid, md.getTab(), md.getTabOrder());
        }

        return ef;
    }

    @Override
    public void populateEntityFormFields(EntityForm ef, Entity entity) {
        ef.setId(entity.findProperty("id").getValue());
        ef.setEntityType(entity.getType()[0]);

        for (Entry<String, Field> entry : ef.getFields().entrySet()) {
            Property entityProp = entity.findProperty(entry.getKey());
            if (entityProp != null) {
                entry.getValue().setValue(entityProp.getValue());
                entry.getValue().setDisplayValue(entityProp.getDisplayValue());
            }
        }
    }

    @Override
    public void populateAdornedEntityFormFields(EntityForm ef, Entity entity, AdornedTargetList adornedList) {
        Field field = ef.getFields().get(adornedList.getTargetObjectPath() + "." + adornedList.getTargetIdProperty());
        Property entityProp = entity.findProperty("id");
        field.setValue(entityProp.getValue());

        field = ef.getFields().get(adornedList.getSortField());
        entityProp = entity.findProperty(adornedList.getSortField());
        field.setValue(entityProp.getValue());
    }

    @Override
    public void populateMapEntityFormFields(EntityForm ef, Entity entity) {
        Field field = ef.getFields().get("priorKey");
        Property entityProp = entity.findProperty("key");
        field.setValue(entityProp.getValue());
    }

    @Override
    public void copyEntityFormValues(EntityForm destinationForm, EntityForm sourceForm) {
        for (Entry<String, Field> entry : sourceForm.getFields().entrySet()) {
            Field destinationField = destinationForm.getFields().get(entry.getKey());
            destinationField.setValue(entry.getValue().getValue());
            destinationField.setDisplayValue(entry.getValue().getDisplayValue());
        }
    }

    @Override
    public EntityForm buildAdornedListForm(AdornedTargetCollectionMetadata adornedMd, AdornedTargetList adornedList,
            String parentId)
            throws ServiceException, ApplicationSecurityException {
        EntityForm ef = new EntityForm();
        ef.setEntityType(adornedList.getAdornedTargetEntityClassname());

        // Get the metadata for this adorned field
        PersistencePackageRequest request = PersistencePackageRequest.adorned()
                .withClassName(adornedMd.getCollectionCeilingEntity())
                .withAdornedList(adornedList);
        ClassMetadata collectionMetadata = adminEntityService.getClassMetadata(request);

        // We want our entity form to only render the maintained adorned target fields
        List<Property> entityFormProperties = new ArrayList<Property>();
        for (String targetFieldName : adornedMd.getMaintainedAdornedTargetFields()) {
            Property p = collectionMetadata.getPMap().get(targetFieldName);
            entityFormProperties.add(p);
        }

        // Set the maintained fields on the form
        setEntityFormFields(ef, entityFormProperties);

        // Add these two additional hidden fields that are required for persistence
        Field f = new Field()
                .withName(adornedList.getLinkedObjectPath() + "." + adornedList.getLinkedIdProperty())
                .withFieldType(SupportedFieldType.HIDDEN.toString())
                .withValue(parentId);
        ef.addHiddenField(f);

        f = new Field()
                .withName(adornedList.getTargetObjectPath() + "." + adornedList.getTargetIdProperty())
                .withFieldType(SupportedFieldType.HIDDEN.toString())
                .withIdOverride("adornedTargetIdProperty");
        ef.addHiddenField(f);

        f = new Field()
                .withName(adornedList.getSortField())
                .withFieldType(SupportedFieldType.HIDDEN.toString());
        ef.addHiddenField(f);

        return ef;
    }

    @Override
    public EntityForm buildMapForm(MapMetadata mapMd, final MapStructure mapStructure, ClassMetadata cmd, String parentId)
            throws ServiceException, ApplicationSecurityException {
        EntityForm ef = new EntityForm();
        ef.setEntityType(mapMd.getTargetClass());

        // We will use a combo field to render the key choices
        ComboField keyField = new ComboField();
        keyField.withName("key")
                .withFieldType("combo_field")
                .withFriendlyName("Key");

        if (mapMd.getKeys() != null) {
            // The keys can be explicitly set in the annotation...
            keyField.setOptions(mapMd.getKeys());
        } else {
            // Or they could be based on a different entity
            PersistencePackageRequest ppr = PersistencePackageRequest.standard()
                    .withClassName(mapMd.getMapKeyOptionEntityClass());

            Entity[] rows = adminEntityService.getRecords(ppr);

            for (Entity entity : rows) {
                String keyValue = entity.getPMap().get(mapMd.getMapKeyOptionEntityValueField()).getValue();
                String keyDisplayValue = entity.getPMap().get(mapMd.getMapKeyOptionEntityDisplayField()).getValue();
                keyField.putOption(keyValue, keyDisplayValue);
            }
        }
        ef.addMapKeyField(keyField);

        // Set the fields for this form
        List<Property> mapFormProperties = new ArrayList<Property>(Arrays.asList(cmd.getProperties()));
        CollectionUtils.filter(mapFormProperties, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                Property p = (Property) object;
                return ArrayUtils.contains(p.getMetadata().getAvailableToTypes(), mapStructure.getValueClassName());
            }
        });

        setEntityFormFields(ef, mapFormProperties);

        // Add the symbolicId field required for persistence
        Field f = new Field()
                .withName("symbolicId")
                .withFieldType(SupportedFieldType.HIDDEN.toString())
                .withValue(parentId);
        ef.addHiddenField(f);

        f = new Field()
                .withName("priorKey")
                .withFieldType(SupportedFieldType.HIDDEN.toString());
        ef.addHiddenField(f);

        return ef;
    }

}