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

package org.broadleafcommerce.openadmin.web.form.entity;

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;



/**
 * @author Andre Azzolini (apazzolini)
 */
public class Field {

    protected String name;
    protected String friendlyName;
    protected String fieldType;
    protected String value;
    protected String displayValue;
    protected String foreignKeyDisplayValueProperty;
    protected String idOverride;
    
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
    
    public Field withIdOverride(String idOverride) {
        setIdOverride(idOverride);
        return this;
    }

    /* ************************ */
    /* CUSTOM GETTERS / SETTERS */
    /* ************************ */

    public Boolean getIsVisible() {
        return !(fieldType.equals(SupportedFieldType.ID.toString()) ||
                fieldType.equals(SupportedFieldType.HIDDEN.toString()) || fieldType.equals(SupportedFieldType.FOREIGN_KEY.toString()));
    }

    public String getDisplayValue() {
        return displayValue == null ? value : displayValue;
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

}
