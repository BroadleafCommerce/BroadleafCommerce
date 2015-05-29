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

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An extendible enumeration of fulfillment group status types.
 * 
 * @author aangus
 *
 */
public class FulfillmentGroupStatusType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, FulfillmentGroupStatusType> TYPES = new LinkedHashMap<String, FulfillmentGroupStatusType>();
    
    /**
     * Use FULFILLED, PARTIALLY_FULFILLED, DELIVERED, or PARTIALLY_DELIVERED
     * @deprecated
     */
    @Deprecated
    public static final FulfillmentGroupStatusType SHIPPED = new FulfillmentGroupStatusType("SHIPPED", "Shipped");
    
    /**
     * CANCELLED: Used to indicate that the fulfillment group will not be shipped.
     */
    public static final FulfillmentGroupStatusType CANCELLED = new FulfillmentGroupStatusType("CANCELLED", "Cancelled");

    /**
     * PROCESSING: Used to indicate that the fulfillment group is being processed. For example, during pick or pack processes 
     * in a warehouse.
     */
    public static final FulfillmentGroupStatusType PROCESSING = new FulfillmentGroupStatusType("PROCESSING", "Processing");
    
    /**
     * FULFILLED: Used to indicate that the Fulfillment Group is completely fulfilled (e.g. shipped, downloaded, etc.). For some systems, 
     * this will be the final status on a fulfillment group. For others that want to differentiate between FULFILLED and DELIVERED, usually 
     * to differentiate between items that have been shipped vs. items that have been received by the customer.
     */
    public static final FulfillmentGroupStatusType FULFILLED = new FulfillmentGroupStatusType("FULFILLED", "Fulfilled");
    
    /**
     * PARTIALLY_FULFILLED: Used to indicate that one or more items has been fulfilled or partially fulfilled, but that there 
     * are some items in the fulfillment group that are not fulfilled.
     */
    public static final FulfillmentGroupStatusType PARTIALLY_FULFILLED = new FulfillmentGroupStatusType("PARTIALLY_FULFILLED", "Partially Fulfilled");
    
    /**
     * DELIVERED: Used to indicate that all items in the fulfillment group have been delivered. This will generally only be used when there is some 
     * integration with a shipping or fulfillment system to indicate that an item has actually been received by the customer.
     */
    public static final FulfillmentGroupStatusType DELIVERED = new FulfillmentGroupStatusType("DELIVERED", "Delivered");
    
    /**
     * PARTIALLY_DELIVERED: Indicates that an item or a FulfillemntGroup has been partially received by the customer.
     */
    public static final FulfillmentGroupStatusType PARTIALLY_DELIVERED = new FulfillmentGroupStatusType("PARTIALLY_DELIVERED", "Partially Delivered");
    
    public static FulfillmentGroupStatusType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public FulfillmentGroupStatusType() {
        //do nothing
    }

    public FulfillmentGroupStatusType(final String type, final String friendlyType) {
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
        FulfillmentGroupStatusType other = (FulfillmentGroupStatusType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
