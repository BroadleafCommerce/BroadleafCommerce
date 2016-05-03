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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.Serializable;
import java.util.Date;


/**
 * 
 * @author jfischer
 *
 */
@JsonAutoDetect
public class Property implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @JsonProperty
    protected String name;

    @JsonProperty
    protected String value;

    @JsonProperty
    protected String displayValue;

    @JsonProperty
    protected String originalDisplayValue;

    @JsonProperty(value = "propertyMetadata")
    protected FieldMetadata metadata = new BasicFieldMetadata();

    @JsonProperty
    protected boolean isAdvancedCollection = false;

    @JsonProperty
    protected Boolean isDirty = false;

    @JsonIgnore
    protected String unHtmlEncodedValue;

    @JsonProperty
    protected String rawValue;

    @JsonProperty
    protected String originalValue;

    @JsonIgnore
    protected Date deployDate;
    protected boolean enabled = true;

    public Property() {
        // Default public constructor
    }

    public Property(String name, String value) {
        this.name = name;
        this.value = value;
    }

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
        if (unHtmlEncodedValue == null && value != null) {
            setUnHtmlEncodedValue(StringEscapeUtils.unescapeHtml(value));
        }
        
        if (rawValue == null && value != null) {
            setRawValue(value);
        }
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
        if (unHtmlEncodedValue == null) {
            return StringEscapeUtils.unescapeHtml(getValue());
        }
        return unHtmlEncodedValue;
    }

    public void setUnHtmlEncodedValue(String unHtmlEncodedValue) {
        this.unHtmlEncodedValue = unHtmlEncodedValue;
    }

    public String getRawValue() {
        if (rawValue == null) {
            return getValue();
        }
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

    public Date getDeployDate() {
        return deployDate;
    }

    public void setDeployDate(Date deployDate) {
        this.deployDate = deployDate;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Property{");
        sb.append("name='").append(name).append('\'');
        String temp = value;
        if (temp != null && temp.length() > 200) {
            temp = temp.substring(0,199) + "...";
        }
        sb.append(", value='").append(temp).append('\'');
        sb.append(", isDirty=").append(isDirty);
        sb.append(", enabled=").append(enabled);
        sb.append('}');
        return sb.toString();
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
        if (!getClass().isAssignableFrom(obj.getClass()))
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
