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
package org.broadleafcommerce.core.offer.service.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.FulfillmentGroupAdjustment;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferRule;
import org.broadleafcommerce.core.offer.domain.OrderAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderItemPriceDetailAdjustment;
import org.broadleafcommerce.core.offer.service.OfferServiceUtilities;
import org.broadleafcommerce.core.offer.service.discount.CandidatePromotionItems;
import org.broadleafcommerce.core.offer.service.discount.PromotionQualifier;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateOrderOffer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableFulfillmentGroup;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableFulfillmentGroupAdjustment;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableItemFactory;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrder;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderAdjustment;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItemPriceDetail;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItemPriceDetailAdjustment;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferRuleType;
import org.broadleafcommerce.core.order.dao.OrderItemDao;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetail;
import org.broadleafcommerce.core.order.domain.OrderItemQualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * @author jfischer, bpolster
 */
@Service("blOrderOfferProcessor")
public class OrderOfferProcessorImpl extends AbstractBaseProcessor implements OrderOfferProcessor {

    private static final Log LOG = LogFactory.getLog(OrderOfferProcessorImpl.class);

    @Resource(name = "blPromotableItemFactory")
    protected PromotableItemFactory promotableItemFactory;

    @Resource(name = "blOrderItemDao")
    protected OrderItemDao orderItemDao;

    @Resource(name = "blOfferDao")
    protected OfferDao offerDao;

    @Resource(name = "blOfferServiceUtilities")
    protected OfferServiceUtilities offerServiceUtilities;

    @Override
    public void filterOrderLevelOffer(PromotableOrder promotableOrder, List<PromotableCandidateOrderOffer> qualifiedOrderOffers, Offer offer) {
        if (offer.getDiscountType().getType().equals(OfferDiscountType.FIX_PRICE.getType())) {
            LOG.warn("Offers of type ORDER may not have a discount type of FIX_PRICE. Ignoring order offer (name=" + offer.getName() + ")");
            return;
        }
        boolean orderLevelQualification = false;
        //Order Qualification
        orderQualification:
        {
            if (couldOfferApplyToOrder(offer, promotableOrder)) {
                orderLevelQualification = true;
                break orderQualification;
            }
            for (PromotableOrderItem orderItem : promotableOrder.getDiscountableOrderItems(offer.getApplyDiscountToSalePrice())) {
                if (couldOfferApplyToOrder(offer, promotableOrder, orderItem)) {
                    orderLevelQualification = true;
                    break orderQualification;
                }
            }
            for (PromotableFulfillmentGroup fulfillmentGroup : promotableOrder.getFulfillmentGroups()) {
                if (couldOfferApplyToOrder(offer, promotableOrder, fulfillmentGroup)) {
                    orderLevelQualification = true;
                    break orderQualification;
                }
            }
        }
        //Item Qualification - new for 1.5!
        if (orderLevelQualification) {
            CandidatePromotionItems candidates = couldOfferApplyToOrderItems(offer, promotableOrder.getDiscountableOrderItems(offer.getApplyDiscountToSalePrice()));
            if (candidates.isMatchedQualifier()) {
                PromotableCandidateOrderOffer candidateOffer = createCandidateOrderOffer(promotableOrder, qualifiedOrderOffers, offer);
                candidateOffer.getCandidateQualifiersMap().putAll(candidates.getCandidateQualifiersMap());
            }
        }
    }

    @Override
    public boolean couldOfferApplyToOrder(Offer offer, PromotableOrder promotableOrder) {
        return couldOfferApplyToOrder(offer, promotableOrder, null, null);
    }

    /**
     * Private method which executes the appliesToOrderRules in the Offer to determine if this offer
     * can be applied to the Order, OrderItem, or FulfillmentGroup.
     *
     * @param offer
     * @param order
     * @param orderItem
     * @return true if offer can be applied, otherwise false
     */
    protected boolean couldOfferApplyToOrder(Offer offer, PromotableOrder promotableOrder, PromotableOrderItem orderItem) {
        return couldOfferApplyToOrder(offer, promotableOrder, orderItem, null);
    }

