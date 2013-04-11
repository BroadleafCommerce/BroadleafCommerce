/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.offer.service.processor;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.service.discount.CandidatePromotionItems;
import org.broadleafcommerce.core.offer.service.discount.ItemOfferComparator;
import org.broadleafcommerce.core.offer.service.discount.OrderOfferComparator;
import org.broadleafcommerce.core.offer.service.discount.PromotionDiscount;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateItemOffer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateOrderOffer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableFulfillmentGroup;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrder;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItemPriceDetail;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItemPriceDetailAdjustment;
import org.broadleafcommerce.core.offer.service.type.OfferType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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

        for (PromotableOrderItem promotableOrderItem : order.getDiscountableOrderItems()) {
            if(couldOfferApplyToOrder(offer, order, promotableOrderItem)) {
                if (!isNewFormat) {
                    //support legacy offers                   
                    PromotableCandidateItemOffer candidate = createCandidateItemOffer(qualifiedItemOffers, offer, order);
                   
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
                        PromotableCandidateItemOffer candidate = createCandidateItemOffer(qualifiedItemOffers, offer, order);
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
            CandidatePromotionItems candidates = couldOfferApplyToOrderItems(offer,
                    order.getDiscountableOrderItems(offer.getApplyDiscountToSalePrice()));
            PromotableCandidateItemOffer candidateOffer = null;
            if (candidates.isMatchedQualifier()) {
                //we don't know the final target yet, so put null for the order item for now
                candidateOffer = createCandidateItemOffer(qualifiedItemOffers, offer, order);
                candidateOffer.getCandidateQualifiersMap().putAll(candidates.getCandidateQualifiersMap());
            }
            if (candidates.isMatchedTarget() && candidates.isMatchedQualifier()) {
                if (candidateOffer == null) {
                    //we don't know the final target yet, so put null for the order item for now
                    candidateOffer = createCandidateItemOffer(qualifiedItemOffers, offer, order);
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
     * @return the candidate item offer
     */
    protected PromotableCandidateItemOffer createCandidateItemOffer(List<PromotableCandidateItemOffer> qualifiedItemOffers,
            Offer offer, PromotableOrder promotableOrder) {

        PromotableCandidateItemOffer promotableCandidateItemOffer =
                promotableItemFactory.createPromotableCandidateItemOffer(promotableOrder, offer);
        qualifiedItemOffers.add(promotableCandidateItemOffer);
        
        return promotableCandidateItemOffer;
    }
    
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.offer.service.processor.ItemOfferProcessor#applyAllItemOffers(java.util.List, java.util.List)
     */
    @Override
    public void applyAllItemOffers(List<PromotableCandidateItemOffer> itemOffers, PromotableOrder order) {
        // Iterate through the collection of CandidateItemOffers. Remember that each one is an offer that may apply to a
        // particular OrderItem.  Multiple CandidateItemOffers may contain a reference to the same OrderItem object.
        // The same offer may be applied to different Order Items
        
        for (PromotableCandidateItemOffer itemOffer : itemOffers) {
            if (offerMeetsSubtotalRequirements(order, itemOffer)) {
                applyItemOffer(order, itemOffer);
            }
        }
    }
    
    
    protected boolean offerMeetsSubtotalRequirements(PromotableOrder order, PromotableCandidateItemOffer itemOffer) {
        if (itemOffer.getOffer().getQualifyingItemSubTotal() == null || itemOffer.getOffer().getQualifyingItemSubTotal().lessThanOrEqual(Money.ZERO)) {
            return true;
        }

        //TODO:  Check subtotal requirement before continuing
           
        return false;
    }

    protected boolean isTotalitarianOfferAppliedToAnyItem(PromotableOrder order) {
        List<PromotableOrderItemPriceDetail> allPriceDetails = order.getAllPromotableOrderItemPriceDetails();       
        for (PromotableOrderItemPriceDetail targetItem : allPriceDetails) {
            if (targetItem.isTotalitarianOfferApplied()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Private method used by applyAdjustments to create an OrderItemAdjustment from a CandidateOrderOffer
     * and associates the OrderItemAdjustment to the OrderItem.
     *
     * @param orderOffer a CandidateOrderOffer to apply to an Order
     */
    protected void applyOrderItemAdjustment(PromotableCandidateItemOffer itemOffer, PromotableOrderItemPriceDetail itemPriceDetail) {
        PromotableOrderItemPriceDetailAdjustment promotableOrderItemPriceDetailAdjustment = promotableItemFactory.createPromotableOrderItemPriceDetailAdjustment(itemOffer, itemPriceDetail);        
        itemPriceDetail.addCandidateItemPriceDetailAdjustment(promotableOrderItemPriceDetailAdjustment);
    }
    
    /**
     * The itemOffer has been qualified and prior methods added PromotionDiscount objects onto the ItemPriceDetail.
     * This code will convert the PromotionDiscounts into Adjustments
     * @param order
     * @param itemOffer
     */
    protected void applyAdjustments(PromotableOrder order, PromotableCandidateItemOffer itemOffer) {
        List<PromotableOrderItemPriceDetail> itemPriceDetails = order.getAllPromotableOrderItemPriceDetails();
        for (PromotableOrderItemPriceDetail itemPriceDetail : itemPriceDetails) {
            for (PromotionDiscount discount : itemPriceDetail.getPromotionDiscounts()) {
                if (discount.getPromotion().equals(itemOffer.getOffer())) {
                    if (itemOffer.getOffer().isTotalitarianOffer() || !itemOffer.getOffer().isCombinableWithOtherOffers()) {
                        // We've decided to apply this adjustment but if it doesn't actually reduce
                        // the value of the item
                        if (adjustmentIsNotGoodEnoughToBeApplied(itemOffer, itemPriceDetail)) {
                            break;
                        }

                    }
                    applyOrderItemAdjustment(itemOffer, itemPriceDetail);
                    break;
                }
            }
        }
    }

    /**
     * Legacy adjustments use the stackable flag instead of item qualifiers and targets
     * @param order
     * @param itemOffer
     */
    protected void applyLegacyAdjustments(PromotableOrder order, PromotableCandidateItemOffer itemOffer) {
        for (PromotableOrderItem item : itemOffer.getCandidateTargets()) {
            for (PromotableOrderItemPriceDetail itemPriceDetail : item.getPromotableOrderItemPriceDetails()) {
                if (!itemOffer.getOffer().isStackable() || !itemOffer.getOffer().isCombinableWithOtherOffers()) {
                    if (itemPriceDetail.getCandidateItemAdjustments().size() != 0) {
                        continue;
                    }
                } else {
                    if (itemPriceDetail.hasNonCombinableAdjustments()) {
                        continue;
                    }
                }
                applyOrderItemAdjustment(itemOffer, itemPriceDetail);
            }
        }
    }

    /**
     * The adjustment might not be better than the sale price.
     * @param itemOffer
     * @param detail
     * @return
     */
    protected boolean adjustmentIsNotGoodEnoughToBeApplied(PromotableCandidateItemOffer itemOffer,
            PromotableOrderItemPriceDetail detail) {
        if (!itemOffer.getOffer().getApplyDiscountToSalePrice()) {
            Money salePrice = detail.getPromotableOrderItem().getSalePriceBeforeAdjustments();
            Money retailPrice = detail.getPromotableOrderItem().getRetailPriceBeforeAdjustments();
            Money savings = itemOffer.calculateSavingsForOrderItem(detail.getPromotableOrderItem(), 1);
            if (salePrice != null) {
                if (salePrice.lessThan(retailPrice.subtract(savings))) {
                    // Not good enough
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return false if a totalitarian offer has already been applied and this order already has
     * item adjustments. 
     *      
     * @param order
     * @param itemOffer
     * @return
     */
    protected boolean itemOfferCanBeApplied(PromotableOrder order, PromotableCandidateItemOffer itemOffer) {

        for (PromotableOrderItemPriceDetail detail : order.getAllPromotableOrderItemPriceDetails()) {
            for (PromotableOrderItemPriceDetailAdjustment adjustment : detail.getCandidateItemAdjustments()) {
                if (adjustment.isTotalitarian() || itemOffer.getOffer().isTotalitarianOffer()) {
                    // A totalitarian offer has already been applied or this offer is totalitarian
                    // and another offer was already applied.
                    return false;
                } else if (itemOffer.isLegacyOffer()) {
                    continue;
                } else if (!adjustment.isCombinable() || !itemOffer.getOffer().isCombinableWithOtherOffers()) {
                    // A nonCombinable offer has been applied or this is a non-combinable offer
                    // and adjustments have already been applied.
                    return false;
                }
            }
        }
        return true;
    }
     

    protected void applyItemOffer(PromotableOrder order, PromotableCandidateItemOffer itemOffer) {
        if (itemOfferCanBeApplied(order, itemOffer)) {
            applyItemQualifiersAndTargets(itemOffer, order);
            if (itemOffer.isLegacyOffer()) {
                applyLegacyAdjustments(order, itemOffer);
            } else {
                applyAdjustments(order, itemOffer);
            }
        }
    }

    /**
     * Some promotions can only apply to the retail price.    This method determines whether
     * retailPrice only promotions should be used instead of those that can apply to the sale
     * price as well.
     * 
     * @param order
     * @return
     */
    protected void chooseSaleOrRetailAdjustments(PromotableOrder order) {
        List<PromotableOrderItemPriceDetail> itemPriceDetails = order.getAllPromotableOrderItemPriceDetails();
        for (PromotableOrderItemPriceDetail itemDetail : itemPriceDetails) {
            itemDetail.chooseSaleOrRetailAdjustments();                
        }
        mergePriceDetails(order);
    }

    /**
     * Checks to see if any priceDetails need to be combined and if so, combines them.
     * 
     * @param order
     * @return
     */
    protected void mergePriceDetails(PromotableOrder order) {
        List<PromotableOrderItem> items = order.getAllOrderItems();
        for (PromotableOrderItem item : items) {
            item.mergeLikeDetails();
        }
    }

   
    protected void applyItemQualifiersAndTargets(PromotableCandidateItemOffer itemOffer, PromotableOrder order) {
        if (itemOffer.isLegacyOffer()) {
            return;
        } else {
            markQualifiersAndTargets(order, itemOffer);
            splitDetailsIfNecessary(order.getAllPromotableOrderItemPriceDetails());
        }
    }

    protected List<PromotableOrderItemPriceDetail> buildPriceDetailListFromOrderItems(List<PromotableOrderItem> items) {
        List<PromotableOrderItemPriceDetail> itemPriceDetails = new ArrayList<PromotableOrderItemPriceDetail>();
        for (PromotableOrderItem item : items) {
            for (PromotableOrderItemPriceDetail detail : item.getPromotableOrderItemPriceDetails()) {
                itemPriceDetails.add(detail);
            }
        }
        return itemPriceDetails;
    }

    /**
     * Loop through ItemCriteria and mark qualifiers required to give the promotion to 1 or more targets.
     * @param itemOffer
     * @param order
     * @return
     */
    protected boolean markQualifiers(PromotableCandidateItemOffer itemOffer, PromotableOrder order) {
        for (OfferItemCriteria itemCriteria : itemOffer.getCandidateQualifiersMap().keySet()) {
            List<PromotableOrderItem> promotableItems = itemOffer.getCandidateQualifiersMap().get(itemCriteria);

            List<PromotableOrderItemPriceDetail> priceDetails = buildPriceDetailListFromOrderItems(promotableItems);
            
            Collections.sort(priceDetails, getQualifierItemComparator(itemOffer.getOffer().getApplyDiscountToSalePrice()));

            // Calculate the number of qualifiers needed that will not receive the promotion.  
            // These will be reserved first before the target is assigned.
            int qualifierQtyNeeded = itemCriteria.getQuantity();
            
            for (PromotableOrderItemPriceDetail detail : priceDetails) {
                
                // Mark Qualifiers
                if (qualifierQtyNeeded > 0) {
                    int itemQtyAvailableToBeUsedAsQualifier = detail.getQuantityAvailableToBeUsedAsQualifier(itemOffer);
                    if (itemQtyAvailableToBeUsedAsQualifier > 0) {
                        int qtyToMarkAsQualifier = Math.min(qualifierQtyNeeded, itemQtyAvailableToBeUsedAsQualifier);
                        qualifierQtyNeeded -= qtyToMarkAsQualifier;
                        detail.addPromotionQualifier(itemOffer, itemCriteria, qtyToMarkAsQualifier);
                    }
                }
                
                if (qualifierQtyNeeded == 0) {
                    break;
                }
            }

            if (qualifierQtyNeeded != 0) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Loop through ItemCriteria and mark targets that can get this promotion to give the promotion to 1 or more targets.
     * @param itemOffer
     * @param order
     * @return
     */
    protected boolean markTargets(PromotableCandidateItemOffer itemOffer, PromotableOrder order) {
        Offer promotion = itemOffer.getOffer();
        List<PromotableOrderItem> promotableItems = itemOffer.getCandidateTargets();
        List<PromotableOrderItemPriceDetail> priceDetails = buildPriceDetailListFromOrderItems(promotableItems);

        int receiveQtyNeeded = 0;
        for (OfferItemCriteria targetCriteria : itemOffer.getOffer().getTargetItemCriteria()) {
            receiveQtyNeeded += targetCriteria.getQuantity();
        }

        Collections.sort(priceDetails, getTargetItemComparator(promotion.getApplyDiscountToSalePrice()));
        for (PromotableOrderItemPriceDetail priceDetail : priceDetails) {
            if (receiveQtyNeeded > 0) {
                int itemQtyAvailableToBeUsedAsTarget = priceDetail.getQuantityAvailableToBeUsedAsTarget(itemOffer);
                if (itemQtyAvailableToBeUsedAsTarget > 0) {
                    if ((promotion.getMaxUses() == 0) || (itemOffer.getUses() < promotion.getMaxUses())) {
                        int qtyToMarkAsTarget = Math.min(receiveQtyNeeded, itemQtyAvailableToBeUsedAsTarget);
                        receiveQtyNeeded -= qtyToMarkAsTarget;
                        priceDetail.addPromotionDiscount(itemOffer, itemOffer.getOffer().getTargetItemCriteria(), qtyToMarkAsTarget);
                    }
                }
            }

            if (receiveQtyNeeded == 0) {
                itemOffer.addUse();
                break;
            }
        }

        return (receiveQtyNeeded == 0);
    }

    /**
     * Used in {@link #applyItemQualifiersAndTargets(PromotableCandidateItemOffer, PromotableOrder)} allow for customized
     * sorting for which qualifier items should be attempted to be used first for a promotion. Default behavior
     * is to sort descending, so higher-value items are attempted to be qualified first.
     * 
     * @param applyToSalePrice - whether or not the Comparator should use the sale price for comparison
     * @return
     */
    protected Comparator<PromotableOrderItemPriceDetail> getQualifierItemComparator(final boolean applyToSalePrice) {
        return new Comparator<PromotableOrderItemPriceDetail>() {
            @Override
            public int compare(PromotableOrderItemPriceDetail o1, PromotableOrderItemPriceDetail o2) {
                Money price = o1.getPromotableOrderItem().getPriceBeforeAdjustments(applyToSalePrice);
                Money price2 = o2.getPromotableOrderItem().getPriceBeforeAdjustments(applyToSalePrice);
                
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
     * items can be paired with lower-valued items. This also ensures that you will <b>not</b> have the scenario where 2 
     * lower-valued items can be used to qualify a higher-valued target.
     * </p>
     * 
     * @param applyToSalePrice - whether or not the Comparator should use the sale price for comparison
     * @return
     */
    protected Comparator<PromotableOrderItemPriceDetail> getTargetItemComparator(final boolean applyToSalePrice) {
        return new Comparator<PromotableOrderItemPriceDetail>() {
            @Override
            public int compare(PromotableOrderItemPriceDetail o1, PromotableOrderItemPriceDetail o2) {
                Money price = o1.getPromotableOrderItem().getPriceBeforeAdjustments(applyToSalePrice);
                Money price2 = o2.getPromotableOrderItem().getPriceBeforeAdjustments(applyToSalePrice);
                
                // highest amount first
                return price2.compareTo(price);
            }
        };
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
    
    /**
     * This method determines the potential savings for each item offer as if it was the only item offer being applied.
     * @param itemOffers
     * @param order
     */
    protected void calculatePotentialSavings(List<PromotableCandidateItemOffer> itemOffers, PromotableOrder order) {
        if (itemOffers.size() > 1) {
            for (PromotableCandidateItemOffer itemOffer : itemOffers) {
                Money potentialSavings = new Money(order.getOrderCurrency());
                if (itemOffer.isLegacyOffer()) {
                    for (PromotableOrderItem item : itemOffer.getCandidateTargets()) {
                        potentialSavings = potentialSavings.add(
                                itemOffer.calculateSavingsForOrderItem(item, item.getQuantity()));
                    }
                } else {
                    markQualifiersAndTargets(order, itemOffer);
                    for (PromotableOrderItemPriceDetail detail : order.getAllPromotableOrderItemPriceDetails()) {
                        PromotableOrderItem item = detail.getPromotableOrderItem();
                        for (PromotionDiscount discount : detail.getPromotionDiscounts()) {
                            potentialSavings = potentialSavings.add(
                                    itemOffer.calculateSavingsForOrderItem(item, discount.getQuantity()));
                        }
                        // Reset state back for next offer
                        detail.getPromotionDiscounts().clear();
                        detail.getPromotionQualifiers().clear();
                    }
                }
                itemOffer.setPotentialSavings(potentialSavings);
            }
        }
    }

    protected void markQualifiersAndTargets(PromotableOrder order, PromotableCandidateItemOffer itemOffer) {
        boolean matchFound = true;

        if (itemOffer.getOffer().getQualifyingItemCriteria().isEmpty() &&
                itemOffer.getOffer().getTargetItemCriteria().isEmpty()) {
            return;
        }

        int count = 1;
        do {
            boolean qualifiersFound = markQualifiers(itemOffer, order);
            boolean targetsFound = markTargets(itemOffer, order);

            if (qualifiersFound && targetsFound) {
                finalizeQuantities(order.getAllPromotableOrderItemPriceDetails());
            } else {
                clearAllNonFinalizedQuantities(order.getAllPromotableOrderItemPriceDetails());
                matchFound = false;
                break;
            }
            // If we found a match, try again to see if the promotion can be applied again.
        } while (matchFound);
    }

    protected boolean offerListStartsWithNonCombinable(List<PromotableCandidateItemOffer> offerList) {
        if (offerList.size() > 1) {
            PromotableCandidateItemOffer offer = offerList.get(0);
            if (offer.getOffer().isTotalitarianOffer() || !offer.getOffer().isCombinableWithOtherOffers()) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method could be overridden to potentially run all permutations of offers.
     * A reasonable alternative is to have a permutation with nonCombinable offers
     * and another with combinable offers. 
     *
     * @param offers
     * @return
     */
    protected List<List<PromotableCandidateItemOffer>> buildItemOfferPermutations(
            List<PromotableCandidateItemOffer> offers) {
        List<List<PromotableCandidateItemOffer>> listOfOfferLists = new ArrayList<List<PromotableCandidateItemOffer>>();
        // add the default list
        listOfOfferLists.add(offers);

        if (offerListStartsWithNonCombinable(offers)) {
            List<PromotableCandidateItemOffer> listWithoutTotalitarianOrNonCombinables =
                    new ArrayList<PromotableCandidateItemOffer>(offers);

            Iterator<PromotableCandidateItemOffer> offerIterator = listWithoutTotalitarianOrNonCombinables.iterator();
            while (offerIterator.hasNext()) {
                PromotableCandidateItemOffer offer = offerIterator.next();
                if (offer.getOffer().isTotalitarianOffer() || !offer.getOffer().isCombinableWithOtherOffers()) {
                    offerIterator.remove();
                }
            }

            if (listWithoutTotalitarianOrNonCombinables.size() > 0) {
                listOfOfferLists.add(listWithoutTotalitarianOrNonCombinables);
            }
        }

        return listOfOfferLists;
    }

    protected void determineBestPermutation(List<PromotableCandidateItemOffer> itemOffers, PromotableOrder order) {
        List<List<PromotableCandidateItemOffer>> permutations = buildItemOfferPermutations(itemOffers);
        List<PromotableCandidateItemOffer> bestOfferList = null;
        Money lowestSubtotal = null;
        if (permutations.size() > 1) {
            for (List<PromotableCandidateItemOffer> offerList : permutations) {
                applyAllItemOffers(offerList, order);
                chooseSaleOrRetailAdjustments(order);
                Money testSubtotal = order.calculateSubtotalWithAdjustments();

                if (lowestSubtotal == null || testSubtotal.lessThan(lowestSubtotal)) {
                    lowestSubtotal = testSubtotal;
                    bestOfferList = offerList;
                }

                // clear price details
                for (PromotableOrderItem item : order.getDiscountableOrderItems()) {
                    item.resetPriceDetails();
                }
            }
        } else {
            bestOfferList = permutations.get(0);
        }
        applyAllItemOffers(bestOfferList, order);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void applyAndCompareOrderAndItemOffers(PromotableOrder order,
            List<PromotableCandidateOrderOffer> qualifiedOrderOffers,
            List<PromotableCandidateItemOffer> qualifiedItemOffers) {
        if (!qualifiedItemOffers.isEmpty()) {
            calculatePotentialSavings(qualifiedItemOffers, order);
            // Sort order item offers by priority and potential total discount
            Collections.sort(qualifiedItemOffers, ItemOfferComparator.INSTANCE);
            
            if (qualifiedItemOffers.size() > 1) {
                determineBestPermutation(qualifiedItemOffers, order);
            } else {
                applyAllItemOffers(qualifiedItemOffers, order);

            }
        }
        chooseSaleOrRetailAdjustments(order);
        order.setOrderSubTotalToPriceWithAdjustments();

        if (!qualifiedOrderOffers.isEmpty()) {
            // Sort order offers by priority and discount
            Collections.sort(qualifiedOrderOffers, OrderOfferComparator.INSTANCE);
            //qualifiedOrderOffers = removeTrailingNotCombinableOrderOffers(qualifiedOrderOffers);
            applyAllOrderOffers(qualifiedOrderOffers, order);
        }

        order.setOrderSubTotalToPriceWithAdjustments();
        
        // TODO: only do this if absolutely required.    If you find one that no longer qualifies, then 
        // pull it out and reapply.
        if (!qualifiedOrderOffers.isEmpty() && !qualifiedItemOffers.isEmpty()) {
            List<PromotableCandidateOrderOffer> finalQualifiedOrderOffers = new ArrayList<PromotableCandidateOrderOffer>();
            order.removeAllCandidateOrderOfferAdjustments();
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
            Collections.sort(finalQualifiedOrderOffers, OrderOfferComparator.INSTANCE);
            if (!finalQualifiedOrderOffers.isEmpty()) {
                applyAllOrderOffers(finalQualifiedOrderOffers, order);
                order.setOrderSubTotalToPriceWithAdjustments();
            }  
        }
    }

    /**
     * Gets rid of totalitarian and nonCombinable item offers that can't possibly be applied.
     * @param qualifiedItemOffers
     */
    private void removeTrailingNonCombinableOrTotalitarianOffers(List<PromotableCandidateItemOffer> qualifiedItemOffers) {
        boolean first = true;
        Iterator<PromotableCandidateItemOffer> offerIterator = qualifiedItemOffers.iterator();
        if (offerIterator.hasNext()) {
            // ignore the first one.
            offerIterator.next();
        }

        while (offerIterator.hasNext()) {
            PromotableCandidateItemOffer itemOffer = offerIterator.next();
            if (itemOffer.getOffer().isTotalitarianOffer()) {
                // Remove Totalitarian offers that aren't the first offer.
                offerIterator.remove();
            } else {
                if (!itemOffer.isLegacyOffer() && !itemOffer.getOffer().isCombinableWithOtherOffers()) {
                    // Remove nonCombinable offers that aren't the first offer
                    offerIterator.remove();
                }
            }
        }
    }
}
