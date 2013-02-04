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

package org.broadleafcommerce.core.offer.service.processor;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.offer.service.discount.CandidatePromotionItems;
import org.broadleafcommerce.core.offer.service.discount.ItemOfferComparator;
import org.broadleafcommerce.core.offer.service.discount.PromotionDiscount;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateItemOffer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateOrderOffer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableFulfillmentGroup;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrder;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItemAdjustment;
import org.broadleafcommerce.core.offer.service.type.OfferType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Filter and apply order item offers.
 * 
 * @author jfischer
 *
 */
@Service("blItemOfferProcessor")
public class ItemOfferProcessorImpl extends OrderOfferProcessorImpl implements ItemOfferProcessor {
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.offer.service.processor.ItemOfferProcessor#filterItemLevelOffer(org.broadleafcommerce.core.order.domain.Order, java.util.List, java.util.List, org.broadleafcommerce.core.offer.domain.Offer)
     */
    public void filterItemLevelOffer(PromotableOrder order, List<PromotableCandidateItemOffer> qualifiedItemOffers, Offer offer) {
        boolean isNewFormat = !CollectionUtils.isEmpty(offer.getQualifyingItemCriteria()) || !CollectionUtils.isEmpty(offer.getTargetItemCriteria());
        boolean itemLevelQualification = false;
        boolean offerCreated = false;
        for (PromotableOrderItem promotableOrderItem : order.getDiscountableOrderItems(offer.getApplyDiscountToSalePrice())) {
            if(couldOfferApplyToOrder(offer, order, promotableOrderItem)) {
                if (!isNewFormat) {
                    //support legacy offers
                    PromotableCandidateItemOffer candidate = createCandidateItemOffer(qualifiedItemOffers, offer, promotableOrderItem);
                    if (!candidate.getCandidateTargets().contains(promotableOrderItem)) {
                        candidate.getCandidateTargets().add(promotableOrderItem);
                    }
                    offerCreated = true;
                    continue;
                }
                itemLevelQualification = true;
                break;
            }
            for (PromotableFulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
                if(couldOfferApplyToOrder(offer, order, promotableOrderItem, fulfillmentGroup)) {
                    if (!isNewFormat) {
                        //support legacy offers
                        PromotableCandidateItemOffer candidate = createCandidateItemOffer(qualifiedItemOffers, offer, promotableOrderItem);
                        if (!candidate.getCandidateTargets().contains(promotableOrderItem)) {
                            candidate.getCandidateTargets().add(promotableOrderItem);
                        }
                        offerCreated = true;
                        continue;
                    }
                    itemLevelQualification = true;
                    break;
                }
            }
        }
        //Item Qualification - new for 1.5!
        if (itemLevelQualification && !offerCreated) {
            CandidatePromotionItems candidates = couldOfferApplyToOrderItems(offer, order.getDiscountableOrderItems(offer.getApplyDiscountToSalePrice()));
            PromotableCandidateItemOffer candidateOffer = null;
            if (candidates.isMatchedQualifier()) {
                //we don't know the final target yet, so put null for the order item for now
                candidateOffer = createCandidateItemOffer(qualifiedItemOffers, offer, null);
                candidateOffer.getCandidateQualifiersMap().putAll(candidates.getCandidateQualifiersMap());
            }
            if (candidates.isMatchedTarget() && candidates.isMatchedQualifier()) {
                if (candidateOffer == null) {
                    //we don't know the final target yet, so put null for the order item for now
                    candidateOffer = createCandidateItemOffer(qualifiedItemOffers, offer, null);
                }
                for (PromotableOrderItem candidateItem : candidates.getCandidateTargets()) {
                    PromotableCandidateItemOffer itemOffer = candidateOffer.clone();
                    itemOffer.setOrderItem(candidateItem);
                    candidateItem.addCandidateItemOffer(itemOffer);
                }
                candidateOffer.getCandidateTargets().addAll(candidates.getCandidateTargets());
            }
        }
    }
    
