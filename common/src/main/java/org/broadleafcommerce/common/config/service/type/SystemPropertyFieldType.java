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
package org.broadleafcommerce.common.config.service.type;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;


public class SystemPropertyFieldType implements BroadleafEnumerationType, Serializable {

    private static final long serialVersionUID = 1L;

    private static final Map<String, SystemPropertyFieldType> TYPES = new LinkedHashMap<String, SystemPropertyFieldType>();

    public static final SystemPropertyFieldType INT_TYPE = new SystemPropertyFieldType("INT_TYPE", "Integer value");
    public static final SystemPropertyFieldType LONG_TYPE = new SystemPropertyFieldType("LONG_TYPE", "Long value");
    public static final SystemPropertyFieldType DOUBLE_TYPE = new SystemPropertyFieldType("DOUBLE_TYPE", "Double value");
    public static final SystemPropertyFieldType BOOLEAN_TYPE = new SystemPropertyFieldType("BOOLEAN_TYPE", "Boolean value");
    public static final SystemPropertyFieldType STRING_TYPE = new SystemPropertyFieldType("STRING", "String value");

    public static SystemPropertyFieldType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public SystemPropertyFieldType() {
        //do nothing
    }

    public SystemPropertyFieldType(final String type, final String friendlyType) {
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
        SystemPropertyFieldType other = (SystemPropertyFieldType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
