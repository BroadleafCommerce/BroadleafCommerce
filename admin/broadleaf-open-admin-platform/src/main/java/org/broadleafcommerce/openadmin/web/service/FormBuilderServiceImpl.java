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
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.MapMetadata;
import org.broadleafcommerce.openadmin.client.dto.MapStructure;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.client.dto.visitor.MetadataVisitor;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.server.service.AdminEntityService;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.component.ListGridRecord;
import org.broadleafcommerce.openadmin.web.form.component.RuleBuilder;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
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
                    Field hf = new Field()
                            .withName(p.getName())
                            .withFriendlyName(fmd.getFriendlyName());
                    hfs.add(hf);
                }
            }
        }

        return buildListGrid(cmd, entities, hfs);
    }

    @Override
    public ListGrid buildMapListGrid(MapMetadata mmd, ClassMetadata cmd, Entity[] entities) {
        List<Field> hfs = new ArrayList<Field>();

        Property p2 = cmd.getPMap().get("key");
        Field hf = new Field()
                .withName(p2.getName())
                .withFriendlyName(p2.getMetadata().getFriendlyName());
        hfs.add(hf);

        // Determine which fields are going to be used in the table header
        // For now, only consider field prominence annotations
        for (Property p : cmd.getProperties()) {
            if (p.getMetadata() instanceof BasicFieldMetadata) {
                BasicFieldMetadata fmd = (BasicFieldMetadata) p.getMetadata();
                if (fmd.getTargetClass().equals(mmd.getValueClassName())) {
                    if (fmd.isProminent() != null && fmd.isProminent()) {
                        hf = new Field()
                                .withName(p.getName())
                                .withFriendlyName(fmd.getFriendlyName());
                        hfs.add(hf);
                    }
                }
            }
        }

        return buildListGrid(cmd, entities, hfs);
    }

    @Override
    public ListGrid buildAdornedListGrid(AdornedTargetCollectionMetadata fmd, ClassMetadata cmd, Entity[] entities) {
        List<Field> hfs = new ArrayList<Field>();
        for (String fieldName : fmd.getGridVisibleFields()) {
            Property p = cmd.getPMap().get(fieldName);
            Field hf = new Field()
                    .withName(p.getName())
                    .withFriendlyName(p.getMetadata().getFriendlyName());
            hfs.add(hf);
        }

        return buildListGrid(cmd, entities, hfs);
    }

    protected ListGrid buildListGrid(ClassMetadata cmd, Entity[] entities, List<Field> headerFields) {
        ListGrid lg = new ListGrid();
        lg.setClassName(cmd.getCeilingType());
        lg.setHeaderFields(headerFields);

        // For each of the entities (rows) in the list grid, we need to build the associated
        // ListGridRecord and set the required fields on the record. These fields are the same ones
        // that are used for the column headers
        for (Entity e : entities) {
            ListGridRecord record = new ListGridRecord();
            record.setId(e.findProperty("id").getValue());

            for (Field headerField : headerFields) {
                Property p = e.findProperty(headerField.getName());
                Field recordField = new Field()
                        .withName(headerField.getName())
                        .withValue(p.getValue());
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
    public EntityForm createEntityForm(ClassMetadata cmd) {
        EntityForm ef = new EntityForm();
        ef.setEntityType(cmd.getCeilingType());
        buildFormMetadata(cmd, ef);
        return ef;
    }

    @Override
    public EntityForm createEntityForm(ClassMetadata cmd, final Entity entity, final Map<String, Entity[]> subCollections)
            throws ClassNotFoundException, ServiceException, ApplicationSecurityException {
        final EntityForm ef = new EntityForm();
        ef.setId(entity.findProperty("id").getValue());
        ef.setEntityType(entity.getType()[0]);
        return buildEntityForm(ef, cmd, entity, subCollections);
    }

    @Override
    public void buildFormMetadata(ClassMetadata cmd, final EntityForm ef) {
        for (final Property p : cmd.getProperties()) {
            if (p.getMetadata() instanceof BasicFieldMetadata) {
                BasicFieldMetadata fmd = (BasicFieldMetadata) p.getMetadata();

                String fieldType = fmd.getFieldType() == null ? null : fmd.getFieldType().toString();

                // Create the field and set some basic attributes
                Field f = new Field()
                        .withName(p.getName())
                        .withFieldType(fieldType)
                        .withFriendlyName(p.getMetadata().getFriendlyName())
                        .withForeignKeyDisplayValueProperty(fmd.getForeignKeyDisplayValueProperty());

                if (StringUtils.isBlank(f.getFriendlyName())) {
                    f.setFriendlyName(f.getName());
                }

                // Add the field to the appropriate FieldGroup
                ef.addField(f, fmd.getGroup());
            }
        }
    }

    @Override
    public EntityForm buildAdornedListForm(AdornedTargetCollectionMetadata adornedMd, AdornedTargetList adornedList,
            String parentId)
            throws ServiceException, ApplicationSecurityException {
        final EntityForm ef = new EntityForm();
        ef.setEntityType(adornedList.getAdornedTargetEntityClassname());

        PersistencePackageRequest request = PersistencePackageRequest.adorned()
                .withClassName(adornedMd.getCollectionCeilingEntity())
                .withAdornedList(adornedList);
        ClassMetadata collectionMetadata = adminEntityService.getClassMetadata(request);

        for (String targetFieldName : adornedMd.getMaintainedAdornedTargetFields()) {
            Property p = collectionMetadata.getPMap().get(targetFieldName);
            BasicFieldMetadata fmd = ((BasicFieldMetadata) p.getMetadata());
            String fieldType = fmd.getFieldType() == null ? null : fmd.getFieldType().toString();

            // Create the field and set some basic attributes
            Field f = new Field()
                    .withName(p.getName())
                    .withFieldType(fieldType)
                    .withFriendlyName(p.getMetadata().getFriendlyName())
                    .withForeignKeyDisplayValueProperty(fmd.getForeignKeyDisplayValueProperty());

            if (StringUtils.isBlank(f.getFriendlyName())) {
                f.setFriendlyName(f.getName());
            }

            // Add the field to the appropriate FieldGroup
            ef.addField(f, fmd.getGroup());
        }

        Field f = new Field()
                .withName(adornedList.getLinkedObjectPath() + "." + adornedList.getLinkedIdProperty())
                .withFieldType(SupportedFieldType.HIDDEN.toString())
                .withValue(parentId);
        ef.addField(f, EntityForm.HIDDEN_GROUP);

        f = new Field()
                .withName(adornedList.getTargetObjectPath() + "." + adornedList.getTargetIdProperty())
                .withFieldType(SupportedFieldType.HIDDEN.toString())
                .withIdOverride("adornedTargetIdProperty");
        ef.addField(f, EntityForm.HIDDEN_GROUP);

        return ef;
    }

    @Override
    public EntityForm buildEntityForm(final EntityForm ef, ClassMetadata cmd, final Entity entity,
            final Map<String, Entity[]> subCollections)
            throws ClassNotFoundException, ServiceException, ApplicationSecurityException {
        buildFormMetadata(cmd, ef);

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
                        ClassMetadata subCollMd = adminEntityService.getClassMetadata(fmd.getCollectionCeilingEntity());

                        if (fmd.getRuleBuilderVars().length > 0) {
                            RuleBuilder subCollectionRuleBuilder = buildRuleBuilder(subCollMd, subCollectionEntities,
                                    fmd.getRuleBuilderVars(), fmd.getRuleBuilderConfigKeys());
                            ef.getCollectionRuleBuilders().add(subCollectionRuleBuilder);
                        } else {
                            ListGrid subCollectionGrid = buildListGrid(subCollMd, subCollectionEntities);
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
                    try {
                        ForeignKey foreignField = (ForeignKey) fmd.getPersistencePerspective()
                                .getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);

                        MapStructure map = (MapStructure) fmd.getPersistencePerspective()
                                .getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE);

                        Entity[] subCollectionEntities = subCollections.get(p.getName());

                        PersistencePackageRequest request = PersistencePackageRequest.map()
                                .withClassName(fmd.getTargetClass())
                                .withMapStructure(map)
                                .addForeignKey(foreignField);
                        ClassMetadata subCollectionMd = adminEntityService.getClassMetadata(request);

                        ListGrid subCollectionGrid = buildMapListGrid(fmd, subCollectionMd, subCollectionEntities);
                        subCollectionGrid.setSubCollectionFieldName(p.getName());
                        ef.getCollectionListGrids().add(subCollectionGrid);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void visit(AdornedTargetCollectionMetadata fmd) {
                    try {
                        AdornedTargetList adornedList = (AdornedTargetList) fmd.getPersistencePerspective()
                                .getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST);

                        Entity[] subCollectionEntities = subCollections.get(p.getName());

                        PersistencePackageRequest request = PersistencePackageRequest.adorned()
                                .withClassName(fmd.getCollectionCeilingEntity())
                                .withAdornedList(adornedList);
                        ClassMetadata subCollectionMd = adminEntityService.getClassMetadata(request);

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