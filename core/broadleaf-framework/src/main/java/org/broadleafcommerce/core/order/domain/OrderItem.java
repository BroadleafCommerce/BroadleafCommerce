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

    public Long getId();

    public void setId(Long id);

    public Order getOrder();

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
     * 
     * @return
     */
    public Money getSalePrice();

    public void setSalePrice(Money salePrice);

    /**
     * Summation of all of the adjustments associated with this {@link OrderItem}
     * 
     * @return
     */
    public Money getAdjustmentValue();

    /**
     * The final price associated with this {@link OrderItem}. Note that this takes into account all of the taxes, fees and
     * promotions that are applied on this {@link OrderItem}. This will also use {@link #getSalePrice()} as a base rather
     * than {@link #getRetailPrice()} when {@link #getIsOnSale()} is true.
     * 
     * @return
     */
    public Money getPrice();

    /**
     * Sets the price of this {@link OrderItem} while taking into account all taxes, fees, promotions and whether or not the
     * item is on sale.
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

    public Money getTaxablePrice();

    /**
     * Default implementation uses {@link #getSalePrice()} &lt; {@link #getRetailPrice()}
     * 
     * @return
     */
    public boolean getIsOnSale();

    /**
     * Whethe or not this 
     * @return
     */
    public boolean getIsDiscounted();

    /**
     * Post-condition should be that {@link #getPrice()} should be the most up-to-date.
     * 
     * @return
     */
	public boolean updatePrices();
	
	public String getName();

	public void setName(String name);

	public OrderItem clone();

	public void assignFinalPrice();
	
	public Money getCurrentPrice();
	
	public Money getPriceBeforeAdjustments(boolean allowSalesPrice);
	
	public void addCandidateItemOffer(CandidateItemOffer candidateItemOffer);
	
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
     * Sets whether or not this item is taxable
     * 
     * @param taxable
     */
    public void setTaxable(Boolean taxable);

    /**
     * If the system automatically split an item to accommodate the promotion logic (e.g. buy one get one free),
     * then this value is set to the originalItemId.
     *
     * Returns null otherwise.
     *
     * @return
     */
    public Long getSplitParentItemId();

    public void setSplitParentItemId(Long id);
    
}