    /**
     * Create a candidate item offer based on the offer in question and a specific order item
     * 
     * @param qualifiedItemOffers the container list for candidate item offers
     * @param offer the offer in question
     * @param promotableOrderItem the specific order item
     * @return the candidate item offer
     */
    protected PromotableCandidateItemOffer createCandidateItemOffer(List<PromotableCandidateItemOffer> qualifiedItemOffers, Offer offer, PromotableOrderItem promotableOrderItem) {
        CandidateItemOffer candidateOffer = offerDao.createCandidateItemOffer();
        candidateOffer.setOffer(offer);
        PromotableCandidateItemOffer promotableCandidateItemOffer = promotableItemFactory.createPromotableCandidateItemOffer(candidateOffer);
        if (promotableOrderItem != null) {
            promotableOrderItem.addCandidateItemOffer(promotableCandidateItemOffer);
        }
        promotableCandidateItemOffer.setOrderItem(promotableOrderItem);
        qualifiedItemOffers.add(promotableCandidateItemOffer);
        
        return promotableCandidateItemOffer;
    }
    
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.offer.service.processor.ItemOfferProcessor#applyAllItemOffers(java.util.List, java.util.List)
     */
    @Override
    public boolean applyAllItemOffers(List<PromotableCandidateItemOffer> itemOffers, PromotableOrder order) {
        // Iterate through the collection of CandidateItemOffers. Remember that each one is an offer that may apply to a
        // particular OrderItem.  Multiple CandidateItemOffers may contain a reference to the same OrderItem object.
        // The same offer may be applied to different Order Items
        //
        // isCombinableWithOtherOffers - not combinable with any offers in the order
        // isStackable - cannot be stack on top of an existing item offer back, other offers can be stack of top of it
        //
        boolean itemOffersApplied = false;
        int appliedItemOffersCount = 0;
        boolean isLegacyFormat = false;
        for (PromotableCandidateItemOffer itemOffer : itemOffers) {
            int beforeCount = appliedItemOffersCount;
            PromotableOrderItem orderItem = itemOffer.getOrderItem();
            if (orderItem != null) {
                isLegacyFormat = true;
                appliedItemOffersCount = applyLegacyAdjustments(appliedItemOffersCount, itemOffer, beforeCount, orderItem);
            } else {
                // TODO:  Add filter for item-subtotal
                skipOfferIfSubtotalRequirementNotMet(order, itemOffer);
                appliedItemOffersCount = applyAdjustments(order, appliedItemOffersCount, itemOffer, beforeCount);
            }
        }
        if (isLegacyFormat) {
            appliedItemOffersCount = checkLegacyAdjustments(order.getDiscountableDiscreteOrderItems(), appliedItemOffersCount);
        } else {
            appliedItemOffersCount = checkAdjustments(order, appliedItemOffersCount);
        }
        if (appliedItemOffersCount > 0) {
            itemOffersApplied = true;
        }
        return itemOffersApplied;
    }
    
    
    protected boolean skipOfferIfSubtotalRequirementNotMet(PromotableOrder order, PromotableCandidateItemOffer itemOffer) {
        if (itemOffer.getOffer().getQualifyingItemSubTotal() == null || itemOffer.getOffer().getQualifyingItemSubTotal().lessThanOrEqual(Money.ZERO)) {
            return false;
        }
           
        /*
        boolean notCombinableOfferApplied = false;
        boolean offerApplied = false;
        List<PromotableOrderItem> allSplitItems = order.getAllSplitItems();
        for (PromotableOrderItem targetItem : allSplitItems) {
            notCombinableOfferApplied = targetItem.isNotCombinableOfferApplied();
            if (!offerApplied) {
                offerApplied = targetItem.isHasOrderItemAdjustments();
            }
            if (notCombinableOfferApplied) {
                break;
            }
        }
        
        if (
                !notCombinableOfferApplied && (
                    (
                            (itemOffer.getOffer().isCombinableWithOtherOffers() || itemOffer.getOffer().isTotalitarianOffer() == null || !itemOffer.getOffer().isTotalitarianOffer()) 
                            //&& itemOffer.getOffer().isStackable()
                    ) 
                    || !offerApplied
                )
            ) 
        {
            // At this point, we should not have any official adjustment on the order
            // for this item.
            applyItemQualifiersAndTargets(itemOffer, order);
            allSplitItems = order.getAllSplitItems();
            for (PromotableOrderItem splitItem : allSplitItems) {
                for (PromotionDiscount discount : splitItem.getPromotionDiscounts()) {
                    if (discount.getPromotion().equals(itemOffer.getOffer())) {
                        applyOrderItemAdjustment(itemOffer, splitItem);
                        break;
                    }
                }
            }
        }
        // check if not combinable offer is better than sale price; if no, remove the not combinable offer so 
        // that another offer may be applied to the item
        if ((!itemOffer.getOffer().isCombinableWithOtherOffers() || (itemOffer.getOffer().isTotalitarianOffer() != null && itemOffer.getOffer().isTotalitarianOffer())) && appliedItemOffersCount > beforeCount) { 
            Money adjustmentTotal = new Money(0D);
            Money saleTotal = new Money(0D);
            for (PromotableOrderItem splitItem : allSplitItems) {
                adjustmentTotal = adjustmentTotal.add(splitItem.getCurrentPrice().multiply(splitItem.getQuantity()));
                saleTotal = saleTotal.add(splitItem.getPriceBeforeAdjustments(true).multiply(splitItem.getQuantity()));
            }
            if (adjustmentTotal.greaterThanOrEqual(saleTotal)) {
                // adjustment price is not best price, remove adjustments for this item
                for (PromotableOrderItem splitItem : allSplitItems) {
                    if (splitItem.isHasOrderItemAdjustments()) {
                        appliedItemOffersCount--;
                    }
                }
                order.getSplitItems().clear();
            }
        }
        return appliedItemOffersCount;
        */
        return false;
    }
     

