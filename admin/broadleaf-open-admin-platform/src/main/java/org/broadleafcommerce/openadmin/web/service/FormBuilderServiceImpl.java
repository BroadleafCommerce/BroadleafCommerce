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

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.client.dto.BasicCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.MapMetadata;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.client.dto.visitor.MetadataVisitor;
import org.broadleafcommerce.openadmin.server.service.AdminEntityService;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.component.ListGridRecord;
import org.broadleafcommerce.openadmin.web.form.component.RuleBuilder;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.broadleafcommerce.openadmin.web.form.entity.FieldGroup;
import org.springframework.stereotype.Service;

import com.gwtincubator.security.exception.ApplicationSecurityException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * @author Andre Azzolini (apazzolini)
 */
@Service("blFormBuilderService")
public class FormBuilderServiceImpl implements FormBuilderService {

    @Resource(name = "blAdminEntityService")
    protected AdminEntityService adminEntityService;

    @Override
    public ListGrid buildListGrid(ClassMetadata cmd, Entity[] entities) {
        List<Field> hfs = new ArrayList<Field>();

        // Determine which fields are going to be used in the table header
        // For now, only consider field prominence annotations
        for (Property p : cmd.getProperties()) {
            if (p.getMetadata() instanceof BasicFieldMetadata) {
                BasicFieldMetadata fmd = (BasicFieldMetadata) p.getMetadata();
                if (fmd.isProminent() != null && fmd.isProminent()) {
                    Field hf = new Field();
                    hf.setName(p.getName());
                    hf.setFriendlyName(fmd.getFriendlyName());
                    hfs.add(hf);
                }
            }
        }

        ListGrid lg = new ListGrid();
        lg.setClassName(cmd.getCeilingType());
        lg.setHeaderFields(hfs);

        // For each of the entities (rows) in the list grid, we need to build the associated
        // ListGridRecord and set the required fields on the record. These fields are the same ones
        // that are used for the column headers
        for (Entity e : entities) {
            ListGridRecord record = new ListGridRecord();
            record.setId(e.findProperty("id").getValue());

            for (Field headerField : hfs) {
                Property p = e.findProperty(headerField.getName());
                Field recordField = new Field();
                recordField.setName(headerField.getName());
                recordField.setValue(p.getValue());
                record.getFields().add(recordField);
            }

            lg.getRecords().add(record);
        }

        return lg;
    }

    @Override
    public ListGrid buildAdornedListGrid(AdornedTargetCollectionMetadata fmd, ClassMetadata cmd, Entity[] entities) {
        List<Field> hfs = new ArrayList<Field>();
        for (String fieldName : fmd.getGridVisibleFields()) {
            Property p = cmd.getPMap().get(fieldName);
            Field hf = new Field();
            hf.setName(p.getName());
            hf.setFriendlyName(p.getMetadata().getFriendlyName());
            hfs.add(hf);
        }

        ListGrid lg = new ListGrid();
        lg.setClassName(cmd.getCeilingType());
        lg.setHeaderFields(hfs);

        // For each of the entities (rows) in the list grid, we need to build the associated
        // ListGridRecord and set the required fields on the record. These fields are the same ones
        // that are used for the column headers
        for (Entity e : entities) {
            ListGridRecord record = new ListGridRecord();
            record.setId(e.findProperty("id").getValue());

            for (Field headerField : hfs) {
                Property p = e.findProperty(headerField.getName());
                Field recordField = new Field();
                recordField.setName(headerField.getName());
                recordField.setValue(p.getValue());
                record.getFields().add(recordField);
            }

            lg.getRecords().add(record);
        }

        return lg;
    }

    @Override
    public RuleBuilder buildRuleBuilder(ClassMetadata cmd, Entity[] entities, String[] ruleVars, String[] configKeys) {
        RuleBuilder rb = new RuleBuilder();
        rb.setClassName(cmd.getCeilingType());
        rb.setRuleVars(ruleVars);
        rb.setConfigKeys(configKeys);
        rb.setEntities(entities);

        return rb;
    }

    @Override
    public EntityForm buildEntityForm(ClassMetadata cmd) {
        final EntityForm ef = new EntityForm();
        ef.setEntityType(cmd.getCeilingType());

        for (final Property p : cmd.getProperties()) {
            p.getMetadata().accept(new MetadataVisitor() {

                @Override
                public void visit(BasicFieldMetadata fmd) {
                    // We have all polymorphic types here since we're looping through the metadata
                    // for the class. Filter out ones that do not apply to this particular entity.

                    String fieldType = fmd.getFieldType() == null ? null : fmd.getFieldType().toString();

                    // Create the field and set some basic attributes
                    Field f = new Field();
                    f.setName(p.getName());
                    f.setFieldType(fieldType);
                    f.setFriendlyName(p.getMetadata().getFriendlyName());
                    if (StringUtils.isBlank(f.getFriendlyName())) {
                        f.setFriendlyName(f.getName());
                    }

                    // Set additional attributes
                    f.setForeignKeyDisplayValueProperty(fmd.getForeignKeyDisplayValueProperty());

                    // Add the field to the appropriate FieldGroup
                    String groupName = ((BasicFieldMetadata) p.getMetadata()).getGroup();
                    groupName = groupName == null ? "Default" : groupName;
                    FieldGroup fieldGroup = ef.getGroups().get(groupName);
                    if (fieldGroup == null) {
                        fieldGroup = new FieldGroup();
                        fieldGroup.setTitle(groupName);
                        ef.getGroups().put(groupName, fieldGroup);
                    }
                    fieldGroup.getFields().add(f);
                }

                @Override
                public void visit(BasicCollectionMetadata fmd) {

                }

                @Override
                public void visit(MapMetadata fmd) {

                }

                @Override
                public void visit(AdornedTargetCollectionMetadata fmd) {

                }
            });
        }

        return ef;

    }

