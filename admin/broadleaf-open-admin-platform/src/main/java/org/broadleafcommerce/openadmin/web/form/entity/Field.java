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

package org.broadleafcommerce.openadmin.web.form.entity;

import org.apache.commons.lang3.ArrayUtils;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;



/**
 * @author Andre Azzolini (apazzolini)
 */
public class Field {

    public static final String ALTERNATE_ORDERING = "AlternateOrdering";

    protected String name;
    protected String friendlyName;
    protected String fieldType;
    protected String value;
    protected String displayValue;
    protected String foreignKeyDisplayValueProperty;
    protected String foreignKeyClass;
    protected String owningEntityClass;
    protected String idOverride;
    protected Integer order;
    protected String onChangeTrigger;
    protected Boolean required = false;
    protected String columnWidth;
    protected Boolean isVisible;
    protected Boolean isAlternateOrdering;
    protected Boolean isReadOnly;
    protected Boolean isTranslatable;
    protected Boolean isMainEntityLink;
    protected Boolean isFilterSortDisabled;
    
    /* ************ */
    /* WITH METHODS */
    /* ************ */
    
    public Field withName(String name) {
        setName(name);
        return this;
    }
    
    public Field withFriendlyName(String friendlyName) {
        setFriendlyName(friendlyName);
        return this;
    }
    
    public Field withFieldType(String fieldType) {
        setFieldType(fieldType);
        return this;
    }
    
    public Field withValue(String value) {
        setValue(value);
        return this;
    }
    
    public Field withDisplayValue(String displayValue) {
        setDisplayValue(displayValue);
        return this;
    }
    
    public Field withForeignKeyDisplayValueProperty(String foreignKeyDisplayValueProperty) {
        setForeignKeyDisplayValueProperty(foreignKeyDisplayValueProperty);
        return this;
    }

    public Field withForeignKeyClass(String foreignKeyClass) {
        setForeignKeyClass(foreignKeyClass);
        return this;
    }

    public Field withOwningEntityClass(String owningEntityClass) {
        setOwningEntityClass(owningEntityClass);
        return this;
    }
    
    public Field withIdOverride(String idOverride) {
        setIdOverride(idOverride);
        return this;
    }

    public Field withOrder(Integer order) {
        setOrder(order);
        return this;
    }

    public Field withAlternateOrdering(Boolean alternateOrdering) {
        setAlternateOrdering(alternateOrdering);
        return this;
    }
    
    public Field withRequired(Boolean required) {
        setRequired(required);
        return this;
    }
    
    public Field withColumnWidth(String columnWidth) {
        setColumnWidth(columnWidth);
        return this;
    }
    
    public Field withReadOnly(Boolean isReadOnly) {
        setReadOnly(isReadOnly);
        return this;
    }
    
    public Field withTranslatable(Boolean isTranslatable) {
        setTranslatable(isTranslatable);
        return this;
    }
    
    public Field withMainEntityLink(Boolean isMainEntityLink) {
        setMainEntityLink(isMainEntityLink);
        return this;
    }
    
    public Field withFilterSortDisabled(Boolean isFilterSortDisabled) {
        setFilterSortDisabled(isFilterSortDisabled);
        return this;
    }

    /* ************************ */
    /* CUSTOM GETTERS / SETTERS */
    /* ************************ */

    public Boolean getIsVisible() {
        String[] invisibleTypes = new String[] {
                SupportedFieldType.ID.toString(),
                SupportedFieldType.HIDDEN.toString(),
                SupportedFieldType.FOREIGN_KEY.toString()
        };
        
        return isVisible == null ? !ArrayUtils.contains(invisibleTypes, fieldType) : isVisible;
    }
    
    public void setColumnWidth(String columnWidth) {
        if ("*".equals(columnWidth)) {
            columnWidth = null;
        }
        this.columnWidth = columnWidth;
    }

    public String getDisplayValue() {
        return displayValue == null ? value : displayValue;
    }

    /**
     * Used to build a link for this particular field value to be displayed in a modal. This is used to build the link for
     * a 'to-one-lookup' field.
     * 
     * @return
     */
    public String getEntityViewPath() {
        return getForeignKeyClass() + "/" + getValue();
    }
    
    /**
     * Used for linking in toOneLookup fields as well as linking to the entity via a 'name' field
     * @return
     */
    public boolean getCanLinkToExternalEntity() {
        return SupportedFieldType.ADDITIONAL_FOREIGN_KEY.toString().equals(fieldType);
    }

    public Boolean getReadOnly() {
        return isReadOnly == null ? false : isReadOnly;
    }
    
    public Boolean getAlternateOrdering() {
        return isAlternateOrdering == null ? false : isAlternateOrdering;
    }
    
    public Boolean getTranslatable() {
        return isTranslatable == null ? false : isTranslatable;
    }
    
    public Boolean getMainEntityLink() {
        return isMainEntityLink == null ? false : isMainEntityLink;
    }
    
    public Boolean getFilterSortDisabled() {
        return isFilterSortDisabled == null ? false : isFilterSortDisabled;
    }
    
    /* ************************** */
    /* STANDARD GETTERS / SETTERS */
    /* ************************** */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getForeignKeyDisplayValueProperty() {
        return foreignKeyDisplayValueProperty;
    }

    public void setForeignKeyDisplayValueProperty(String foreignKeyDisplayValueProperty) {
        this.foreignKeyDisplayValueProperty = foreignKeyDisplayValueProperty;
    }

    public String getIdOverride() {
        return idOverride;
    }

    public void setIdOverride(String idOverride) {
        this.idOverride = idOverride;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getOnChangeTrigger() {
        return onChangeTrigger;
    }
    
    public void setOnChangeTrigger(String onChangeTrigger) {
        this.onChangeTrigger = onChangeTrigger;
    }

    public Boolean getRequired() {
        return required == null ? false : required;
    }
    
    public void setRequired(Boolean required) {
        this.required = required;
    }
    
    public String getColumnWidth() {
        return columnWidth;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    public void setAlternateOrdering(Boolean alternateOrdering) {
        this.isAlternateOrdering = alternateOrdering;
    }
    
    public void setReadOnly(Boolean readOnly) {
        this.isReadOnly = readOnly;
    }
    
    public void setTranslatable(Boolean translatable) {
        this.isTranslatable = translatable;
    }

    public String getForeignKeyClass() {
        return foreignKeyClass;
    }

    public void setForeignKeyClass(String foreignKeyClass) {
        this.foreignKeyClass = foreignKeyClass;
    }

    public String getOwningEntityClass() {
        return owningEntityClass;
    }

    public void setOwningEntityClass(String owningEntityClass) {
        this.owningEntityClass = owningEntityClass;
    }

    public void setMainEntityLink(Boolean isMainEntityLink) {
        this.isMainEntityLink = isMainEntityLink;
    }

    public void setFilterSortDisabled(Boolean isFilterSortDisabled) {
        this.isFilterSortDisabled = isFilterSortDisabled;
    }
    
}
