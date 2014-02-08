/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.field.type;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * Supported field types within the Broadleaf CMS admin.
 *
 * @author bpolster
 *
 */
public class FieldType implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Map<String, FieldType> TYPES = new HashMap<String, FieldType>();

    public static final FieldType BOOLEAN = new FieldType("BOOLEAN", "Boolean");
    public static final FieldType DATE = new FieldType("DATE", "Date");
    public static final FieldType TIME = new FieldType("TIME", "Time");
    public static final FieldType INTEGER = new FieldType("INTEGER", "Integer");
    public static final FieldType DECIMAL = new FieldType("DECIMAL", "Decimal");
    public static final FieldType STRING = new FieldType("STRING", "String");
    public static final FieldType RICH_TEXT = new FieldType("RICH_TEXT", "Rich Text");
    public static final FieldType HTML = new FieldType("HTML", "HTML");
    public static final FieldType ENUMERATION = new FieldType("ENUMERATION", "Enumeration");


    public static FieldType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public FieldType() {
        //do nothing
    }

    public FieldType(final String type, final String friendlyType) {
        this.friendlyType = friendlyType;
        setType(type);
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
        FieldType other = (FieldType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