    @Override
    public EntityForm buildAdornedListForm(AdornedTargetCollectionMetadata adornedMd, AdornedTargetList adornedList,
            String parentId)
            throws ServiceException, ApplicationSecurityException {
        final EntityForm ef = new EntityForm();
        ef.setEntityType(adornedList.getAdornedTargetEntityClassname());

        ClassMetadata collectionMetadata =
                adminEntityService.getClassMetadata(adornedMd.getCollectionCeilingEntity(), adornedList);

        for (String targetFieldName : adornedMd.getMaintainedAdornedTargetFields()) {
            Property p = collectionMetadata.getPMap().get(targetFieldName);
            BasicFieldMetadata fmd = ((BasicFieldMetadata) p.getMetadata());
            String fieldType = fmd.getFieldType() == null ? null : fmd.getFieldType().toString();

            // Create the field and set some basic attributes
            Field f = new Field();
            f.setName(p.getName());
            f.setFieldType(fieldType);
            f.setFriendlyName(p.getMetadata().getFriendlyName());
            if (StringUtils.isBlank(f.getFriendlyName())) {
                f.setFriendlyName(f.getName());
            }

            // Set additional attributes
            f.setForeignKeyDisplayValueProperty(fmd.getForeignKeyDisplayValueProperty());

            // Add the field to the appropriate FieldGroup
            String groupName = ((BasicFieldMetadata) p.getMetadata()).getGroup();
            groupName = groupName == null ? "Default" : groupName;
            FieldGroup fieldGroup = ef.getGroups().get(groupName);
            if (fieldGroup == null) {
                fieldGroup = new FieldGroup();
                fieldGroup.setTitle(groupName);
                ef.getGroups().put(groupName, fieldGroup);
            }
            fieldGroup.getFields().add(f);
        }

        FieldGroup fieldGroup = ef.getGroups().get(EntityForm.HIDDEN_GROUP);
        if (fieldGroup == null) {
            fieldGroup = new FieldGroup();
            ef.getGroups().put(EntityForm.HIDDEN_GROUP, fieldGroup);
        }

        Field f = new Field();
        f.setName(adornedList.getLinkedObjectPath() + "." + adornedList.getLinkedIdProperty());
        f.setFieldType(SupportedFieldType.HIDDEN.toString());
        f.setValue(parentId);
        fieldGroup.getFields().add(f);

        f = new Field();
        f.setName(adornedList.getTargetObjectPath() + "." + adornedList.getTargetIdProperty());
        f.setFieldType(SupportedFieldType.HIDDEN.toString());
        f.setIdOverride("adornedTargetIdProperty");
        fieldGroup.getFields().add(f);

        return ef;
    }

    @Override
    public EntityForm buildEntityForm(ClassMetadata cmd, final Entity entity, final Map<String, Entity[]> subCollections)
            throws ClassNotFoundException, ServiceException, ApplicationSecurityException {
        final EntityForm ef = buildEntityForm(cmd);
        ef.setId(entity.findProperty("id").getValue());

        for (final Property p : cmd.getProperties()) {
            p.getMetadata().accept(new MetadataVisitor() {
                @Override
                public void visit(BasicFieldMetadata fmd) {
                    // We have all polymorphic types here since we're looping through the metadata
                    // for the class. Filter out ones that do not apply to this particular entity.
                    Property entityProp = entity.findProperty(p.getName());

                    if (entityProp == null) {
                        ef.removeField(p.getName());
                    } else {
                        Field field = ef.findField(p.getName());
                        field.setValue(entityProp.getValue());
                        field.setDisplayValue(entityProp.getDisplayValue());
                    }
                }
                
                @Override
                public void visit(BasicCollectionMetadata fmd) {
                    try {
                        Entity[] subCollectionEntities = subCollections.get(p.getName());
                        ClassMetadata subCollectionMd = adminEntityService.getClassMetadata(fmd.getCollectionCeilingEntity());

                        if (fmd.getRuleBuilderVars().length > 0) {
                            RuleBuilder subCollectionRuleBuilder = buildRuleBuilder(subCollectionMd, subCollectionEntities,
                                    fmd.getRuleBuilderVars(), fmd.getRuleBuilderConfigKeys());
                            ef.getCollectionRuleBuilders().add(subCollectionRuleBuilder);
                        } else {
                            ListGrid subCollectionGrid = buildListGrid(subCollectionMd, subCollectionEntities);
                            subCollectionGrid.setSubCollectionFieldName(p.getName());
                            subCollectionGrid.setAddMethodType(fmd.getAddMethodType());
                            subCollectionGrid.setListGridType("inline");
                            ef.getCollectionListGrids().add(subCollectionGrid);
                        }

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void visit(MapMetadata fmd) {

                }

                @Override
                public void visit(AdornedTargetCollectionMetadata fmd) {
                    try {
                        AdornedTargetList adornedList = (AdornedTargetList) fmd.getPersistencePerspective()
                                .getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST);
                        Entity[] subCollectionEntities = subCollections.get(p.getName());
                        ClassMetadata subCollectionMd = adminEntityService.getClassMetadata(fmd.getCollectionCeilingEntity(), adornedList);

                        ListGrid subCollectionGrid = buildAdornedListGrid(fmd, subCollectionMd, subCollectionEntities);
                        subCollectionGrid.setSubCollectionFieldName(p.getName());
                        ef.getCollectionListGrids().add(subCollectionGrid);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        return ef;
    }

}


