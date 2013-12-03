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
package org.broadleafcommerce.core.offer.service.discount.domain;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.service.discount.PromotionDiscount;
import org.broadleafcommerce.core.offer.service.discount.PromotionQualifier;
import org.broadleafcommerce.core.offer.service.type.OfferItemRestrictionRuleType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class PromotableOrderItemPriceDetailImpl implements PromotableOrderItemPriceDetail {

    protected PromotableOrderItem promotableOrderItem;
    protected List<PromotableOrderItemPriceDetailAdjustment> promotableOrderItemPriceDetailAdjustments = new ArrayList<PromotableOrderItemPriceDetailAdjustment>();
    protected List<PromotionDiscount> promotionDiscounts = new ArrayList<PromotionDiscount>();
    protected List<PromotionQualifier> promotionQualifiers = new ArrayList<PromotionQualifier>();
    protected int quantity;
    protected boolean useSaleAdjustments = false;
    protected boolean adjustmentsFinalized = false;
    protected Money adjustedTotal;

    public PromotableOrderItemPriceDetailImpl(PromotableOrderItem promotableOrderItem, int quantity) {
        this.promotableOrderItem = promotableOrderItem;
        this.quantity = quantity;
    }
    
    @Override
    public boolean isAdjustmentsFinalized() {
        return adjustmentsFinalized;
    }
    
    @Override
    public void setAdjustmentsFinalized(boolean adjustmentsFinalized) {
        this.adjustmentsFinalized = adjustmentsFinalized;
    }

    @Override
    public void addCandidateItemPriceDetailAdjustment(PromotableOrderItemPriceDetailAdjustment itemAdjustment) {
        promotableOrderItemPriceDetailAdjustments.add(itemAdjustment);
    }

    @Override
    public List<PromotableOrderItemPriceDetailAdjustment> getCandidateItemAdjustments() {
        return Collections.unmodifiableList(promotableOrderItemPriceDetailAdjustments);
    }

    @Override
    public boolean hasNonCombinableAdjustments() {
        for (PromotableOrderItemPriceDetailAdjustment adjustment : promotableOrderItemPriceDetailAdjustments) {
            if (!adjustment.isCombinable()) {
                return true;
            }
        }
        return false;
    }

    protected boolean hasOrderItemAdjustments() {
        return promotableOrderItemPriceDetailAdjustments.size() > 0;
    }

    @Override
    public boolean isTotalitarianOfferApplied() {
        for (PromotableOrderItemPriceDetailAdjustment adjustment : promotableOrderItemPriceDetailAdjustments) {
            if (adjustment.isTotalitarian()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isNonCombinableOfferApplied() {
        for (PromotableOrderItemPriceDetailAdjustment adjustment : promotableOrderItemPriceDetailAdjustments) {
            if (!adjustment.isCombinable()) {
                return true;
            }
        }
        return false;
    }
    
    public Money calculateSaleAdjustmentUnitPrice() {
        Money returnPrice = promotableOrderItem.getSalePriceBeforeAdjustments();
        if (returnPrice == null) {
            returnPrice = promotableOrderItem.getRetailPriceBeforeAdjustments();
        }
        for (PromotableOrderItemPriceDetailAdjustment adjustment : promotableOrderItemPriceDetailAdjustments) {
            returnPrice = returnPrice.subtract(adjustment.getSaleAdjustmentValue());
        }
        return returnPrice;
    }
    
    public Money calculateRetailAdjustmentUnitPrice() {
        Money returnPrice = promotableOrderItem.getRetailPriceBeforeAdjustments();
        for (PromotableOrderItemPriceDetailAdjustment adjustment : promotableOrderItemPriceDetailAdjustments) {
            returnPrice = returnPrice.subtract(adjustment.getRetailAdjustmentValue());
        }
        return returnPrice;
    }

    /**
     * This method will check to see if the salePriceAdjustments or retailPriceAdjustments are better
     * and remove those that should not apply.
     * @return 
     */
    public void chooseSaleOrRetailAdjustments() {
        adjustmentsFinalized = true;
        adjustedTotal = null;
        this.useSaleAdjustments = Boolean.FALSE;
        Money salePriceBeforeAdjustments = promotableOrderItem.getSalePriceBeforeAdjustments();
        Money retailPriceBeforeAdjustments = promotableOrderItem.getRetailPriceBeforeAdjustments();

        if (hasOrderItemAdjustments()) {
            Money saleAdjustmentPrice = calculateSaleAdjustmentUnitPrice();
            Money retailAdjustmentPrice = calculateRetailAdjustmentUnitPrice();
            
            if (promotableOrderItem.isOnSale()) {
                if (saleAdjustmentPrice.lessThanOrEqual(retailAdjustmentPrice)) {
                    this.useSaleAdjustments = Boolean.TRUE;
                    adjustedTotal = saleAdjustmentPrice;
                } else {
                    adjustedTotal = retailAdjustmentPrice;
                }

                if (!adjustedTotal.lessThan(salePriceBeforeAdjustments)) {
                    // Adjustments are not as good as the sale price.  So, clear them and use the sale price.                    
                    promotableOrderItemPriceDetailAdjustments.clear();
                    adjustedTotal = salePriceBeforeAdjustments;
                }
            } else {
                if (!retailAdjustmentPrice.lessThan(promotableOrderItem.getRetailPriceBeforeAdjustments())) {
                    // Adjustments are not as good as the retail price.
                    promotableOrderItemPriceDetailAdjustments.clear();
                    adjustedTotal = retailPriceBeforeAdjustments;
                } else {
                    adjustedTotal = retailAdjustmentPrice;
                }
            }

            if (useSaleAdjustments) {
                removeRetailOnlyAdjustments();
            }

            removeZeroDollarAdjustments(useSaleAdjustments);

            finalizeAdjustments(useSaleAdjustments);
        }

        if (adjustedTotal == null) {
            if (salePriceBeforeAdjustments != null) {
                this.useSaleAdjustments = true;
                adjustedTotal = salePriceBeforeAdjustments;
            } else {
                adjustedTotal = retailPriceBeforeAdjustments;
            }
        }

        adjustedTotal = adjustedTotal.multiply(quantity);

    }

    public void removeAllAdjustments() {
        promotableOrderItemPriceDetailAdjustments.clear();
        chooseSaleOrRetailAdjustments();
    }

    protected void finalizeAdjustments(boolean useSaleAdjustments) {
        for (PromotableOrderItemPriceDetailAdjustment adjustment : promotableOrderItemPriceDetailAdjustments) {
            adjustment.finalizeAdjustment(useSaleAdjustments);
        }
    }

    /**
     * Removes retail only adjustments.
     */
    protected void removeRetailOnlyAdjustments() {
        Iterator<PromotableOrderItemPriceDetailAdjustment> adjustments = promotableOrderItemPriceDetailAdjustments.iterator();
        while (adjustments.hasNext()) {
            PromotableOrderItemPriceDetailAdjustment adjustment = adjustments.next();
            if (adjustment.getOffer().getApplyDiscountToSalePrice() == false) {
                adjustments.remove();
            }
        }
    }

    /**
     * If removeUnusedAdjustments is s 
     * @param useSaleAdjustments
     */
    protected void removeZeroDollarAdjustments(boolean useSalePrice) {
        Iterator<PromotableOrderItemPriceDetailAdjustment> adjustments = promotableOrderItemPriceDetailAdjustments.iterator();
        while (adjustments.hasNext()) {
            PromotableOrderItemPriceDetailAdjustment adjustment = adjustments.next();
            if (useSalePrice) {
                if (adjustment.getSaleAdjustmentValue().isZero()) {
                    adjustments.remove();
                }
            } else {
                if (adjustment.getRetailAdjustmentValue().isZero()) {
                    adjustments.remove();
                }
            }            
        }
    }
    
    public PromotableOrderItem getPromotableOrderItem() {
        return promotableOrderItem;
    }

    @Override
    public List<PromotionDiscount> getPromotionDiscounts() {
        return promotionDiscounts;
    }

    @Override
    public List<PromotionQualifier> getPromotionQualifiers() {
        return promotionQualifiers;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    private boolean restrictTarget(Offer offer, boolean targetType) {
        OfferItemRestrictionRuleType qualifierType;
        if (targetType) {
            qualifierType = offer.getOfferItemTargetRuleType();
        } else {
            qualifierType = offer.getOfferItemQualifierRuleType();
        }
        return OfferItemRestrictionRuleType.NONE.equals(qualifierType) ||
                OfferItemRestrictionRuleType.QUALIFIER.equals(qualifierType);
    }

    private boolean restrictQualifier(Offer offer, boolean targetType) {
        OfferItemRestrictionRuleType qualifierType;
        if (targetType) {
            qualifierType = offer.getOfferItemTargetRuleType();
        } else {
            qualifierType = offer.getOfferItemQualifierRuleType();
        }
        return OfferItemRestrictionRuleType.NONE.equals(qualifierType) ||
                OfferItemRestrictionRuleType.TARGET.equals(qualifierType);
    }

    @Override
    public int getQuantityAvailableToBeUsedAsTarget(PromotableCandidateItemOffer itemOffer) {
        int qtyAvailable = quantity;
        Offer promotion = itemOffer.getOffer();

        // 1. Any quantities of this item that have already received the promotion are not eligible.
        // 2. If this promotion is not combinable then any quantities that have received discounts
        //    from other promotions cannot receive this discount
        // 3. If this promotion is combinable then any quantities that have received discounts from
        //    other combinable promotions are eligible to receive this discount as well
        boolean combinable = promotion.isCombinableWithOtherOffers();
        if (!combinable && isNonCombinableOfferApplied()) {
            return 0;
        }

        // Any quantities of this item that have already received the promotion are not eligible.
        // Also, any quantities of this item that have received another promotion are not eligible
        // if this promotion cannot be combined with another discount.
        for (PromotionDiscount promotionDiscount : promotionDiscounts) {
            if (promotionDiscount.getPromotion().equals(promotion) || restrictTarget(promotion, true)) {
                qtyAvailable = qtyAvailable - promotionDiscount.getQuantity();
            } else {
                // The other promotion is Combinable, but we must make sure that the item qualifier also allows
                // it to be reused as a target.                   
                if (restrictTarget(promotionDiscount.getPromotion(), true)) {
                    qtyAvailable = qtyAvailable - promotionDiscount.getQuantity();
                }
            }
        }

        // 4.  Any quantities of this item that have been used as a qualifier for this promotion are not eligible as targets
        // 5.  Any quantities of this item that have been used as a qualifier for another promotion that does 
        //     not allow the qualifier to be reused must be deduced from the qtyAvailable.
        for (PromotionQualifier promotionQualifier : promotionQualifiers) {
            if (promotionQualifier.getPromotion().equals(promotion) || restrictQualifier(promotion, true)) {
                qtyAvailable = qtyAvailable - promotionQualifier.getQuantity();
            } else {
                if (restrictTarget(promotionQualifier.getPromotion(), false)) {
                    qtyAvailable = qtyAvailable - promotionQualifier.getQuantity();
                }
            }
        }

        return qtyAvailable;
    }

    public PromotionQualifier lookupOrCreatePromotionQualifier(PromotableCandidateItemOffer candidatePromotion) {
        Offer promotion = candidatePromotion.getOffer();
        for (PromotionQualifier pq : promotionQualifiers) {
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
        for (PromotionDiscount pd : promotionDiscounts) {
            if (pd.getPromotion().equals(promotion)) {
                return pd;
            }
        }

        PromotionDiscount pd = new PromotionDiscount();
        pd.setPromotion(promotion);

        promotionDiscounts.add(pd);
        return pd;
    }

    @Override
    public PromotionQualifier addPromotionQualifier(PromotableCandidateItemOffer itemOffer, OfferItemCriteria itemCriteria, int qtyToMarkAsQualifier) {
        PromotionQualifier pq = lookupOrCreatePromotionQualifier(itemOffer);
        pq.incrementQuantity(qtyToMarkAsQualifier);
        pq.setItemCriteria(itemCriteria);
        return pq;
    }

    @Override
    public void addPromotionDiscount(PromotableCandidateItemOffer itemOffer, OfferItemCriteria itemCriteria, int qtyToMarkAsTarget) {
        PromotionDiscount pd = lookupOrCreatePromotionDiscount(itemOffer);
        if (pd == null) {
            return;
        }
        pd.incrementQuantity(qtyToMarkAsTarget);
        pd.setItemCriteria(itemCriteria);
        pd.setCandidateItemOffer(itemOffer);
    }

    @Override
    public void finalizeQuantities() {
        for (PromotionDiscount promotionDiscount : promotionDiscounts) {
            promotionDiscount.setFinalizedQuantity(promotionDiscount.getQuantity());
        }
        for (PromotionQualifier promotionQualifier : promotionQualifiers) {
            promotionQualifier.setFinalizedQuantity(promotionQualifier.getQuantity());
        }
    }

    @Override
    public void clearAllNonFinalizedQuantities() {
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

    @Override
    public int getQuantityAvailableToBeUsedAsQualifier(PromotableCandidateItemOffer itemOffer) {
        int qtyAvailable = quantity;
        Offer promotion = itemOffer.getOffer();

        // Any quantities of this item that have already received the promotion are not eligible.
        for (PromotionDiscount promotionDiscount : promotionDiscounts) {
            if (promotionDiscount.getPromotion().equals(promotion) || restrictTarget(promotion, false)) {
                qtyAvailable = qtyAvailable - promotionDiscount.getQuantity();
            } else {
                // Item's that receive other discounts might still be allowed to be qualifiers                 
                if (restrictQualifier(promotionDiscount.getPromotion(), true)) {
                    qtyAvailable = qtyAvailable - promotionDiscount.getQuantity();
                }
            }
        }

        // Any quantities of this item that have already been used as a qualifier for this promotion or for 
        // another promotion that has a qualifier type of NONE or TARGET_ONLY cannot be used for this promotion
        for (PromotionQualifier promotionQualifier : promotionQualifiers) {
            if (promotionQualifier.getPromotion().equals(promotion) || restrictQualifier(promotion, false)) {
                qtyAvailable = qtyAvailable - promotionQualifier.getQuantity();
            } else {
                if (restrictQualifier(promotionQualifier.getPromotion(), false)) {
                    qtyAvailable = qtyAvailable - promotionQualifier.getQuantity();
                }
            }
        }
        return qtyAvailable;
    }

    @Override
    public Money calculateItemUnitPriceWithAdjustments(boolean allowSalePrice) {
        Money priceWithAdjustments = null;
        if (allowSalePrice) {
            priceWithAdjustments = promotableOrderItem.getSalePriceBeforeAdjustments();
            if (priceWithAdjustments == null) {
                return promotableOrderItem.getRetailPriceBeforeAdjustments();
            }
        } else {
            priceWithAdjustments = promotableOrderItem.getRetailPriceBeforeAdjustments();
        }

        for (PromotableOrderItemPriceDetailAdjustment adjustment : promotableOrderItemPriceDetailAdjustments) {
            if (allowSalePrice) {
                priceWithAdjustments = priceWithAdjustments.subtract(adjustment.getSaleAdjustmentValue());
            } else {
                priceWithAdjustments = priceWithAdjustments.subtract(adjustment.getRetailAdjustmentValue());
            }
        }

        return priceWithAdjustments;
    }

    protected Money calculateAdjustmentsUnitValue() {
        Money adjustmentUnitValue = new Money(promotableOrderItem.getCurrency());

        for (PromotableOrderItemPriceDetailAdjustment adjustment : promotableOrderItemPriceDetailAdjustments) {
            adjustmentUnitValue = adjustmentUnitValue.add(adjustment.getAdjustmentValue());
        }

        return adjustmentUnitValue;
    }

    /**
     * Creates a key that represents a unique priceDetail
     * @return
     */
    @Override
    public String buildDetailKey() {
        List<Long> offerIds = new ArrayList<Long>();
        for (PromotableOrderItemPriceDetailAdjustment adjustment : promotableOrderItemPriceDetailAdjustments) {
            Long offerId = adjustment.getOffer().getId();
            offerIds.add(offerId);
        }
        Collections.sort(offerIds);
        return promotableOrderItem.getOrderItem().toString() + offerIds.toString() + useSaleAdjustments;
    }

    @Override
    public Money getFinalizedTotalWithAdjustments() {
        chooseSaleOrRetailAdjustments();
        return adjustedTotal;
    }

    @Override
    public Money calculateTotalAdjustmentValue() {
        return calculateAdjustmentsUnitValue().multiply(quantity);
    }

    protected PromotableOrderItemPriceDetail split(int discountQty, Long offerId) {
        int originalQty = quantity;
        quantity = discountQty;

        int splitItemQty = originalQty - discountQty;

        // Create the new item with the correct quantity
        PromotableOrderItemPriceDetail newDetail = promotableOrderItem.createNewDetail(splitItemQty);

        // copy discounts
        for (PromotionDiscount existingDiscount : promotionDiscounts) {
            PromotionDiscount newDiscount = existingDiscount.split(discountQty);
            if (newDiscount != null) {
                newDetail.getPromotionDiscounts().add(newDiscount);
            }
        }

        Iterator<PromotionQualifier> qualifiers = promotionQualifiers.iterator();
        while (qualifiers.hasNext()) {
            PromotionQualifier currentQualifier = qualifiers.next();
            Long qualifierOfferId = currentQualifier.getPromotion().getId();
            if (qualifierOfferId.equals(offerId) && currentQualifier.getQuantity() <= splitItemQty) {
                // Remove this one from the original detail
                qualifiers.remove();
                newDetail.getPromotionQualifiers().add(currentQualifier);
            } else {
                PromotionQualifier newQualifier = currentQualifier.split(splitItemQty);
                newDetail.getPromotionQualifiers().add(newQualifier);
            }            
        }

        for (PromotableOrderItemPriceDetailAdjustment existingAdjustment : promotableOrderItemPriceDetailAdjustments) {
            PromotableOrderItemPriceDetailAdjustment newAdjustment = existingAdjustment.copy();
            newDetail.addCandidateItemPriceDetailAdjustment(newAdjustment);
        }

        return newDetail;
    }

    @Override
    public PromotableOrderItemPriceDetail splitIfNecessary() {
        PromotableOrderItemPriceDetail returnDetail = null;
        for (PromotionDiscount discount : promotionDiscounts) {
            if (discount.getQuantity() != quantity) {
                Long offerId = discount.getCandidateItemOffer().getOffer().getId();
                return this.split(discount.getQuantity(), offerId);
            }
        }
        return returnDetail;
    }

    @Override
    public boolean useSaleAdjustments() {
        return useSaleAdjustments;
    }

}
