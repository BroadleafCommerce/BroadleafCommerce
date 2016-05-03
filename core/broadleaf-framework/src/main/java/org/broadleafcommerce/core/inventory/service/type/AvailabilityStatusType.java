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
package org.broadleafcommerce.core.inventory.service.type;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An extendible enumeration of availability status types.
 * 
 * @author jfischer
 * 
 * @deprecated This is no longer required and is instead implemented as a third-party inventory module
 */
@Deprecated
public class AvailabilityStatusType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;
    private static final Map<String, AvailabilityStatusType> TYPES = new LinkedHashMap<String, AvailabilityStatusType>();

    public static final AvailabilityStatusType AVAILABLE  = new AvailabilityStatusType("AVAILABLE", "Available");
    public static final AvailabilityStatusType UNAVAILABLE  = new AvailabilityStatusType("UNAVAILABLE", "Unavailable");
    public static final AvailabilityStatusType BACKORDERED  = new AvailabilityStatusType("BACKORDERED", "Back Ordered");
    
    public static AvailabilityStatusType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public AvailabilityStatusType() {
        //do nothing
    }

    public AvailabilityStatusType(final String type, final String friendlyType) {
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
        AvailabilityStatusType other = (AvailabilityStatusType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
