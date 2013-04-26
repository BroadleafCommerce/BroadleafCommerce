/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
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
import org.broadleafcommerce.common.presentation.client.LookupType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.openadmin.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.dto.BasicCollectionMetadata;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.CollectionMetadata;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.ForeignKey;
import org.broadleafcommerce.openadmin.dto.MapMetadata;
import org.broadleafcommerce.openadmin.dto.MapStructure;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.security.service.AdminNavigationService;
import org.broadleafcommerce.openadmin.server.service.AdminEntityService;
import org.broadleafcommerce.openadmin.server.service.persistence.module.BasicPersistenceModule;
import org.broadleafcommerce.openadmin.web.form.component.DefaultListGridActions;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.component.ListGridRecord;
import org.broadleafcommerce.openadmin.web.form.component.RuleBuilderField;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
    public ListGrid buildMainListGrid(DynamicResultSet drs, ClassMetadata cmd, String sectionKey)
            throws ServiceException {

        List<Field> headerFields = new ArrayList<Field>();
        ListGrid.Type type = ListGrid.Type.MAIN;
        String idProperty = "id";

        for (Property p : cmd.getProperties()) {
            if (p.getMetadata() instanceof BasicFieldMetadata) {
                BasicFieldMetadata fmd = (BasicFieldMetadata) p.getMetadata();
                
                if (SupportedFieldType.ID.equals(fmd.getFieldType())) {
                    idProperty = fmd.getName();
                }
                
                if (fmd.isProminent() != null && fmd.isProminent() 
                        && !VisibilityEnum.HIDDEN_ALL.equals(fmd.getVisibility())
                        && !VisibilityEnum.GRID_HIDDEN.equals(fmd.getVisibility())) {
                    Field hf = createHeaderField(p, fmd);
                    headerFields.add(hf);
                }
            }
        }

        ListGrid listGrid = createListGrid(cmd.getCeilingType(), headerFields, type, drs, sectionKey, 0, idProperty);
        return listGrid;
    }
    
    protected Field createHeaderField(Property p, BasicFieldMetadata fmd) {
        Field hf;
        if (fmd.getFieldType().equals(SupportedFieldType.EXPLICIT_ENUMERATION) ||
                fmd.getFieldType().equals(SupportedFieldType.BROADLEAF_ENUMERATION) ||
                fmd.getFieldType().equals(SupportedFieldType.DATA_DRIVEN_ENUMERATION) ||
                fmd.getFieldType().equals(SupportedFieldType.EMPTY_ENUMERATION)) {
            hf = new ComboField();
            ((ComboField) hf).setOptions(fmd.getEnumerationValues());
        } else {
            hf = new Field();
        }
        
        hf.withName(p.getName())
          .withFriendlyName(fmd.getFriendlyName())
          .withOrder(fmd.getGridOrder())
          .withColumnWidth(fmd.getColumnWidth())
          .withForeignKeyDisplayValueProperty(fmd.getForeignKeyDisplayValueProperty());
        String fieldType = fmd.getFieldType() == null ? null : fmd.getFieldType().toString();
        hf.setFieldType(fieldType);
        
        return hf;
    }

    @Override
    public ListGrid buildCollectionListGrid(String containingEntityId, DynamicResultSet drs, Property field, 
            String sectionKey)
            throws ServiceException {
        FieldMetadata fmd = field.getMetadata();
        // Get the class metadata for this particular field
        PersistencePackageRequest ppr = PersistencePackageRequest.fromMetadata(fmd);
        ClassMetadata cmd = adminEntityService.getClassMetadata(ppr);

        List<Field> headerFields = new ArrayList<Field>();
        ListGrid.Type type = null;
        boolean editable = false;
        boolean sortable = false;
        String idProperty = "id";
        // Get the header fields for this list grid. Note that the header fields are different depending on the
        // kind of field this is.
        if (fmd instanceof BasicFieldMetadata) {
            for (Property p : cmd.getProperties()) {
                if (p.getMetadata() instanceof BasicFieldMetadata) {
                    BasicFieldMetadata md = (BasicFieldMetadata) p.getMetadata();
                    
                    if (SupportedFieldType.ID.equals(md.getFieldType())) {
                        idProperty = md.getName();
                    }
                    
                    if (md.isProminent() != null && md.isProminent() 
                            && !VisibilityEnum.HIDDEN_ALL.equals(md.getVisibility())
                            && !VisibilityEnum.GRID_HIDDEN.equals(md.getVisibility())) {
                        Field hf = createHeaderField(p, md);
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
                        Field hf = createHeaderField(p, md);
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
                BasicFieldMetadata md = (BasicFieldMetadata) p.getMetadata();
                
                Field hf = createHeaderField(p, md);
                headerFields.add(hf);
            }

            type = ListGrid.Type.ADORNED;

            if (atcmd.getMaintainedAdornedTargetFields().length > 0) {
                editable = true;
            }
            
            AdornedTargetList adornedList = (AdornedTargetList) atcmd.getPersistencePerspective()
                    .getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST);
            sortable = StringUtils.isNotBlank(adornedList.getSortField());
        } else if (fmd instanceof MapMetadata) {
            MapMetadata mmd = (MapMetadata) fmd;

            Property p2 = cmd.getPMap().get("key");
            BasicFieldMetadata keyMd = (BasicFieldMetadata) p2.getMetadata();
            Field hf = createHeaderField(p2, keyMd);
            headerFields.add(hf);

            for (Property p : cmd.getProperties()) {
                if (p.getMetadata() instanceof BasicFieldMetadata) {
                    BasicFieldMetadata md = (BasicFieldMetadata) p.getMetadata();
                    if (md.getTargetClass().equals(mmd.getValueClassName())) {
                        if (md.isProminent() != null && md.isProminent() 
                                && !VisibilityEnum.HIDDEN_ALL.equals(md.getVisibility())
                                && !VisibilityEnum.GRID_HIDDEN.equals(md.getVisibility())) {
                            hf = createHeaderField(p, md);
                            headerFields.add(hf);
                        }
                    }
                }
            }

            type = ListGrid.Type.MAP;
            editable = true;
        }

        String ceilingType = "";
        if (fmd instanceof BasicFieldMetadata) {
            ceilingType = cmd.getCeilingType();
        } else if (fmd instanceof CollectionMetadata) {
            ceilingType = ((CollectionMetadata) fmd).getCollectionCeilingEntity();
        }
        ListGrid listGrid = createListGrid(ceilingType, headerFields, type, drs, sectionKey, fmd.getOrder(), idProperty);
        listGrid.setSubCollectionFieldName(field.getName());
        listGrid.setFriendlyName(field.getMetadata().getFriendlyName());
        if (StringUtils.isEmpty(listGrid.getFriendlyName())) {
            listGrid.setFriendlyName(field.getName());
        }
        listGrid.setContainingEntityId(containingEntityId);
        
        if (editable) {
            listGrid.getRowActions().add(DefaultListGridActions.UPDATE);
        }
        if (sortable) {
            listGrid.getToolbarActions().add(DefaultListGridActions.REORDER);
        }
        listGrid.getRowActions().add(DefaultListGridActions.REMOVE);

        return listGrid;
    }

    protected ListGrid createListGrid(String className, List<Field> headerFields, ListGrid.Type type, DynamicResultSet drs, 
            String sectionKey, int order, String idProperty) {
        // Create the list grid and set some basic attributes
        ListGrid listGrid = new ListGrid();
        listGrid.setClassName(className);
        listGrid.getHeaderFields().addAll(headerFields);
        listGrid.setListGridType(type);
        listGrid.setSectionKey(sectionKey);
        listGrid.setOrder(order);
        listGrid.setIdProperty(idProperty);
        listGrid.setStartIndex(drs.getStartIndex());
        listGrid.setTotalRecords(drs.getTotalRecords());
        listGrid.setPageSize(drs.getPageSize());
        
        AdminSection section = navigationService.findAdminSectionByClass(className);
        if (section != null) {
            listGrid.setExternalEntitySectionKey(section.getUrl());
        }

        // For each of the entities (rows) in the list grid, we need to build the associated
        // ListGridRecord and set the required fields on the record. These fields are the same ones
        // that are used for the header fields.
        for (Entity e : drs.getRecords()) {
            ListGridRecord record = new ListGridRecord();
            record.setListGrid(listGrid);
            
            if (e.findProperty(idProperty) != null) {
                record.setId(e.findProperty(idProperty).getValue());
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
                
                if (!(VisibilityEnum.HIDDEN_ALL.equals(fmd.getVisibility())
                                      || VisibilityEnum.FORM_HIDDEN.equals(fmd.getVisibility()))) {
                    // Depending on visibility, field for the particular property is not created on the form
                    String fieldType = fmd.getFieldType() == null ? null : fmd.getFieldType().toString();
                    
                    // Create the field and set some basic attributes
                    Field f;
                    
                    if (fieldType.equals(SupportedFieldType.BROADLEAF_ENUMERATION.toString())
                            || fieldType.equals(SupportedFieldType.EXPLICIT_ENUMERATION.toString())
                            || fieldType.equals(SupportedFieldType.DATA_DRIVEN_ENUMERATION.toString())
                            || fieldType.equals(SupportedFieldType.EMPTY_ENUMERATION.toString())) {
                        // We're dealing with fields that should render as drop-downs, so set their possible values
                        f = new ComboField();
                        ((ComboField) f).setOptions(fmd.getEnumerationValues());
                    } else if (fieldType.equals(SupportedFieldType.RULE_SIMPLE.toString())
                            || fieldType.equals(SupportedFieldType.RULE_WITH_QUANTITY.toString())) {
                        // We're dealing with rule builders, so we'll create those specialized fields
                        f = new RuleBuilderField();
                        ((RuleBuilderField) f).setJsonFieldName(fmd.getName() + "Json");
                        ((RuleBuilderField) f).setDataWrapper(new DataWrapper());
                        ((RuleBuilderField) f).setFieldBuilder(fmd.getRuleIdentifier());
                        
                        String blankJsonString =  "{\"data\":[]}";
                        ((RuleBuilderField) f).setJson(blankJsonString);
                        DataWrapper dw = convertJsonToDataWrapper(blankJsonString);
                        if (dw != null) {
                            ((RuleBuilderField) f).setDataWrapper(dw);
                        }
                        
                        if (fieldType.equals(SupportedFieldType.RULE_SIMPLE.toString())) {
                            ((RuleBuilderField) f).setStyleClass("rule-builder-simple");
                        } else if (fieldType.equals(SupportedFieldType.RULE_WITH_QUANTITY.toString())) {
                            ((RuleBuilderField) f).setStyleClass("rule-builder-complex");
                        }
                    } else if (LookupType.DROPDOWN.equals(fmd.getLookupType())) {
                        // We're dealing with a to-one field that wants to be rendered as a dropdown instead of in a 
                        // modal, so we'll provision the combo field here. Available options will be set as part of a
                        // subsequent operation
                        f = new ComboField();
                    } else {
                        // Create a default field since there was no specialized handler
                        f = new Field();
                    }
                    
                    Boolean required = fmd.getRequiredOverride();
                    if (required == null) {
                        required = fmd.getRequired();
                    }

                    f.withName(property.getName())
                         .withFieldType(fieldType)
                         .withOrder(fmd.getOrder())
                         .withFriendlyName(fmd.getFriendlyName())
                         .withForeignKeyDisplayValueProperty(fmd.getForeignKeyDisplayValueProperty())
                         .withRequired(required)
                         .withReadOnly(fmd.getReadOnly())
                         .withTranslatable(fmd.getTranslatable())
                         .withAlternateOrdering((Boolean) fmd.getAdditionalMetadata().get(Field.ALTERNATE_ORDERING));

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
    public EntityForm createEntityForm(ClassMetadata cmd)
            throws ServiceException {
        EntityForm ef = createStandardEntityForm();
        populateEntityForm(cmd, ef);
        return ef;
    }
    
    @Override
    public void populateEntityForm(ClassMetadata cmd, EntityForm ef)
            throws ServiceException {
        ef.setCeilingEntityClassname(cmd.getCeilingType());
        
        AdminSection section = navigationService.findAdminSectionByClass(cmd.getCeilingType());
        if (section != null) {
            ef.setSectionKey(section.getUrl());
        }

        setEntityFormFields(ef, Arrays.asList(cmd.getProperties()));
        
        populateDropdownToOneFields(ef, cmd);
    }
    
    @Override
    public EntityForm createEntityForm(ClassMetadata cmd, Entity entity)
            throws ServiceException {
        EntityForm ef = createStandardEntityForm();
        populateEntityForm(cmd, entity, ef);
        return ef;
    }

    @Override
    public void populateEntityForm(ClassMetadata cmd, Entity entity, EntityForm ef) 
            throws ServiceException {
        // Get the empty form with appropriate fields
        populateEntityForm(cmd, ef);

        String idProperty = adminEntityService.getIdProperty(cmd);
        ef.setId(entity.findProperty(idProperty).getValue());
        ef.setEntityType(entity.getType()[0]);

        // Set the appropriate property values
        for (Property p : cmd.getProperties()) {
            if (p.getMetadata() instanceof BasicFieldMetadata) {
                BasicFieldMetadata basicFM = (BasicFieldMetadata) p.getMetadata();

                Property entityProp = entity.findProperty(p.getName());

                if (entityProp == null) {
                    ef.removeField(p.getName());
                } else {
                    Field field = ef.findField(p.getName());
                    if (field != null) {
                        if (basicFM.getFieldType()==SupportedFieldType.RULE_SIMPLE 
                                || basicFM.getFieldType()==SupportedFieldType.RULE_WITH_QUANTITY) {
                            RuleBuilderField rbf = (RuleBuilderField) field;
                            String json = entity.getPMap().get(rbf.getJsonFieldName()).getValue();
                            rbf.setJson(json);
                            DataWrapper dw = convertJsonToDataWrapper(json);
                            if (dw != null) {
                                rbf.setDataWrapper(dw);
                            }
                        } else {
                            field.setValue(entityProp.getValue());
                            field.setDisplayValue(entityProp.getDisplayValue());
                        }
                    }
                }
            }
        }
        
        Property p = entity.findProperty(BasicPersistenceModule.MAIN_ENTITY_NAME_PROPERTY);
        if (p != null) {
            ef.setMainEntityName(p.getValue());
        }
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
    
    protected void populateDropdownToOneFields(EntityForm ef, ClassMetadata cmd) 
            throws ServiceException {
        for (Property p : cmd.getProperties()) {
            if (p.getMetadata() instanceof BasicFieldMetadata) {
                BasicFieldMetadata fmd = (BasicFieldMetadata) p.getMetadata();
                if (LookupType.DROPDOWN.equals(fmd.getLookupType())) {
                    // Get the records
                    PersistencePackageRequest toOnePpr = PersistencePackageRequest.standard()
                            .withCeilingEntityClassname(fmd.getForeignKeyClass());
                    Entity[] rows = adminEntityService.getRecords(toOnePpr).getRecords();
                    
                    // Determine the id field
                    String idProp = null;
                    ClassMetadata foreignClassMd = adminEntityService.getClassMetadata(toOnePpr);
                    for (Property foreignP : foreignClassMd.getProperties()) {
                        if (foreignP.getMetadata() instanceof BasicFieldMetadata) {
                            BasicFieldMetadata foreignFmd = (BasicFieldMetadata) foreignP.getMetadata();
                            if (SupportedFieldType.ID.equals(foreignFmd.getFieldType())) {
                                idProp = foreignP.getName();
                            }
                        }
                    }
                    
                    if (idProp == null) {
                        throw new RuntimeException("Could not determine ID property for " + fmd.getForeignKeyClass());
                    }
                    
                    // Determine the display field
                    String displayProp = fmd.getLookupDisplayProperty();
                    
                    // Build the options map
                    Map<String, String> options = new HashMap<String, String>();
                    for (Entity row : rows) {
                        String displayValue = row.findProperty(displayProp).getDisplayValue();
                        if (StringUtils.isBlank(displayValue)) {
                            displayValue = row.findProperty(displayProp).getValue();
                        }
                        options.put(row.findProperty(idProp).getValue(), displayValue);
                    }
                    
                    // Set the options on the entity field
                    ComboField cf = (ComboField) ef.findField(p.getName());
                    cf.setOptions(options);
                }
            }
        }
    }

   @Override
    public EntityForm createEntityForm(ClassMetadata cmd, Entity entity, Map<String, DynamicResultSet> collectionRecords)
            throws ServiceException {
        EntityForm ef = createStandardEntityForm();
        populateEntityForm(cmd, entity, collectionRecords, ef);
        return ef;
    }

    @Override
    public void populateEntityForm(ClassMetadata cmd, Entity entity, Map<String, DynamicResultSet> collectionRecords, EntityForm ef)
            throws ServiceException {
        // Get the form with values for this entity
        populateEntityForm(cmd, entity, ef);
        
        // Attach the sub-collection list grids and specialty UI support
        for (Property p : cmd.getProperties()) {
            if (p.getMetadata() instanceof BasicFieldMetadata) {
                continue;
            }
            
            if (!ArrayUtils.contains(p.getMetadata().getAvailableToTypes(), entity.getType()[0])) {
                continue;
            }

            DynamicResultSet subCollectionEntities = collectionRecords.get(p.getName());
            String containingEntityId = entity.getPMap().get("id").getValue();
            ListGrid listGrid = buildCollectionListGrid(containingEntityId, subCollectionEntities, p, ef.getSectionKey());
            listGrid.setListGridType(ListGrid.Type.INLINE);

            CollectionMetadata md = ((CollectionMetadata) p.getMetadata());
            ef.addListGrid(listGrid, md.getTab(), md.getTabOrder());
        }
        
        for (ListGrid lg : ef.getAllListGrids()) {
            // We always want the add option to be the first toolbar action for consistency
            if (lg.getToolbarActions().isEmpty()) {
                lg.addToolbarAction(DefaultListGridActions.ADD);
            } else {
                lg.getToolbarActions().add(0, DefaultListGridActions.ADD);
            }
        }
        
        if (CollectionUtils.isEmpty(ef.getActions())) {
            ef.addAction(DefaultEntityFormActions.SAVE);
        }
        
        ef.addAction(DefaultEntityFormActions.DELETE);
        
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

        if (StringUtils.isNotBlank(adornedList.getSortField())) {
            field = ef.getFields().get(adornedList.getSortField());
            entityProp = entity.findProperty(adornedList.getSortField());
            if (field != null && entityProp != null) {
                field.setValue(entityProp.getValue());
            }
        }
    }

    @Override
    public void populateMapEntityFormFields(EntityForm ef, Entity entity) {
        Field field = ef.getFields().get("priorKey");
        Property entityProp = entity.findProperty("key");
        field.setValue(entityProp.getValue());
    }

    @Override
    public EntityForm buildAdornedListForm(AdornedTargetCollectionMetadata adornedMd, AdornedTargetList adornedList,
            String parentId)
            throws ServiceException {
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

        if (StringUtils.isNotBlank(adornedList.getSortField())) {
            f = new Field()
                    .withName(adornedList.getSortField())
                    .withFieldType(SupportedFieldType.HIDDEN.toString());
            ef.addHiddenField(f);
        }

        return ef;
    }

    @Override
    public EntityForm buildMapForm(MapMetadata mapMd, final MapStructure mapStructure, ClassMetadata cmd, String parentId)
            throws ServiceException {
        EntityForm ef = createStandardEntityForm();
        ForeignKey foreignKey = (ForeignKey) mapMd.getPersistencePerspective()
                .getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);
        ef.setEntityType(foreignKey.getForeignKeyClass());

        Field keyField;
        if (!mapMd.getForceFreeFormKeys()) {
            // We will use a combo field to render the key choices
            ComboField temp = new ComboField();
            temp.withName("key")
                    .withFieldType("combo_field")
                    .withFriendlyName("Key");
            if (mapMd.getKeys() != null) {
                // The keys can be explicitly set in the annotation...
                temp.setOptions(mapMd.getKeys());
            } else {
                // Or they could be based on a different entity
                PersistencePackageRequest ppr = PersistencePackageRequest.standard()
                        .withCeilingEntityClassname(mapMd.getMapKeyOptionEntityClass());

                DynamicResultSet drs = adminEntityService.getRecords(ppr);
    
                for (Entity entity : drs.getRecords()) {
                    String keyValue = entity.getPMap().get(mapMd.getMapKeyOptionEntityValueField()).getValue();
                    String keyDisplayValue = entity.getPMap().get(mapMd.getMapKeyOptionEntityDisplayField()).getValue();
                    temp.putOption(keyValue, keyDisplayValue);
                }
            }
            keyField = temp;
        } else {
            keyField = new Field().withName("key")
                                .withFieldType(SupportedFieldType.STRING.toString())
                                .withFriendlyName("Key");
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
