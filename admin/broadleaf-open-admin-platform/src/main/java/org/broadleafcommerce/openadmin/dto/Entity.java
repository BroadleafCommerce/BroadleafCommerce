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

import org.apache.commons.collections.MapUtils;
import org.broadleafcommerce.common.util.BLCMapUtils;
import org.broadleafcommerce.common.util.TypedClosure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generic DTO for a domain object. Each property of the domain object is represented by the 'properties' instance variable
 * which allows for further display metadata to be stored.
 * 
 * @author jfischer
 * @see {@link Property}
 *
 */
public class Entity implements Serializable {

    protected static final long serialVersionUID = 1L;

    protected String[] type;
    protected Property[] properties;
    protected boolean isDirty = false;
    protected Date deployDate;
    protected Boolean isDeleted = false;
    protected Boolean isInactive = false;
    protected Boolean isActive = false;
    protected boolean multiPartAvailableOnThread = false;
    protected boolean isValidationFailure = false;
    protected Map<String, List<String>> validationErrors = new HashMap<String, List<String>>();
    
    protected Map<String, Property> pMap = null;

    public String[] getType() {
        return type;
    }

    public void setType(String[] type) {
        if (type != null && type.length > 0) {
            Arrays.sort(type);
        }
        this.type = type;
    }

    public Map<String, Property> getPMap() {
        if (pMap == null) {
            pMap = BLCMapUtils.keyedMap(properties, new TypedClosure<String, Property>() {
                @Override
                public String getKey(Property value) {
                    return value.getName();
                }
            });
        }
        return pMap;
    }

    public Property[] getProperties() {
        return properties;
    }
    
    public void setProperties(Property[] properties) {
        this.properties = properties;
        pMap = null;
    }
    
    public void mergeProperties(String prefix, Entity entity) {
        int j = 0;
        Property[] merged = new Property[properties.length + entity.getProperties().length];
        for (Property property : properties) {
            merged[j] = property;
            j++;
        }
        for (Property property : entity.getProperties()) {
            property.setName(prefix!=null?prefix+"."+property.getName():""+property.getName());
            merged[j] = property;
            j++;
        }
        properties = merged;
    }
    
    /**
     * Replaces all property values in this entity with the values from the given entity. This also resets the {@link #pMap}
     * 
     * @param entity
     */
    public void overridePropertyValues(Entity entity) {
        for (Property property : entity.getProperties()) {
            Property myProperty = findProperty(property.getName());
            if (myProperty != null) {
                myProperty.setValue(property.getValue());
                myProperty.setRawValue(property.getRawValue());
            }
        }
        pMap = null;
    }
    
    public Property findProperty(String name) {
        Arrays.sort(properties, new Comparator<Property>() {
            @Override
            public int compare(Property o1, Property o2) {
                if (o1 == null && o2 == null) {
                    return 0;
                } else if (o1 == null) {
                    return 1;
                } else if (o2 == null) {
                    return -1;
                }
                return o1.getName().compareTo(o2.getName());
            }
        });
        Property searchProperty = new Property();
        searchProperty.setName(name);
        int index = Arrays.binarySearch(properties, searchProperty, new Comparator<Property>() {
            @Override
            public int compare(Property o1, Property o2) {
                if (o1 == null && o2 == null) {
                    return 0;
                } else if (o1 == null) {
                    return 1;
                } else if (o2 == null) {
                    return -1;
                }
                return o1.getName().compareTo(o2.getName());
            }
        });
        if (index >= 0) {
            return properties[index];
        }
        return null;
    }
    
    public void addProperty(Property property) {
        boolean exists = findProperty(property.getName()) != null;
        Property[] allProps = getProperties();
        Property[] newProps = new Property[exists?allProps.length:allProps.length + 1];
        int count = 0;
        for (int j=0;j<allProps.length;j++) {
            if (!allProps[j].getName().equals(property.getName())) {
                newProps[count] = allProps[j];
                count++;
            }
        }
        newProps[newProps.length - 1] = property;
        setProperties(newProps);
    }

