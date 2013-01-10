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

package org.broadleafcommerce.core.offer.service.discount.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.broadleafcommerce.common.money.BankersRounding;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.offer.service.discount.PromotionDiscount;
import org.broadleafcommerce.core.offer.service.discount.PromotionQualifier;
import org.broadleafcommerce.core.offer.service.type.OfferItemRestrictionRuleType;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;

public class PromotableOrderItemImpl implements PromotableOrderItem {

    private static final long serialVersionUID = 1L;
    
    protected BigDecimal retailAdjustmentPrice; 
    protected BigDecimal saleAdjustmentPrice;
    protected List<PromotionDiscount> promotionDiscounts = new ArrayList<PromotionDiscount>();
    protected List<PromotionQualifier> promotionQualifiers = new ArrayList<PromotionQualifier>();
    protected DiscreteOrderItem delegate;
    protected PromotableOrder order;
    protected PromotableItemFactory itemFactory;    
    
    public PromotableOrderItemImpl(DiscreteOrderItem orderItem, PromotableOrder order, PromotableItemFactory itemFactory) {
        this.delegate = (DiscreteOrderItem) orderItem;
        this.order = order;
        this.itemFactory = itemFactory;
    }
    
    public DiscreteOrderItem getDelegate() {
        return delegate;
    }
    
    public void reset() {
        delegate = null;
    }
    
    public void assignFinalPrice() {
        delegate.setPrice(getCurrentPrice());
    }
    
    public Money getCurrentPrice() {
        delegate.updatePrices();
        Money currentPrice = null;
        
        if (delegate.getIsOnSale()) {
            currentPrice = delegate.getSalePrice();
        } else {
            currentPrice = delegate.getRetailPrice();
        }
        
        if (getRetailAdjustmentPrice() != null && getRetailAdjustmentPrice().lessThan(currentPrice)) {
            currentPrice = getRetailAdjustmentPrice();
        }
        
        if (getSaleAdjustmentPrice() != null && getSaleAdjustmentPrice().lessThan(currentPrice)) {
            currentPrice = getSaleAdjustmentPrice();
        }

        return currentPrice;
    }
    
    public void computeAdjustmentPrice() {
        delegate.updatePrices();

        Money tempDiscountedRetailPrice = delegate.getRetailPrice();
        Money tempDiscountedSalePrice = delegate.getSalePrice();
        
        for (OrderItemAdjustment adjustment : delegate.getOrderItemAdjustments()) {
            Money salesValue = adjustment.getSalesPriceValue();
            Money retailValue = adjustment.getRetailPriceValue();

            if (adjustment.getOffer().getApplyDiscountToSalePrice()) {
                if (adjustment.getOrderItem().getIsOnSale()) {
                    tempDiscountedSalePrice = tempDiscountedSalePrice.subtract(salesValue);
                }
            }
            tempDiscountedRetailPrice = tempDiscountedRetailPrice.subtract(retailValue);
        }

        if (tempDiscountedSalePrice != null) {
            saleAdjustmentPrice = tempDiscountedSalePrice.getAmount();
        }
        retailAdjustmentPrice = tempDiscountedRetailPrice.getAmount();
    }

    public void resetAdjustmentPrice() {
        delegate.updatePrices();

        Money tempDiscountedRetailPrice = delegate.getRetailPrice();
        Money tempDiscountedSalePrice = delegate.getSalePrice();

        if (tempDiscountedSalePrice != null) {
            saleAdjustmentPrice = tempDiscountedSalePrice.getAmount();
        }
        retailAdjustmentPrice = tempDiscountedRetailPrice.getAmount();
    }
    
    public void addOrderItemAdjustment(PromotableOrderItemAdjustment orderItemAdjustment) {
        orderItemAdjustment.computeAdjustmentValues();
        delegate.getOrderItemAdjustments().add(orderItemAdjustment.getDelegate());
        order.resetTotalitarianOfferApplied();
        computeAdjustmentPrice();
    }

