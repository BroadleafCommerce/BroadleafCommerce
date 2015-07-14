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
package org.broadleafcommerce.openadmin.dto.override;

import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.common.presentation.client.AdornedTargetAddMethodType;
import org.broadleafcommerce.common.presentation.client.LookupType;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.UnspecifiedBooleanType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.openadmin.dto.MergedPropertyType;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
public class FieldMetadataOverride extends MetadataOverride {

    //fields everyone depends on
    private Boolean excluded;
    private String friendlyName;
    private String securityLevel;

    public Boolean getExcluded() {
        return excluded;
    }

    public void setExcluded(Boolean excluded) {
        this.excluded = excluded;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }

    //basic fields
    private SupportedFieldType fieldType;
    private SupportedFieldType secondaryType = SupportedFieldType.INTEGER;
    private Integer length;
    private Boolean required;
    private Boolean unique;
    private Integer scale;
    private Integer precision;
    private String foreignKeyProperty;
    private String foreignKeyClass;
    private String foreignKeyDisplayValueProperty;
    private Boolean foreignKeyCollection;
    private MergedPropertyType mergedPropertyType;
    private String[][] enumerationValues;
    private String enumerationClass;
    protected Boolean isDerived;

    //@AdminPresentation derived fields
    private VisibilityEnum visibility;
    private String group;
    private Integer groupOrder;
    protected Integer gridOrder;
    private String tab;
    private Integer tabOrder;
    private Boolean groupCollapsed;
    private SupportedFieldType explicitFieldType;
    private Boolean largeEntry;
    private Boolean prominent;
    private String columnWidth;
    private String broadleafEnumeration;
    private Boolean readOnly;
    private Map<String, Map<String, String>> validationConfigurations;
    private Boolean requiredOverride;
    private String tooltip;
    private String helpText;
    private String hint;
    private String lookupDisplayProperty;
    private Boolean forcePopulateChildProperties;
    private Boolean enableTypeaheadLookup;
    private String optionListEntity;
    private String optionValueFieldName;
    private String optionDisplayFieldName;
    private Boolean optionCanEditValues;
    private Serializable[][] optionFilterValues;
    private String showIfProperty;
    private String ruleIdentifier;
    private Boolean translatable;
    private LookupType lookupType;
    private String defaultValue;

    //@AdminPresentationMapField derived fields
    private Boolean searchable;
    private String mapFieldValueClass;

    //Not a user definable field
    private Boolean toOneLookupCreatedViaAnnotation;

    public Boolean getToOneLookupCreatedViaAnnotation() {
        return toOneLookupCreatedViaAnnotation;
    }

    public void setToOneLookupCreatedViaAnnotation(Boolean toOneLookupCreatedViaAnnotation) {
        this.toOneLookupCreatedViaAnnotation = toOneLookupCreatedViaAnnotation;
    }

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

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public Integer getTabOrder() {
        return tabOrder;
    }

