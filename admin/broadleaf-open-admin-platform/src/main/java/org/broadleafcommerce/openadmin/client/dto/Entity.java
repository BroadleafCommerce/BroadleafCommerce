/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.client.dto;

import org.broadleafcommerce.common.util.BLCMapUtils;
import org.broadleafcommerce.common.util.TypedClosure;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class Entity implements IsSerializable, Serializable {

    private static final long serialVersionUID = 1L;

    private String[] type;
    private Property[] properties;
    private boolean isDirty = false;
    private Boolean isDeleted = false;
    private Boolean isInactive = false;
    private Boolean isActive = false;
    private Boolean isLocked = false;
    private String lockedBy;
    private String lockedDate;
    private boolean multiPartAvailableOnThread = false;
    private boolean isValidationFailure;
    private String[][] validationErrors;
    
    private Map<String, Property> pMap = null;

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
    
    public Property findProperty(String name) {
        Arrays.sort(properties, new Comparator<Property>() {
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
        Property[] allProps = getProperties();
        Property[] newProps = new Property[allProps.length + 1];
        for (int j=0;j<allProps.length;j++) {
            newProps[j] = allProps[j];
        }
        newProps[newProps.length - 1] = property;
        setProperties(newProps);
    }
    
    public void addValidationError(String fieldName, String errorOrErrorKey) {
        if (getValidationErrors() == null) {
            setValidationErrors(new String[0][2]);
        }
        String[][] allErrors = getValidationErrors();
        String[][] newErrors = new String[allErrors.length + 1][2];
        for (int j=0;j<allErrors.length;j++) {
            newErrors[j][0] = allErrors[j][0];
            newErrors[j][1] = allErrors[j][1];
        }
        newErrors[newErrors.length - 1][0] = fieldName;
        newErrors[newErrors.length - 1][1] = errorOrErrorKey;
        setValidationErrors(newErrors);
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

    public boolean isValidationFailure() {
        return isValidationFailure;
    }

    public void setValidationFailure(boolean validationFailure) {
        isValidationFailure = validationFailure;
    }

    public String[][] getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(String[][] validationErrors) {
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

    public Boolean getLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }

    public String getLockedDate() {
        return lockedDate;
    }

    public void setLockedDate(String lockedDate) {
        this.lockedDate = lockedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity)) return false;

        Entity entity = (Entity) o;

        if (isDirty != entity.isDirty) return false;
        if (isValidationFailure != entity.isValidationFailure) return false;
        if (multiPartAvailableOnThread != entity.multiPartAvailableOnThread) return false;
        if (isActive != null ? !isActive.equals(entity.isActive) : entity.isActive != null) return false;
        if (isDeleted != null ? !isDeleted.equals(entity.isDeleted) : entity.isDeleted != null) return false;
        if (isInactive != null ? !isInactive.equals(entity.isInactive) : entity.isInactive != null) return false;
        if (isLocked != null ? !isLocked.equals(entity.isLocked) : entity.isLocked != null) return false;
        if (lockedBy != null ? !lockedBy.equals(entity.lockedBy) : entity.lockedBy != null) return false;
        if (lockedDate != null ? !lockedDate.equals(entity.lockedDate) : entity.lockedDate != null) return false;
        if (!Arrays.equals(properties, entity.properties)) return false;
        if (!Arrays.equals(type, entity.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? Arrays.hashCode(type) : 0;
        result = 31 * result + (properties != null ? Arrays.hashCode(properties) : 0);
        result = 31 * result + (isDirty ? 1 : 0);
        result = 31 * result + (isDeleted != null ? isDeleted.hashCode() : 0);
        result = 31 * result + (isInactive != null ? isInactive.hashCode() : 0);
        result = 31 * result + (isActive != null ? isActive.hashCode() : 0);
        result = 31 * result + (isLocked != null ? isLocked.hashCode() : 0);
        result = 31 * result + (lockedBy != null ? lockedBy.hashCode() : 0);
        result = 31 * result + (lockedDate != null ? lockedDate.hashCode() : 0);
        result = 31 * result + (multiPartAvailableOnThread ? 1 : 0);
        result = 31 * result + (isValidationFailure ? 1 : 0);
        return result;
    }
}
