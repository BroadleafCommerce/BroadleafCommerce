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
public class StackabilityType implements Serializable, BroadleafEnumerationType, Comparable<StackabilityType> {

    private static final long serialVersionUID = 1L;

    private static final Map<String, StackabilityType> TYPES = new LinkedHashMap<String, StackabilityType>();
    public static final StackabilityType NO = new StackabilityType("NO", "No", 1000);
    public static final StackabilityType YES = new StackabilityType("YES", "Yes, with other stackable offers", 2000);
    public static final StackabilityType YES_ALWAYS = new StackabilityType("YES_ALWAYS", "Yes, always", 3000);


    public static StackabilityType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;
    private int order;

    public StackabilityType() {
        //do nothing
    }

    public StackabilityType(final String type, final String friendlyType, int order) {
        this.friendlyType = friendlyType;
        setType(type);
        setOrder(order);
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
    
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
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
        StackabilityType other = (StackabilityType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
    
    @Override
    public int compareTo(StackabilityType arg0) {
        return this.order - arg0.order;
    }

}
