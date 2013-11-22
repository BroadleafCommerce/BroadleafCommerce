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
package org.broadleafcommerce.openadmin.dto;

import org.broadleafcommerce.common.presentation.client.LookupType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.openadmin.dto.visitor.MetadataVisitor;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.PropertyValidator;

import java.util.HashMap;
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
    protected String group;
    protected Integer groupOrder;
    protected Boolean groupCollapsed;
    protected SupportedFieldType explicitFieldType;
    protected Boolean largeEntry;
    protected Boolean prominent;
    protected Integer gridOrder;
    protected String columnWidth;
    protected String broadleafEnumeration;
    protected Boolean readOnly;
    protected Map<String, Map<String, String>> validationConfigurations = new HashMap<String, Map<String, String>>(5);
    protected Boolean requiredOverride;
    protected String tooltip;
    protected String helpText;
    protected String hint;
    protected String lookupDisplayProperty;
    protected Boolean forcePopulateChildProperties;
    protected String optionListEntity;
    protected String optionValueFieldName;
    protected String optionDisplayFieldName;
    protected Boolean optionCanEditValues;
    protected String[][] optionFilterParams;
    protected String[] customCriteria;
    protected Boolean useServerSideInspectionCache;
    protected Boolean toOneLookupCreatedViaAnnotation;
    protected String ruleIdentifier;
    protected LookupType lookupType;
    protected Boolean translatable;

    //for MapFields
    protected String mapFieldValueClass;
    protected Boolean searchable;
    protected String manyToField;

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
        return foreignKeyCollection;
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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
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

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public Integer getGroupOrder() {
        return groupOrder;
    }

    public void setGroupOrder(Integer groupOrder) {
        this.groupOrder = groupOrder;
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
    public Map<String, Map<String, String>> getValidationConfigurations() {
        return validationConfigurations;
    }

    public void setValidationConfigurations(Map<String, Map<String, String>> validationConfigurations) {
        this.validationConfigurations = validationConfigurations;
    }

    public Boolean getRequiredOverride() {
        return requiredOverride;
    }

    public void setRequiredOverride(Boolean requiredOverride) {
        this.requiredOverride = requiredOverride;
    }

    public Boolean getGroupCollapsed() {
        return groupCollapsed;
    }

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

    public Boolean getOptionCanEditValues() {
        return optionCanEditValues;
    }

    public void setOptionCanEditValues(Boolean optionCanEditValues) {
        this.optionCanEditValues = optionCanEditValues;
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

    public void setLookupType(LookupType lookupType) {
        this.lookupType = lookupType;
    }
    
    public Boolean getTranslatable() {
        return translatable;
    }
    
    public void setTranslatable(Boolean translatable) {
        this.translatable = translatable;
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
        metadata.group = group;
        metadata.groupOrder = groupOrder;
        metadata.groupCollapsed = groupCollapsed;
        metadata.setTab(getTab());
        metadata.setTabOrder(getTabOrder());
        metadata.explicitFieldType = explicitFieldType;
        metadata.largeEntry = largeEntry;
        metadata.prominent = prominent;
        metadata.gridOrder = gridOrder;        
        metadata.columnWidth = columnWidth;
        metadata.broadleafEnumeration = broadleafEnumeration;
        metadata.readOnly = readOnly;
        metadata.requiredOverride = requiredOverride;
        metadata.tooltip = tooltip;
        metadata.helpText = helpText;
        metadata.hint = hint;
        for (Map.Entry<String, Map<String, String>> entry : validationConfigurations.entrySet()) {
            Map<String, String> clone = new HashMap<String, String>(entry.getValue().size());
            for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
                clone.put(entry2.getKey(), entry2.getValue());
            }
            metadata.validationConfigurations.put(entry.getKey(), clone);
        }
        metadata.lookupDisplayProperty = lookupDisplayProperty;
        metadata.forcePopulateChildProperties = forcePopulateChildProperties;
        metadata.optionListEntity = optionListEntity;
        metadata.optionCanEditValues = optionCanEditValues;
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
        metadata.lookupType = lookupType;
        metadata.translatable = translatable;
        metadata.isDerived = isDerived;

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
        if (!(o instanceof BasicFieldMetadata)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        BasicFieldMetadata metadata = (BasicFieldMetadata) o;

        if (broadleafEnumeration != null ? !broadleafEnumeration.equals(metadata.broadleafEnumeration) : metadata.broadleafEnumeration != null) {
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
        if (group != null ? !group.equals(metadata.group) : metadata.group != null) {
            return false;
        }
        if (groupCollapsed != null ? !groupCollapsed.equals(metadata.groupCollapsed) : metadata.groupCollapsed != null) {
            return false;
        }
        if (groupOrder != null ? !groupOrder.equals(metadata.groupOrder) : metadata.groupOrder != null) {
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
        if (lookupType != null ? !lookupType.equals(metadata.lookupType) : metadata.lookupType != null) {
            return false;
        }
        if (isDerived != null ? !isDerived.equals(metadata.isDerived) : metadata.isDerived != null) {
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
        result = 31 * result + (group != null ? group.hashCode() : 0);
        result = 31 * result + (groupOrder != null ? groupOrder.hashCode() : 0);
        result = 31 * result + (groupCollapsed != null ? groupCollapsed.hashCode() : 0);
        result = 31 * result + (explicitFieldType != null ? explicitFieldType.hashCode() : 0);
        result = 31 * result + (largeEntry != null ? largeEntry.hashCode() : 0);
        result = 31 * result + (prominent != null ? prominent.hashCode() : 0);
        result = 31 * result + (gridOrder != null ? gridOrder.hashCode() : 0);
        result = 31 * result + (columnWidth != null ? columnWidth.hashCode() : 0);
        result = 31 * result + (broadleafEnumeration != null ? broadleafEnumeration.hashCode() : 0);
        result = 31 * result + (readOnly != null ? readOnly.hashCode() : 0);
        result = 31 * result + (validationConfigurations != null ? validationConfigurations.hashCode() : 0);
        result = 31 * result + (requiredOverride != null ? requiredOverride.hashCode() : 0);
        result = 31 * result + (tooltip != null ? tooltip.hashCode() : 0);
        result = 31 * result + (helpText != null ? helpText.hashCode() : 0);
        result = 31 * result + (hint != null ? hint.hashCode() : 0);
        result = 31 * result + (lookupDisplayProperty != null ? lookupDisplayProperty.hashCode() : 0);
        result = 31 * result + (forcePopulateChildProperties != null ? forcePopulateChildProperties.hashCode() : 0);
        result = 31 * result + (optionListEntity != null ? optionListEntity.hashCode() : 0);
        result = 31 * result + (optionValueFieldName != null ? optionValueFieldName.hashCode() : 0);
        result = 31 * result + (optionDisplayFieldName != null ? optionDisplayFieldName.hashCode() : 0);
        result = 31 * result + (optionCanEditValues != null ? optionCanEditValues.hashCode() : 0);
        result = 31 * result + (ruleIdentifier != null ? ruleIdentifier.hashCode() : 0);
        result = 31 * result + (mapFieldValueClass != null ? mapFieldValueClass.hashCode() : 0);
        result = 31 * result + (searchable != null ? searchable.hashCode() : 0);
        result = 31 * result + (manyToField != null ? manyToField.hashCode() : 0);
        result = 31 * result + (lookupType != null ? lookupType.hashCode() : 0);
        result = 31 * result + (isDerived != null ? isDerived.hashCode() : 0);
        return result;
    }


}