    protected int applyAdjustments(PromotableOrder order, int appliedItemOffersCount, PromotableCandidateItemOffer itemOffer, int beforeCount) {
        boolean notCombinableOfferApplied = false;
        boolean offerApplied = false;
        List<PromotableOrderItem> allSplitItems = order.getAllSplitItems();
        for (PromotableOrderItem targetItem : allSplitItems) {
            notCombinableOfferApplied = targetItem.isNotCombinableOfferApplied();
            if (!offerApplied) {
                offerApplied = targetItem.isHasOrderItemAdjustments();
            }
            if (notCombinableOfferApplied) {
                break;
            }
        }
        if (
                !notCombinableOfferApplied && (
                    (
                            (itemOffer.getOffer().isCombinableWithOtherOffers() || itemOffer.getOffer().isTotalitarianOffer() == null || !itemOffer.getOffer().isTotalitarianOffer()) 
                            //&& itemOffer.getOffer().isStackable()
                    ) 
                    || !offerApplied
                )
            ) 
        {
            // At this point, we should not have any official adjustment on the order
            // for this item.
            applyItemQualifiersAndTargets(itemOffer, order);
            allSplitItems = order.getAllSplitItems();
            for (PromotableOrderItem splitItem : allSplitItems) {
                for (PromotionDiscount discount : splitItem.getPromotionDiscounts()) {
                    if (discount.getPromotion().equals(itemOffer.getOffer())) {
                        applyOrderItemAdjustment(itemOffer, splitItem);
                        appliedItemOffersCount++;
                        break;
                    }
                }
            }
        }
        // check if not combinable offer is better than sale price; if no, remove the not combinable offer so 
        // that another offer may be applied to the item
        if ((!itemOffer.getOffer().isCombinableWithOtherOffers() || (itemOffer.getOffer().isTotalitarianOffer() != null && itemOffer.getOffer().isTotalitarianOffer())) && appliedItemOffersCount > beforeCount) { 
            Money adjustmentTotal = new Money(0D);
            Money saleTotal = new Money(0D);
            for (PromotableOrderItem splitItem : allSplitItems) {
                adjustmentTotal = adjustmentTotal.add(splitItem.getCurrentPrice().multiply(splitItem.getQuantity()));
                saleTotal = saleTotal.add(splitItem.getPriceBeforeAdjustments(true).multiply(splitItem.getQuantity()));
            }
            if (adjustmentTotal.greaterThanOrEqual(saleTotal)) {
                // adjustment price is not best price, remove adjustments for this item
                for (PromotableOrderItem splitItem : allSplitItems) {
                    if (splitItem.isHasOrderItemAdjustments()) {
                        appliedItemOffersCount--;
                    }
                }
                order.getSplitItems().clear();
            }
        }
        return appliedItemOffersCount;
    }