    public void setTabOrder(Integer tabOrder) {
        this.tabOrder = tabOrder;
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

    public Serializable[][] getOptionFilterValues() {
        return optionFilterValues;
    }

    public void setOptionFilterValues(Serializable[][] optionFilterValues) {
        this.optionFilterValues = optionFilterValues;
    }

    public String getRuleIdentifier() {
        return ruleIdentifier;
    }

    public void setRuleIdentifier(String ruleIdentifier) {
        this.ruleIdentifier = ruleIdentifier;
    }

    public Boolean getSearchable() {
        return searchable;
    }

    public void setSearchable(Boolean searchable) {
        this.searchable = searchable;
    }

    public String getMapFieldValueClass() {
        return mapFieldValueClass;
    }

    public void setMapFieldValueClass(String mapFieldValueClass) {
        this.mapFieldValueClass = mapFieldValueClass;
    }

    //collection fields
    private String[] customCriteria;
    private OperationType addType;
    private OperationType removeType;
    private OperationType updateType;
    private OperationType fetchType;
    private OperationType inspectType;
    private Boolean useServerSideInspectionCache;

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

    public OperationType getAddType() {
        return addType;
    }

    public void setAddType(OperationType addType) {
        this.addType = addType;
    }

    public OperationType getFetchType() {
        return fetchType;
    }

    public void setFetchType(OperationType fetchType) {
        this.fetchType = fetchType;
    }

    public OperationType getInspectType() {
        return inspectType;
    }

    public void setInspectType(OperationType inspectType) {
        this.inspectType = inspectType;
    }

    public OperationType getRemoveType() {
        return removeType;
    }

    public void setRemoveType(OperationType removeType) {
        this.removeType = removeType;
    }

    public OperationType getUpdateType() {
        return updateType;
    }

    public void setUpdateType(OperationType updateType) {
        this.updateType = updateType;
    }

    //basic collection fields
    private AddMethodType addMethodType;
    private String manyToField;

    public AddMethodType getAddMethodType() {
        return addMethodType;
    }

    public void setAddMethodType(AddMethodType addMethodType) {
        this.addMethodType = addMethodType;
    }

    public String getManyToField() {
        return manyToField;
    }

    public void setManyToField(String manyToField) {
        this.manyToField = manyToField;
    }

    //Adorned target fields
    private String parentObjectProperty;
    private String parentObjectIdProperty;
    private String targetObjectProperty;
    private String[] maintainedAdornedTargetFields;
    private String[] gridVisibleFields;
    private String targetObjectIdProperty;
    private String joinEntityClass;
    private String sortProperty;
    private Boolean sortAscending;
    private Boolean ignoreAdornedProperties;
    private AdornedTargetAddMethodType adornedTargetAddMethodType;

    public String[] getGridVisibleFields() {
        return gridVisibleFields;
    }

    public void setGridVisibleFields(String[] gridVisibleFields) {
        this.gridVisibleFields = gridVisibleFields;
    }

    public Boolean isIgnoreAdornedProperties() {
        return ignoreAdornedProperties;
    }

    public void setIgnoreAdornedProperties(Boolean ignoreAdornedProperties) {
        this.ignoreAdornedProperties = ignoreAdornedProperties;
    }

    public String[] getMaintainedAdornedTargetFields() {
        return maintainedAdornedTargetFields;
    }

    public void setMaintainedAdornedTargetFields(String[] maintainedAdornedTargetFields) {
        this.maintainedAdornedTargetFields = maintainedAdornedTargetFields;
    }

    public String getParentObjectIdProperty() {
        return parentObjectIdProperty;
    }

    public void setParentObjectIdProperty(String parentObjectIdProperty) {
        this.parentObjectIdProperty = parentObjectIdProperty;
    }

    public String getParentObjectProperty() {
        return parentObjectProperty;
    }

    public void setParentObjectProperty(String parentObjectProperty) {
        this.parentObjectProperty = parentObjectProperty;
    }

    public Boolean isSortAscending() {
        return sortAscending;
    }

    public void setSortAscending(Boolean sortAscending) {
        this.sortAscending = sortAscending;
    }

    public String getSortProperty() {
        return sortProperty;
    }

    public void setSortProperty(String sortProperty) {
        this.sortProperty = sortProperty;
    }

    public String getTargetObjectIdProperty() {
        return targetObjectIdProperty;
    }

    public void setTargetObjectIdProperty(String targetObjectIdProperty) {
        this.targetObjectIdProperty = targetObjectIdProperty;
    }

    public String getJoinEntityClass() {
        return joinEntityClass;
    }

    public void setJoinEntityClass(String joinEntityClass) {
        this.joinEntityClass = joinEntityClass;
    }

    public String getTargetObjectProperty() {
        return targetObjectProperty;
    }

    public void setTargetObjectProperty(String targetObjectProperty) {
        this.targetObjectProperty = targetObjectProperty;
    }

    public AdornedTargetAddMethodType getAdornedTargetAddMethodType() {
        return adornedTargetAddMethodType;
    }

    public void setAdornedTargetAddMethodType(AdornedTargetAddMethodType adornedTargetAddMethodType) {
        this.adornedTargetAddMethodType = adornedTargetAddMethodType;
    }

    //Map fields
    private String keyClass;
    private String keyPropertyFriendlyName;
    private String valueClass;
    private Boolean deleteEntityUponRemove;
    private String valuePropertyFriendlyName;
    private UnspecifiedBooleanType isSimpleValue;
    private String mediaField;
    private String[][] keys;
    private String mapKeyValueProperty;
    private String mapKeyOptionEntityClass;
    private String mapKeyOptionEntityDisplayField;
    private String mapKeyOptionEntityValueField;
    private String currencyCodeField;
    private Boolean forceFreeFormKeys;
    private String toOneTargetProperty;
    private String toOneParentProperty;

    public Boolean isDeleteEntityUponRemove() {
        return deleteEntityUponRemove;
    }

    public void setDeleteEntityUponRemove(Boolean deleteEntityUponRemove) {
        this.deleteEntityUponRemove = deleteEntityUponRemove;
    }

    public UnspecifiedBooleanType getSimpleValue() {
        return isSimpleValue;
    }

    public void setSimpleValue(UnspecifiedBooleanType simpleValue) {
        isSimpleValue = simpleValue;
    }

    public String getKeyClass() {
        return keyClass;
    }

    public void setKeyClass(String keyClass) {
        this.keyClass = keyClass;
    }
    

    public String getKeyPropertyFriendlyName() {
        return keyPropertyFriendlyName;
    }

    public void setKeyPropertyFriendlyName(String keyPropertyFriendlyName) {
        this.keyPropertyFriendlyName = keyPropertyFriendlyName;
    }

    public String[][] getKeys() {
        return keys;
    }

    public void setKeys(String[][] keys) {
        this.keys = keys;
    }

    public String getMapKeyOptionEntityClass() {
        return mapKeyOptionEntityClass;
    }

    public void setMapKeyOptionEntityClass(String mapKeyOptionEntityClass) {
        this.mapKeyOptionEntityClass = mapKeyOptionEntityClass;
    }

    public String getMapKeyOptionEntityDisplayField() {
        return mapKeyOptionEntityDisplayField;
    }

    public void setMapKeyOptionEntityDisplayField(String mapKeyOptionEntityDisplayField) {
        this.mapKeyOptionEntityDisplayField = mapKeyOptionEntityDisplayField;
    }

    public String getMapKeyOptionEntityValueField() {
        return mapKeyOptionEntityValueField;
    }

    public void setMapKeyOptionEntityValueField(String mapKeyOptionEntityValueField) {
        this.mapKeyOptionEntityValueField = mapKeyOptionEntityValueField;
    }

    public String getMediaField() {
        return mediaField;
    }

    public void setMediaField(String mediaField) {
        this.mediaField = mediaField;
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

    public String getValueClass() {
        return valueClass;
    }

    public void setValueClass(String valueClass) {
        this.valueClass = valueClass;
    }

    public String getValuePropertyFriendlyName() {
        return valuePropertyFriendlyName;
    }

    public void setValuePropertyFriendlyName(String valuePropertyFriendlyName) {
        this.valuePropertyFriendlyName = valuePropertyFriendlyName;
    }

    public String getShowIfProperty() {
        return showIfProperty;
    }

    public void setShowIfProperty(String showIfProperty) {
        this.showIfProperty = showIfProperty;
    }

    public String getCurrencyCodeField() {
        return currencyCodeField;
    }

    public void setCurrencyCodeField(String currencyCodeField) {
        this.currencyCodeField = currencyCodeField;
    }
    
    public LookupType getLookupType() {
        return lookupType;
    }

    public void setLookupType(LookupType lookupType) {
        this.lookupType = lookupType;
    }

    public Boolean getForceFreeFormKeys() {
        return forceFreeFormKeys;
    }

    public void setForceFreeFormKeys(Boolean forceFreeFormKeys) {
        this.forceFreeFormKeys = forceFreeFormKeys;
    }
    
    public String getMapKeyValueProperty() {
        return mapKeyValueProperty;
    }

    public void setMapKeyValueProperty(String mapKeyValueProperty) {
        this.mapKeyValueProperty = mapKeyValueProperty;
    }
    
}
