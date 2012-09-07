package org.broadleafcommerce.openadmin.client.dto;

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.openadmin.client.dto.visitor.MetadataVisitor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
public class BasicFieldMetadata extends FieldMetadata {

    private SupportedFieldType fieldType;
    private SupportedFieldType secondaryType = SupportedFieldType.INTEGER;
    private Integer length;
    private Boolean required;
    private Boolean unique;
    private Integer scale;
    private Integer precision;
    private Boolean mutable;
    private String foreignKeyProperty;
    private String foreignKeyClass;
    private String foreignKeyDisplayValueProperty;
    private Boolean foreignKeyCollection;
    private MergedPropertyType mergedPropertyType;
    private String[][] enumerationValues;
    private String enumerationClass;

    //@AdminPresentation derived fields
    private String name;
    private VisibilityEnum visibility;
    private String group;
    private Integer groupOrder;
    private Boolean groupCollapsed;
    private SupportedFieldType explicitFieldType;
    private Boolean largeEntry;
    private Boolean prominent;
    private String columnWidth;
    private String broadleafEnumeration;
    private Boolean readOnly;
    private Map<String, Map<String, String>> validationConfigurations = new HashMap<String, Map<String, String>>(5);
    private Boolean requiredOverride;
    private String tooltip;
    private String helpText;
    private String hint;
    private String lookupDisplayProperty;
    private String lookupParentDataSourceName;
    private String targetDynamicFormDisplayId;
    private String optionListEntity;
    private String optionValueFieldName;
    private String optionDisplayFieldName;
    private Boolean optionCanEditValues;
    private String[][] optionFilterParams;

    //temporary fields
    private String targetClass;

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

    public String getLookupParentDataSourceName() {
        return lookupParentDataSourceName;
    }

    public void setLookupParentDataSourceName(String lookupParentDataSourceName) {
        this.lookupParentDataSourceName = lookupParentDataSourceName;
    }

    public String getTargetDynamicFormDisplayId() {
        return targetDynamicFormDisplayId;
    }

    public void setTargetDynamicFormDisplayId(String targetDynamicFormDisplayId) {
        this.targetDynamicFormDisplayId = targetDynamicFormDisplayId;
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

    public String getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(String targetClass) {
        this.targetClass = targetClass;
    }

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
        metadata.explicitFieldType = explicitFieldType;
        metadata.largeEntry = largeEntry;
        metadata.prominent = prominent;
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
        metadata.lookupParentDataSourceName = lookupParentDataSourceName;
        metadata.targetDynamicFormDisplayId = targetDynamicFormDisplayId;
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
        metadata.targetClass = targetClass;

        metadata = (BasicFieldMetadata) populate(metadata);

        return metadata;
    }

    @Override
    public void accept(MetadataVisitor visitor) {
        visitor.visit(this);
    }
}
