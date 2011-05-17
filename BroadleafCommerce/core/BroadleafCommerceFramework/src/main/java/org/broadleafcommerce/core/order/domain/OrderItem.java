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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.offer.service.discount.PromotionDiscount;
import org.broadleafcommerce.core.offer.service.discount.PromotionQualifier;
import org.broadleafcommerce.core.order.service.type.OrderItemType;
import org.broadleafcommerce.money.Money;

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

    public Money getAdjustmentPrice();

    public void setAdjustmentPrice(Money adjustmentPrice);

    public Money getPrice();

    public void setPrice(Money price);

    public void assignFinalPrice();

    public Money getCurrentPrice();

    public int getQuantity();

    public void setQuantity(int quantity);

    public Category getCategory();

    public void setCategory(Category category);

    public List<CandidateItemOffer> getCandidateItemOffers();

    public void setCandidateItemOffers(List<CandidateItemOffer> candidateItemOffers);

    public void addCandidateItemOffer(CandidateItemOffer candidateItemOffer);

    public void removeAllCandidateItemOffers();

    public boolean markForOffer();

    public int getMarkedForOffer();

    public boolean unmarkForOffer();

    public boolean isAllQuantityMarkedForOffer();

    /**
     * Returns a unmodifiable List of OrderItemAdjustment.  To modify the List of OrderItemAdjustment, please
     * use the addOrderItemAdjustment or removeAllAdjustments methods.
     * @return a unmodifiable List of OrderItemAdjustment
     */
    public List<OrderItemAdjustment> getOrderItemAdjustments();

    /**
     * Adds the adjustment to the order item's adjustment list and discounts the
     * order item's adjustment price by the value of the adjustment.
     * @param orderItemAdjustment
     */
    public void addOrderItemAdjustment(OrderItemAdjustment orderItemAdjustment);

    //public void setOrderItemAdjustments(List<OrderItemAdjustment> orderItemAdjustments);

    /**
     * Removes all adjustment for this order item and reset the adjustment price.
     */
    public int removeAllAdjustments();

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

    public boolean isNotCombinableOfferApplied();

	public boolean isHasOrderItemAdjustments();
	
	public boolean updatePrices();
	
	public String getName();

	public void setName(String name);
	
	public List<PromotionDiscount> getPromotionDiscounts();

	public void setPromotionDiscounts(List<PromotionDiscount> promotionDiscounts);

	public List<PromotionQualifier> getPromotionQualifiers();

	public void setPromotionQualifiers(List<PromotionQualifier> promotionQualifiers);

	public int getQuantityAvailableToBeUsedAsQualifier(Offer promotion);
	
	public int getQuantityAvailableToBeUsedAsTarget(Offer promotion);
	
	public Money getPriceBeforeAdjustments(boolean allowSalesPrice);
	
	public void addPromotionQualifier(CandidateItemOffer candidatePromotion, OfferItemCriteria itemCriteria, int quantity);
	
	public void addPromotionDiscount(CandidateItemOffer candidatePromotion, OfferItemCriteria itemCriteria, int quantity);
	
	public void clearAllNonFinalizedQuantities();
	
	public void finalizeQuantities();
	
	public OrderItem clone();
	
	public List<OrderItem> split();
	
	public void clearAllDiscount();
	
	public void clearAllQualifiers();
	
	public Map<String, BigDecimal> getAdditionalFees();

	public void setAdditionalFees(Map<String, BigDecimal> additionalFees);
	
}