    public int removeAllAdjustments() {
        int removedAdjustmentCount = delegate.removeAllAdjustments();
        retailAdjustmentPrice = null;
        saleAdjustmentPrice = null;
        order.resetTotalitarianOfferApplied();
        
        if (promotionDiscounts != null) {
            promotionDiscounts.clear();
        }
        if (promotionQualifiers != null) {
            promotionQualifiers.clear();
        }
        assignFinalPrice();
        return removedAdjustmentCount;
    }

    public Money getRetailAdjustmentPrice() {
        return retailAdjustmentPrice == null ? null : new Money(retailAdjustmentPrice, delegate.getRetailPrice().getCurrency(), retailAdjustmentPrice.scale()==0? BankersRounding.DEFAULT_SCALE:retailAdjustmentPrice.scale());
    }

    public void setRetailAdjustmentPrice(Money retailAdjustmentPrice) {
        this.retailAdjustmentPrice = Money.toAmount(retailAdjustmentPrice);        
    }

    public Money getSaleAdjustmentPrice() {
        return saleAdjustmentPrice == null ? null : new Money(saleAdjustmentPrice, delegate.getRetailPrice().getCurrency(), saleAdjustmentPrice.scale()==0? BankersRounding.DEFAULT_SCALE:saleAdjustmentPrice.scale());
    }

    public void setSaleAdjustmentPrice(Money saleAdjustmentPrice) {
        this.saleAdjustmentPrice = Money.toAmount(saleAdjustmentPrice);
    }

    public boolean isNotCombinableOfferApplied() {
        for (OrderItemAdjustment orderItemAdjustment : delegate.getOrderItemAdjustments()) {
            boolean notCombineableApplied = !orderItemAdjustment.getOffer().isCombinableWithOtherOffers() || (orderItemAdjustment.getOffer().isTotalitarianOffer() != null && orderItemAdjustment.getOffer().isTotalitarianOffer());
            if (notCombineableApplied) return true;
        }
        
        return false;
    }
    
    public boolean isHasOrderItemAdjustments() {
        return delegate.getOrderItemAdjustments() != null && delegate.getOrderItemAdjustments().size() > 0;
    }
    
    public List<PromotionDiscount> getPromotionDiscounts() {
        return promotionDiscounts;
    }

    public void setPromotionDiscounts(List<PromotionDiscount> promotionDiscounts) {
        this.promotionDiscounts = promotionDiscounts;
    }

    public List<PromotionQualifier> getPromotionQualifiers() {
        return promotionQualifiers;
    }

    public void setPromotionQualifiers(List<PromotionQualifier> promotionQualifiers) {
        this.promotionQualifiers = promotionQualifiers;
    }

    public int getQuantityAvailableToBeUsedAsQualifier(Offer promotion) {
        int qtyAvailable = delegate.getQuantity();
        // Any quantities of this item that have already received the promotion are not eligible.
        for (PromotionDiscount promotionDiscount : promotionDiscounts) {
            if (promotionDiscount.getPromotion().equals(promotion)) {
                qtyAvailable = qtyAvailable - promotionDiscount.getQuantity();
            } else {
                // Item's that receive discounts are also qualifiers
                OfferItemRestrictionRuleType qualifierType = promotionDiscount.getPromotion().getOfferItemTargetRuleType();
                if (OfferItemRestrictionRuleType.NONE.equals(qualifierType) || OfferItemRestrictionRuleType.TARGET.equals(qualifierType)) {
                    qtyAvailable = qtyAvailable - promotionDiscount.getQuantity();
                }
            }
        }
        
        // Any quantities of this item that have already been used as a qualifier for this promotion or for 
        // another promotion that has a qualifier type of NONE or TARGET_ONLY cannot be used for this promotion
        for (PromotionQualifier promotionQualifier : promotionQualifiers) {
            if (promotionQualifier.getPromotion().equals(promotion)) {
                qtyAvailable = qtyAvailable - promotionQualifier.getQuantity();
            } else {
                OfferItemRestrictionRuleType qualifierType = promotionQualifier.getPromotion().getOfferItemQualifierRuleType();
                if (OfferItemRestrictionRuleType.NONE.equals(qualifierType) || OfferItemRestrictionRuleType.TARGET.equals(qualifierType)) {
                    qtyAvailable = qtyAvailable - promotionQualifier.getQuantity();
                }
            }
        }
        return qtyAvailable;
    }
    
