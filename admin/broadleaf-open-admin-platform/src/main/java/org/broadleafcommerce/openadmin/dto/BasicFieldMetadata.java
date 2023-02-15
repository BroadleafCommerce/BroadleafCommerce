/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.dto;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.presentation.client.LookupType;
import org.broadleafcommerce.common.presentation.client.RuleBuilderDisplayType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.openadmin.dto.visitor.MetadataVisitor;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.PropertyValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
public class BasicFieldMetadata extends FieldMetadata {

    private static final long serialVersionUID = 1L;

    protected SupportedFieldType fieldType;
    protected SupportedFieldType secondaryType = SupportedFieldType.INTEGER;
    protected Integer length;
    protected Boolean required;
    protected Boolean unique;
    protected Integer scale;
    protected Integer precision;
    protected Boolean mutable;
    protected String foreignKeyProperty;
    protected String foreignKeyClass;
    protected String foreignKeyDisplayValueProperty;
    protected Boolean foreignKeyCollection;
    protected MergedPropertyType mergedPropertyType;
    protected String[][] enumerationValues;
    protected String enumerationClass;
    protected Boolean isDerived;

    //@AdminPresentation derived fields
    protected String name;
    protected VisibilityEnum visibility;
    @Deprecated
    protected Boolean groupCollapsed;
    protected SupportedFieldType explicitFieldType;
    protected RuleBuilderDisplayType displayType;
    protected Boolean largeEntry;
    protected Boolean prominent;
    protected Integer gridOrder;
    protected String columnWidth;
    protected String broadleafEnumeration;
    protected Boolean hideEnumerationIfEmpty;
    protected SupportedFieldType fieldComponentRenderer;
    protected String fieldComponentRendererTemplate;
    protected SupportedFieldType gridFieldComponentRenderer;
    private String gridFieldComponentRendererTemplate;
    protected Boolean readOnly;
    protected Map<String, List<Map<String, String>>> validationConfigurations = new HashMap<>(5);
    protected Boolean requiredOverride;
    protected String tooltip;
    protected String helpText;
    protected String hint;
    protected String lookupDisplayProperty;
    protected Boolean forcePopulateChildProperties;
    protected Boolean enableTypeaheadLookup;
    protected String optionListEntity;
    protected String optionValueFieldName;
    protected String optionDisplayFieldName;
    protected Boolean optionCanEditValues;
    protected Boolean optionHideIfEmpty;
    protected String[][] optionFilterParams;
    protected String[] customCriteria;
    protected Boolean useServerSideInspectionCache;
    protected Boolean toOneLookupCreatedViaAnnotation;
    protected String ruleIdentifier;
    protected LookupType lookupType;
    protected Boolean translatable;
    protected String defaultValue;
    protected Boolean isFilter;
    protected Boolean canLinkToExternalEntity;
    protected String associatedFieldName;

    //for MapFields
    protected String mapFieldValueClass;
    protected Boolean searchable;
    protected String manyToField;
    protected String toOneTargetProperty;
    protected String toOneParentProperty;
    protected String mapKeyValueProperty;

    protected Boolean allowNoValueEnumOption;

    public SupportedFieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(SupportedFieldType fieldType) {
        this.fieldType = fieldType;
    }

    public SupportedFieldType getSecondaryType() {
        return secondaryType;
    }

