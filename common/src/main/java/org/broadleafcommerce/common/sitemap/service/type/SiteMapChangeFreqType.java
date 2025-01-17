/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.common.sitemap.service.type;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An enumeration of Sitemap change frequency values
 *
 * @author Joshua Skorton (jskorton)
 */
public class SiteMapChangeFreqType implements Serializable, BroadleafEnumerationType {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final Map<String, SiteMapChangeFreqType> TYPES = new LinkedHashMap<>();

    public static final SiteMapChangeFreqType ALWAYS = new SiteMapChangeFreqType("ALWAYS", "always");
    public static final SiteMapChangeFreqType HOURLY = new SiteMapChangeFreqType("HOURLY", "hourly");
    public static final SiteMapChangeFreqType DAILY = new SiteMapChangeFreqType("DAILY", "daily");
    public static final SiteMapChangeFreqType WEEKLY = new SiteMapChangeFreqType("WEEKLY", "weekly");
    public static final SiteMapChangeFreqType MONTHLY = new SiteMapChangeFreqType("MONTHLY", "monthly");
    public static final SiteMapChangeFreqType YEARLY = new SiteMapChangeFreqType("YEARLY", "yearly");
    public static final SiteMapChangeFreqType NEVER = new SiteMapChangeFreqType("NEVER", "never");

    private String type;
    private String friendlyType;

    public SiteMapChangeFreqType() {
        //do nothing
    }

    public SiteMapChangeFreqType(final String type, final String friendlyType) {
        this.friendlyType = friendlyType;
        setType(type);
    }

    public static SiteMapChangeFreqType getInstance(final String type) {
        return TYPES.get(type);
    }

    public String getType() {
        return type;
    }

    protected void setType(final String type) {
        this.type = type;
        if (!TYPES.containsKey(type)) {
            TYPES.put(type, this);
        }
    }

    public String getFriendlyType() {
        return friendlyType;
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
        SiteMapChangeFreqType other = (SiteMapChangeFreqType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
