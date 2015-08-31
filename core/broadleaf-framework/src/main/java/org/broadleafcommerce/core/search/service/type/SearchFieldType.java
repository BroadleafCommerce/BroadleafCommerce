/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.service.type;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Chad Harchar (charchar)
 */
public class SearchFieldType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, SearchFieldType> TYPES = new HashMap<String, SearchFieldType>();

    //standard persistence field types, which can be persisted as custom fields in an attribute map
    public static final SearchFieldType BOOLEAN = new SearchFieldType("BOOLEAN", "Boolean");
    public static final SearchFieldType INTEGER = new SearchFieldType("INTEGER", "Integer");
    public static final SearchFieldType MONEY = new SearchFieldType("MONEY", "Money");
    public static final SearchFieldType DECIMAL = new SearchFieldType("DECIMAL", "Decimal");
    public static final SearchFieldType DATE = new SearchFieldType("DATE", "Date");
    public static final SearchFieldType STRING = new SearchFieldType("STRING", "Text");

    public static SearchFieldType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public SearchFieldType() {
        //do nothing
    }

    public SearchFieldType(final String type, final String friendlyType) {
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
        if (obj != null && getClass().isAssignableFrom(obj.getClass())) {
            SearchFieldType other = (SearchFieldType) obj;
            return new EqualsBuilder()
                    .append(type, other.type)
                    .build();
        }
        return false;
    }
}
