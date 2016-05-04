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
import org.broadleafcommerce.core.order.domain.Order;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * An extendible enumeration of order status types.
 * 
 * @author jfischer
 */
public class OrderStatus implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final LinkedHashMap<String, OrderStatus> TYPES = new LinkedHashMap<String, OrderStatus>();

    /**
     * Represents a wishlist. This also usually means that the {@link Order} has its {@link Order#getName()} set although
     * not required
     */
    public static final OrderStatus NAMED = new OrderStatus("NAMED", "Named");
    public static final OrderStatus QUOTE = new OrderStatus("QUOTE", "Quote");
    
    /**
     * Represents a cart (non-submitted {@link Order}s)
     */
    public static final OrderStatus IN_PROCESS = new OrderStatus("IN_PROCESS", "In Process");
    
    /**
     * Used to represent a completed {@link Order}. Note that this also means that the {@link Order}
     * should have its {@link Order#getOrderNumber} set
     */
    public static final OrderStatus SUBMITTED = new OrderStatus("SUBMITTED", "Submitted");
    public static final OrderStatus CANCELLED = new OrderStatus("CANCELLED", "Cancelled");
    public static final OrderStatus ARCHIVED = new OrderStatus("ARCHIVED", "Archived");
    
    /**
     * Used when a CSR has locked a cart to act on behalf of a customer
     */
    public static final OrderStatus CSR_OWNED = new OrderStatus("CSR_OWNED", "Owned by CSR");


    public static OrderStatus getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public OrderStatus() {
        //do nothing
    }

    public OrderStatus(final String type, final String friendlyType) {
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
        OrderStatus other = (OrderStatus) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