    public int getQuantityAvailableToBeUsedAsTarget(Offer promotion) {
        int qtyAvailable = delegate.getQuantity();
        
        // 1. Any quantities of this item that have already received the promotion are not eligible.
        // 2. If this promotion is not stackable then any quantities that have received discounts
        //    from other promotions cannot receive this discount
        // 3. If this promotion is stackable then any quantities that have received discounts from
        //    other stackable promotions are eligible to receive this discount as well
        boolean stackable = promotion.isStackable();
        
        // Any quantities of this item that have already received the promotion are not eligible.
        for (PromotionDiscount promotionDiscount : promotionDiscounts) {
            if (promotionDiscount.getPromotion().equals(promotion) || ! stackable) {
                qtyAvailable = qtyAvailable - promotionDiscount.getQuantity();
            } else if (promotionDiscount.getPromotion().isStackable()) {
                // The other promotion is Stackable, but we must make sure that the item qualifier also allows
                // it to be reused as a target.   
                OfferItemRestrictionRuleType qualifierType = promotionDiscount.getPromotion().getOfferItemTargetRuleType();
                if (OfferItemRestrictionRuleType.NONE.equals(qualifierType) || OfferItemRestrictionRuleType.QUALIFIER.equals(qualifierType)) {
                    qtyAvailable = qtyAvailable - promotionDiscount.getQuantity();
                }
            }
        }
        
        // 4.  Any quantities of this item that have been used as a qualifier for this promotion are not eligible as targets
        // 5.  Any quantities of this item that have been used as a qualifier for another promotion that does not allow the qualifier to be reused
        //     must be deduced from the qtyAvailable.
        for (PromotionQualifier promotionQualifier : promotionQualifiers) {
            if (promotionQualifier.getPromotion().equals(promotion)) {
                qtyAvailable = qtyAvailable - promotionQualifier.getQuantity();
            } else {
                OfferItemRestrictionRuleType qualifierType = promotionQualifier.getPromotion().getOfferItemQualifierRuleType();
                if (OfferItemRestrictionRuleType.NONE.equals(qualifierType) || OfferItemRestrictionRuleType.QUALIFIER.equals(qualifierType)) {
                    qtyAvailable = qtyAvailable - promotionQualifier.getQuantity();
                }
            }
        }
        
        return qtyAvailable;
    }
    
    public void addPromotionQualifier(PromotableCandidateItemOffer candidatePromotion, OfferItemCriteria itemCriteria, int quantity) {
        PromotionQualifier pq = lookupOrCreatePromotionQualifier(candidatePromotion);
        pq.incrementQuantity(quantity);
        pq.setItemCriteria(itemCriteria);
    }
    
    public void addPromotionDiscount(PromotableCandidateItemOffer candidatePromotion, OfferItemCriteria itemCriteria, int quantity) {
        PromotionDiscount pd = lookupOrCreatePromotionDiscount(candidatePromotion);
        if (pd == null) {
            return;
        }
        pd.incrementQuantity(quantity);
        pd.setItemCriteria(itemCriteria);
        pd.setCandidateItemOffer(candidatePromotion);
        candidatePromotion.addUse();
    }
    
    public PromotionQualifier lookupOrCreatePromotionQualifier(PromotableCandidateItemOffer candidatePromotion) {
        Offer promotion = candidatePromotion.getOffer();
        for(PromotionQualifier pq : promotionQualifiers) {
            if (pq.getPromotion().equals(promotion)) {
                return pq;
            }
        }
        
        PromotionQualifier pq = new PromotionQualifier();
        pq.setPromotion(promotion);
        promotionQualifiers.add(pq);
        return pq;
    }
    