    protected int checkAdjustments(PromotableOrder order, int appliedItemOffersCount) {
        if (appliedItemOffersCount > 0) {
            List<PromotableOrderItem> allSplitItems = order.getAllSplitItems();
            // compare adjustment price to sales price and remove adjustments if sales price is better
            for (PromotableOrderItem splitItem : allSplitItems) {
                if (splitItem.isHasOrderItemAdjustments()) {
                    boolean useSaleAdjustments = false;
                    int adjustmentsRemoved = 0;
                    
                    Money adjustmentPrice;
                    if (splitItem.getDelegate().getIsOnSale()) {
                        if (splitItem.getSaleAdjustmentPrice().lessThanOrEqual(splitItem.getRetailAdjustmentPrice())) {
                            adjustmentPrice = splitItem.getSaleAdjustmentPrice();
                            useSaleAdjustments = true;
                        }  else {
                            adjustmentPrice = splitItem.getRetailAdjustmentPrice();
                        }

                        if (! adjustmentPrice.lessThan(splitItem.getSalePrice())) {
                            adjustmentsRemoved = adjustmentsRemoved + splitItem.removeAllAdjustments();
                        }
                    } else {
                        if (! splitItem.getRetailAdjustmentPrice().lessThan(splitItem.getRetailPrice())) {
                            adjustmentsRemoved = adjustmentsRemoved + splitItem.removeAllAdjustments();
                        }
                    }

                    adjustmentsRemoved = adjustmentsRemoved + splitItem.fixAdjustments(useSaleAdjustments);
                    appliedItemOffersCount -= adjustmentsRemoved;
                }


            }
            orderItemMergeService.mergeSplitItems(order);
        }
        return appliedItemOffersCount;
    }

    protected int checkLegacyAdjustments(List<PromotableOrderItem> discreteOrderItems, int appliedItemOffersCount) {
        if (appliedItemOffersCount > 0) {
            for (PromotableOrderItem discreteOrderItem : discreteOrderItems) {
                if (discreteOrderItem.isHasOrderItemAdjustments()) {
                    boolean useSaleAdjustments = false;
                    int adjustmentsRemoved = 0;

                    Money adjustmentPrice;
                    if (discreteOrderItem.getDelegate().getIsOnSale()) {
                        if (discreteOrderItem.getSaleAdjustmentPrice().lessThanOrEqual(discreteOrderItem.getRetailAdjustmentPrice())) {
                            adjustmentPrice = discreteOrderItem.getSaleAdjustmentPrice();
                            useSaleAdjustments = true;
                        }  else {
                            adjustmentPrice = discreteOrderItem.getRetailAdjustmentPrice();
                        }

                        if (! adjustmentPrice.lessThanOrEqual(discreteOrderItem.getSalePrice())) {
                            adjustmentsRemoved = adjustmentsRemoved + discreteOrderItem.removeAllAdjustments();
                        }
                    } else {
                        if (! discreteOrderItem.getRetailAdjustmentPrice().lessThanOrEqual(discreteOrderItem.getRetailPrice())) {
                            adjustmentsRemoved = adjustmentsRemoved + discreteOrderItem.removeAllAdjustments();
                        }
                    }

                    adjustmentsRemoved = adjustmentsRemoved + discreteOrderItem.fixAdjustments(useSaleAdjustments);
                    appliedItemOffersCount -= adjustmentsRemoved;
                }
            }
        }
        return appliedItemOffersCount;
    }

