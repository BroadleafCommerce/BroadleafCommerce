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
package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.OrderItemPriceDetailAdjustment;

import java.io.Serializable;
import java.util.List;

public interface OrderItemPriceDetail extends Serializable {

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