    /**
     * Adds a single validation error to this entity. This will also set the entire
     * entity in an error state by invoking {@link #setValidationFailure(boolean)}.
     * 
     * @param fieldName - the field that is in error. This works on top-level properties (like a 'manufacturer' field on a
     * Product entity) but can also work on properties gleaned from a related entity (like
     * 'defaultSku.weight.weightUnitOfMeasure' on a Product entity)
     * @param errorOrErrorKey - the error message to present to a user. Could be the actual error message or a key to a
     * property in messages.properties to support different locales
     */
    public void addValidationError(String fieldName, String errorOrErrorKey) {
        Map<String, List<String>> fieldErrors = getValidationErrors();
        List<String> errorMessages = fieldErrors.get(fieldName);
        if (errorMessages == null) {
            errorMessages = new ArrayList<String>();
            fieldErrors.put(fieldName, errorMessages);
        }
        errorMessages.add(errorOrErrorKey);
        setValidationFailure(true);
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    public boolean isMultiPartAvailableOnThread() {
        return multiPartAvailableOnThread;
    }

    public void setMultiPartAvailableOnThread(boolean multiPartAvailableOnThread) {
        this.multiPartAvailableOnThread = multiPartAvailableOnThread;
    }

    /**
     * 
     * @return if this entity has failed validation. This will also check the {@link #getValidationErrors()} map if this
     * boolean has not been explicitly set
     */
    public boolean isValidationFailure() {
        if (!getValidationErrors().isEmpty()) {
            isValidationFailure = true;
        }
        return isValidationFailure;
    }

    public void setValidationFailure(boolean validationFailure) {
        isValidationFailure = validationFailure;
    }

    /**
     * Validation error map where the key corresponds to the property that failed validation (which could be dot-separated)
     * and the value corresponds to a list of the error messages, in the case of multiple errors on the same field.
     * 
     * For instance, you might have a configuration where the field is both a Required validator and a regex validator.
     * The validation map in this case might contain something like:
     *      
     *      defaultSku.name => ['This field is required', 'Cannot have numbers in name']
     * 
     * @return a map keyed by property name to the list of error messages for that property
     */
    public Map<String, List<String>> getValidationErrors() {
        return validationErrors;
    }

    /**
     * Completely reset the validation errors for this Entity. In most cases it is more appropriate to use the convenience
     * method for adding a single error via {@link #addValidationError(String, String)}. This will also set the entire
     * entity in an error state by invoking {@link #setValidationFailure(boolean)}.
     * 
     * @param validationErrors
     * @see #addValidationError(String, String)
     */
    public void setValidationErrors(Map<String, List<String>> validationErrors) {
        if (MapUtils.isNotEmpty(validationErrors)) {
            setValidationFailure(true);
        }
        this.validationErrors = validationErrors;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Boolean getInactive() {
        return isInactive;
    }

    public void setInactive(Boolean inactive) {
        isInactive = inactive;
    }

    public Date getDeployDate() {
        return deployDate;
    }

    public void setDeployDate(Date deployDate) {
        this.deployDate = deployDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!getClass().isAssignableFrom(o.getClass())) return false;

        Entity entity = (Entity) o;

        if (isDirty != entity.isDirty) return false;
        if (isValidationFailure != entity.isValidationFailure) return false;
        if (multiPartAvailableOnThread != entity.multiPartAvailableOnThread) return false;
        if (deployDate != null ? !deployDate.equals(entity.deployDate) : entity.deployDate != null) return false;
        if (isActive != null ? !isActive.equals(entity.isActive) : entity.isActive != null) return false;
        if (isDeleted != null ? !isDeleted.equals(entity.isDeleted) : entity.isDeleted != null) return false;
        if (isInactive != null ? !isInactive.equals(entity.isInactive) : entity.isInactive != null) return false;
        if (pMap != null ? !pMap.equals(entity.pMap) : entity.pMap != null) return false;
        if (!Arrays.equals(properties, entity.properties)) return false;
        if (!Arrays.equals(type, entity.type)) return false;
        if (validationErrors != null ? !validationErrors.equals(entity.validationErrors) : entity.validationErrors !=
                null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? Arrays.hashCode(type) : 0;
        result = 31 * result + (properties != null ? Arrays.hashCode(properties) : 0);
        result = 31 * result + (isDirty ? 1 : 0);
        result = 31 * result + (deployDate != null ? deployDate.hashCode() : 0);
        result = 31 * result + (isDeleted != null ? isDeleted.hashCode() : 0);
        result = 31 * result + (isInactive != null ? isInactive.hashCode() : 0);
        result = 31 * result + (isActive != null ? isActive.hashCode() : 0);
        result = 31 * result + (multiPartAvailableOnThread ? 1 : 0);
        result = 31 * result + (isValidationFailure ? 1 : 0);
        result = 31 * result + (validationErrors != null ? validationErrors.hashCode() : 0);
        result = 31 * result + (pMap != null ? pMap.hashCode() : 0);
        return result;
    }
}
