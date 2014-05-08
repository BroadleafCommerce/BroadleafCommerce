/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.rating.service.type;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class RatingSortType implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Map<String, RatingSortType> TYPES = new HashMap<String, RatingSortType>();

    public static final RatingSortType MOST_HELPFUL = new RatingSortType("MOST_HELPFUL");
    public static final RatingSortType MOST_RECENT = new RatingSortType("MOST_RECENT");
    public static final RatingSortType DEFAULT = new RatingSortType("DEFAULT");

    public static RatingSortType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;

    public RatingSortType() {
    }

    public RatingSortType(final String type) {
        setType(type);
    }

    public String getType() {
        return type;
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
        if (!(obj instanceof RatingSortType))
            return false;
        RatingSortType other = (RatingSortType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
