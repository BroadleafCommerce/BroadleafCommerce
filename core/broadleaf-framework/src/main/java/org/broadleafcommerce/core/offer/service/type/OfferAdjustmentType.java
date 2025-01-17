/*-
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.offer.service.type;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This indicates how an Offer should be fulfilled to the customer, defaulting to order time discount.
 * Currently, this enumeration can be ORDER_DISCOUNT or FUTURE_CREDIT. "Future credit" means that the associated 
 * adjustment will be discounted at a later time to the customer via a credit. It is up to the implementor to 
 * decide how to achieve this. The adjustment entities have a new "isFutureCredit" field used to determine if an 
 * adjustment originated from an offer marked as FUTURE_CREDIT. Order, OrderItem and FulfillmentGroup have new 
 * accessor methods for retrieving the future credit values when they are needed to be fulfilled. 
 *
 * Out-of-box, this field is disabled from admin and must be manually enabled to view, since it is not a typical 
 * requirement to most implementations. To enable, add the following to AdminConfig.java:
 *
 * @Merge("blAppConfigurationMap")
 * public Map<String, String> adminOfferAdjustmentType() {
 *     Map<String, String> appConfigMap = new HashMap<>();
 *     appConfigMap.put("admin.showIfProperty.offerAdjustmentType", "true");
 *     return appConfigMap;
 * }
 * 
 * @author Chad Harchar (charchar)
 */
public class OfferAdjustmentType implements Serializable, BroadleafEnumerationType {
    private static final long serialVersionUID = 1L;

    private static final Map<String, OfferAdjustmentType> TYPES = new LinkedHashMap<String, OfferAdjustmentType>();

    public static final OfferAdjustmentType ORDER_DISCOUNT = new OfferAdjustmentType("ORDER_DISCOUNT", "Order Discount");
    public static final OfferAdjustmentType FUTURE_CREDIT = new OfferAdjustmentType("FUTURE_CREDIT", "Future Credit");

    public static OfferAdjustmentType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public OfferAdjustmentType() {
        //do nothing
    }

    public OfferAdjustmentType(final String type, final String friendlyType) {
        this.friendlyType = friendlyType;
        setType(type);
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
        OfferAdjustmentType other = (OfferAdjustmentType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
