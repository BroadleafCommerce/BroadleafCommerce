/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.offer.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.service.GenericEntityService;
import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.domain.OfferPriceData;
import org.broadleafcommerce.core.offer.domain.OrderItemPriceDetailAdjustment;
import org.broadleafcommerce.core.offer.service.discount.PromotionDiscount;
import org.broadleafcommerce.core.offer.service.discount.PromotionQualifier;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateItemOffer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableItemFactory;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOfferUtility;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrder;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItemPriceDetail;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItemPriceDetailAdjustment;
import org.broadleafcommerce.core.offer.service.processor.ItemOfferMarkTargets;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemContainer;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetail;
import org.broadleafcommerce.core.order.domain.OrderItemQualifier;
import org.broadleafcommerce.core.order.domain.dto.OrderItemHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

/**
 * 
 * @author bpolster
 *
 */
@Service("blOfferServiceUtilities")
public class OfferServiceUtilitiesImpl implements OfferServiceUtilities {
    protected static final Log LOG = LogFactory.getLog(OfferServiceUtilitiesImpl.class);

    @Resource(name = "blPromotableItemFactory")
    protected PromotableItemFactory promotableItemFactory;

    @Resource(name = "blOfferDao")
    protected OfferDao offerDao;

    @Resource(name = "blOfferServiceExtensionManager")
    protected OfferServiceExtensionManager extensionManager;

    @Resource(name = "blGenericEntityService")                                                                                                                                                                   
    protected GenericEntityService entityService;

    protected final PromotableOfferUtility promotableOfferUtility;

    public OfferServiceUtilitiesImpl(PromotableOfferUtility promotableOfferUtility) {
        this.promotableOfferUtility = promotableOfferUtility;
    }

    @Override
    public void sortTargetItemDetails(List<PromotableOrderItemPriceDetail> itemPriceDetails, boolean applyToSalePrice) {
        Collections.sort(itemPriceDetails, getPromotableItemComparator(applyToSalePrice));
    }

    @Override
    public void sortQualifierItemDetails(List<PromotableOrderItemPriceDetail> itemPriceDetails, boolean applyToSalePrice) {
        Collections.sort(itemPriceDetails, getPromotableItemComparator(applyToSalePrice));
    }

