/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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

    public Money getRetailPrice();

    public void setRetailPrice(Money retailPrice);

    public Money getSalePrice();

    public void setSalePrice(Money salePrice);

    public Money getAdjustmentValue();

    public Money getPrice();

    public void setPrice(Money price);

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

    public boolean getIsOnSale();

    public boolean getIsDiscounted();

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


    public Boolean isTaxable();

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
