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
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
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
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.MapMetadata;
import org.broadleafcommerce.openadmin.client.dto.MapStructure;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.security.service.AdminNavigationService;
import org.broadleafcommerce.openadmin.server.service.AdminEntityService;
import org.broadleafcommerce.openadmin.server.service.persistence.module.BasicPersistenceModule;
import org.broadleafcommerce.openadmin.web.form.component.DefaultListGridActions;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.component.ListGridRecord;
import org.broadleafcommerce.openadmin.web.form.component.RuleBuilder;
import org.broadleafcommerce.openadmin.web.form.entity.ComboField;
import org.broadleafcommerce.openadmin.web.form.entity.DefaultEntityFormActions;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.broadleafcommerce.openadmin.web.rulebuilder.DataDTODeserializer;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataWrapper;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.springframework.stereotype.Service;

import com.gwtincubator.security.exception.ApplicationSecurityException;

import java.io.IOException;
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
                if (fmd.isProminent() != null && fmd.isProminent() 
                        && !VisibilityEnum.HIDDEN_ALL.equals(fmd.getVisibility())
                        && !VisibilityEnum.GRID_HIDDEN.equals(fmd.getVisibility())) {
                    
                    Field hf;
                    if (fmd.getFieldType().equals(SupportedFieldType.EXPLICIT_ENUMERATION) ||
                            fmd.getFieldType().equals(SupportedFieldType.BROADLEAF_ENUMERATION) ||
                            fmd.getFieldType().equals(SupportedFieldType.EMPTY_ENUMERATION)) {
                        hf = new ComboField();
                        ((ComboField) hf).setOptions(fmd.getEnumerationValues());
                    } else {
                        hf = new Field();
                    }
                    
                    hf.withName(p.getName())
                      .withFriendlyName(fmd.getFriendlyName())
                      .withOrder(fmd.getGridOrder())
                      .withForeignKeyDisplayValueProperty(fmd.getForeignKeyDisplayValueProperty());
                    String fieldType = fmd.getFieldType() == null ? null : fmd.getFieldType().toString();
                    hf.setFieldType(fieldType);
                    
                    headerFields.add(hf);
                }
            }
        }

        return createListGrid(cmd.getCeilingType(), headerFields, type, entities, sectionKey, 0);
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
                    if (md.isProminent() != null && md.isProminent() 
                            && !VisibilityEnum.HIDDEN_ALL.equals(md.getVisibility())
                            && !VisibilityEnum.GRID_HIDDEN.equals(md.getVisibility())) {
                        Field hf = new Field()
                                .withName(p.getName())
                                .withFriendlyName(fmd.getFriendlyName())
                                .withForeignKeyDisplayValueProperty(md.getForeignKeyDisplayValueProperty())
                                .withOrder(md.getGridOrder());
                        String fieldType = md.getFieldType() == null ? null : md.getFieldType().toString();
                        hf.setFieldType(fieldType);
                        headerFields.add(hf);
                    }
                }
            }

            type = ListGrid.Type.TO_ONE;
        } else if (fmd instanceof BasicCollectionMetadata) {
            for (Property p : cmd.getProperties()) {
                if (p.getMetadata() instanceof BasicFieldMetadata) {
                    BasicFieldMetadata md = (BasicFieldMetadata) p.getMetadata();
                    if (md.isProminent() != null && md.isProminent() 
                            && !VisibilityEnum.HIDDEN_ALL.equals(md.getVisibility())
                            && !VisibilityEnum.GRID_HIDDEN.equals(md.getVisibility())) {
                        Field hf = new Field()
                                .withName(p.getName())
                                .withFriendlyName(md.getFriendlyName())
                                .withOrder(md.getGridOrder());
                        String fieldType = md.getFieldType() == null ? null : md.getFieldType().toString();
                        hf.setFieldType(fieldType);
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
                if (p.getMetadata() instanceof BasicFieldMetadata) {
                    hf.setOrder(((BasicFieldMetadata) p.getMetadata()).getGridOrder());
                }
                //TODO FIXME: PJV
                String fieldType = "default";
                hf.setFieldType(fieldType);
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
                    .withFriendlyName(p2.getMetadata().getFriendlyName())
                    .withOrder(p2.getMetadata().getOrder());
            //TODO FIXME : PJV
            String fieldType = "default";
            hf.setFieldType(fieldType);
            headerFields.add(hf);

            for (Property p : cmd.getProperties()) {
                if (p.getMetadata() instanceof BasicFieldMetadata) {
                    BasicFieldMetadata md = (BasicFieldMetadata) p.getMetadata();
                    if (md.getTargetClass().equals(mmd.getValueClassName())) {
                        if (md.isProminent() != null && md.isProminent() 
                                && !VisibilityEnum.HIDDEN_ALL.equals(md.getVisibility())
                                && !VisibilityEnum.GRID_HIDDEN.equals(md.getVisibility())) {
                            hf = new Field()
                                    .withName(p.getName())
                                    .withFriendlyName(md.getFriendlyName())
                                    .withOrder(md.getGridOrder());
                            //TODO FIXME: PJV
                            fieldType = "default";
                            hf.setFieldType(fieldType);
                            headerFields.add(hf);
                        }
                    }
                }
            }

            type = ListGrid.Type.MAP;
            editable = true;
        }

        ListGrid listGrid = createListGrid(cmd.getCeilingType(), headerFields, type, entities, sectionKey, fmd.getOrder());
        listGrid.setSubCollectionFieldName(field.getName());
        listGrid.setFriendlyName(field.getMetadata().getFriendlyName());
        if (StringUtils.isEmpty(listGrid.getFriendlyName())) {
            listGrid.setFriendlyName(field.getName());
        }
        listGrid.setContainingEntityId(containingEntityId);
        
        if (editable) {
            listGrid.getRowActions().add(DefaultListGridActions.UPDATE);
        }
        listGrid.getRowActions().add(DefaultListGridActions.REMOVE);

        return listGrid;
    }

    protected ListGrid createListGrid(String className, List<Field> headerFields, ListGrid.Type type, Entity[] entities, 
            String sectionKey, int order) {
        // Create the list grid and set some basic attributes
        ListGrid listGrid = new ListGrid();
        listGrid.setClassName(className);
        listGrid.getHeaderFields().addAll(headerFields);
        listGrid.setListGridType(type);
        listGrid.setSectionKey(sectionKey);
        listGrid.setOrder(order);
        
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
                    Field recordField = new Field().withName(headerField.getName())
                                                   .withFriendlyName(headerField.getFriendlyName())
                                                   .withOrder(p.getMetadata().getOrder());
                    
                    if (headerField instanceof ComboField) {
                        recordField.setValue(((ComboField) headerField).getOption(p.getValue()));
                    } else {
                        recordField.setValue(p.getValue());
                        recordField.setDisplayValue(p.getDisplayValue());
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

                if (
                    fmd.getFieldType()==SupportedFieldType.RULE_SIMPLE ||
                    fmd.getFieldType()==SupportedFieldType.RULE_WITH_QUANTITY
                ) {
                    //this field needs to be added to the rule builders list
                    continue;
                }
                if (!(VisibilityEnum.HIDDEN_ALL.equals(fmd.getVisibility())
                                      || VisibilityEnum.FORM_HIDDEN.equals(fmd.getVisibility()))) {
                    // Depending on visibility, field for the particular property is not created on the form
                    String fieldType = fmd.getFieldType() == null ? null : fmd.getFieldType().toString();
                    // Create the field and set some basic attributes
                    Field f;
                    if (fieldType.equals(SupportedFieldType.BROADLEAF_ENUMERATION.toString())
                            || fieldType.equals(SupportedFieldType.EXPLICIT_ENUMERATION.toString())
                            || fieldType.equals(SupportedFieldType.EMPTY_ENUMERATION.toString())) {
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
        EntityForm ef = createStandardEntityForm();
        ef.setCeilingEntityClassname(cmd.getCeilingType());
        
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
                BasicFieldMetadata basicFM = (BasicFieldMetadata) p.getMetadata();
                if (
                    basicFM.getFieldType()==SupportedFieldType.RULE_SIMPLE ||
                    basicFM.getFieldType()==SupportedFieldType.RULE_WITH_QUANTITY
                ) {
                    //this field needs to be added to the rule builders list
                    continue;
                }

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

    protected void constructRuleBuilder(EntityForm entityForm, Entity entity,
            String fieldName, String friendlyName, String styleClass, String fieldService,
            String fieldJson, String tab, Integer tabOrder) {
        RuleBuilder ruleBuilder = new RuleBuilder();
        ruleBuilder.setFieldName(fieldName);
        ruleBuilder.setFriendlyName(friendlyName);
        ruleBuilder.setStyleClass(styleClass);
        ruleBuilder.setFieldBuilder(entity.getPMap().get(fieldService).getValue());
        ruleBuilder.setJsonFieldName(fieldJson);
        ruleBuilder.setDataWrapper(new DataWrapper());
        if (entity.getPMap().get(fieldJson) != null) {
            String json = entity.getPMap().get(fieldJson).getValue();
            ruleBuilder.setJson(json);
            DataWrapper dw = (convertJsonToDataWrapper(json) != null)? convertJsonToDataWrapper(json) : new DataWrapper();
            ruleBuilder.setDataWrapper(dw);
        }
        entityForm.addRuleBuilder(ruleBuilder, tab, tabOrder);
    }

    /**
     * When using Thymeleaf, we need to convert the JSON string back to
     * a DataWrapper object because Thymeleaf escapes JSON strings.
     * Thymeleaf uses it's own object de-serializer
     * see: https://github.com/thymeleaf/thymeleaf/issues/84
     * see: http://forum.thymeleaf.org/Spring-Javascript-and-escaped-JSON-td4024739.html
     * @param json
     * @return DataWrapper
     * @throws IOException
     */
    protected DataWrapper convertJsonToDataWrapper(String json) {
        ObjectMapper mapper = new ObjectMapper();
        DataDTODeserializer dtoDeserializer = new DataDTODeserializer();
        SimpleModule module = new SimpleModule("DataDTODeserializerModule", new Version(1, 0, 0, null));
        module.addDeserializer(DataDTO.class, dtoDeserializer);
        mapper.registerModule(module);
        try {
            return mapper.readValue(json, DataWrapper.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EntityForm buildEntityForm(ClassMetadata cmd, Entity entity, Map<String, Entity[]> collectionRecords)
            throws ServiceException, ApplicationSecurityException {
        // Get the form with values for this entity
        EntityForm ef = buildEntityForm(cmd, entity);

        // Attach the sub-collection list grids and specialty UI support
        for (Property p : cmd.getProperties()) {

            if (p.getMetadata() instanceof BasicFieldMetadata) {
                BasicFieldMetadata basicFieldMetadata = (BasicFieldMetadata) p.getMetadata();
                if (basicFieldMetadata.getFieldType()==SupportedFieldType.RULE_SIMPLE) {
                    constructRuleBuilder(ef, entity, basicFieldMetadata.getName(), basicFieldMetadata.getFriendlyName(),
                            "rule-builder-simple",basicFieldMetadata.getName()+"FieldService",basicFieldMetadata.getName()+"Json",
                            basicFieldMetadata.getTab(), basicFieldMetadata.getTabOrder());
                }
                if (basicFieldMetadata.getFieldType()==SupportedFieldType.RULE_WITH_QUANTITY) {
                    constructRuleBuilder(ef, entity, basicFieldMetadata.getName(), basicFieldMetadata.getFriendlyName(),
                            "rule-builder-complex",basicFieldMetadata.getName()+"FieldService",basicFieldMetadata.getName()+"Json",
                            basicFieldMetadata.getTab(), basicFieldMetadata.getTabOrder());
                }
                continue;
            }

            Entity[] subCollectionEntities = collectionRecords.get(p.getName());
            String containingEntityId = entity.getPMap().get("id").getValue();
            ListGrid listGrid = buildCollectionListGrid(containingEntityId, subCollectionEntities, p, ef.getSectionKey());
            listGrid.setListGridType(ListGrid.Type.INLINE);

            CollectionMetadata md = ((CollectionMetadata) p.getMetadata());
            ef.addListGrid(listGrid, md.getTab(), md.getTabOrder());
        }
        
        for (ListGrid lg : ef.getAllListGrids()) {
            lg.addToolbarAction(DefaultListGridActions.ADD);
        }
        
        ef.addAction(DefaultEntityFormActions.DELETE);

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
        EntityForm ef = createStandardEntityForm();
        ef.setEntityType(adornedList.getAdornedTargetEntityClassname());

        // Get the metadata for this adorned field
        PersistencePackageRequest request = PersistencePackageRequest.adorned()
                .withCeilingEntityClassname(adornedMd.getCollectionCeilingEntity())
                .withAdornedList(adornedList);
        ClassMetadata collectionMetadata = adminEntityService.getClassMetadata(request);

        // We want our entity form to only render the maintained adorned target fields
        List<Property> entityFormProperties = new ArrayList<Property>();
        for (String targetFieldName : adornedMd.getMaintainedAdornedTargetFields()) {
            Property p = collectionMetadata.getPMap().get(targetFieldName);
            ((BasicFieldMetadata) p.getMetadata()).setVisibility(VisibilityEnum.VISIBLE_ALL);
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
        EntityForm ef = createStandardEntityForm();
        ForeignKey foreignKey = (ForeignKey) mapMd.getPersistencePerspective()
                .getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);
        ef.setEntityType(foreignKey.getForeignKeyClass());

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
                    .withCeilingEntityClassname(mapMd.getMapKeyOptionEntityClass());

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

        Field f = new Field()
                .withName("priorKey")
                .withFieldType(SupportedFieldType.HIDDEN.toString());
        ef.addHiddenField(f);

        return ef;
    }
    
    protected EntityForm createStandardEntityForm() {
        EntityForm ef = new EntityForm();
        ef.addAction(DefaultEntityFormActions.SAVE);
        return ef;
    }
    

}