    protected Comparator<PromotableOrderItemPriceDetail> getPromotableItemComparator(final boolean applyToSalePrice) {
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
    public OrderItem findRelatedQualifierRoot(OrderItem relatedQualifier) {
        OrderItem relatedQualifierRoot = null;
        if (relatedQualifier != null) {
            relatedQualifierRoot = relatedQualifier;
            while (relatedQualifierRoot.getParentOrderItem() != null) {
                relatedQualifierRoot = relatedQualifierRoot.getParentOrderItem();
            }
        }
        return relatedQualifierRoot;
    }

    @Override
    public boolean itemOfferCanBeApplied(PromotableCandidateItemOffer itemOffer, List<PromotableOrderItemPriceDetail> details) {

        for (PromotableOrderItemPriceDetail detail : details) {
            for (PromotableOrderItemPriceDetailAdjustment adjustment : detail.getCandidateItemAdjustments()) {
                if (adjustment.isTotalitarian() || itemOffer.getOffer().isTotalitarianOffer()) {
                    // A totalitarian offer has already been applied or this offer is totalitarian
                    // and another offer was already applied.
                    return false;
                } else if (!adjustment.isCombinable() || !itemOffer.getOffer().isCombinableWithOtherOffers()) {
                    // A nonCombinable offer has been applied or this is a non-combinable offer
                    // and adjustments have already been applied.
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int markQualifiersForCriteria(PromotableCandidateItemOffer itemOffer, OfferItemCriteria itemCriteria,
            List<PromotableOrderItemPriceDetail> priceDetails) {

        sortQualifierItemDetails(priceDetails, itemOffer.getOffer().getApplyDiscountToSalePrice());

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
        return qualifierQtyNeeded;
    }

    @Override
    public int markTargetsForCriteria(PromotableCandidateItemOffer itemOffer, OrderItem relatedQualifier,
            boolean checkOnly, Offer promotion, OrderItem relatedQualifierRoot, OfferItemCriteria itemCriteria,
            List<PromotableOrderItemPriceDetail> priceDetails, int targetQtyNeeded) {
        for (PromotableOrderItemPriceDetail priceDetail : priceDetails) {
            if (relatedQualifier != null) {
                // We need to make sure that this item is either a parent, child, or the same as the qualifier root
                OrderItem thisItem = priceDetail.getPromotableOrderItem().getOrderItem();
                if (!relatedQualifierRoot.isAParentOf(thisItem) && !thisItem.isAParentOf(relatedQualifierRoot) &&
                        !thisItem.equals(relatedQualifierRoot)) {
                    continue;
                }
            }

            int itemQtyAvailableToBeUsedAsTarget = priceDetail.getQuantityAvailableToBeUsedAsTarget(itemOffer);
            if (itemQtyAvailableToBeUsedAsTarget > 0) {
                if (promotion.isUnlimitedUsePerOrder() || (itemOffer.getUses() < promotion.getMaxUsesPerOrder())) {
                    int qtyToMarkAsTarget = Math.min(targetQtyNeeded, itemQtyAvailableToBeUsedAsTarget);
                    targetQtyNeeded -= qtyToMarkAsTarget;
                    if (!checkOnly) {
                        priceDetail.addPromotionDiscount(itemOffer, itemCriteria, qtyToMarkAsTarget);
                    }
                }
            }

            if (targetQtyNeeded == 0) {
                break;
            }
        }
        return targetQtyNeeded;
    }

    @Override
    public boolean markTargetsForOfferPriceData(PromotableCandidateItemOffer itemOffer, OrderItem relatedQualifier,
                                                boolean checkOnly, Offer promotion, OrderItem relatedQualifierRoot, OfferPriceData offerPriceData,
                                                List<PromotableOrderItemPriceDetail> priceDetails) {
        int targetQtyNeeded = offerPriceData.getQuantity();
        int minRequiredTargetQuantity = itemOffer.getMinimumRequiredTargetQuantity();
        if (minRequiredTargetQuantity > 1 && minRequiredTargetQuantity > targetQtyNeeded) {
            targetQtyNeeded = minRequiredTargetQuantity;
        }

        for (PromotableOrderItemPriceDetail priceDetail : priceDetails) {
            if (relatedQualifier != null) {
                // We need to make sure that this item is either a parent, child, or the same as the qualifier root
                OrderItem thisItem = priceDetail.getPromotableOrderItem().getOrderItem();
                if (!relatedQualifierRoot.isAParentOf(thisItem) && !thisItem.isAParentOf(relatedQualifierRoot) &&
                        !thisItem.equals(relatedQualifierRoot)) {
                    continue;
                }
            }

            int itemQtyAvailableToBeUsedAsTarget = priceDetail.getQuantityAvailableToBeUsedAsTarget(itemOffer);
            if (itemQtyAvailableToBeUsedAsTarget > 0) {
                if (promotion.isUnlimitedUsePerOrder() || (itemOffer.getUses() < promotion.getMaxUsesPerOrder())) {
                    int qtyToMarkAsTarget = Math.min(targetQtyNeeded, itemQtyAvailableToBeUsedAsTarget);
                    targetQtyNeeded -= qtyToMarkAsTarget;
                    if (!checkOnly) {
                        priceDetail.addPromotionDiscount(itemOffer, null, qtyToMarkAsTarget);
                    }
                }
            }

            if (targetQtyNeeded == 0) {
                break;
            }
        }
        return targetQtyNeeded == 0;
    }

    @Override
    public int markRelatedQualifiersAndTargetsForItemCriteria(PromotableCandidateItemOffer itemOffer, PromotableOrder order,
            OrderItemHolder orderItemHolder, OfferItemCriteria itemCriteria,
            List<PromotableOrderItemPriceDetail> priceDetails, ItemOfferMarkTargets itemOfferMarkTargets) {
        sortQualifierItemDetails(priceDetails, itemOffer.getOffer().getApplyDiscountToSalePrice());

        // Calculate the number of qualifiers needed that will not receive the promotion.  
        // These will be reserved first before the target is assigned.
        int qualifierQtyNeeded = itemCriteria.getQuantity();

        for (PromotableOrderItemPriceDetail detail : priceDetails) {
            OrderItem oi = detail.getPromotableOrderItem().getOrderItem();

            if (qualifierQtyNeeded > 0) {
                int itemQtyAvailableToBeUsedAsQualifier = detail.getQuantityAvailableToBeUsedAsQualifier(itemOffer);
                if (itemQtyAvailableToBeUsedAsQualifier > 0) {
                    // We have found a qualifier that meets this offer criteria. First, we'll save some state that we 
                    // might need in the future.
                    OfferItemCriteria previousQualifierCriteria = null;
                    for (PromotionQualifier possibleQualifier : detail.getPromotionQualifiers()) {
                        if (possibleQualifier.getPromotion().equals(itemOffer.getOffer())) {
                            previousQualifierCriteria = possibleQualifier.getItemCriteria();
                            break;
                        }
                    }

                    // Go ahead and mark this item as a qualifier
                    int qtyToMarkAsQualifier = Math.min(qualifierQtyNeeded, itemQtyAvailableToBeUsedAsQualifier);
                    qualifierQtyNeeded -= qtyToMarkAsQualifier;
                    PromotionQualifier pq = detail.addPromotionQualifier(itemOffer, itemCriteria, qtyToMarkAsQualifier);
                    pq.setPrice(detail.getPromotableOrderItem().getPriceBeforeAdjustments(itemOffer.getOffer().getApplyDiscountToSalePrice()));

                    // Now, we need to see if there exists a target(s) that is suitable for this qualifier due to
                    // the relationship flag. If we are on the last qualifier required for this offer, we want to 
                    // actually go ahead and mark the target that we'll be using. Otherwise, we just want to check 
                    // that there is an eligible target(s) and continue on.
                    if (itemOfferMarkTargets.markTargets(itemOffer, order, oi, true)) {
                        // We found a target. Let's save this related order item used as the qualifier in case
                        // we succeed
                        orderItemHolder.setOrderItem(oi);
                    } else {
                        // If we didn't find a target, we need to roll back how we marked this item as a qualifier.
                        qualifierQtyNeeded += qtyToMarkAsQualifier;
                        if (pq.getQuantity() == qtyToMarkAsQualifier) {
                            detail.getPromotionQualifiers().remove(pq);
                        } else {
                            pq.setItemCriteria(previousQualifierCriteria);
                            pq.setQuantity(pq.getQuantity() - qtyToMarkAsQualifier);
                        }
                    }
                }
            }

            if (qualifierQtyNeeded == 0) {
                break;
            }
        }
        return qualifierQtyNeeded;
    }

    @Override
    public void applyAdjustmentsForItemPriceDetails(PromotableCandidateItemOffer itemOffer, List<PromotableOrderItemPriceDetail> itemPriceDetails) {
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
                    OrderItem orderItem = itemPriceDetail.getPromotableOrderItem().getOrderItem();
                    Boolean offerCanApplyToChildOrderItems = itemOffer.getOffer().getApplyToChildItems();
                    if (isAddOnOrderItem(orderItem) && !offerCanApplyToChildOrderItems) {
                        break;
                    }
                    applyOrderItemAdjustment(itemOffer, itemPriceDetail);
                    break;
                }
            }
        }
    }

    @Override
    public boolean isAddOnOrderItem(OrderItem orderItem) {
        if (DiscreteOrderItem.class.isAssignableFrom(orderItem.getClass())) {
            DiscreteOrderItem discreteOrderItem = (DiscreteOrderItem) orderItem;

            Map<String, String> attributes = discreteOrderItem.getAdditionalAttributes();
            boolean isAddOnOrderItem = attributes.containsKey("addOnXrefId");
            boolean isChildOrderItem = discreteOrderItem.isChildOrderItem();

            return isChildOrderItem && isAddOnOrderItem;
        }
        return false;
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
            Money savings = promotableOfferUtility.calculateSavingsForOrderItem(itemOffer, detail.getPromotableOrderItem(), 1);
            if (salePrice != null) {
                if (salePrice.lessThan(retailPrice.subtract(savings))) {
                    // Not good enough
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void applyOrderItemAdjustment(PromotableCandidateItemOffer itemOffer,
            PromotableOrderItemPriceDetail itemPriceDetail) {
        PromotableOrderItemPriceDetailAdjustment promotableOrderItemPriceDetailAdjustment =
                promotableItemFactory.createPromotableOrderItemPriceDetailAdjustment(itemOffer, itemPriceDetail);
        //don't want to have negative adjustments
        if(promotableOrderItemPriceDetailAdjustment.getRetailAdjustmentValue().lessThan(BigDecimal.ZERO) ||
                promotableOrderItemPriceDetailAdjustment.getSaleAdjustmentValue().lessThan(BigDecimal.ZERO)){
            return;
        }
        itemPriceDetail.addCandidateItemPriceDetailAdjustment(promotableOrderItemPriceDetailAdjustment);
    }

    @Override
    public List<OrderItem> buildOrderItemList(Order order) {
        List<OrderItem> orderItemList = new ArrayList<OrderItem>();
        for (OrderItem currentItem : order.getOrderItems()) {
            if (currentItem instanceof OrderItemContainer) {
                OrderItemContainer container = (OrderItemContainer) currentItem;
                if (container.isPricingAtContainerLevel()) {
                    orderItemList.add(currentItem);
                } else {
                    for (OrderItem containedItem : container.getOrderItems()) {
                        orderItemList.add(containedItem);
                    }
                }
            } else {
                orderItemList.add(currentItem);
            }
        }

        return orderItemList;
    }

    @Override
    public Map<OrderItem, PromotableOrderItem> buildPromotableItemMap(PromotableOrder promotableOrder) {
        Map<OrderItem, PromotableOrderItem> promotableItemMap = new HashMap<OrderItem, PromotableOrderItem>();
        for (PromotableOrderItem item : promotableOrder.getAllOrderItems()) {
            promotableItemMap.put(item.getOrderItem(), item);
        }
        return promotableItemMap;
    }

    @Override
    public Map<Long, OrderItemPriceDetailAdjustment> buildItemDetailAdjustmentMap(OrderItemPriceDetail itemDetail) {
        Map<Long, OrderItemPriceDetailAdjustment> itemAdjustmentMap = new HashMap<Long, OrderItemPriceDetailAdjustment>();
        
        List<OrderItemPriceDetailAdjustment> adjustmentsToRemove = new ArrayList<OrderItemPriceDetailAdjustment>();
        for (OrderItemPriceDetailAdjustment adjustment : itemDetail.getOrderItemPriceDetailAdjustments()) {
            if (itemAdjustmentMap.containsKey(adjustment.getOffer().getId())) {
                if (LOG.isDebugEnabled()) {
                    StringBuilder sb = new StringBuilder("Detected collisions for item adjustments with ids ")
                        .append(itemAdjustmentMap.get(adjustment.getOffer().getId()).getId())
                        .append(" and ")
                        .append(adjustment.getId());
                    LOG.debug(sb.toString());
                }
                adjustmentsToRemove.add(adjustment);
            } else {
                itemAdjustmentMap.put(adjustment.getOffer().getId(), adjustment);
            }
        }
        
        for (OrderItemPriceDetailAdjustment adjustment : adjustmentsToRemove) {
            itemDetail.getOrderItemPriceDetailAdjustments().remove(adjustment);
        }
        
        return itemAdjustmentMap;
    }

    @Override
    public void updatePriceDetail(OrderItemPriceDetail itemDetail,
            PromotableOrderItemPriceDetail promotableDetail) {
        Map<Long, OrderItemPriceDetailAdjustment> itemAdjustmentMap =
                buildItemDetailAdjustmentMap(itemDetail);

        if (itemDetail.getQuantity() != promotableDetail.getQuantity()) {
            itemDetail.setQuantity(promotableDetail.getQuantity());
        }

        if (promotableDetail.isAdjustmentsFinalized()) {
            itemDetail.setUseSalePrice(promotableDetail.useSaleAdjustments());
        }

        for (PromotableOrderItemPriceDetailAdjustment adjustment : promotableDetail.getCandidateItemAdjustments()) {
            OrderItemPriceDetailAdjustment itemAdjustment = itemAdjustmentMap.remove(adjustment.getOfferId());
            if (itemAdjustment != null) {
                // Update existing adjustment
                if (!itemAdjustment.getValue().equals(adjustment.getAdjustmentValue())) {
                    updateItemAdjustment(itemAdjustment, adjustment);
                }
            } else {
                // Add a new adjustment
                final OrderItemPriceDetailAdjustment newItemAdjustment;
                ExtensionResultHolder resultHolder = new ExtensionResultHolder();

                if (extensionManager != null) {
                    extensionManager.createOrderItemPriceDetailAdjustment(resultHolder, itemDetail);
                }
                if (resultHolder != null && resultHolder.getContextMap().containsKey("OrderItemPriceDetailAdjustment")) {
                    newItemAdjustment = (OrderItemPriceDetailAdjustment) resultHolder.getContextMap().get("OrderItemPriceDetailAdjustment");
                } else {
                    newItemAdjustment = offerDao.createOrderItemPriceDetailAdjustment();
                }
                newItemAdjustment.init(itemDetail, adjustment.getOffer(), null);
                updateItemAdjustment(newItemAdjustment, adjustment);
                itemDetail.getOrderItemPriceDetailAdjustments().add(newItemAdjustment);
            }
        }

        if (itemAdjustmentMap.size() > 0) {
            // Remove adjustments that were on the order item but no longer needed.
            List<Long> adjustmentIdsToRemove = new ArrayList<Long>();
            for (OrderItemPriceDetailAdjustment adjustmentToRemove : itemAdjustmentMap.values()) {
                adjustmentIdsToRemove.add(adjustmentToRemove.getOffer().getId());
            }

            Iterator<OrderItemPriceDetailAdjustment> iterator = itemDetail.getOrderItemPriceDetailAdjustments().iterator();
            while (iterator.hasNext()) {
                OrderItemPriceDetailAdjustment adj = iterator.next();
                if (adjustmentIdsToRemove.contains(adj.getOffer().getId())) {
                    iterator.remove();
                    entityService.remove(adj);
                }
            }
        }
    }

    protected void updateItemAdjustment(OrderItemPriceDetailAdjustment itemAdjustment,
            PromotableOrderItemPriceDetailAdjustment promotableAdjustment) {
        itemAdjustment.setValue(promotableAdjustment.getAdjustmentValue());
        itemAdjustment.setSalesPriceValue(promotableAdjustment.getSaleAdjustmentValue());
        itemAdjustment.setRetailPriceValue(promotableAdjustment.getRetailAdjustmentValue());
        itemAdjustment.setAppliedToSalePrice(promotableAdjustment.isAppliedToSalePrice());
    }

    @Override
    public void removeUnmatchedPriceDetails(Map<Long, ? extends OrderItemPriceDetail> unmatchedDetailsMap,
            Iterator<? extends OrderItemPriceDetail> pdIterator) {
        while (pdIterator.hasNext()) {
            OrderItemPriceDetail currentDetail = pdIterator.next();
            if (unmatchedDetailsMap.containsKey(currentDetail.getId())) {
                pdIterator.remove();
                entityService.remove(currentDetail);
            }
        }
    }

    @Override
    public void removeUnmatchedQualifiers(Map<Long, ? extends OrderItemQualifier> unmatchedQualifiersMap,
            Iterator<? extends OrderItemQualifier> qIterator) {
        while (qIterator.hasNext()) {
            OrderItemQualifier currentQualifier = qIterator.next();
            if (unmatchedQualifiersMap.containsKey(currentQualifier.getId())) {
                qIterator.remove();
                entityService.remove(currentQualifier);
            }
        }
    }

    @Override
    public boolean orderMeetsQualifyingSubtotalRequirements(PromotableOrder order, Offer offer, HashMap<OfferItemCriteria, List<PromotableOrderItem>> qualifiersMap) {
        Money qualifyingItemSubTotal = offer.getQualifyingItemSubTotal();
        if (qualifyingItemSubTotal == null || qualifyingItemSubTotal.lessThanOrEqual(Money.ZERO)) {
            return true;
        }
        return orderMeetsProvidedSubtotalRequirement(offer, qualifiersMap, qualifyingItemSubTotal);
    }

    @Override
    public boolean orderMeetsTargetSubtotalRequirements(PromotableOrder order, Offer offer, HashMap<OfferItemCriteria, List<PromotableOrderItem>> targetsMap) {
        Money targetMinSubTotal = offer.getTargetMinSubTotal();
        if (targetMinSubTotal == null || targetMinSubTotal.lessThanOrEqual(Money.ZERO)) {
            return true;
        }
        return orderMeetsProvidedSubtotalRequirement(offer, targetsMap, targetMinSubTotal);
    }

    protected boolean orderMeetsProvidedSubtotalRequirement(Offer offer, HashMap<OfferItemCriteria, List<PromotableOrderItem>> promotableOrderItems, Money minSubTotal) {
        Money subtotal = Money.ZERO;

        for (OfferItemCriteria itemCriteria : promotableOrderItems.keySet()) {
            List<PromotableOrderItem> promotableItems = promotableOrderItems.get(itemCriteria);

            for (PromotableOrderItem item : promotableItems) {
                boolean shouldApplyDiscountToSalePrice = offer.getApplyDiscountToSalePrice();
                Money priceBeforeAdjustments = item.getPriceBeforeAdjustments(shouldApplyDiscountToSalePrice);
                int quantity = item.getQuantity();

                Money lineItemAmount = priceBeforeAdjustments.multiply(quantity);
                subtotal = subtotal.add(lineItemAmount);
                if (subtotal.greaterThanOrEqual(minSubTotal)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean orderMeetsSubtotalRequirements(PromotableOrder order, Offer offer) {
        Money orderMinSubTotal = offer.getOrderMinSubTotal();
        return orderMinSubTotal == null
                || orderMinSubTotal.lessThanOrEqual(Money.ZERO)
                || orderMinSubTotal.lessThanOrEqual(order.getOrder().getSubTotal());
    }

    public PromotableItemFactory getPromotableItemFactory() {
        return promotableItemFactory;
    }

    public void setPromotableItemFactory(PromotableItemFactory promotableItemFactory) {
        this.promotableItemFactory = promotableItemFactory;
    }

    public OfferDao getOfferDao() {
        return offerDao;
    }

    public void setOfferDao(OfferDao offerDao) {
        this.offerDao = offerDao;
    }

    public GenericEntityService getGenericEntityService() {
        return entityService;
    }

    public void setGenericEntityService(GenericEntityService entityService) {
        this.entityService = entityService;
    }
}