    protected int applyLegacyAdjustments(int appliedItemOffersCount, PromotableCandidateItemOffer itemOffer, int beforeCount, PromotableOrderItem orderItem) {
        //legacy promotion
        if (!orderItem.isNotCombinableOfferApplied()) {
            if ((itemOffer.getOffer().isCombinableWithOtherOffers() && itemOffer.getOffer().isStackable()) || !orderItem.isHasOrderItemAdjustments()) {
                applyOrderItemAdjustment(itemOffer, orderItem);
                appliedItemOffersCount++;
            }
        }
        // check if not combinable offer is better than sale price; if no, remove the not combinable offer so 
        // that another offer may be applied to the item
        if (!itemOffer.getOffer().isCombinableWithOtherOffers() && appliedItemOffersCount > beforeCount) { 
            Money adjustmentTotal = new Money(0D);
            Money saleTotal = new Money(0D);
            adjustmentTotal = adjustmentTotal.add(orderItem.getCurrentPrice().multiply(orderItem.getQuantity()));
            saleTotal = saleTotal.add(orderItem.getPriceBeforeAdjustments(true).multiply(orderItem.getQuantity()));
            if (adjustmentTotal.greaterThanOrEqual(saleTotal)) {
                // adjustment price is not best price, remove adjustments for this item
                orderItem.removeAllAdjustments();
                appliedItemOffersCount--;
            }
        }
        return appliedItemOffersCount;
    }
    
    protected void applyItemQualifiersAndTargets(PromotableCandidateItemOffer itemOffer, PromotableOrder order) {
        Offer promotion = itemOffer.getOffer();
        boolean matchFound = false;
        do {
            matchFound = false;
            int totalQualifiersNeeded = 0;
            for (OfferItemCriteria itemCriteria : itemOffer.getCandidateQualifiersMap().keySet()) {
                totalQualifiersNeeded += itemCriteria.getQuantity();
            }
            int receiveQtyNeeded = 0;
            for (OfferItemCriteria targetCriteria : promotion.getTargetItemCriteria()) {
                receiveQtyNeeded += targetCriteria.getQuantity();
            }
            
            checkAll: {
                for (OfferItemCriteria itemCriteria : itemOffer.getCandidateQualifiersMap().keySet()) {
                    List<PromotableOrderItem> chargeableItems = itemOffer.getCandidateQualifiersMap().get(itemCriteria);
                    
                    Collections.sort(chargeableItems, getQualifierItemComparator(promotion.getApplyDiscountToSalePrice()));

                    // Calculate the number of qualifiers needed that will not receive the promotion.  
                    // These will be reserved first before the target is assigned.
                    int qualifierQtyNeeded = itemCriteria.getQuantity();
                    
                    for (PromotableOrderItem chargeableItem : chargeableItems) {
                        
                        // Mark Qualifiers
                        if (qualifierQtyNeeded > 0) {
                            int itemQtyAvailableToBeUsedAsQualifier = chargeableItem.getQuantityAvailableToBeUsedAsQualifier(promotion);
                            if (itemQtyAvailableToBeUsedAsQualifier > 0) {
                                int qtyToMarkAsQualifier = Math.min(qualifierQtyNeeded, itemQtyAvailableToBeUsedAsQualifier);
                                qualifierQtyNeeded -= qtyToMarkAsQualifier;
                                chargeableItem.addPromotionQualifier(itemOffer, itemCriteria, qtyToMarkAsQualifier);
                            }
                        }
                        
                        if (qualifierQtyNeeded == 0) {
                            totalQualifiersNeeded -= itemCriteria.getQuantity();
                            break;
                        }
                    }
                    if (qualifierQtyNeeded != 0) {
                        break checkAll;
                    }
                }
                checkTargets :{
                    List<PromotableOrderItem> chargeableItems = itemOffer.getCandidateTargets();
                    Collections.sort(chargeableItems, getTargetItemComparator(promotion.getApplyDiscountToSalePrice()));
                    for (PromotableOrderItem chargeableItem : chargeableItems) {
                        // Mark Targets
                        if (receiveQtyNeeded > 0) {
                            int itemQtyAvailableToBeUsedAsTarget = chargeableItem.getQuantityAvailableToBeUsedAsTarget(promotion);
                            if (itemQtyAvailableToBeUsedAsTarget > 0) {
                                if (promotion.getMaxUses() == 0 || itemOffer.getUses() < promotion.getMaxUses()) {
                                    int qtyToMarkAsTarget = Math.min(receiveQtyNeeded, itemQtyAvailableToBeUsedAsTarget);
                                    receiveQtyNeeded -= qtyToMarkAsTarget;
                                    //atLeastOneCriteriaMatched = true;
                                    chargeableItem.addPromotionDiscount(itemOffer, itemOffer.getOffer().getTargetItemCriteria(), qtyToMarkAsTarget);
                                }
                            }
                        }
                        
                        if (receiveQtyNeeded == 0) {
                            itemOffer.addUse();
                            break checkTargets;
                        }
                    }
                }
            }
            boolean criteriaMatched = true;
            if (receiveQtyNeeded != 0 || totalQualifiersNeeded != 0) {
                // This ItemCriteria did not match.  Therefore, we need to clear all non-finalized quantities.
                for (OfferItemCriteria itemCriteria : itemOffer.getCandidateQualifiersMap().keySet()) {
                    List<PromotableOrderItem> chargeableItems = itemOffer.getCandidateQualifiersMap().get(itemCriteria);
                    clearAllNonFinalizedQuantities(chargeableItems);
                }
                clearAllNonFinalizedQuantities(itemOffer.getCandidateTargets());
                criteriaMatched = false;
            }
            
            if (criteriaMatched) {
                matchFound = true;
                finalizeQuantities(order.getDiscountableDiscreteOrderItems());
            }
            //This promotion may be able to be applied multiple times if there is enough
            //product quantity in the order. Continue to loop through the order until
            //there are no more matches
        } while (matchFound);
        
        if (order.getSplitItems().size() == 0) {
            orderItemMergeService.initializeSplitItems(order);
        }
        List<PromotableOrderItem> allSplitItems = order.getAllSplitItems();
        for (PromotableOrderItem chargeableItem : allSplitItems) {
            if (itemOffer.getCandidateTargets().contains(chargeableItem)) {
                List<PromotableOrderItem> splitItems = chargeableItem.split();
                if (splitItems != null && splitItems.size() > 0) {
                    // Remove this item from the list
                    List<PromotableOrderItem> temp = order.searchSplitItems(chargeableItem);
                    if (!CollectionUtils.isEmpty(temp)) {
                        temp.remove(chargeableItem);
                        temp.addAll(splitItems);
                    }
                }
            } 
        }
    }
    
