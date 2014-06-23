/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.presentation.client;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This extensible enumeration controls the field types that are available for users to choose from when creating
 * FieldDefinitions in the admin tool. This list should be a strict subset of {@link SupportedFieldType} and will
 * throw an exception if a non-matching type is added.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class DynamicSupportedFieldType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, DynamicSupportedFieldType> TYPES = new LinkedHashMap<String, DynamicSupportedFieldType>();

    public static final DynamicSupportedFieldType STRING = new DynamicSupportedFieldType("STRING", "String");
    public static final DynamicSupportedFieldType HTML = new DynamicSupportedFieldType("HTML", "Rich Text");
    public static final DynamicSupportedFieldType MONEY = new DynamicSupportedFieldType("MONEY", "Money");
    public static final DynamicSupportedFieldType COLOR = new DynamicSupportedFieldType("COLOR", "Color");
    public static final DynamicSupportedFieldType PRODUCT_LOOKUP = new DynamicSupportedFieldType("ADDITIONAL_FOREIGN_KEY|org.broadleafcommerce.core.catalog.domain.Product", "Product Lookup");
    public static final DynamicSupportedFieldType CATEGORY_LOOKUP = new DynamicSupportedFieldType("ADDITIONAL_FOREIGN_KEY|org.broadleafcommerce.core.catalog.domain.Category", "Category Lookup");

    public static DynamicSupportedFieldType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public DynamicSupportedFieldType() {
        //do nothing
    }

    public DynamicSupportedFieldType(final String type, final String friendlyType) {
        verifyLegalType(type);
        this.friendlyType = friendlyType;
        setType(type);
    }
    
    /**
     * @param type
     * @throws IllegalArgumentException when the given type does not exist in {@link SupportedFieldType}
     */
    public static void verifyLegalType(String type) {
        if (type.contains("|")) {
            type = type.substring(0, type.indexOf('|'));
        }
        SupportedFieldType.valueOf(type);
    }
    
    /**
     * @return a cloned list of the currently known {@link DynamicSupportedFieldType}s.
     */
    public static List<DynamicSupportedFieldType> getTypes() {
        List<DynamicSupportedFieldType> list = new ArrayList<DynamicSupportedFieldType>(TYPES.size());
        for (Entry<String, DynamicSupportedFieldType> entry : TYPES.entrySet()) {
            list.add(entry.getValue());
        }
        return list;
    }
    
    public String getType() {
        return type;
    }

    public String getFriendlyType() {
        return friendlyType;
    }

    private void setType(final String type) {
        this.type = type;
        if (!TYPES.containsKey(type)) {
            TYPES.put(type, this);
        } else {
            throw new RuntimeException("Cannot add the type: (" + type + "). It already exists as a type via " + getInstance(type).getClass().getName());
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        DynamicSupportedFieldType other = (DynamicSupportedFieldType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
