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
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.order.service.manipulation.OrderItemVisitor;
import org.broadleafcommerce.core.order.service.type.OrderItemType;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface OrderItem extends Serializable {

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
     * Reference back to the containing order.
     * @return
     */
    public Order getOrder();

    /**
     * Sets the order for this orderItem.
     * @param order
     */
    public void setOrder(Order order);

    /**
     * The retail price of the item that was added to the {@link Order} at the time that this was added. This is preferable
     * to use as opposed to checking the price of the item that was added from the catalog domain (like in
     * {@link DiscreteOrderItem}, using {@link DiscreteOrderItem#getSku()}'s retail price) since the price in the catalog
     * domain could have changed since the item was added to the {@link Order}.
     * 
     * @return
     */
    public Money getRetailPrice();

    /**
     * Sets the retail price of the item at the time that it is added to the {@link Order}
     * 
     * @param retailPrice
     */
    public void setRetailPrice(Money retailPrice);

    /**
     * The sale price of the item (e.g. SKU) that was added to the {@link Order} at the time that it was added.      
     * 
     * @return
     */
    public Money getSalePrice();

    /**
     * Sets the sale price of the item at the time that is is added to the {@link Order}
     * @param salePrice
     */
    public void setSalePrice(Money salePrice);

    /**
     * @deprecated 
     * Delegates to {@link #getTotalItemAdjustmentValue()} instead. 
     * 
     * @return
     */
    public Money getAdjustmentValue();

    /**
     * @deprecated
     * Calls {@link #getAverageUnitDisplayPrice()}
     * 
     * @return
     */
    public Money getPrice();

    /**
     * @deprecated
     * Do not use.
     * 
     * @param price
     */
    public void setPrice(Money price);

    /**
     * The quantity of this {@link OrderItem}.
     * 
     * @return
     */
    public int getQuantity();

    public void setQuantity(int quantity);

    public Category getCategory();

    public void setCategory(Category category);

    public List<CandidateItemOffer> getCandidateItemOffers();

    public void setCandidateItemOffers(List<CandidateItemOffer> candidateItemOffers);

    /**
     * Returns a unmodifiable List of OrderItemAdjustment.  To modify the List of OrderItemAdjustment, please
     * use the addOrderItemAdjustment or removeAllAdjustments methods.
     * @return a unmodifiable List of OrderItemAdjustment
     */
    public List<OrderItemAdjustment> getOrderItemAdjustments();

    public void setOrderItemAdjustments(List<OrderItemAdjustment> orderItemAdjustments);

    public PersonalMessage getPersonalMessage();

    public void setPersonalMessage(PersonalMessage personalMessage);

    public boolean isInCategory(String categoryName);

    public GiftWrapOrderItem getGiftWrapOrderItem();

    public void setGiftWrapOrderItem(GiftWrapOrderItem giftWrapOrderItem);

    public OrderItemType getOrderItemType();

    public void setOrderItemType(OrderItemType orderItemType);

    /**
     * Returns the amount of this item subject to tax
     * 
     * @return
     */
    public Money getTaxablePrice();

    /**
     * Default implementation uses {@link #getSalePrice()} &lt; {@link #getRetailPrice()}
     * 
     * @return
     */
    public boolean getIsOnSale();

    /**
     * Returns true if this item received a discount. 
     * @return
     */
    public boolean getIsDiscounted();

    /**
     * Post-condition should be that {@link #getPrice()} should be the most up-to-date.
     * 
     * @return
     */
    public boolean updatePrices();
    
    /**
     * Generally copied from the Sku.getName()
     * @return
     */
    public String getName();

    /**
     * Sets the name of this order item. 
     * @param name
     */
    public void setName(String name);

    /**
     * Copies this orderItem.
     * @return
     */
    public OrderItem clone();

    public void assignFinalPrice();
    
    public Money getCurrentPrice();
    
    /**
     * Returns the price of this item.   If the parameter allowSalesPrice is true, will 
     * return the sale price if applicable.
     * 
     * @param allowSalesPrice
     * @return
     */
    public Money getPriceBeforeAdjustments(boolean allowSalesPrice);
    
    /**
     * Used by the promotion engine to add offers that might apply to this orderItem.
     * @param candidateItemOffer
     */
    public void addCandidateItemOffer(CandidateItemOffer candidateItemOffer);
    
    /**
     * Removes all candidate offers.   Used by the promotion engine which subsequently adds 
     * the candidate offers that might apply back to this item.
     */
    public void removeAllCandidateItemOffers();
    
    /**
     * Removes all adjustment for this order item and reset the adjustment price.
     */
    public int removeAllAdjustments();
    
    public void accept(OrderItemVisitor visitor) throws PricingException;

    /**
     * A list of arbitrary attributes added to this item.
     */
    public Map<String,OrderItemAttribute> getOrderItemAttributes();

    /**
     * Sets the map of order item attributes.
     *
     * @param orderItemAttributes
     */
    public void setOrderItemAttributes(Map<String,OrderItemAttribute> orderItemAttributes);


    /**
     * Returns whether or not this item is taxable. If this flag is not set, it returns true by default
     * 
     * @return the taxable flag. If null, returns true
     */
    public Boolean isTaxable();

    /**
     * Sets whether or not this item is taxable.   Generally, this has been copied from the 
     * setting of the relevant SKU at the time it was added.
     * 
     * @param taxable
     */
    public void setTaxable(Boolean taxable);

    /**
     * @deprecated
     * No longer applicable as of Broadleaf 2.3.   Now managed via OrderItemPriceDetails
     * 
     * With prior versions, if the system automatically split an item to accommodate the promotion logic (e.g. buy one get one free),
     * then this value is set to the originalItemId.
     *
     * Returns null otherwise.
     *
     * @return
     */
    public Long getSplitParentItemId();

    /**
     * @deprecated
     * @param id
     */
    public void setSplitParentItemId(Long id);
    
    /**
     * The price of the item before discounts and prices.
     * 
     * Generally equivalent to {@link #getSalePrice()}
     * 
     * @return
     */
    public Money getUnitAmount();

    /**
     * Returns the average unit display price for the item.  
     * 
     * Some implementations may choose to show this on a view cart screen.    Due to fractional discounts,
     * it the display could represent a unit price that is off.  
     * 
     * For example, if this OrderItem had 3 items at $1 each and also received a $1 discount.   The net 
     * effect under the default rounding scenario would be an average price of $0.666666
     * 
     * Most systems would represent this as $0.67 as the discounted unit price; however, this amount times 3 ($2.01)
     * would not equal the getTotalAmount() which would be $2.00. 
     * 
     * @return
     */
    public Money getAverageUnitDisplayPrice();

    /**
     * Returns the average unit taxes for the item.  
     * 
     * Some implementations may choose to show this on a view cart screen.    Due to fractional discounts,
     * it the display could represent a unit tax amount that is off due to rounding.
     * 
     * Typically not used as most implementations will not display unit level taxes and will opt to show a 
     * single tax line item per order using Order.getTotalTax().
     *     
     * @see #getAverageUnitDisplayPrice() for an example of the rounding issue
     * 
     * @return
     */
    public Money getAverageUnitTaxes();

    /**
     * Returns the average unit item adjustments.  
     * 
     * Some implementations may choose to show this on a view cart screen.    Due to fractional discounts,
     * the display could represent a unit tax adjustment value that is off due to rounding.
     * 
     * Implementations wishing to show unit prices may choose instead to show the individual ItemPriceDetails 
     * instead of this value to avoid the rounding problem.    This would result in multiple cart item 
     * display rows for each OrderItem.
     *     
     * @see #getAverageUnitDisplayPrice() for an example of the rounding issue
     * 
     * @return
     */
    public Money getAverageUnitItemAdjustmentValue();

    /**
     * Returns the total amount of this orderItem by summing the TotalAmount of each of the ItemPriceDetails.
     * 
     * This should be the equivalent of multiplying the quantity times the getUnitAmount
     * 
     * 
     * @return
     */
    public Money getTotalAmount();

    /**
     * Returns the total item display price by summing the displayPrice from the ItemPriceDetails. 
     * 
     * @return
     */
    public Money getTotalItemDisplayPrice();

    /**
     * Returns the total return price by summing the returnPrice from the ItemPriceDetails. 
     * 
     * @return
     */
    public Money getTotalReturnPrice();

    /**
     * Returns the total taxes by summing the total taxes from the ItemPriceDetails. 
     * 
     * @return
     */
    public Money getTotalTaxes();

    /**
     * Returns the total of all adjustments on this line item.
     * Most implementations would NOT use this value.  It represents item adjustments as well as order level
     * adjustments that have been distributed to the item.
     * 
     * Typically, a view cart implementation would show the {@link getTotalItemDisplayPrice()} and indicate the
     * amount saved using the {@link getTotalItemAdjustmentValue()} 
     * 
     * @return
     */
    public Money getTotalAdjustmentValue();

    /**
     * Returns the total of all item level adjustments for this line item by summing the totalItemAdjustmentValue(s) 
     * from the ItemPriceDetails. 
     * 
     * @return
     */
    public Money getTotalItemAdjustmentValue();

    /**
     * Collection of priceDetails for this orderItem.    
     * 
     * Without discounts, an orderItem would have exactly 1 ItemPriceDetail.   When orderItem discounting or 
     * tax-calculations result in an orderItem having multiple prices like in a buy-one-get-one free example, 
     * the orderItem will get an additional ItemPriceDetail.  
     * 
     * Generally, an OrderItem will have 1 ItemPriceDetail record for each uniquely priced version of the item.
     */
    public List<OrderItemPriceDetail> getOrderItemPriceDetails();

}
