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
package org.broadleafcommerce.core.promotionMessage.domain.type;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An extendible enumeration of promotion message location types.
 *
 */
public class PromotionMessagePlacementType implements Serializable, BroadleafEnumerationType, Comparable<PromotionMessagePlacementType> {

    private static final long serialVersionUID = 1L;

    private static final Map<String, PromotionMessagePlacementType> TYPES = new LinkedHashMap<String, PromotionMessagePlacementType>();
    public static final PromotionMessagePlacementType PRODUCT_DETAIL = new PromotionMessagePlacementType("PRODUCT_DETAIL", "Product Detail", 1000);
    public static final PromotionMessagePlacementType BROWSE = new PromotionMessagePlacementType("BROWSE", "Browse", 2000);
    public static final PromotionMessagePlacementType SEARCH_RESULTS = new PromotionMessagePlacementType("SEARCH_RESULTS", "Search Results", 3000);
    public static final PromotionMessagePlacementType CART = new PromotionMessagePlacementType("CART", "Cart", 4000);
    public static final PromotionMessagePlacementType EVERYWHERE = new PromotionMessagePlacementType("EVERYWHERE", "Everywhere", 5000);


    public static PromotionMessagePlacementType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;
    private int order;

    public PromotionMessagePlacementType() {
        //do nothing
    }

    public PromotionMessagePlacementType(final String type, final String friendlyType, int order) {
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
        PromotionMessagePlacementType other = (PromotionMessagePlacementType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
    
    @Override
    public int compareTo(PromotionMessagePlacementType arg0) {
        return this.order - arg0.order;
    }

}
