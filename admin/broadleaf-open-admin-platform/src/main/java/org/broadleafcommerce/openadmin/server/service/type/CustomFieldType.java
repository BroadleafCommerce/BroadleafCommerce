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

package org.broadleafcommerce.openadmin.server.service.type;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * An extensible enumeration for a Custom Field Type.
 * Represents the possible custom field types available to admin users.
 *
 * @author Jeff Fischer
 *
 */
public class CustomFieldType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, CustomFieldType> TYPES = new HashMap<String, CustomFieldType>();

    public static final CustomFieldType BOOLEAN = new CustomFieldType("BOOLEAN", "Boolean");
    public static final CustomFieldType INTEGER = new CustomFieldType("INTEGER", "Integer");
    public static final CustomFieldType MONEY = new CustomFieldType("MONEY", "Money");
    public static final CustomFieldType DECIMAL = new CustomFieldType("DECIMAL", "Decimal");
    public static final CustomFieldType DATE = new CustomFieldType("DATE", "Date");
    public static final CustomFieldType STRING_LIST = new CustomFieldType("STRING_LIST", "List of Text Items");
    public static final CustomFieldType STRING = new CustomFieldType("STRING", "Text");

    public static CustomFieldType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public CustomFieldType() {
        //do nothing
    }

    public CustomFieldType(final String type, final String friendlyType) {
        this.friendlyType = friendlyType;
        setType(type);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getFriendlyType() {
        return friendlyType;
    }

    private void setType(final String type) {
        this.type = type;
        if (!TYPES.containsKey(type)) {
            TYPES.put(type, this);
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
        if (getClass() != obj.getClass())
            return false;
        CustomFieldType other = (CustomFieldType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
