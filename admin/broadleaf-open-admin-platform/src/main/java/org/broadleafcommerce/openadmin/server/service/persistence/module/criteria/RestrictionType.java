/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.service.persistence.module.criteria;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An extendible enumeration of service status types.
 * 
 * @author jfischer
 *
 */
public class RestrictionType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, RestrictionType> TYPES = new LinkedHashMap<String, RestrictionType>();

    public static final RestrictionType STRING_LIKE  = new RestrictionType("STRING_LIKE", "STRING_LIKE");
    public static final RestrictionType BOOLEAN  = new RestrictionType("BOOLEAN", "BOOLEAN");
    public static final RestrictionType CHARACTER  = new RestrictionType("CHARACTER", "CHARACTER");
    public static final RestrictionType DATE  = new RestrictionType("DATE", "DATE");
    public static final RestrictionType DECIMAL  = new RestrictionType("DECIMAL", "DECIMAL");
    public static final RestrictionType LONG  = new RestrictionType("LONG", "LONG");
    public static final RestrictionType COLLECTION_SIZE_EQUAL  = new RestrictionType("COLLECTION_SIZE_EQUAL", "COLLECTION_SIZE_EQUAL");
    public static final RestrictionType IS_NULL_LONG  = new RestrictionType("IS_NULL_LONG", "IS_NULL_LONG");
    public static final RestrictionType STRING_EQUAL  = new RestrictionType("STRING_EQUAL", "STRING_EQUAL");
    public static final RestrictionType LONG_EQUAL  = new RestrictionType("LONG_EQUAL", "LONG_EQUAL");
    public static final RestrictionType STRING_NOT_EQUAL = new RestrictionType("STRING_NOT_EQUAL", "STRING_NOT_EQUAL");
    public static final RestrictionType LONG_NOT_EQUAL  = new RestrictionType("LONG_NOT_EQUAL", "LONG_NOT_EQUAL");

    public static RestrictionType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public RestrictionType() {
        //do nothing
    }

    public RestrictionType(final String type, final String friendlyType) {
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
        if (!TYPES.containsKey(type)){
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
        RestrictionType other = (RestrictionType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
