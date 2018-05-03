/*
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 *
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.cms.admin.server.handler;

import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.cms.field.domain.FieldDefinition;
import org.broadleafcommerce.cms.field.domain.FieldGroup;
import org.broadleafcommerce.cms.structure.domain.StructuredContentType;
import org.broadleafcommerce.common.enumeration.domain.DataDrivenEnumerationValue;
import org.broadleafcommerce.common.presentation.ConfigurationItem;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.RegexPropertyValidator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Commonalities between {@link PageTemplateCustomPersistenceHandler} and {@link StructuredContentTypeCustomPersistenceHandler}
 * since they share similar issues in regards to dynamic fields
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component("blDynamicFieldPersistenceHandlerHelper")
public class DynamicFieldPersistenceHandlerHelper {

    public Property buildDynamicProperty(FieldDefinition definition, Class<?> inheritedType) {
        Property property = new Property();
        property.setName(definition.getName());
        BasicFieldMetadata fieldMetadata = new BasicFieldMetadata();
        property.setMetadata(fieldMetadata);
        fieldMetadata.setFieldType(definition.getFieldType());

        fieldMetadata.setMutable(true);
        fieldMetadata.setInheritedFromType(inheritedType.getName());
        fieldMetadata.setAvailableToTypes(new String[] {inheritedType.getName()});
        fieldMetadata.setForeignKeyCollection(false);
        fieldMetadata.setMergedPropertyType(MergedPropertyType.PRIMARY);
        fieldMetadata.setLength(definition.getMaxLength());
        if (definition.getDataDrivenEnumeration() != null && !CollectionUtils.isEmpty(definition.getDataDrivenEnumeration().getEnumValues())) {
            int count = definition.getDataDrivenEnumeration().getEnumValues().size();
            String[][] enumItems = new String[count][2];
            for (int j = 0; j < count; j++) {
                DataDrivenEnumerationValue item = definition.getDataDrivenEnumeration().getEnumValues().get(j);
                enumItems[j][0] = item.getKey();
                enumItems[j][1] = item.getDisplay();
            }
            fieldMetadata.setEnumerationValues(enumItems);
        }
        fieldMetadata.setName(definition.getName());
        fieldMetadata.setFriendlyName(definition.getFriendlyName());
        fieldMetadata.setSecurityLevel(definition.getSecurityLevel()==null?"":definition.getSecurityLevel());
        fieldMetadata.setVisibility(definition.getHiddenFlag()?VisibilityEnum.HIDDEN_ALL:VisibilityEnum.VISIBLE_ALL);
        fieldMetadata.setTab("General");
        fieldMetadata.setTabOrder(100);
        fieldMetadata.setOrder(definition.getFieldOrder());
        fieldMetadata.setExplicitFieldType(SupportedFieldType.UNKNOWN);
        fieldMetadata.setLargeEntry(definition.getTextAreaFlag());
        fieldMetadata.setProminent(false);
        fieldMetadata.setColumnWidth(String.valueOf(definition.getColumnWidth()));
        fieldMetadata.setBroadleafEnumeration("");
        fieldMetadata.setReadOnly(false);
        fieldMetadata.setRequiredOverride(definition.getRequiredFlag());
        fieldMetadata.setHint(definition.getHint());
        fieldMetadata.setHelpText(definition.getHelpText());
        fieldMetadata.setTooltip(definition.getTooltip());
        fieldMetadata.setTranslatable(true);
        if (definition.getValidationRegEx() != null) {
            Map<String, String> itemMap = new HashMap<String, String>();
            itemMap.put("regularExpression", definition.getValidationRegEx());
            itemMap.put(ConfigurationItem.ERROR_MESSAGE, definition.getValidationErrorMesageKey());
            List<Map<String, String>> configurationItems = new ArrayList<Map<String, String>>();
            configurationItems.add(itemMap);
            fieldMetadata.getValidationConfigurations().put(RegexPropertyValidator.class.getName(), configurationItems);
        }


        if (definition.getFieldType().equals(SupportedFieldType.ADDITIONAL_FOREIGN_KEY)) {
            fieldMetadata.setForeignKeyClass(definition.getAdditionalForeignKeyClass());
            fieldMetadata.setOwningClass(definition.getAdditionalForeignKeyClass());
            fieldMetadata.setForeignKeyDisplayValueProperty("__adminMainEntity");
        }

        return property;
    }

    /**
     * Builds all of the metadata for all of the dynamic properties within a {@link StructuredContentType}, gleaned from
     * the {@link FieldGroup}s and {@link FieldDefinition}s.
     *
     * @param fieldGroups groups that the {@link Property}s are built from
     * @param inheritedType the value that each built {@link FieldMetadata} for each property will use to notate where the
     * dynamic field actually came from (meaning {@link FieldMetadata#setAvailableToTypes(String[])} and {@link FieldMetadata#setInheritedFromType(String)}
     * @return
     */
    public Property[] buildDynamicPropertyList(List<FieldGroup> fieldGroups, Class<?> inheritedType) {
        List<Property> propertiesList = new ArrayList<Property>();
        for (FieldGroup group : fieldGroups) {
            List<FieldDefinition> definitions = group.getFieldDefinitions();
            for (FieldDefinition def : definitions) {
                Property property = buildDynamicProperty(def, inheritedType);
                BasicFieldMetadata fieldMetadata = (BasicFieldMetadata) property.getMetadata();
                fieldMetadata.setGroup(group.getName());
                fieldMetadata.setGroupCollapsed(group.getInitCollapsedFlag());
                propertiesList.add(property);
            }
        }
        Property property = new Property();
        property.setName("id");
        BasicFieldMetadata fieldMetadata = new BasicFieldMetadata();
        property.setMetadata(fieldMetadata);
        fieldMetadata.setFieldType(SupportedFieldType.ID);
        fieldMetadata.setSecondaryType(SupportedFieldType.INTEGER);
        fieldMetadata.setMutable(true);
        fieldMetadata.setInheritedFromType(inheritedType.getName());
        fieldMetadata.setAvailableToTypes(new String[] {inheritedType.getName()});
        fieldMetadata.setForeignKeyCollection(false);
        fieldMetadata.setMergedPropertyType(MergedPropertyType.PRIMARY);
        fieldMetadata.setName("id");
        fieldMetadata.setFriendlyName("ID");
        fieldMetadata.setSecurityLevel("");
        fieldMetadata.setVisibility(VisibilityEnum.HIDDEN_ALL);
        fieldMetadata.setExplicitFieldType(SupportedFieldType.UNKNOWN);
        fieldMetadata.setLargeEntry(false);
        fieldMetadata.setProminent(false);
        fieldMetadata.setColumnWidth("*");
        fieldMetadata.setBroadleafEnumeration("");
        fieldMetadata.setReadOnly(true);
        propertiesList.add(property);

        Property[] properties = new Property[propertiesList.size()];
        properties = propertiesList.toArray(properties);
        Arrays.sort(properties, new Comparator<Property>() {
            @Override
            public int compare(Property o1, Property o2) {
                /*
                     * First, compare properties based on order fields
                     */
                if (o1.getMetadata().getOrder() != null && o2.getMetadata().getOrder() != null) {
                    return o1.getMetadata().getOrder().compareTo(o2.getMetadata().getOrder());
                } else if (o1.getMetadata().getOrder() != null && o2.getMetadata().getOrder() == null) {
                    /*
                          * Always favor fields that have an order identified
                          */
                    return -1;
                } else if (o1.getMetadata().getOrder() == null && o2.getMetadata().getOrder() != null) {
                    /*
                          * Always favor fields that have an order identified
                          */
                    return 1;
                } else if (o1.getMetadata().getFriendlyName() != null && o2.getMetadata().getFriendlyName() != null) {
                    return o1.getMetadata().getFriendlyName().compareTo(o2.getMetadata().getFriendlyName());
                } else {
                    return o1.getName().compareTo(o2.getName());
                }
            }
        });
        return properties;
    }

}
