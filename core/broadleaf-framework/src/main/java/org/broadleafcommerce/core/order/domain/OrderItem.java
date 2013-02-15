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
import org.broadleafcommerce.core.order.service.type.OrderItemType;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface OrderItem extends Serializable, Cloneable {

    /**
     * The unique identifier of this OrderItem
     * @return
     */
    public Long getId();

    /**
     * Sets the unique id of the OrderItem.   Typically left null for new items and Broadleaf will
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
     * @deprecated The retail price used in sales calculations will be set during 
     * price finalization but otherwise is not used by the system.
     * 
     * If trying to override a price or set a fixed price, use {@link #setOverridePrice(Money)}
     * along with {@link #setDiscountingAllowed(boolean)}
     * 
     * @param retailPrice
     */
    public void setRetailPrice(Money retailPrice);

    /**
     * The sale price of the item (e.g. SKU) that was added to the {@link Order} at the time that that updatePrices
     * was last called.      
     * 
     * @return
     */
    public Money getSalePrice();

    /**
     * @deprecated - The sale price used in price calculations will be
     * set by the system when prices are finalized prior to order submission.
     * 
     * If trying to override a price or set a fixed price, use {@link #setBasePrice(Money)}
     * along with {@link #setDiscountingAllowed(boolean)}
     * 
     * @param salePrice
     */
    public void setSalePrice(Money salePrice);

    /**
     * @deprecated 
     * Delegates to {@link #getAverageAdjustmentValue()} instead but this method is of little
     * or no value in most practical applications unless only simple promotions are being used.
     * 
     * @return
     */
    public Money getAdjustmentValue();

    /**
     * @deprecated
     * Delegates to {@link #getAveragePrice()}
     * 
     * @return
     */
    public Money getPrice();

    /**
     * @deprecated
     * Should not be called.   Consider {@link setOverridePrice(Money price)} along with {@link #setDiscountingAllowed(boolean)}
     * to set the price to a final value.    Otherwise, the system will compute the price as part of the
     * discounting and dynamic price evaluation.     
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

    /**
     * Sets the quantity of this item
     */
    public void setQuantity(int quantity);
    
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

    /**
     * Returns the list of orderItem price details.
     * @see {@link #getOrderItemPriceDetails()}
     * @param orderItemPriceDetails
     */
    public void setOrderItemPriceDetails(List<OrderItemPriceDetail> orderItemPriceDetails);

    public Category getCategory();

    public void setCategory(Category category);

    public List<CandidateItemOffer> getCandidateItemOffers();

    public void setCandidateItemOffers(List<CandidateItemOffer> candidateItemOffers);

    /**    
     * Returns item level adjustment for versions of Broadleaf Commerce prior to 2.3.0 which replaced
     * this concept with OrderItemPriceDetail adjustments.
     * @return a List of OrderItemAdjustment
     */
    public List<OrderItemAdjustment> getOrderItemAdjustments();

    /**
     * @deprecated
     * Item level adjustments are now stored at the OrderItemPriceDetail level instead to 
     * prevent unnecessary item splitting of OrderItems when evaluating promotions 
     * in the pricing engine.
     */
    public void setOrderItemAdjustments(List<OrderItemAdjustment> orderItemAdjustments);

    public PersonalMessage getPersonalMessage();

    public void setPersonalMessage(PersonalMessage personalMessage);

    public boolean isInCategory(String categoryName);

    public GiftWrapOrderItem getGiftWrapOrderItem();

    public void setGiftWrapOrderItem(GiftWrapOrderItem giftWrapOrderItem);

    public OrderItemType getOrderItemType();

    public void setOrderItemType(OrderItemType orderItemType);

    /**
     * @deprecated
     * If the item is taxable, returns {@link #getAveragePrice()}   
     * 
     * It is recommended instead that tax calculation engines use the {@link #getTotalTaxableAmount()} which provides the taxable 
     * total for all quantities of this item.    This method suffers from penny rounding errors in some
     * situations.         
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
     * If true, this item can be discounted..
     */
    public boolean isDiscountingAllowed();
    
    /**
     * Turns off discount processing for this line item.
     * @param disableDiscounts
     */
    public void setDiscountingAllowed(boolean discountingAllowed);

    /**
     * Returns true if this item received a discount. 
     * @return
     */
    public boolean getIsDiscounted();
    
    /**
     * Generally copied from the Sku.getName()
     * @return
     */
    public String getName();

    /**
     * Used to reset the base price of the item that the pricing engine uses. 
     * 
     * Generally, this will update the retailPrice and salePrice based on the 
     * corresponding value in the SKU.   
     * 
     * Since prices can change based on system activities such as 
     * locale changes and customer authentication, this method is used to 
     * ensure that all cart items reflect the current base price before
     * executing other pricing / adjustment operations. 
     * 
     * Other known scenarios that can effect the base prices include the automatic bundling 
     * or loading a stale cart from the database.
     * 
     * See notes in subclasses for specific behavior of this method.
     *      
     * @return true if the base prices changed as a result of this call
     */
    public boolean updateSaleAndRetailBasePrices();

    /**
     * Sets the name of this order item. 
     * @param name
     */
    public void setName(String name);

    public OrderItem clone();

    public void assignFinalPrice();
    
    public Money getCurrentPrice();
    
    /**
     * Returns the unit price of this item.   If the parameter allowSalesPrice is true, will 
     * return the sale price if one exists.
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
     * Returns the average unit display price for the item.  
     * 
     * Some implementations may choose to show this on a view cart screen.    Due to fractional discounts,
     * it the display could represent a unit price that is off.  
     * 
     * For example, if this OrderItem had 3 items at $1 each and also received a $1 discount.   The net 
     * effect under the default rounding scenario would be an average price of $0.666666
     * 
     * Most systems would represent this as $0.67 as the discounted unit price; however, this amount times 3 ($2.01)
     * would not equal the getTotalPrice() which would be $2.00.    For this reason, it is not recommended
     * that implementations utilize this field.    Instead, they may choose not to show the unit price or show 
     * multiple prices by looping through the OrderItemPriceDetails. 
     * 
     * @return
     */
    public Money getAveragePrice();

    /**
     * Returns the average unit item adjustments.
     * 
     * For example, if this item has a quantity of 2 at a base-price of $10 and a 10% discount applies to both
     * then this method would return $1. 
     * 
     * Some implementations may choose to show this on a view cart screen.    Due to fractional discounts,
     * the display could represent a unit adjustment value that is off due to rounding.    See {@link #getAveragePrice()}
     * for an example of this.
     * 
     * Implementations wishing to show unit prices may choose instead to show the individual OrderItemPriceDetails 
     * instead of this value to avoid the rounding problem.    This would result in multiple cart item 
     * display rows for each OrderItem.
     * 
     * Alternatively, the cart display should use {@link #getTotalAdjustmentValue()}.
     * 
     * @return
     */
    public Money getAverageAdjustmentValue();

    /**
     * Returns the total for all item level adjustments.
     * 
     * For example, if the item has a 2 items priced at $10 a piece and a 10% discount applies to both
     * quantities.    This method would return $2. 
     * 
     * @return
     */
    public Money getTotalAdjustmentValue();

    /**
     * Returns the total price to be paid for this order item including item-level adjustments.
     * 
     * It does not include the effect of order level adjustments.   Calculated by looping through
     * the orderItemPriceDetails
     * 
     * @return
     */
    public Money getTotalPrice();
    
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
     * Returns the total amount of this item subject to taxes.
     * 
     * This is calculated as {@link #getTotalPrice()} - {@link #getTaxableProratedOrderAdjustment()}
     * 
     * @return
     */
    public Money getTotalTaxableAmount();    

    /**
     * Returns the total taxes paid (or due) for this OrderItem. 
     * 
     * @return
     */
    public Money getTotalTax();
    
    /**
     * Sets the total taxes paid (or due) for this OrderItem. 
     * 
     * @return
     */
    public void setTotalTax(Money tax);

    /**
     * Order level discounts need to be distributed to the item level.    Most of these will 
     * effect the taxes owed but in some cases they may not.    This method returns the 
     * total of the total order adjustments that do reduce the overall taxes.
     * 
     * @return
     */
    public Money getTaxableProratedOrderAdjustment();
    
    /**
     * Sets the relative order level discounts that are distributed to the item level and that should
     * reduce the amount of taxes paid.        
     */
    public void setTaxableProratedOrderAdjustment(Money taxableProratedOrderAdjustment);

    /**
     * The value of an order-level offer gets distributed to each of the OrderItems.   This field 
     * represents that value.   
     * 
     * @return
     */
    public Money getProratedOrderAdjustment();

    /**
     * Sets the pro-rated amount of order-level offer that benefits this OrderItem.
     * 
     */
    public void setProratedOrderAdjustment(Money proratedOrderAdjustmentAmount);

    /**
     * This field represents the value of fulfillment charges gets distributed to each of the OrderItems   
     * 
     * @return
     */
    public Money getProratedFulfillmentCharges();

    /**
     * Sets the prorated fulfillment charges that apply to this OrderItem   
     * 
     */
    public void setProratedFulfillmentCharges(Money proratedFulfillmentCharges);
}
