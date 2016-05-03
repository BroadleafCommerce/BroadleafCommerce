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
package org.broadleafcommerce.core.order.service.type;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An extendible enumeration of fulfillment group types.
 * 
 * @author jfischer
 *
 */
public class FulfillmentType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, FulfillmentType> TYPES = new LinkedHashMap<String, FulfillmentType>();

    public static final FulfillmentType DIGITAL = new FulfillmentType("DIGITAL", "Digital");
    public static final FulfillmentType PHYSICAL_SHIP = new FulfillmentType("PHYSICAL_SHIP", "Physical Ship");
    public static final FulfillmentType PHYSICAL_PICKUP = new FulfillmentType("PHYSICAL_PICKUP", "Physical Pickup");
    public static final FulfillmentType PHYSICAL_PICKUP_OR_SHIP = new FulfillmentType("PHYSICAL_PICKUP_OR_SHIP", "Physical Pickup or Ship");
    public static final FulfillmentType GIFT_CARD = new FulfillmentType("GIFT_CARD", "Gift Card");

    public static FulfillmentType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public FulfillmentType() {
        //do nothing
    }

    public FulfillmentType(final String type, final String friendlyType) {
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
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!getClass().isAssignableFrom(obj.getClass()))
            return false;
        FulfillmentType other = (FulfillmentType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