    public PromotionDiscount lookupOrCreatePromotionDiscount(PromotableCandidateItemOffer candidatePromotion) {
        Offer promotion = candidatePromotion.getOffer();
        for(PromotionDiscount pd : promotionDiscounts) {
            if (pd.getPromotion().equals(promotion)) {
                return pd;
            }
        }
        
        PromotionDiscount pd = new PromotionDiscount();
        pd.setPromotion(promotion);
        
        promotionDiscounts.add(pd);
        return pd;
    }
    
    public void clearAllNonFinalizedQuantities() {
        clearAllNonFinalizedDiscounts();
        clearAllNonFinalizedQualifiers();
    }
    
    public void clearAllDiscount() {
        promotionDiscounts.clear();
    }
    
    public void clearAllQualifiers() {
        promotionQualifiers.clear();
    }
    
    public void clearAllNonFinalizedDiscounts() {
        Iterator<PromotionDiscount> promotionDiscountIterator = promotionDiscounts.iterator();
        while (promotionDiscountIterator.hasNext()) {
            PromotionDiscount promotionDiscount = promotionDiscountIterator.next();
            if (promotionDiscount.getFinalizedQuantity() == 0) {
                // If there are no quantities of this item that are finalized, then remove the item.
                promotionDiscountIterator.remove();
            } else {
                // Otherwise, set the quantity to the number of finalized items.
                promotionDiscount.setQuantity(promotionDiscount.getFinalizedQuantity());
            }
        }   
    }
    
    public void clearAllNonFinalizedQualifiers() {
        Iterator<PromotionQualifier> promotionQualifierIterator = promotionQualifiers.iterator();
        while (promotionQualifierIterator.hasNext()) {
            PromotionQualifier promotionQualifier = promotionQualifierIterator.next();
            if (promotionQualifier.getFinalizedQuantity() == 0) {
                // If there are no quantities of this item that are finalized, then remove the item.
                promotionQualifierIterator.remove();
            } else {
                // Otherwise, set the quantity to the number of finalized items.
                promotionQualifier.setQuantity(promotionQualifier.getFinalizedQuantity());
            }
        }   
    }
    
    public void finalizeQuantities() {
        for (PromotionDiscount promotionDiscount : promotionDiscounts) {
            promotionDiscount.setFinalizedQuantity(promotionDiscount.getQuantity());
        }
        for (PromotionQualifier promotionQualifier : promotionQualifiers) {
            promotionQualifier.setFinalizedQuantity(promotionQualifier.getQuantity());
        }
    }
    
    public int getPromotionDiscountMismatchQuantity() {
        Iterator<PromotionDiscount> promotionDiscountIterator = promotionDiscounts.iterator();
        while (promotionDiscountIterator.hasNext()) {
            PromotionDiscount promotionDiscount = promotionDiscountIterator.next();
            if (promotionDiscount.getQuantity() != delegate.getQuantity()) {
                return promotionDiscount.getQuantity();
            }
        }
        return 0;
    }
    
