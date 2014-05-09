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

package org.broadleafcommerce.common.sitemap.service.type;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Reasonsable set of SiteMap URL priorities
 * 
 * @author bpolster
 */
public class SiteMapPriorityType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, SiteMapPriorityType> TYPES = new LinkedHashMap<String, SiteMapPriorityType>();

    public static final SiteMapPriorityType ZERO = new SiteMapPriorityType("0.0", "0.0");
    public static final SiteMapPriorityType POINT1 = new SiteMapPriorityType("0.1", "0.1");
    public static final SiteMapPriorityType POINT2 = new SiteMapPriorityType("0.2", "0.2");
    public static final SiteMapPriorityType POINT3 = new SiteMapPriorityType("0.3", "0.3");
    public static final SiteMapPriorityType POINT4 = new SiteMapPriorityType("0.4", "0.4");
    public static final SiteMapPriorityType POINT5 = new SiteMapPriorityType("0.5", "0.5");
    public static final SiteMapPriorityType POINT6 = new SiteMapPriorityType("0.6", "0.6");
    public static final SiteMapPriorityType POINT7 = new SiteMapPriorityType("0.7", "0.7");
    public static final SiteMapPriorityType POINT8 = new SiteMapPriorityType("0.8", "0.8");
    public static final SiteMapPriorityType POINT9 = new SiteMapPriorityType("0.9", "0.9");
    public static final SiteMapPriorityType ONE = new SiteMapPriorityType("1.0", "1.0");

    public static SiteMapPriorityType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public SiteMapPriorityType() {
        //do nothing
    }

    public SiteMapPriorityType(final String type, final String friendlyType) {
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
        if (!getClass().isAssignableFrom(obj.getClass()))
            return false;
        SiteMapPriorityType other = (SiteMapPriorityType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
