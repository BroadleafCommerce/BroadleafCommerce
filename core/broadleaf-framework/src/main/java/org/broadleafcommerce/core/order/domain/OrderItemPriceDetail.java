/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;

import java.io.Serializable;
import java.util.List;

public interface OrderItemPriceDetail extends Serializable {

    /**
     * The unique identifier of this OrderItem
     * @return
     */
    public Long getId();

    /**
     * Sets the unique id of the OrderItem.   Typically left blank for new items and Broadleaf will
     * set using the next sequence number.
     * @param id
     */
    public void setId(Long id);

    /**
     * Reference back to the containing orderItem.
     * @return
     */
    public OrderItem getOrderItem();

    /**
     * Sets the orderItem for this itemPriceDetail.
     * @param order
     */
    public void setOrderItem(OrderItem order);



    /**
     * The final price associated with this {@link OrderItemPriceDetail}. Note that this takes into account all of the taxes, fees and
     * promotions that are applied on this {@link OrderItemPriceDetail}. This will also use {@link #getSalePrice()} as a base rather
     * than {@link #getRetailPrice()} when {@link #getIsOnSale()} is true.
     * 
     * @return
     */
    public Money getPrice();

    /**
     * Sets the price of this {@link OrderItemPriceDetail} while taking into account all taxes, fees, promotions and whether or not the
     * item is on sale.
     * 
     * @param price
     */
    public void setPrice(Money price);

    /**
     * The quantity of this {@link OrderItemPriceDetail}.
     * 
     * @return
     */
    public int getQuantity();

    public void setQuantity(int quantity);

    /**
     * Returns a unmodifiable List of OrderItemAdjustment.  To modify the List of OrderItemAdjustment, please
     * use the addOrderItemAdjustment or removeAllAdjustments methods.
     * @return a unmodifiable List of OrderItemAdjustment
     */
    public List<OrderItemAdjustment> getOrderItemAdjustments();

    /**
     * Sets the list of OrderItemAdjustments
     * @param orderItemAdjustments
     */
    public void setOrderItemAdjustments(List<OrderItemAdjustment> orderItemAdjustments);
    
    /**
     * Removes all candidate offers.   Used by the promotion engine which subsequently adds 
     * the candidate offers that might apply back to this item.
     */
    public void removeAllCandidateItemOffers();

    /**
     * Removes all adjustment for this order item and reset the adjustment price.
     */
    public int removeAllAdjustments();

    /**
     * The price of the item before discounts and prices.
     * 
     * @return
     */
    public Money getUnitAmount();

    /**
     * Returns the unit display price for the item.  
     * 
     * This represents the item price after subtracting all discounts from item offers.
     * 
     * @return
     */
    public Money getUnitDisplayPrice();

    /**
     * Returns the unit tax amount.  
     * 
     * @return
     */
    public Money getUnitTaxes();

    /**
     * Returns the itemAdjustmentValue that is applied to each quantity  
     * 
     * @return
     */
    public Money getUnitItemAdjustmentValue();

    /**
     * Returns the total amount of this orderItem which is the equivalent of 
     * {@link #getUnitAmount()} * {@link #getQuantity()}. 
     * 
     * @return
     */
    public Money getTotalAmount();

    /**
     * Returns the total item display price, calculated as:
     * {@link #getUnitDisplayPrice()} * {@link #getQuantity()}.          
     * 
     * The unitDisplay price subtracts item level (but not order level) adjustments.
     * @return
     */
    public Money getTotalItemDisplayPrice();

    /**
     * Returns the total return price.   The return price subtracts both 
     * order and item level adjustments.
     * 
     * @return
     */
    public Money getTotalReturnPrice();

    /**
     * Returns the total taxes to be paid. 
     * 
     * @return
     */
    public Money getTotalTaxes();

    /**
     * Returns the total of all adjustments on this line item (specifically, bother order and item level
     * adjustments).
     * 
     * @return
     */
    public Money getTotalAdjustmentValue();

    /**
     * Returns the total of all item level adjustments. 
     * 
     * @return
     */
    public Money getTotalItemAdjustmentValue();


}