    /**
     * Private method which executes the appliesToOrderRules in the Offer to determine if this offer
     * can be applied to the Order, OrderItem, or FulfillmentGroup.
     *
     * @param offer
     * @param order
     * @param fulfillmentGroup
     * @return true if offer can be applied, otherwise false
     */
    protected boolean couldOfferApplyToOrder(Offer offer, PromotableOrder promotableOrder, PromotableFulfillmentGroup fulfillmentGroup) {
        return couldOfferApplyToOrder(offer, promotableOrder, null, fulfillmentGroup);
    }

    /**
     * Private method which executes the appliesToOrderRules in the Offer to determine if this offer
     * can be applied to the Order, OrderItem, or FulfillmentGroup.
     *
     * @param offer
     * @param order
     * @param promotableOrderItem
     * @param promotableFulfillmentGroup
     * @return true if offer can be applied, otherwise false
     */
    protected boolean couldOfferApplyToOrder(Offer offer, PromotableOrder promotableOrder, PromotableOrderItem promotableOrderItem, PromotableFulfillmentGroup promotableFulfillmentGroup) {
        boolean appliesToItem = false;
        String rule = null;
        if (offer.getAppliesToOrderRules() != null && offer.getAppliesToOrderRules().trim().length() != 0) {
            rule = offer.getAppliesToOrderRules();
        } else {
            OfferRule orderRule = offer.getOfferMatchRules().get(OfferRuleType.ORDER.getType());
            if (orderRule != null) {
                rule = orderRule.getMatchRule();
            }
        }

        if (rule != null) {

            HashMap<String, Object> vars = new HashMap<String, Object>();
            promotableOrder.updateRuleVariables(vars);
            vars.put("offer", offer);
            if (promotableFulfillmentGroup != null) {
                promotableFulfillmentGroup.updateRuleVariables(vars);
            }
            if (promotableOrderItem != null) {
                promotableOrderItem.updateRuleVariables(vars);
            }
            Boolean expressionOutcome = executeExpression(rule, vars);
            if (expressionOutcome != null && expressionOutcome) {
                appliesToItem = true;
            }
        } else {
            appliesToItem = true;
        }

        return appliesToItem;
    }

    protected PromotableCandidateOrderOffer createCandidateOrderOffer(PromotableOrder promotableOrder, List<PromotableCandidateOrderOffer> qualifiedOrderOffers, Offer offer) {
        PromotableCandidateOrderOffer promotableCandidateOrderOffer = promotableItemFactory.createPromotableCandidateOrderOffer(promotableOrder, offer);
        qualifiedOrderOffers.add(promotableCandidateOrderOffer);

        return promotableCandidateOrderOffer;
    }

    @Override
    public List<PromotableCandidateOrderOffer> removeTrailingNotCombinableOrderOffers(List<PromotableCandidateOrderOffer> candidateOffers) {
        List<PromotableCandidateOrderOffer> remainingCandidateOffers = new ArrayList<PromotableCandidateOrderOffer>();
        int offerCount = 0;
        for (PromotableCandidateOrderOffer candidateOffer : candidateOffers) {
            if (offerCount == 0) {
                remainingCandidateOffers.add(candidateOffer);
            } else {
                boolean treatAsNewFormat = false;
                if (candidateOffer.getOffer().getTreatAsNewFormat() != null && candidateOffer.getOffer().getTreatAsNewFormat()) {
                    treatAsNewFormat = true;
                }
                if ((!treatAsNewFormat && candidateOffer.getOffer().isCombinableWithOtherOffers()) || (treatAsNewFormat && (candidateOffer.getOffer().isTotalitarianOffer() == null || !candidateOffer.getOffer().isTotalitarianOffer()))) {
                    remainingCandidateOffers.add(candidateOffer);
                }
            }
            offerCount++;
        }
        return remainingCandidateOffers;
    }