    public void setSecondaryType(SupportedFieldType secondaryType) {
        this.secondaryType = secondaryType;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Boolean getRequired() {
        if (required == null) {
            return false;
        }
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Boolean getUnique() {
        return unique;
    }

    public void setUnique(Boolean unique) {
        this.unique = unique;
    }

    public Boolean getMutable() {
        return mutable;
    }

    public void setMutable(Boolean mutable) {
        this.mutable = mutable;
    }

    public String getForeignKeyProperty() {
        return foreignKeyProperty;
    }

    public void setForeignKeyProperty(String foreignKeyProperty) {
        this.foreignKeyProperty = foreignKeyProperty;
    }

    public String getForeignKeyClass() {
        return foreignKeyClass;
    }

    public void setForeignKeyClass(String foreignKeyClass) {
        this.foreignKeyClass = foreignKeyClass;
    }

    public Boolean getForeignKeyCollection() {
        return foreignKeyCollection == null ? false : foreignKeyCollection;
    }

    public void setForeignKeyCollection(Boolean foreignKeyCollection) {
        this.foreignKeyCollection = foreignKeyCollection;
    }

    public MergedPropertyType getMergedPropertyType() {
        return mergedPropertyType;
    }

    public void setMergedPropertyType(MergedPropertyType mergedPropertyType) {
        this.mergedPropertyType = mergedPropertyType;
    }

    public String[][] getEnumerationValues() {
        return enumerationValues;
    }

    public void setEnumerationValues(String[][] enumerationValues) {
        this.enumerationValues = enumerationValues;
    }

    public String getForeignKeyDisplayValueProperty() {
        return foreignKeyDisplayValueProperty;
    }

    public void setForeignKeyDisplayValueProperty(String foreignKeyDisplayValueProperty) {
        this.foreignKeyDisplayValueProperty = foreignKeyDisplayValueProperty;
    }

    public String getEnumerationClass() {
        return enumerationClass;
    }

    public void setEnumerationClass(String enumerationClass) {
        this.enumerationClass = enumerationClass;
    }
    
    public Boolean getIsDerived() {
        return isDerived;
    }

    public void setDerived(Boolean isDerived) {
        this.isDerived = isDerived;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SupportedFieldType getExplicitFieldType() {
        return explicitFieldType;
    }

    public void setExplicitFieldType(SupportedFieldType fieldType) {
        this.explicitFieldType = fieldType;
    }

    public RuleBuilderDisplayType getDisplayType() {
        return displayType;
    }

    public void setDisplayType(RuleBuilderDisplayType displayType) {
        this.displayType = displayType;
    }

    public Boolean isLargeEntry() {
        return largeEntry;
    }

    public void setLargeEntry(Boolean largeEntry) {
        this.largeEntry = largeEntry;
    }

    public Boolean isProminent() {
        return prominent;
    }

    public void setProminent(Boolean prominent) {
        this.prominent = prominent;
    }

    public String getColumnWidth() {
        return columnWidth;
    }

    public void setColumnWidth(String columnWidth) {
        this.columnWidth = columnWidth;
    }

    public String getBroadleafEnumeration() {
        return broadleafEnumeration;
    }

    public void setBroadleafEnumeration(String broadleafEnumeration) {
        this.broadleafEnumeration = broadleafEnumeration;
    }

    public Boolean getHideEnumerationIfEmpty() {
        return hideEnumerationIfEmpty;
    }

    public void setHideEnumerationIfEmpty(Boolean hideEnumerationIfEmpty) {
        this.hideEnumerationIfEmpty = hideEnumerationIfEmpty;
    }

    public SupportedFieldType getFieldComponentRenderer() {
        return fieldComponentRenderer;
    }

    public void setFieldComponentRenderer(SupportedFieldType fieldComponentRenderer) {
        this.fieldComponentRenderer = fieldComponentRenderer;
    }

    public SupportedFieldType getGridFieldComponentRenderer() {
        return gridFieldComponentRenderer;
    }

    public void setGridFieldComponentRenderer(SupportedFieldType gridFieldComponentRenderer) {
        this.gridFieldComponentRenderer = gridFieldComponentRenderer;
    }

    public String getFieldComponentRendererTemplate() {
        return fieldComponentRendererTemplate;
    }

    public void setFieldComponentRendererTemplate(String fieldComponentRendererTemplate) {
        this.fieldComponentRendererTemplate = fieldComponentRendererTemplate;
    }

    public String getGridFieldComponentRendererTemplate() {
        return gridFieldComponentRendererTemplate;
    }

    public void setGridFieldComponentRendererTemplate(String gridFieldComponentRendererTemplate) {
        this.gridFieldComponentRendererTemplate = gridFieldComponentRendererTemplate;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public Integer getGridOrder() {
        return gridOrder;
    }

    public void setGridOrder(Integer gridOrder) {
        this.gridOrder = gridOrder;
    }

    /**
     * @return the validation configurations for this property keyed by the fully-qualified name of the
     * {@link PropertyValidator} implementation
     */
    public Map<String, List<Map<String, String>>> getValidationConfigurations() {
        return validationConfigurations;
    }

    public void setValidationConfigurations(Map<String, List<Map<String, String>>> validationConfigurations) {
        this.validationConfigurations = validationConfigurations;
    }

    public Boolean getRequiredOverride() {
        return requiredOverride;
    }

    public void setRequiredOverride(Boolean requiredOverride) {
        this.requiredOverride = requiredOverride;
    }

    @Deprecated
    public Boolean getGroupCollapsed() {
        return groupCollapsed;
    }

    @Deprecated
    public void setGroupCollapsed(Boolean groupCollapsed) {
        this.groupCollapsed = groupCollapsed;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public String getHelpText() {
        return helpText;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public VisibilityEnum getVisibility() {
        return visibility;
    }

    public void setVisibility(VisibilityEnum visibility) {
        this.visibility = visibility;
    }

    public String getLookupDisplayProperty() {
        return lookupDisplayProperty;
    }

    public void setLookupDisplayProperty(String lookupDisplayProperty) {
        this.lookupDisplayProperty = lookupDisplayProperty;
    }
    
    public Boolean getForcePopulateChildProperties() {
        return forcePopulateChildProperties;
    }
    
    public void setForcePopulateChildProperties(Boolean forcePopulateChildProperties) {
        this.forcePopulateChildProperties = forcePopulateChildProperties;
    }
    public Boolean getEnableTypeaheadLookup() {
        return enableTypeaheadLookup;
    }
    
    public void setEnableTypeaheadLookup(Boolean enableTypeaheadLookup) {
        this.enableTypeaheadLookup = enableTypeaheadLookup;
    }

    public Boolean getOptionCanEditValues() {
        return optionCanEditValues;
    }

    public void setOptionCanEditValues(Boolean optionCanEditValues) {
        this.optionCanEditValues = optionCanEditValues;
    }

    public Boolean getOptionHideIfEmpty() {
        return optionHideIfEmpty;
    }

    public void setOptionHideIfEmpty(Boolean optionHideIfEmpty) {
        this.optionHideIfEmpty = optionHideIfEmpty;
    }

    public String getOptionDisplayFieldName() {
        return optionDisplayFieldName;
    }

    public void setOptionDisplayFieldName(String optionDisplayFieldName) {
        this.optionDisplayFieldName = optionDisplayFieldName;
    }

    public String getOptionListEntity() {
        return optionListEntity;
    }

    public void setOptionListEntity(String optionListEntity) {
        this.optionListEntity = optionListEntity;
    }

    public String getOptionValueFieldName() {
        return optionValueFieldName;
    }

    public void setOptionValueFieldName(String optionValueFieldName) {
        this.optionValueFieldName = optionValueFieldName;
    }

    public String[][] getOptionFilterParams() {
        return optionFilterParams;
    }

    public void setOptionFilterParams(String[][] optionFilterParams) {
        this.optionFilterParams = optionFilterParams;
    }

    public String[] getCustomCriteria() {
        return customCriteria;
    }

    public void setCustomCriteria(String[] customCriteria) {
        this.customCriteria = customCriteria;
    }

    public Boolean getUseServerSideInspectionCache() {
        return useServerSideInspectionCache;
    }

    public void setUseServerSideInspectionCache(Boolean useServerSideInspectionCache) {
        this.useServerSideInspectionCache = useServerSideInspectionCache;
    }

    public Boolean getToOneLookupCreatedViaAnnotation() {
        return toOneLookupCreatedViaAnnotation;
    }

    public void setToOneLookupCreatedViaAnnotation(Boolean toOneLookupCreatedViaAnnotation) {
        this.toOneLookupCreatedViaAnnotation = toOneLookupCreatedViaAnnotation;
    }

    public String getRuleIdentifier() {
        return ruleIdentifier;
    }

    public void setRuleIdentifier(String ruleIdentifier) {
        this.ruleIdentifier = ruleIdentifier;
    }

    public String getMapFieldValueClass() {
        return mapFieldValueClass;
    }

    public void setMapFieldValueClass(String mapFieldValueClass) {
        this.mapFieldValueClass = mapFieldValueClass;
    }
    
    public LookupType getLookupType() {
        return lookupType;
    }

    public Boolean getSearchable() {
        return searchable;
    }

    public void setSearchable(Boolean searchable) {
        this.searchable = searchable;
    }

    public String getManyToField() {
        return manyToField;
    }

    public void setManyToField(String manyToField) {
        this.manyToField = manyToField;
    }

    public String getToOneTargetProperty() {
        return toOneTargetProperty;
    }

    public void setToOneTargetProperty(String toOneTargetProperty) {
        this.toOneTargetProperty = toOneTargetProperty;
    }

    public String getToOneParentProperty() {
        return toOneParentProperty;
    }

    public void setToOneParentProperty(String toOneParentProperty) {
        this.toOneParentProperty = toOneParentProperty;
    }

    public String getMapKeyValueProperty() {
        return mapKeyValueProperty;
    }

    public void setMapKeyValueProperty(String mapKeyValueProperty) {
        this.mapKeyValueProperty = mapKeyValueProperty;
    }

    public void setLookupType(LookupType lookupType) {
        this.lookupType = lookupType;
    }
    
    public Boolean getTranslatable() {
        return translatable;
    }
    
    public void setTranslatable(Boolean translatable) {
        this.translatable = translatable;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getAssociatedFieldName() {
        return associatedFieldName;
    }

    public void setAssociatedFieldName(String associatedFieldName) {
        this.associatedFieldName = associatedFieldName;
    }

    public Boolean getIsFilter() {
        return isFilter;
    }

    public void setIsFilter(Boolean isFilter) {
        this.isFilter = isFilter;
    }

    public Boolean getAllowNoValueEnumOption() {
        if (allowNoValueEnumOption==null || !allowNoValueEnumOption) {
            return StringUtils.isEmpty(getDefaultValue())
                    && (!getRequired() && !(getRequiredOverride() != null && getRequiredOverride()));
        }else{
            return allowNoValueEnumOption;
        }
    }

    public void setCanLinkToExternalEntity(Boolean canLinkToExternalEntity) {
        this.canLinkToExternalEntity = canLinkToExternalEntity;
    }

    public Boolean getCanLinkToExternalEntity() {
        return canLinkToExternalEntity;
    }

    public void setAllowNoValueEnumOption(Boolean allowNoValueEnumOption) {
        this.allowNoValueEnumOption = allowNoValueEnumOption;
    }

    @Override
    public FieldMetadata cloneFieldMetadata() {
        BasicFieldMetadata metadata = new BasicFieldMetadata();
        metadata.fieldType = fieldType;
        metadata.secondaryType = secondaryType;
        metadata.length = length;
        metadata.required = required;
        metadata.unique = unique;
        metadata.scale = scale;
        metadata.precision = precision;
        metadata.mutable = mutable;
        metadata.foreignKeyProperty = foreignKeyProperty;
        metadata.foreignKeyClass = foreignKeyClass;
        metadata.foreignKeyDisplayValueProperty = foreignKeyDisplayValueProperty;
        metadata.foreignKeyCollection = foreignKeyCollection;
        metadata.mergedPropertyType = mergedPropertyType;
        metadata.enumerationClass = enumerationClass;
        if (enumerationValues != null) {
            metadata.enumerationValues = new String[enumerationValues.length][];
            for (int j=0;j<enumerationValues.length;j++) {
                metadata.enumerationValues[j] = new String[enumerationValues[j].length];
                System.arraycopy(enumerationValues[j], 0, metadata.enumerationValues[j], 0, enumerationValues[j].length);
            }
        }

        metadata.name = name;
        metadata.visibility = visibility;
        metadata.groupCollapsed = groupCollapsed;
        metadata.explicitFieldType = explicitFieldType;
        metadata.displayType = displayType;
        metadata.largeEntry = largeEntry;
        metadata.prominent = prominent;
        metadata.gridOrder = gridOrder;        
        metadata.columnWidth = columnWidth;
        metadata.broadleafEnumeration = broadleafEnumeration;
        metadata.hideEnumerationIfEmpty = hideEnumerationIfEmpty;
        metadata.fieldComponentRenderer = fieldComponentRenderer;
        metadata.fieldComponentRendererTemplate = fieldComponentRendererTemplate;
        metadata.gridFieldComponentRenderer = gridFieldComponentRenderer;
        metadata.gridFieldComponentRendererTemplate = gridFieldComponentRendererTemplate;
        metadata.readOnly = readOnly;
        metadata.requiredOverride = requiredOverride;
        metadata.tooltip = tooltip;
        metadata.helpText = helpText;
        metadata.hint = hint;
        for (Map.Entry<String, List<Map<String, String>>> entry : validationConfigurations.entrySet()) {
            List<Map<String, String>> clonedConfigItems = new ArrayList<>(entry.getValue().size());
            
            for (Map<String, String> configEntries : entry.getValue()) {
                Map<String, String> clone = new HashMap<>(configEntries.keySet().size());
                for (Map.Entry<String, String> entry2 : configEntries.entrySet()) {
                    clone.put(entry2.getKey(), entry2.getValue());
                }
                clonedConfigItems.add(clone);
            }
            metadata.validationConfigurations.put(entry.getKey(), clonedConfigItems);
        }
        metadata.lookupDisplayProperty = lookupDisplayProperty;
        metadata.forcePopulateChildProperties = forcePopulateChildProperties;
        metadata.enableTypeaheadLookup = enableTypeaheadLookup;
        metadata.optionListEntity = optionListEntity;
        metadata.optionCanEditValues = optionCanEditValues;
        metadata.optionHideIfEmpty = optionHideIfEmpty;
        metadata.optionDisplayFieldName = optionDisplayFieldName;
        metadata.optionValueFieldName = optionValueFieldName;
        if (optionFilterParams != null) {
            metadata.optionFilterParams = new String[optionFilterParams.length][];
            for (int j=0;j<optionFilterParams.length;j++) {
                metadata.optionFilterParams[j] = new String[optionFilterParams[j].length];
                System.arraycopy(optionFilterParams[j], 0, metadata.optionFilterParams[j], 0, optionFilterParams[j].length);
            }
        }
        metadata.customCriteria = customCriteria;
        metadata.useServerSideInspectionCache = useServerSideInspectionCache;
        metadata.toOneLookupCreatedViaAnnotation = toOneLookupCreatedViaAnnotation;
        metadata.ruleIdentifier = ruleIdentifier;
        metadata.mapFieldValueClass = mapFieldValueClass;
        metadata.searchable = searchable;
        metadata.manyToField = manyToField;
        metadata.toOneTargetProperty = toOneTargetProperty;
        metadata.toOneParentProperty = toOneParentProperty;
        metadata.mapKeyValueProperty = mapKeyValueProperty;
        metadata.lookupType = lookupType;
        metadata.translatable = translatable;
        metadata.isDerived = isDerived;
        metadata.defaultValue = defaultValue;
        metadata.associatedFieldName = associatedFieldName;
        metadata.canLinkToExternalEntity = canLinkToExternalEntity;
        metadata.allowNoValueEnumOption = allowNoValueEnumOption;

        metadata = (BasicFieldMetadata) populate(metadata);

        return metadata;
    }

    @Override
    public void accept(MetadataVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!getClass().isAssignableFrom(o.getClass())) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        BasicFieldMetadata metadata = (BasicFieldMetadata) o;

        if (broadleafEnumeration != null ? !broadleafEnumeration.equals(metadata.broadleafEnumeration) : metadata.broadleafEnumeration != null) {
            return false;
        }
        if (hideEnumerationIfEmpty != null ? !hideEnumerationIfEmpty.equals(metadata.hideEnumerationIfEmpty) : metadata.hideEnumerationIfEmpty != null) {
            return false;
        }
        if (fieldComponentRenderer != null ? !fieldComponentRenderer.equals(metadata.fieldComponentRenderer) : metadata.fieldComponentRenderer != null) {
            return false;
        }
        if (fieldComponentRendererTemplate != null ? !fieldComponentRendererTemplate.equals(metadata.fieldComponentRendererTemplate) : metadata.fieldComponentRendererTemplate != null) {
            return false;
        }
        if (gridFieldComponentRenderer != null ? !gridFieldComponentRenderer.equals(metadata.gridFieldComponentRenderer) : metadata.gridFieldComponentRenderer != null) {
            return false;
        }
        if (gridFieldComponentRendererTemplate != null ? !gridFieldComponentRendererTemplate.equals(metadata.gridFieldComponentRendererTemplate) : metadata.gridFieldComponentRendererTemplate != null) {
            return false;
        }
        if (columnWidth != null ? !columnWidth.equals(metadata.columnWidth) : metadata.columnWidth != null) {
            return false;
        }
        if (enumerationClass != null ? !enumerationClass.equals(metadata.enumerationClass) : metadata.enumerationClass != null) {
            return false;
        }
        if (explicitFieldType != metadata.explicitFieldType) {
            return false;
        }
        if (displayType != metadata.displayType) {
            return false;
        }
        if (fieldType != metadata.fieldType) {
            return false;
        }
        if (foreignKeyClass != null ? !foreignKeyClass.equals(metadata.foreignKeyClass) : metadata.foreignKeyClass != null) {
            return false;
        }
        if (foreignKeyCollection != null ? !foreignKeyCollection.equals(metadata.foreignKeyCollection) : metadata.foreignKeyCollection != null) {
            return false;
        }
        if (foreignKeyDisplayValueProperty != null ? !foreignKeyDisplayValueProperty.equals(metadata.foreignKeyDisplayValueProperty) : metadata.foreignKeyDisplayValueProperty != null) {
            return false;
        }
        if (foreignKeyProperty != null ? !foreignKeyProperty.equals(metadata.foreignKeyProperty) : metadata.foreignKeyProperty != null) {
            return false;
        }
        if (groupCollapsed != null ? !groupCollapsed.equals(metadata.groupCollapsed) : metadata.groupCollapsed != null) {
            return false;
        }
        if (helpText != null ? !helpText.equals(metadata.helpText) : metadata.helpText != null) {
            return false;
        }
        if (hint != null ? !hint.equals(metadata.hint) : metadata.hint != null) {
            return false;
        }
        if (largeEntry != null ? !largeEntry.equals(metadata.largeEntry) : metadata.largeEntry != null) {
            return false;
        }
        if (length != null ? !length.equals(metadata.length) : metadata.length != null) {
            return false;
        }
        if (lookupDisplayProperty != null ? !lookupDisplayProperty.equals(metadata.lookupDisplayProperty) : metadata.lookupDisplayProperty != null) {
            return false;
        }
        if (forcePopulateChildProperties != null ? !forcePopulateChildProperties.equals(metadata.forcePopulateChildProperties) : metadata.forcePopulateChildProperties != null) {
            return false;
        }
        if (enableTypeaheadLookup != null ? !enableTypeaheadLookup.equals(metadata.enableTypeaheadLookup) : metadata.enableTypeaheadLookup != null) {
            return false;
        }
        if (mergedPropertyType != metadata.mergedPropertyType) {
            return false;
        }
        if (mutable != null ? !mutable.equals(metadata.mutable) : metadata.mutable != null) {
            return false;
        }
        if (name != null ? !name.equals(metadata.name) : metadata.name != null) {
            return false;
        }
        if (optionCanEditValues != null ? !optionCanEditValues.equals(metadata.optionCanEditValues) : metadata.optionCanEditValues != null) {
            return false;
        }
        if (optionHideIfEmpty != null ? !optionHideIfEmpty.equals(metadata.optionHideIfEmpty) : metadata.optionHideIfEmpty != null) {
            return false;
        }
        if (optionDisplayFieldName != null ? !optionDisplayFieldName.equals(metadata.optionDisplayFieldName) : metadata.optionDisplayFieldName != null) {
            return false;
        }
        if (optionListEntity != null ? !optionListEntity.equals(metadata.optionListEntity) : metadata.optionListEntity != null) {
            return false;
        }
        if (optionValueFieldName != null ? !optionValueFieldName.equals(metadata.optionValueFieldName) : metadata.optionValueFieldName != null) {
            return false;
        }
        if (precision != null ? !precision.equals(metadata.precision) : metadata.precision != null) {
            return false;
        }
        if (prominent != null ? !prominent.equals(metadata.prominent) : metadata.prominent != null) {
            return false;
        }
        if (gridOrder != null ? !gridOrder.equals(metadata.gridOrder) : metadata.gridOrder != null) {
            return false;
        }
        if (readOnly != null ? !readOnly.equals(metadata.readOnly) : metadata.readOnly != null) {
            return false;
        }
        if (required != null ? !required.equals(metadata.required) : metadata.required != null) {
            return false;
        }
        if (requiredOverride != null ? !requiredOverride.equals(metadata.requiredOverride) : metadata.requiredOverride != null) {
            return false;
        }
        if (scale != null ? !scale.equals(metadata.scale) : metadata.scale != null) {
            return false;
        }
        if (secondaryType != metadata.secondaryType) {
            return false;
        }
        if (tooltip != null ? !tooltip.equals(metadata.tooltip) : metadata.tooltip != null) {
            return false;
        }
        if (unique != null ? !unique.equals(metadata.unique) : metadata.unique != null) {
            return false;
        }
        if (validationConfigurations != null ? !validationConfigurations.equals(metadata.validationConfigurations) : metadata.validationConfigurations != null) {
            return false;
        }
        if (visibility != metadata.visibility) {
            return false;
        }
        if (ruleIdentifier != null ? !ruleIdentifier.equals(metadata.ruleIdentifier) : metadata.ruleIdentifier != null) {
            return false;
        }
        if (mapFieldValueClass != null ? !mapFieldValueClass.equals(metadata.mapFieldValueClass) : metadata.mapFieldValueClass != null) {
            return false;
        }
        if (searchable != null ? !searchable.equals(metadata.searchable) : metadata.searchable != null) {
            return false;
        }
        if (manyToField != null ? !manyToField.equals(metadata.manyToField) : metadata.manyToField != null) {
            return false;
        }
        if (toOneTargetProperty != null ? !toOneTargetProperty.equals(metadata.toOneTargetProperty) : metadata.toOneTargetProperty != null) {
            return false;
        }
        if (toOneParentProperty != null ? !toOneParentProperty.equals(metadata.toOneParentProperty) : metadata.toOneParentProperty != null) {
            return false;
        }
        if (mapKeyValueProperty != null ? !mapKeyValueProperty.equals(metadata.mapKeyValueProperty) : metadata.mapKeyValueProperty != null) {
            return false;
        }
        if (lookupType != null ? !lookupType.equals(metadata.lookupType) : metadata.lookupType != null) {
            return false;
        }
        if (isDerived != null ? !isDerived.equals(metadata.isDerived) : metadata.isDerived != null) {
            return false;
        }
        if (canLinkToExternalEntity != null ? !canLinkToExternalEntity.equals(metadata.canLinkToExternalEntity) : metadata.canLinkToExternalEntity != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (fieldType != null ? fieldType.hashCode() : 0);
        result = 31 * result + (secondaryType != null ? secondaryType.hashCode() : 0);
        result = 31 * result + (length != null ? length.hashCode() : 0);
        result = 31 * result + (required != null ? required.hashCode() : 0);
        result = 31 * result + (unique != null ? unique.hashCode() : 0);
        result = 31 * result + (scale != null ? scale.hashCode() : 0);
        result = 31 * result + (precision != null ? precision.hashCode() : 0);
        result = 31 * result + (mutable != null ? mutable.hashCode() : 0);
        result = 31 * result + (foreignKeyProperty != null ? foreignKeyProperty.hashCode() : 0);
        result = 31 * result + (foreignKeyClass != null ? foreignKeyClass.hashCode() : 0);
        result = 31 * result + (foreignKeyDisplayValueProperty != null ? foreignKeyDisplayValueProperty.hashCode() : 0);
        result = 31 * result + (foreignKeyCollection != null ? foreignKeyCollection.hashCode() : 0);
        result = 31 * result + (mergedPropertyType != null ? mergedPropertyType.hashCode() : 0);
        result = 31 * result + (enumerationClass != null ? enumerationClass.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (visibility != null ? visibility.hashCode() : 0);
        result = 31 * result + (groupCollapsed != null ? groupCollapsed.hashCode() : 0);
        result = 31 * result + (explicitFieldType != null ? explicitFieldType.hashCode() : 0);
        result = 31 * result + (displayType != null ? displayType.hashCode() : 0);
        result = 31 * result + (largeEntry != null ? largeEntry.hashCode() : 0);
        result = 31 * result + (prominent != null ? prominent.hashCode() : 0);
        result = 31 * result + (gridOrder != null ? gridOrder.hashCode() : 0);
        result = 31 * result + (columnWidth != null ? columnWidth.hashCode() : 0);
        result = 31 * result + (broadleafEnumeration != null ? broadleafEnumeration.hashCode() : 0);
        result = 31 * result + (hideEnumerationIfEmpty != null ? hideEnumerationIfEmpty.hashCode() : 0);
        result = 31 * result + (fieldComponentRenderer != null ? fieldComponentRenderer.hashCode() : 0);
        result = 31 * result + (fieldComponentRendererTemplate != null ? fieldComponentRendererTemplate.hashCode() : 0);
        result = 31 * result + (gridFieldComponentRenderer != null ? gridFieldComponentRenderer.hashCode() : 0);
        result = 31 * result + (gridFieldComponentRendererTemplate != null ? gridFieldComponentRendererTemplate.hashCode() : 0);
        result = 31 * result + (readOnly != null ? readOnly.hashCode() : 0);
        result = 31 * result + (validationConfigurations != null ? validationConfigurations.hashCode() : 0);
        result = 31 * result + (requiredOverride != null ? requiredOverride.hashCode() : 0);
        result = 31 * result + (tooltip != null ? tooltip.hashCode() : 0);
        result = 31 * result + (helpText != null ? helpText.hashCode() : 0);
        result = 31 * result + (hint != null ? hint.hashCode() : 0);
        result = 31 * result + (lookupDisplayProperty != null ? lookupDisplayProperty.hashCode() : 0);
        result = 31 * result + (forcePopulateChildProperties != null ? forcePopulateChildProperties.hashCode() : 0);
        result = 31 * result + (enableTypeaheadLookup != null ? enableTypeaheadLookup.hashCode() : 0);
        result = 31 * result + (optionListEntity != null ? optionListEntity.hashCode() : 0);
        result = 31 * result + (optionValueFieldName != null ? optionValueFieldName.hashCode() : 0);
        result = 31 * result + (optionDisplayFieldName != null ? optionDisplayFieldName.hashCode() : 0);
        result = 31 * result + (optionCanEditValues != null ? optionCanEditValues.hashCode() : 0);
        result = 31 * result + (optionHideIfEmpty != null ? optionHideIfEmpty.hashCode() : 0);
        result = 31 * result + (ruleIdentifier != null ? ruleIdentifier.hashCode() : 0);
        result = 31 * result + (mapFieldValueClass != null ? mapFieldValueClass.hashCode() : 0);
        result = 31 * result + (searchable != null ? searchable.hashCode() : 0);
        result = 31 * result + (manyToField != null ? manyToField.hashCode() : 0);
        result = 31 * result + (toOneTargetProperty != null ? toOneTargetProperty.hashCode() : 0);
        result = 31 * result + (toOneParentProperty != null ? toOneParentProperty.hashCode() : 0);
        result = 31 * result + (mapKeyValueProperty != null ? mapKeyValueProperty.hashCode() : 0);
        result = 31 * result + (lookupType != null ? lookupType.hashCode() : 0);
        result = 31 * result + (isDerived != null ? isDerived.hashCode() : 0);
        result = 31 * result + (canLinkToExternalEntity != null ? canLinkToExternalEntity.hashCode() : 0);
        return result;
    }


}
