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
package org.broadleafcommerce.core.order.service.call;

import org.broadleafcommerce.core.order.domain.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class GiftWrapOrderItemRequest extends DiscreteOrderItemRequest {

    private List<OrderItem> wrappedItems = new ArrayList<OrderItem>();

    public List<OrderItem> getWrappedItems() {
        return wrappedItems;
    }

    public void setWrappedItems(List<OrderItem> wrappedItems) {
        this.wrappedItems = wrappedItems;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((wrappedItems == null) ? 0 : wrappedItems.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null) 
            return false;
        if (!super.equals(obj))
            return false;
        if (!getClass().isAssignableFrom(obj.getClass()))
            return false;
        GiftWrapOrderItemRequest other = (GiftWrapOrderItemRequest) obj;
        if (wrappedItems == null) {
            if (other.wrappedItems != null)
                return false;
        } else if (!wrappedItems.equals(other.wrappedItems))
            return false;
        return true;
    }

}
