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
package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.OrderItemPriceDetailAdjustment;

import java.io.Serializable;
import java.util.List;

public interface OrderItemPriceDetail extends Serializable, MultiTenantCloneable<OrderItemPriceDetail> {

    /**
     * The unique identifier of this OrderItem
     * @return
     */
    Long getId();

    /**
     * Sets the unique id of the OrderItem.   Typically left blank for new items and Broadleaf will
     * set using the next sequence number.
     * @param id
     */
    void setId(Long id);

    /**
     * Reference back to the containing orderItem.
     * @return
     */
    OrderItem getOrderItem();

    /**
     * Sets the orderItem for this itemPriceDetail.
     * @param order
     */
    void setOrderItem(OrderItem order);

    /**
     * Returns a List of the adjustments that effected this priceDetail. 
     * @return a  List of OrderItemPriceDetailAdjustment
     */
    List<OrderItemPriceDetailAdjustment> getOrderItemPriceDetailAdjustments();

    /**
     * Sets the list of OrderItemPriceDetailAdjustment
     * @param orderItemPriceDetailAdjustments
     */
    void setOrderItemAdjustments(List<OrderItemPriceDetailAdjustment> orderItemPriceDetailAdjustments);

    /**
     * The quantity of this {@link OrderItemPriceDetail}.
     * 
     * @return
     */
    int getQuantity();

    /**
     * Returns the quantity
     * @param quantity
     */
    void setQuantity(int quantity);

    /**
     * Returns the value of all adjustments for a single quantity of the item.
     * 
     * Use {@link #getTotalAdjustmentValue()} to get the total for all quantities of this item.
     *
     * @return
     */
    Money getAdjustmentValue();

    /**
     * Returns getAdjustmentValue() * the quantity.
     *
     * @return
     */
    Money getTotalAdjustmentValue();

    /**
     * Returns the total adjustedPrice.
     *
     * @return
     */
    Money getTotalAdjustedPrice();

    /**
     * Indicates that the adjustments were based off of the item's sale price.
     * @return
     */
    boolean getUseSalePrice();

    /**
     * Set that the adjustments should be taken off of the item's sale price.
     * @param useSalePrice
     */
    void setUseSalePrice(boolean useSalePrice);

}