    @Override
    public void applyAllOrderOffers(List<PromotableCandidateOrderOffer> orderOffers, PromotableOrder promotableOrder) {
        // If order offer is not combinable, first verify order adjustment is zero, if zero, compare item discount total vs this offer's total
        Iterator<PromotableCandidateOrderOffer> orderOfferIterator = orderOffers.iterator();
        while (orderOfferIterator.hasNext()) {
            PromotableCandidateOrderOffer orderOffer = orderOfferIterator.next();
            
            if (promotableOrder.canApplyOrderOffer(orderOffer)) {
                applyOrderOffer(promotableOrder, orderOffer);
                
                if (orderOffer.isTotalitarian() || promotableOrder.isTotalitarianItemOfferApplied()) {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Totalitarian Order Offer Applied.   Comparing order and item offers for best outcome.");
                    }
                    compareAndAdjustOrderAndItemOffers(promotableOrder);
                    // We continue because this could be the first offer and marked as totalitarian, but not as good as an
                    // item offer. There could be other order offers that are not totalitarian that also qualify.
                    continue; 
                }
                
                if (!orderOffer.isCombinable()) {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Non-Combinable Order Offer Applied with id=[" + orderOffer.getOffer().getId() +"].  No other order offers can be applied");
                    }
                    break;
                }
                    
            }
        }
        promotableOrder.getOrder().setSubTotal(promotableOrder.calculateSubtotalWithAdjustments());
    }

    /**
     * Called when the system must determine whether to apply order or item adjustments.
     * @param promotableOrder
     * @param orderOffersApplied
     */
    protected void compareAndAdjustOrderAndItemOffers(PromotableOrder promotableOrder) {
        Money orderAdjustmentTotal = promotableOrder.calculateOrderAdjustmentTotal();
        Money itemAdjustmentTotal = promotableOrder.calculateItemAdjustmentTotal();

        if (orderAdjustmentTotal.greaterThanOrEqual(itemAdjustmentTotal)) {
            promotableOrder.removeAllCandidateItemOfferAdjustments();
        } else {
            promotableOrder.removeAllCandidateOrderOfferAdjustments();
        }
    }

    /**
     * Private method used by applyAllOrderOffers to create an OrderAdjustment from a CandidateOrderOffer
     * and associates the OrderAdjustment to the Order.
     *
     * @param orderOffer a CandidateOrderOffer to apply to an Order
     */
    protected void applyOrderOffer(PromotableOrder promotableOrder, PromotableCandidateOrderOffer orderOffer) {
        PromotableOrderAdjustment promotableOrderAdjustment = promotableItemFactory.createPromotableOrderAdjustment(orderOffer, promotableOrder);
        promotableOrder.addCandidateOrderAdjustment(promotableOrderAdjustment);
    }

    @Override
    public PromotableItemFactory getPromotableItemFactory() {
        return promotableItemFactory;
    }

    @Override
    public void setPromotableItemFactory(PromotableItemFactory promotableItemFactory) {
        this.promotableItemFactory = promotableItemFactory;
    }
    
    protected Map<Long, PromotableOrderAdjustment> buildPromotableOrderAdjustmentsMap(PromotableOrder promotableOrder) {
        Map<Long, PromotableOrderAdjustment> adjustmentsMap = new HashMap<Long, PromotableOrderAdjustment>();
        for (PromotableOrderAdjustment adjustment : promotableOrder.getCandidateOrderAdjustments()) {
            adjustmentsMap.put(adjustment.getOffer().getId(), adjustment);
        }
        return adjustmentsMap;
    }

    protected void synchronizeOrderAdjustments(PromotableOrder promotableOrder) {
        Order order = promotableOrder.getOrder();

        if (order.getOrderAdjustments().isEmpty() && promotableOrder.getCandidateOrderAdjustments().isEmpty()) {
            return;
        }

        Map<Long, PromotableOrderAdjustment> newAdjustmentsMap = buildPromotableOrderAdjustmentsMap(promotableOrder);
        Iterator<OrderAdjustment> orderAdjIterator = order.getOrderAdjustments().iterator();

        while (orderAdjIterator.hasNext()) {
            OrderAdjustment adjustment = orderAdjIterator.next();
            if (adjustment.getOffer() != null) {
                Long offerId = adjustment.getOffer().getId();
                PromotableOrderAdjustment promotableAdjustment = newAdjustmentsMap.remove(offerId);
                if (promotableAdjustment != null) {
                    if (!adjustment.getValue().equals(promotableAdjustment.getAdjustmentValue())) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Updating value for order adjustment with offer Id " + offerId + " to " +
                                    promotableAdjustment.getAdjustmentValue());
                        }
                        adjustment.setValue(promotableAdjustment.getAdjustmentValue());
                    }
                } else {
                    // No longer using this order adjustment, remove it.
                    orderAdjIterator.remove();
                }
            }
        }

        for (PromotableOrderAdjustment promotableOrderAdjustment : newAdjustmentsMap.values()) {
            // Add the newly introduced adjustments.
            Offer offer = promotableOrderAdjustment.getOffer();
            OrderAdjustment orderAdjustment = offerDao.createOrderAdjustment();
            orderAdjustment.init(order, offer, offer.getName());
            orderAdjustment.setValue(promotableOrderAdjustment.getAdjustmentValue());
            order.getOrderAdjustments().add(orderAdjustment);
        }
    }



    protected void synchronizeOrderItems(PromotableOrder promotableOrder) {
        Order order = promotableOrder.getOrder();
        Map<OrderItem, PromotableOrderItem> promotableItemMap = offerServiceUtilities.buildPromotableItemMap(promotableOrder);

        List<OrderItem> orderItemList = offerServiceUtilities.buildOrderItemList(order);

        for (OrderItem orderItem : orderItemList) {
            PromotableOrderItem promotableItem = promotableItemMap.get(orderItem);
            if (promotableItem == null) {
                continue;
            }
            synchronizeItemPriceDetails(orderItem, promotableItem);
            synchronizeItemQualifiers(orderItem, promotableItem);

        }
    }

    protected void synchronizeItemPriceDetails(OrderItem orderItem, PromotableOrderItem promotableOrderItem) {
        Map<String, PromotableOrderItemPriceDetail> promotableDetailsMap = buildPromotableDetailsMap(promotableOrderItem);
        Map<Long, OrderItemPriceDetail> unmatchedDetailsMap = new HashMap<Long, OrderItemPriceDetail>();

        for (OrderItemPriceDetail orderItemPriceDetail : orderItem.getOrderItemPriceDetails()) {
            String detailKey = buildItemPriceDetailKey(orderItemPriceDetail);
            PromotableOrderItemPriceDetail promotableDetail = promotableDetailsMap.remove(detailKey);
            if (promotableDetail != null) {
                processMatchingDetails(orderItemPriceDetail, promotableDetail);
            } else {
                unmatchedDetailsMap.put(orderItemPriceDetail.getId(), orderItemPriceDetail);
            }
        }

        Iterator<OrderItemPriceDetail> unmatchedDetailsIterator = unmatchedDetailsMap.values().iterator();

        for (PromotableOrderItemPriceDetail priceDetail : promotableDetailsMap.values()) {
            if (unmatchedDetailsIterator.hasNext()) {
                // Reuse an existing priceDetail
                OrderItemPriceDetail existingDetail = unmatchedDetailsIterator.next();

                // Reset use Sale flag to true
                existingDetail.setUseSalePrice(true);

                offerServiceUtilities.updatePriceDetail(existingDetail, priceDetail);
                unmatchedDetailsIterator.remove();
            } else {
                // Create a new priceDetail
                OrderItemPriceDetail newPriceDetail = orderItemDao.createOrderItemPriceDetail();
                newPriceDetail.setOrderItem(orderItem);
                offerServiceUtilities.updatePriceDetail(newPriceDetail, priceDetail);
                orderItem.getOrderItemPriceDetails().add(newPriceDetail);
            }
        }

        // Remove any unmatched details        
        Iterator<OrderItemPriceDetail> pdIterator = orderItem.getOrderItemPriceDetails().iterator();
        offerServiceUtilities.removeUnmatchedPriceDetails(unmatchedDetailsMap, pdIterator);
    }

    protected void synchronizeItemQualifiers(OrderItem orderItem, PromotableOrderItem promotableOrderItem) {
        Map<Long, PromotionQualifier> qualifiersMap = buildPromotableQualifiersMap(promotableOrderItem);
        Map<Long, OrderItemQualifier> unmatchedQualifiersMap = new HashMap<Long, OrderItemQualifier>();

        for (OrderItemQualifier orderItemQualifier : orderItem.getOrderItemQualifiers()) {
            PromotionQualifier promotableQualifier = qualifiersMap.remove(orderItemQualifier.getOffer().getId());
            if (promotableQualifier != null) {
                // Offer was used as a qualifier on previous run.   Update quantity if needed.
                if (orderItemQualifier.getQuantity() != promotableQualifier.getQuantity()) {
                    orderItemQualifier.setQuantity(new Long(promotableQualifier.getQuantity()));
                }
            } else {
                unmatchedQualifiersMap.put(orderItemQualifier.getId(), orderItemQualifier);
            }
        }

        Iterator<OrderItemQualifier> unmatchedQualifiersIterator = unmatchedQualifiersMap.values().iterator();

        for (PromotionQualifier qualifier : qualifiersMap.values()) {
            if (unmatchedQualifiersIterator.hasNext()) {
                // Reuse an existing qualifier
                OrderItemQualifier existingQualifier = unmatchedQualifiersIterator.next();
                existingQualifier.setOffer(qualifier.getPromotion());
                existingQualifier.setQuantity(Long.valueOf(qualifier.getQuantity()));
                unmatchedQualifiersIterator.remove();
            } else {
                // Create a new qualifier
                OrderItemQualifier newQualifier = orderItemDao.createOrderItemQualifier();
                newQualifier.setOrderItem(orderItem);
                newQualifier.setOffer(qualifier.getPromotion());
                newQualifier.setQuantity(Long.valueOf(qualifier.getQuantity()));
                orderItem.getOrderItemQualifiers().add(newQualifier);
            }
        }

        // Remove any unmatched qualifiers        
        Iterator<OrderItemQualifier> qIterator = orderItem.getOrderItemQualifiers().iterator();
        offerServiceUtilities.removeUnmatchedQualifiers(unmatchedQualifiersMap, qIterator);
    }

    protected void processMatchingDetails(OrderItemPriceDetail itemDetail,
            PromotableOrderItemPriceDetail promotableItemDetail) {
        Map<Long, OrderItemPriceDetailAdjustment> itemAdjustmentMap =
                offerServiceUtilities.buildItemDetailAdjustmentMap(itemDetail);

        if (itemDetail.getQuantity() != promotableItemDetail.getQuantity()) {
            itemDetail.setQuantity(promotableItemDetail.getQuantity());
        }
        
        for (PromotableOrderItemPriceDetailAdjustment adjustment : promotableItemDetail.getCandidateItemAdjustments()) {
            OrderItemPriceDetailAdjustment itemAdjustment = itemAdjustmentMap.get(adjustment.getOfferId());
            if (!itemAdjustment.getValue().equals(adjustment.getAdjustmentValue())) {
                itemAdjustment.setValue(adjustment.getAdjustmentValue());
                itemAdjustment.setAppliedToSalePrice(adjustment.isAppliedToSalePrice());
            }
        }
    }

    protected String buildItemPriceDetailKey(OrderItemPriceDetail itemDetail) {
        List<Long> offerIds = new ArrayList<Long>();
        for (OrderItemPriceDetailAdjustment adjustment : itemDetail.getOrderItemPriceDetailAdjustments()) {
            Long offerId = adjustment.getOffer().getId();
            offerIds.add(offerId);
        }
        Collections.sort(offerIds);
        return itemDetail.getOrderItem().toString() + offerIds.toString() + itemDetail.getUseSalePrice();
    }

    protected Map<String, PromotableOrderItemPriceDetail> buildPromotableDetailsMap(PromotableOrderItem item) {
        Map<String, PromotableOrderItemPriceDetail> detailsMap = new HashMap<String, PromotableOrderItemPriceDetail>();
        for (PromotableOrderItemPriceDetail detail : item.getPromotableOrderItemPriceDetails()) {
            detailsMap.put(detail.buildDetailKey(), detail);
        }
        return detailsMap;
    }

    protected Map<Long, PromotionQualifier> buildPromotableQualifiersMap(PromotableOrderItem item) {
        Map<Long, PromotionQualifier> qualifiersMap = new HashMap<Long, PromotionQualifier>();
        for (PromotableOrderItemPriceDetail detail : item.getPromotableOrderItemPriceDetails()) {
            for (PromotionQualifier qualifier : detail.getPromotionQualifiers()) {
                PromotionQualifier existingQualifier = qualifiersMap.get(qualifier.getPromotion().getId());
                if (existingQualifier != null) {
                    existingQualifier.setQuantity(existingQualifier.getQuantity() + qualifier.getQuantity());
                } else {
                    qualifiersMap.put(qualifier.getPromotion().getId(), qualifier);
                }
            }
        }
        return qualifiersMap;
    }

    protected void synchronizeFulfillmentGroups(PromotableOrder promotableOrder) {
        Order order = promotableOrder.getOrder();
        Map<Long, PromotableFulfillmentGroup> fgMap = buildPromotableFulfillmentGroupMap(promotableOrder);

        for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
            synchronizeFulfillmentGroupAdjustments(fg, fgMap.get(fg.getId()));
        }
    }

    protected Map<Long, PromotableFulfillmentGroup> buildPromotableFulfillmentGroupMap(PromotableOrder order) {
        Map<Long, PromotableFulfillmentGroup> fgMap = new HashMap<Long, PromotableFulfillmentGroup>();
        for (PromotableFulfillmentGroup fg : order.getFulfillmentGroups()) {
            fgMap.put(fg.getFulfillmentGroup().getId(), fg);
        }
        return fgMap;
    }

    protected Map<Long, PromotableFulfillmentGroupAdjustment> buildPromFulfillmentAdjMap(PromotableFulfillmentGroup fg) {
        Map<Long, PromotableFulfillmentGroupAdjustment> fgMap = new HashMap<Long, PromotableFulfillmentGroupAdjustment>();
        for (PromotableFulfillmentGroupAdjustment adjustment : fg.getCandidateFulfillmentGroupAdjustments()) {
            fgMap.put(adjustment.getPromotableCandidateFulfillmentGroupOffer().getOffer().getId(), adjustment);
        }
        return fgMap;
    }

    protected void synchronizeFulfillmentGroupAdjustments(FulfillmentGroup fg, PromotableFulfillmentGroup promotableFG) {
        Iterator<FulfillmentGroupAdjustment> adjustmentIterator = fg.getFulfillmentGroupAdjustments().iterator();
        Map<Long, PromotableFulfillmentGroupAdjustment> promotableAdjMap = buildPromFulfillmentAdjMap(promotableFG);

        // First try and update existing adjustment records
        while (adjustmentIterator.hasNext()) {
            FulfillmentGroupAdjustment currentAdj = adjustmentIterator.next();
            PromotableFulfillmentGroupAdjustment newAdj = promotableAdjMap.remove(currentAdj.getOffer().getId());
            if (newAdj != null) {
                if (!currentAdj.getValue().equals(newAdj.getAdjustmentValue())) {
                    // Update the currentAdj.
                    currentAdj.setValue(newAdj.getAdjustmentValue());
                }
            } else {
                // Removing no longer valid adjustment
                adjustmentIterator.remove();
            }
        }

        // Now add missing adjustments
        for (PromotableFulfillmentGroupAdjustment newAdj : promotableAdjMap.values()) {
            FulfillmentGroupAdjustment fa = offerDao.createFulfillmentGroupAdjustment();
            fa.setFulfillmentGroup(fg);
            fa.init(fg, newAdj.getPromotableCandidateFulfillmentGroupOffer().getOffer(), null);
            fa.setValue(newAdj.getAdjustmentValue());
            fg.getFulfillmentGroupAdjustments().add(fa);
        }

    }

    @Override
    public void synchronizeAdjustmentsAndPrices(PromotableOrder promotableOrder) {
        synchronizeOrderAdjustments(promotableOrder);
        synchronizeOrderItems(promotableOrder);
        if (extensionManager != null) {
            extensionManager.getProxy().synchronizeAdjustmentsAndPrices(promotableOrder);
        }
        synchronizeFulfillmentGroups(promotableOrder);
    }

    @Override
    public void setOfferDao(OfferDao offerDao) {
        this.offerDao = offerDao;
    }

    @Override
    public void setOrderItemDao(OrderItemDao orderItemDao) {
        this.orderItemDao = orderItemDao;
    }

    public OfferServiceUtilities getOfferServiceUtilities() {
        return offerServiceUtilities;
    }

    public void setOfferServiceUtilities(OfferServiceUtilities offerServiceUtilities) {
        this.offerServiceUtilities = offerServiceUtilities;
    }
}
