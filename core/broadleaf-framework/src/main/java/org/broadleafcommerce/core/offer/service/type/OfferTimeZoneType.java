/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.core.offer.service.type;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An extendible enumeration of offer types.
 *
 */
public class OfferTimeZoneType implements Serializable, BroadleafEnumerationType {
    
    private static final long serialVersionUID = 1L;

    private static final Map<String, OfferTimeZoneType> TYPES = new LinkedHashMap<String, OfferTimeZoneType>();

    public static final OfferTimeZoneType SERVER = new OfferTimeZoneType("SERVER", "Server");
    public static final OfferTimeZoneType APPLICATION = new OfferTimeZoneType("APPLICATION", "Application Supplied");
    public static final OfferTimeZoneType CST = new OfferTimeZoneType("CST", "CST", true);
    public static final OfferTimeZoneType UTC = new OfferTimeZoneType("UTC", "UTC", true);
    public static OfferTimeZoneType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;
    private Boolean javaStandardTimeZone;


    public OfferTimeZoneType() {
        //do nothing
    }

    public OfferTimeZoneType(final String type, final String friendlyType) {
        this(type, friendlyType, false);
    }

    public OfferTimeZoneType(final String type, final String friendlyType, Boolean javaStandardTimeZone) {
        this.friendlyType = friendlyType;
        setType(type);
        setJavaStandardTimeZone(javaStandardTimeZone);
    }
    public void setType(final String type) {
        this.type = type;
        if (!TYPES.containsKey(type)) {
            TYPES.put(type, this);
        }
    }

    public String getType() {
        return type;
    }

    public String getFriendlyType() {
        return friendlyType;
    }

    public Boolean getJavaStandardTimeZone() {
        return javaStandardTimeZone;
    }

    public void setJavaStandardTimeZone(Boolean javaStandardTimeZone) {
        this.javaStandardTimeZone = javaStandardTimeZone;
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
        OfferTimeZoneType other = (OfferTimeZoneType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
