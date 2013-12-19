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
 * An enumeration of Sitemap change frequency values
 * 
 * @author Joshua Skorton (jskorton)
 */
public class SiteMapChangeFreqType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, SiteMapChangeFreqType> TYPES = new LinkedHashMap<String, SiteMapChangeFreqType>();

    public static final SiteMapChangeFreqType ALWAYS = new SiteMapChangeFreqType("ALWAYS", "always");
    public static final SiteMapChangeFreqType HOURLY = new SiteMapChangeFreqType("HOURLY", "hourly");
    public static final SiteMapChangeFreqType DAILY = new SiteMapChangeFreqType("DAILY", "daily");
    public static final SiteMapChangeFreqType WEEKLY = new SiteMapChangeFreqType("WEEKLY", "weekly");
    public static final SiteMapChangeFreqType MONTHLY = new SiteMapChangeFreqType("MONTHLY", "monthly");
    public static final SiteMapChangeFreqType YEARLY = new SiteMapChangeFreqType("YEARLY", "yearly");
    public static final SiteMapChangeFreqType NEVER = new SiteMapChangeFreqType("NEVER", "never");

    public static SiteMapChangeFreqType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public SiteMapChangeFreqType() {
        //do nothing
    }

    public SiteMapChangeFreqType(final String type, final String friendlyType) {
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
        SiteMapChangeFreqType other = (SiteMapChangeFreqType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