    /**
     * Used in {@link #applyItemQualifiersAndTargets(PromotableCandidateItemOffer, PromotableOrder)} allow for customized
     * sorting for which qualifier items should be attempted to be used first for a promotion. Default behavior
     * is to sort descending, so higher-value items are attempted to be qualified first.
     * 
     * @param applyToSalePrice - whether or not the Comparator should use the sale price for comparison
     * @return
     */
    protected Comparator<PromotableOrderItem> getQualifierItemComparator(final boolean applyToSalePrice) {
        return new Comparator<PromotableOrderItem>() {
            @Override
            public int compare(PromotableOrderItem o1, PromotableOrderItem o2) {
                Money price = o1.getPriceBeforeAdjustments(applyToSalePrice);
                Money price2 = o2.getPriceBeforeAdjustments(applyToSalePrice);
                
                // highest amount first
                return price2.compareTo(price);
            }
        };
    }

    /**
     * <p>
     * Used in {@link #applyItemQualifiersAndTargets(PromotableCandidateItemOffer, PromotableOrder)} allow for customized
     * sorting for which target items the promotion should be attempted to be applied to first. Default behavior is to
     * sort descending, so higher-value items get the promotion over lesser-valued items.
     * </p>
     * <p>
     * Note: By default, both the {@link #getQualifierItemComparator(boolean)} and this target comparator are sorted
     * in descending order.  This means that higher-valued items can be paired with higher-valued items and lower-valued
     * items can be paired with lower-valued items. This also ensures that you will <b>not</b> have the scenario where 2 lower-valued
     * items can be used to qualify a higher-valued target.
     * </p>
     * 
     * @param applyToSalePrice - whether or not the Comparator should use the sale price for comparison
     * @return
     */
    protected Comparator<PromotableOrderItem> getTargetItemComparator(final boolean applyToSalePrice) {
        return new Comparator<PromotableOrderItem>() {
            @Override
            public int compare(PromotableOrderItem o1, PromotableOrderItem o2) {
                Money price = o1.getPriceBeforeAdjustments(applyToSalePrice);
                Money price2 = o2.getPriceBeforeAdjustments(applyToSalePrice);
                
                // highest amount first
                return price2.compareTo(price);
            }
        };
    }

