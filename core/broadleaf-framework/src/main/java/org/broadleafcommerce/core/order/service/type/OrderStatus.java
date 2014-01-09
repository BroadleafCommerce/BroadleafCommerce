/*
 * #%L
 * BroadleafCommerce Framework
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
        if (getClass() != obj.getClass())
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
