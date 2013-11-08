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

package org.broadleafcommerce.openadmin.dto;

import java.io.Serializable;


/**
 * 
 * @author jfischer
 *
 */
public class Property implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    protected String name;
    protected String value;
    protected String displayValue;
    protected String originalDisplayValue;
    protected FieldMetadata metadata = new BasicFieldMetadata();
    protected boolean isAdvancedCollection = false;
    protected Boolean isDirty = false;
    protected String unHtmlEncodedValue;
    protected String rawValue;
    protected String originalValue;

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public FieldMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(FieldMetadata metadata) {
        this.metadata = metadata;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public Boolean getIsDirty() {
        return isDirty;
    }

    public void setIsDirty(Boolean isDirty) {
        this.isDirty = isDirty;
    }

    public String getUnHtmlEncodedValue() {
        return unHtmlEncodedValue;
    }

    public void setUnHtmlEncodedValue(String unHtmlEncodedValue) {
        this.unHtmlEncodedValue = unHtmlEncodedValue;
    }

    public String getRawValue() {
        return rawValue;
    }

    public void setRawValue(String rawValue) {
        this.rawValue = rawValue;
    }

    public boolean isAdvancedCollection() {
        return isAdvancedCollection;
    }

    public void setAdvancedCollection(boolean advancedCollection) {
        isAdvancedCollection = advancedCollection;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(String originalValue) {
        this.originalValue = originalValue;
    }

    public String getOriginalDisplayValue() {
        return originalDisplayValue;
    }

    public void setOriginalDisplayValue(String originalDisplayValue) {
        this.originalDisplayValue = originalDisplayValue;
    }
    
    @Override
    public String toString() {
        return getName() + ": " + getValue();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((metadata == null || metadata instanceof CollectionMetadata || ((BasicFieldMetadata) metadata).getMergedPropertyType() == null) ? 0 : ((BasicFieldMetadata) metadata).getMergedPropertyType().hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Property other = (Property) obj;
        if (metadata == null || metadata instanceof CollectionMetadata || ((BasicFieldMetadata) metadata).getMergedPropertyType() == null) {
            if (other.metadata != null && other.metadata instanceof BasicFieldMetadata && ((BasicFieldMetadata) other.metadata).getMergedPropertyType() != null)
                return false;
        } else if (metadata instanceof BasicFieldMetadata && other.metadata instanceof BasicFieldMetadata && !((BasicFieldMetadata) metadata).getMergedPropertyType().equals(((BasicFieldMetadata) other.metadata).getMergedPropertyType()))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