    /**
     * Private method used by applyAllItemOffers to create an OrderItemAdjustment from a CandidateItemOffer
     * and associates the OrderItemAdjustment to the OrderItem.
     *
     * @param itemOffer a CandidateItemOffer to apply to an OrderItem
     */
    protected void applyOrderItemAdjustment(PromotableCandidateItemOffer itemOffer, PromotableOrderItem orderItem) {
        OrderItemAdjustment itemAdjustment = offerDao.createOrderItemAdjustment();
        itemAdjustment.init(orderItem.getDelegate(), itemOffer.getOffer(), itemOffer.getOffer().getName());
        //add to adjustment
        PromotableOrderItemAdjustment promotableOrderItemAdjustment = promotableItemFactory.createPromotableOrderItemAdjustment(itemAdjustment, orderItem);
        orderItem.addOrderItemAdjustment(promotableOrderItemAdjustment); //This is how we can tell if an item has been discounted
    }
    
    @Override
    public void filterOffers(PromotableOrder order, List<Offer> filteredOffers, List<PromotableCandidateOrderOffer> qualifiedOrderOffers, List<PromotableCandidateItemOffer> qualifiedItemOffers) {
        // set order subTotal price to total item price without adjustments
        order.setOrderSubTotalToPriceWithoutAdjustments();

        for (Offer offer : filteredOffers) {            
            if(offer.getType().equals(OfferType.ORDER)){
                filterOrderLevelOffer(order, qualifiedOrderOffers, offer);
            } else if(offer.getType().equals(OfferType.ORDER_ITEM)){
                filterItemLevelOffer(order, qualifiedItemOffers, offer);
            }
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void applyAndCompareOrderAndItemOffers(PromotableOrder order, List<PromotableCandidateOrderOffer> qualifiedOrderOffers, List<PromotableCandidateItemOffer> qualifiedItemOffers) {
        if (!qualifiedItemOffers.isEmpty()) {
            // Sort order item offers by priority and potential total discount
            Collections.sort(qualifiedItemOffers, ItemOfferComparator.INSTANCE);

            // At this point, the list of qualifiedItemOffers contains all
            // offers that might effect an item on this order.   The orders have
            // been sorted in the order they will be applied based on the
            // potential order savings.
            applyAllItemOffers(qualifiedItemOffers, order);
        }
        
        if (!qualifiedOrderOffers.isEmpty()) {
            // Sort order offers by priority and discount
            Collections.sort(qualifiedOrderOffers, new BeanComparator("discountedPrice"));
            Collections.sort(qualifiedOrderOffers, new BeanComparator("priority"));
            qualifiedOrderOffers = removeTrailingNotCombinableOrderOffers(qualifiedOrderOffers);
            applyAllOrderOffers(qualifiedOrderOffers, order);
        }
        
        compileOrderTotal(order);
        
        if (!qualifiedOrderOffers.isEmpty() && !qualifiedItemOffers.isEmpty()) {
            List<PromotableCandidateOrderOffer> finalQualifiedOrderOffers = new ArrayList<PromotableCandidateOrderOffer>();
            order.removeAllOrderAdjustments();
            for (PromotableCandidateOrderOffer candidateOrderOffer : qualifiedOrderOffers) {
                // recheck the list of order offers and verify if they still apply with the new subtotal
                /*
                 * Note - there is an edge case possibility where this logic would miss an order promotion
                 * that had a subtotal requirement that was missed because of item deductions, but without
                 * the item deductions, the order promotion would have been included and ended up giving the 
                 * customer a better deal than the item deductions.
                 */
                if (couldOfferApplyToOrder(candidateOrderOffer.getOffer(), order)) {
                    finalQualifiedOrderOffers.add(candidateOrderOffer);
                }
            }

            // Sort order offers by priority and discount
            Collections.sort(finalQualifiedOrderOffers, new BeanComparator("discountedPrice"));
            Collections.sort(finalQualifiedOrderOffers, new BeanComparator("priority"));
            if (!finalQualifiedOrderOffers.isEmpty()) {
                applyAllOrderOffers(finalQualifiedOrderOffers, order);
            }  
        }
    }
}
