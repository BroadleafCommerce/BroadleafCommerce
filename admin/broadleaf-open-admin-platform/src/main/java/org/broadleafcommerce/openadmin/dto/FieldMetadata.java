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

import org.broadleafcommerce.openadmin.dto.visitor.MetadataVisitor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
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
    private String securityLevel;
    private Integer order;
    private String owningClassFriendlyName;

    private String tab;
    private Integer tabOrder;

    //temporary fields
    private Boolean childrenExcluded;
    private String targetClass;
    private String owningClass;
    private String prefix;
    private String fieldName;
    private String showIfProperty;
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
        metadata.owningClassFriendlyName = owningClassFriendlyName;
        metadata.securityLevel = securityLevel;
        metadata.order = order;
        metadata.targetClass = targetClass;
        metadata.owningClass = owningClass;
        metadata.prefix = prefix;
        metadata.childrenExcluded = childrenExcluded;
        metadata.fieldName = fieldName;
        metadata.showIfProperty = showIfProperty;
        metadata.currencyCodeField = currencyCodeField;
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

    public Integer getTabOrder() {
        return tabOrder;
    }

    public void setTabOrder(Integer tabOrder) {
        this.tabOrder = tabOrder;
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
        if (tab != null ? !tab.equals(that.tab) : that.tab != null) return false;
        if (tabOrder != null ? !tabOrder.equals(that.tabOrder) : that.tabOrder != null) return false;
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
        result = 31 * result + (childrenExcluded != null ? childrenExcluded.hashCode() : 0);
        result = 31 * result + (targetClass != null ? targetClass.hashCode() : 0);
        result = 31 * result + (owningClass != null ? owningClass.hashCode() : 0);
        result = 31 * result + (prefix != null ? prefix.hashCode() : 0);
        result = 31 * result + (fieldName != null ? fieldName.hashCode() : 0);
        result = 31 * result + (showIfProperty != null ? showIfProperty.hashCode() : 0);
        result = 31 * result + (currencyCodeField != null ? currencyCodeField.hashCode() : 0);
        result = 31 * result + (additionalMetadata != null ? additionalMetadata.hashCode() : 0);
        return result;
    }
}
