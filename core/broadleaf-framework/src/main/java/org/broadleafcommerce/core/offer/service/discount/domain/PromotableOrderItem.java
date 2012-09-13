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

package org.broadleafcommerce.core.offer.service.discount.domain;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.service.discount.PromotionDiscount;
import org.broadleafcommerce.core.offer.service.discount.PromotionQualifier;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;

import java.util.List;
import java.util.Set;

public interface PromotableOrderItem {
	 
	 /**
     * Adds the adjustment to the order item's adjustment list and discounts the
     * order item's adjustment price by the value of the adjustment.
     * @param orderItemAdjustment
     */
    public void addOrderItemAdjustment(PromotableOrderItemAdjustment orderItemAdjustment);

    /**
     * The price after discounts if all applicable discounts are applied
     * to the retail price.
     *
     * @return
     */
    public Money getRetailAdjustmentPrice();


    public void setRetailAdjustmentPrice(Money adjustmentPrice);

    /**
     * The price after discounts if all applicable discounts are applied
     * to the sale price.
     *
     */
    public Money getSaleAdjustmentPrice();

    public void setSaleAdjustmentPrice(Money adjustmentPrice);


    public boolean isNotCombinableOfferApplied();
    
    public boolean isHasOrderItemAdjustments();
    
    public List<PromotionDiscount> getPromotionDiscounts();
    
    public void setPromotionDiscounts(List<PromotionDiscount> promotionDiscounts);
    
    public List<PromotionQualifier> getPromotionQualifiers();
    
    public void setPromotionQualifiers(List<PromotionQualifier> promotionQualifiers);
    
    public int getQuantityAvailableToBeUsedAsQualifier(Offer promotion);
    
    public int getQuantityAvailableToBeUsedAsTarget(Offer promotion);
    
    public void addPromotionQualifier(PromotableCandidateItemOffer candidatePromotion, OfferItemCriteria itemCriteria, int quantity);
	
	public void addPromotionDiscount(PromotableCandidateItemOffer candidatePromotion, Set<OfferItemCriteria> itemCriteria, int quantity);
	
	public void clearAllNonFinalizedQuantities();
	
	public void clearAllDiscount();
	
	public void clearAllQualifiers();
	
	public void finalizeQuantities();
	
	public List<PromotableOrderItem> split();
	
	public DiscreteOrderItem getDelegate();

    public void setDelegate(DiscreteOrderItem discreteOrderItem);
	
	public void reset();
	
	public PromotionQualifier lookupOrCreatePromotionQualifier(PromotableCandidateItemOffer candidatePromotion);
	
	public PromotionDiscount lookupOrCreatePromotionDiscount(PromotableCandidateItemOffer candidatePromotion);
	
	public void clearAllNonFinalizedDiscounts();
	
	public void clearAllNonFinalizedQualifiers();
	
	public int getPromotionDiscountMismatchQuantity();
	
	public void computeAdjustmentPrice();
	
	public int removeAllAdjustments();
	
	public void assignFinalPrice();
	
	public Money getCurrentPrice();
	
	public int getQuantity();
	
	public void setQuantity(int quantity);
	
	public Sku getSku();
	
	public Money getPriceBeforeAdjustments(boolean allowSalesPrice);
	
	public Money getSalePrice();
	
	public Money getRetailPrice();
	
	public void addCandidateItemOffer(PromotableCandidateItemOffer candidateItemOffer);
	
	public PromotableOrderItem clone();

    /**
     * Removes all zero based adjustments and sets the adjusted price on the delegate.
     *
     * @param useSaleAdjustments
     * @return
     */
    int fixAdjustments(boolean useSaleAdjustments);

    public void resetAdjustmentPrice();

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
