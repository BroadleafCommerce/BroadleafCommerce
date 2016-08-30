/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.dto;

import org.broadleafcommerce.openadmin.dto.visitor.MetadataVisitor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public abstract class FieldMetadata implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String inheritedFromType;
    private String[] availableToTypes;
    private Boolean excluded;
    private String friendlyName;
    private String addFriendlyName;
    private String securityLevel;
    private Integer order;
    private String owningClassFriendlyName;

    private String tab;
    @Deprecated
    private Integer tabOrder;
    private String group;
    @Deprecated
    private Integer groupOrder;
    private Boolean lazyFetch;
    private Boolean manualFetch;
    
    //temporary fields
    private Boolean childrenExcluded;
    private String targetClass;
    private String owningClass;
    private String prefix;
    private String fieldName;
    private String showIfProperty;
    private Map<String, List<String>> showIfFieldEquals;
    private String currencyCodeField;

    //Additional metadata not supported as first class
    private Map<String, Object> additionalMetadata = new HashMap<String, Object>();

    public String[] getAvailableToTypes() {
        return availableToTypes;
    }

    public void setAvailableToTypes(String[] availableToTypes) {
        Arrays.sort(availableToTypes);
        this.availableToTypes = availableToTypes;
    }

    public String getInheritedFromType() {
        return inheritedFromType;
    }

    public void setInheritedFromType(String inheritedFromType) {
        this.inheritedFromType = inheritedFromType;
    }

    public Boolean getExcluded() {
        return excluded;
    }

    public void setExcluded(Boolean excluded) {
        this.excluded = excluded;
    }

    public Map<String, Object> getAdditionalMetadata() {
        return additionalMetadata;
    }

    public void setAdditionalMetadata(Map<String, Object> additionalMetadata) {
        this.additionalMetadata = additionalMetadata;
    }

    protected FieldMetadata populate(FieldMetadata metadata) {
        metadata.inheritedFromType = inheritedFromType;
        if (availableToTypes != null) {
            metadata.availableToTypes = new String[availableToTypes.length];
            System.arraycopy(availableToTypes, 0, metadata.availableToTypes, 0, availableToTypes.length);
        }
        metadata.excluded = excluded;
        metadata.friendlyName = friendlyName;
        metadata.addFriendlyName = addFriendlyName;
        metadata.owningClassFriendlyName = owningClassFriendlyName;
        metadata.securityLevel = securityLevel;
        metadata.order = order;
        metadata.group = group;
        metadata.groupOrder = groupOrder;
        metadata.tab = tab;
        metadata.tabOrder = tabOrder;
        metadata.targetClass = targetClass;
        metadata.owningClass = owningClass;
        metadata.prefix = prefix;
        metadata.childrenExcluded = childrenExcluded;
        metadata.fieldName = fieldName;
        metadata.showIfProperty = showIfProperty;
        metadata.showIfFieldEquals = showIfFieldEquals;
        metadata.currencyCodeField = currencyCodeField;
        metadata.lazyFetch = lazyFetch;
        metadata.manualFetch = manualFetch;
        for (Map.Entry<String, Object> entry : additionalMetadata.entrySet()) {
            metadata.additionalMetadata.put(entry.getKey(), entry.getValue());
        }
        return metadata;
    }

    public String getShowIfProperty() {
        return showIfProperty;
    }

    public void setShowIfProperty(String showIfProperty) {
        this.showIfProperty = showIfProperty;
    }

    public Map<String, List<String>> getShowIfFieldEquals() {
        return showIfFieldEquals;
    }

    public void setShowIfFieldEquals(Map<String, List<String>> showIfFieldEquals) {
        this.showIfFieldEquals = showIfFieldEquals;
    }

    public String getCurrencyCodeField() {
        return currencyCodeField;
    }

    public void setCurrencyCodeField(String currencyCodeField) {
        this.currencyCodeField = currencyCodeField;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getAddFriendlyName() {
        return addFriendlyName;
    }

    public void setAddFriendlyName(String addFriendlyName) {
        this.addFriendlyName = addFriendlyName;
    }

    public String getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(String targetClass) {
        this.targetClass = targetClass;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getOwningClassFriendlyName() {
        return owningClassFriendlyName;
    }

    public void setOwningClassFriendlyName(String owningClassFriendlyName) {
        this.owningClassFriendlyName = owningClassFriendlyName;
    }

    public String getOwningClass() {
        return owningClass;
    }

    public void setOwningClass(String owningClass) {
        this.owningClass = owningClass;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Boolean getChildrenExcluded() {
        return childrenExcluded;
    }

    public void setChildrenExcluded(Boolean childrenExcluded) {
        this.childrenExcluded = childrenExcluded;
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    @Deprecated
    public Integer getTabOrder() {
        return tabOrder;
    }

    @Deprecated
    public void setTabOrder(Integer tabOrder) {
        this.tabOrder = tabOrder;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Deprecated
    public Integer getGroupOrder() {
        return groupOrder;
    }

    @Deprecated
    public void setGroupOrder(Integer groupOrder) {
        this.groupOrder = groupOrder;
    }

    public Boolean getLazyFetch() {
        return lazyFetch;
    }

    public void setLazyFetch(Boolean lazyFetch) {
        this.lazyFetch = lazyFetch;
    }

    public boolean getManualFetch() {
        if (manualFetch == null) {
            return false;
        }
        return manualFetch;
    }

    public void setManualFetch(boolean manualFetch) {
        this.manualFetch = manualFetch;
    }

    public abstract FieldMetadata cloneFieldMetadata();

    public abstract void accept(MetadataVisitor visitor);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!getClass().isAssignableFrom(o.getClass())) return false;

        FieldMetadata that = (FieldMetadata) o;

        if (additionalMetadata != null ? !additionalMetadata.equals(that.additionalMetadata) : that
                .additionalMetadata != null)
            return false;
        if (!Arrays.equals(availableToTypes, that.availableToTypes)) return false;
        if (childrenExcluded != null ? !childrenExcluded.equals(that.childrenExcluded) : that.childrenExcluded != null)
            return false;
        if (currencyCodeField != null ? !currencyCodeField.equals(that.currencyCodeField) : that.currencyCodeField !=
                null)
            return false;
        if (excluded != null ? !excluded.equals(that.excluded) : that.excluded != null) return false;
        if (fieldName != null ? !fieldName.equals(that.fieldName) : that.fieldName != null) return false;
        if (friendlyName != null ? !friendlyName.equals(that.friendlyName) : that.friendlyName != null) return false;
        if (inheritedFromType != null ? !inheritedFromType.equals(that.inheritedFromType) : that.inheritedFromType !=
                null)
            return false;
        if (order != null ? !order.equals(that.order) : that.order != null) return false;
        if (owningClass != null ? !owningClass.equals(that.owningClass) : that.owningClass != null) return false;
        if (owningClassFriendlyName != null ? !owningClassFriendlyName.equals(that.owningClassFriendlyName) : that
                .owningClassFriendlyName != null)
            return false;
        if (prefix != null ? !prefix.equals(that.prefix) : that.prefix != null) return false;
        if (securityLevel != null ? !securityLevel.equals(that.securityLevel) : that.securityLevel != null)
            return false;
        if (showIfProperty != null ? !showIfProperty.equals(that.showIfProperty) : that.showIfProperty != null)
            return false;
        if (showIfFieldEquals != null ? !showIfFieldEquals.equals(that.showIfFieldEquals) : that.showIfFieldEquals != null)
            return false;
        if (tab != null ? !tab.equals(that.tab) : that.tab != null) return false;
        if (tabOrder != null ? !tabOrder.equals(that.tabOrder) : that.tabOrder != null) return false;
        if (group != null ? !group.equals(that.group) : that.group != null) return false;
        if (groupOrder != null ? !groupOrder.equals(that.groupOrder) : that.groupOrder != null) return false;
        if (targetClass != null ? !targetClass.equals(that.targetClass) : that.targetClass != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = inheritedFromType != null ? inheritedFromType.hashCode() : 0;
        result = 31 * result + (availableToTypes != null ? Arrays.hashCode(availableToTypes) : 0);
        result = 31 * result + (excluded != null ? excluded.hashCode() : 0);
        result = 31 * result + (friendlyName != null ? friendlyName.hashCode() : 0);
        result = 31 * result + (securityLevel != null ? securityLevel.hashCode() : 0);
        result = 31 * result + (order != null ? order.hashCode() : 0);
        result = 31 * result + (owningClassFriendlyName != null ? owningClassFriendlyName.hashCode() : 0);
        result = 31 * result + (tab != null ? tab.hashCode() : 0);
        result = 31 * result + (tabOrder != null ? tabOrder.hashCode() : 0);
        result = 31 * result + (group != null ? group.hashCode() : 0);
        result = 31 * result + (groupOrder != null ? groupOrder.hashCode() : 0);
        result = 31 * result + (childrenExcluded != null ? childrenExcluded.hashCode() : 0);
        result = 31 * result + (targetClass != null ? targetClass.hashCode() : 0);
        result = 31 * result + (owningClass != null ? owningClass.hashCode() : 0);
        result = 31 * result + (prefix != null ? prefix.hashCode() : 0);
        result = 31 * result + (fieldName != null ? fieldName.hashCode() : 0);
        result = 31 * result + (showIfProperty != null ? showIfProperty.hashCode() : 0);
        result = 31 * result + (showIfFieldEquals != null ? showIfFieldEquals.hashCode() : 0);
        result = 31 * result + (currencyCodeField != null ? currencyCodeField.hashCode() : 0);
        result = 31 * result + (additionalMetadata != null ? additionalMetadata.hashCode() : 0);
        return result;
    }
}