    public List<PromotableOrderItem> split() {  
        List<PromotableOrderItem> splitItems = null;
        if (delegate.getQuantity() != 1) {
            int discountQty = getPromotionDiscountMismatchQuantity();
            if (discountQty != 0) {
                // Item needs to be split.
                splitItems = new ArrayList<PromotableOrderItem>();
                PromotableOrderItem firstItem = (PromotableOrderItem) clone();
                PromotableOrderItem secondItem = (PromotableOrderItem) clone();
                splitItems.add(firstItem);
                splitItems.add(secondItem);
                
                // set the quantity
                int firstItemQty = discountQty;
                int secondItemQty = delegate.getQuantity() - discountQty;
                firstItem.getDelegate().setQuantity(firstItemQty);
                secondItem.getDelegate().setQuantity(secondItemQty);
                
                // distribute the qualifiers
                for(PromotionQualifier pq : promotionQualifiers) {
                    if (pq.getQuantity() > firstItemQty) {
                        PromotionQualifier pq1 = pq.copy();
                        pq1.resetQty(firstItemQty);
                        firstItem.getPromotionQualifiers().add(pq1);
                        
                        PromotionQualifier pq2 = pq.copy();
                        pq2.resetQty(pq.getQuantity() - firstItemQty);
                        secondItem.getPromotionQualifiers().add(pq2);
                        
                    } else {
                        firstItem.getPromotionQualifiers().add(pq);
                    }
                }
                
                // distribute the discounts
                for(PromotionDiscount pd : promotionDiscounts) {
                    if (pd.getQuantity() > firstItemQty) {
                        PromotionDiscount pd1 = pd.copy();
                        pd1.resetQty(firstItemQty);
                        firstItem.getPromotionDiscounts().add(pd1);
                        
                        PromotionDiscount pd2 = pd.copy();
                        pd2.resetQty(pd.getQuantity() - firstItemQty);
                        secondItem.getPromotionDiscounts().add(pd2);
                    } else {
                        firstItem.getPromotionDiscounts().add(pd);
                    }
                }
            }
        }
        return splitItems;
    }
    
    public int getQuantity() {
        return delegate.getQuantity();
    }
    
    public void setQuantity(int quantity) {
        delegate.setQuantity(quantity);
    }
    
    public Sku getSku() {
        return delegate.getSku();
    }
    
    public Money getPriceBeforeAdjustments(boolean allowSalesPrice) {
        return delegate.getPriceBeforeAdjustments(allowSalesPrice);
    }
    
    public Money getSalePrice() {
        return delegate.getSalePrice();
    }
    
    public Money getRetailPrice() {
        return delegate.getRetailPrice();
    }
    
    public void addCandidateItemOffer(PromotableCandidateItemOffer candidateItemOffer) {
        delegate.addCandidateItemOffer(candidateItemOffer.getDelegate());
    }
    
    public PromotableOrderItem clone() {
        PromotableOrderItem copy = itemFactory.createPromotableOrderItem((DiscreteOrderItem) delegate.clone(), order);
        copy.setRetailAdjustmentPrice(getRetailAdjustmentPrice());
        copy.setSaleAdjustmentPrice(getSaleAdjustmentPrice());
        
        return copy;
    }

    /**
     * Removes all zero based adjustments and sets the adjusted price on the delegate.
     *
     * For remaining adjustments sets the value to the retail or sale price.
     *
     * @param useSaleAdjustments
     * @return
     */
    @Override
    public int fixAdjustments(boolean useSaleAdjustments) {
        
        Iterator<OrderItemAdjustment> adjustmentsIterator = delegate.getOrderItemAdjustments().iterator();
        int removeCount = 0;
        
        while (adjustmentsIterator.hasNext()) {
            OrderItemAdjustment currentAdjustment = adjustmentsIterator.next();
            if (useSaleAdjustments) {
                if (currentAdjustment.getSalesPriceValue().lessThanOrEqual(BigDecimal.ZERO)) {
                    currentAdjustment.setOrderItem(null);
                    adjustmentsIterator.remove();
                    removeCount++;
                } else {
                    currentAdjustment.setAppliedToSalePrice(true);
                    currentAdjustment.setValue(currentAdjustment.getSalesPriceValue());
                }
            } else {
                if (currentAdjustment.getRetailPriceValue().lessThanOrEqual(BigDecimal.ZERO)) {
                    currentAdjustment.setOrderItem(null);
                    adjustmentsIterator.remove();
                    removeCount++;
                } else {
                    currentAdjustment.setAppliedToSalePrice(false);
                    currentAdjustment.setValue(currentAdjustment.getRetailPriceValue());
                }
            }
        }
        return removeCount;
    }

}
