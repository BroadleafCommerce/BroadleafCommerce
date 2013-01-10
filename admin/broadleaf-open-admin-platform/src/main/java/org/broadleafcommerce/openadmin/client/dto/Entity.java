/*
 * Copyright 2008-2009 the original author or authors.
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

package org.broadleafcommerce.openadmin.client.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

/**
 * 
 * @author jfischer
 *
 */
public class Entity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String[] type;
    private Property[] properties;
    private boolean isDirty = false;
    private boolean multiPartAvailableOnThread = false;
    
    public String[] getType() {
        return type;
    }

    public void setType(String[] type) {
        if (type != null && type.length > 0) {
            Arrays.sort(type);
        }
        this.type = type;
    }

    public Property[] getProperties() {
        return properties;
    }
    
    public void setProperties(Property[] properties) {
        this.properties = properties;
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
                return o1.getName().compareTo(o2.getName());
            }
        });
        Property searchProperty = new Property();
        searchProperty.setName(name);
        int index = Arrays.binarySearch(properties, searchProperty, new Comparator<Property>() {
            public int compare(Property o1, Property o2) {
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
}
