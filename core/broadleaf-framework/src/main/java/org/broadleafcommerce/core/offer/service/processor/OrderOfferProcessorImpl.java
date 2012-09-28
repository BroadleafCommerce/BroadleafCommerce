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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferRule;
import org.broadleafcommerce.core.offer.domain.OrderAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.offer.service.MergeService;
import org.broadleafcommerce.core.offer.service.discount.CandidatePromotionItems;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateOrderOffer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableFulfillmentGroup;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableItemFactory;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrder;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderAdjustment;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItemAdjustment;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItemImpl;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferRuleType;
import org.broadleafcommerce.core.order.dao.FulfillmentGroupItemDao;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.GiftWrapOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemAttribute;
import org.broadleafcommerce.core.order.domain.OrderMultishipOption;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.OrderMultishipOptionService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupRequest;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.order.service.manipulation.BundleOrderItemSplitContainer;
import org.broadleafcommerce.core.order.service.manipulation.OrderItemSplitContainer;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author jfischer
 */
@Service("blOrderOfferProcessor")
public class OrderOfferProcessorImpl extends AbstractBaseProcessor implements OrderOfferProcessor {

    private static final Log LOG = LogFactory.getLog(OrderOfferProcessorImpl.class);

    @Resource(name = "blOfferDao")
    protected OfferDao offerDao;

    @Resource(name = "blPromotableItemFactory")
    protected PromotableItemFactory promotableItemFactory;

    @Resource(name = "blMergeService")
    protected MergeService mergeService;

    @Override
    public void filterOrderLevelOffer(PromotableOrder order, List<PromotableCandidateOrderOffer> qualifiedOrderOffers, Offer offer) {
        if (offer.getDiscountType().getType().equals(OfferDiscountType.FIX_PRICE.getType())) {
            LOG.warn("Offers of type ORDER may not have a discount type of FIX_PRICE. Ignoring order offer (name=" + offer.getName() + ")");
            return;
        }
        boolean orderLevelQualification = false;
        //Order Qualification
        orderQualification:
        {
            if (couldOfferApplyToOrder(offer, order)) {
                orderLevelQualification = true;
                break orderQualification;
            }
            for (PromotableOrderItem discreteOrderItem : order.getDiscountableDiscreteOrderItems(offer.getApplyDiscountToSalePrice())) {
                if (couldOfferApplyToOrder(offer, order, discreteOrderItem)) {
                    orderLevelQualification = true;
                    break orderQualification;
                }
            }
            for (PromotableFulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
                if (couldOfferApplyToOrder(offer, order, fulfillmentGroup)) {
                    orderLevelQualification = true;
                    break orderQualification;
                }
            }
        }
        //Item Qualification - new for 1.5!
        if (orderLevelQualification) {
            CandidatePromotionItems candidates = couldOfferApplyToOrderItems(offer, order.getDiscountableDiscreteOrderItems(offer.getApplyDiscountToSalePrice()));
            if (candidates.isMatchedQualifier()) {
                PromotableCandidateOrderOffer candidateOffer = createCandidateOrderOffer(order, qualifiedOrderOffers, offer);
                candidateOffer.getCandidateQualifiersMap().putAll(candidates.getCandidateQualifiersMap());
            }
        }
    }

    @Override
    public boolean couldOfferApplyToOrder(Offer offer, PromotableOrder order) {
        return couldOfferApplyToOrder(offer, order, null, null);
    }

    /**
     * Private method which executes the appliesToOrderRules in the Offer to determine if this offer
     * can be applied to the Order, OrderItem, or FulfillmentGroup.
     *
     * @param offer
     * @param order
     * @param discreteOrderItem
     * @return true if offer can be applied, otherwise false
     */
    protected boolean couldOfferApplyToOrder(Offer offer, PromotableOrder order, PromotableOrderItem discreteOrderItem) {
        return couldOfferApplyToOrder(offer, order, discreteOrderItem, null);
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
    protected boolean couldOfferApplyToOrder(Offer offer, PromotableOrder order, PromotableFulfillmentGroup fulfillmentGroup) {
        return couldOfferApplyToOrder(offer, order, null, fulfillmentGroup);
    }

    /**
     * Private method which executes the appliesToOrderRules in the Offer to determine if this offer
     * can be applied to the Order, OrderItem, or FulfillmentGroup.
     *
     * @param offer
     * @param order
     * @param discreteOrderItem
     * @param fulfillmentGroup
     * @return true if offer can be applied, otherwise false
     */
    protected boolean couldOfferApplyToOrder(Offer offer, PromotableOrder order, PromotableOrderItem discreteOrderItem, PromotableFulfillmentGroup fulfillmentGroup) {
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
            vars.put("order", order.getDelegate());
            vars.put("offer", offer);
            if (fulfillmentGroup != null) {
                vars.put("fulfillmentGroup", fulfillmentGroup.getDelegate());
            }
            if (discreteOrderItem != null) {
                vars.put("discreteOrderItem", discreteOrderItem.getDelegate());
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

    protected PromotableCandidateOrderOffer createCandidateOrderOffer(PromotableOrder order, List<PromotableCandidateOrderOffer> qualifiedOrderOffers, Offer offer) {
        CandidateOrderOffer candidateOffer = offerDao.createCandidateOrderOffer();
        candidateOffer.setOrder(order.getDelegate());
        candidateOffer.setOffer(offer);
        // Why do we add offers here when we set the sorted list later
        //order.addCandidateOrderOffer(candidateOffer);
        PromotableCandidateOrderOffer promotableCandidateOrderOffer = promotableItemFactory.createPromotableCandidateOrderOffer(candidateOffer, order);
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
    public boolean applyAllOrderOffers(List<PromotableCandidateOrderOffer> orderOffers, PromotableOrder order) {
        // If order offer is not combinable, first verify order adjustment is zero, if zero, compare item discount total vs this offer's total
        boolean orderOffersApplied = false;
        Iterator<PromotableCandidateOrderOffer> orderOfferIterator = orderOffers.iterator();
        while (orderOfferIterator.hasNext()) {
            PromotableCandidateOrderOffer orderOffer = orderOfferIterator.next();
            if (orderOffer.getOffer().getTreatAsNewFormat() == null || !orderOffer.getOffer().getTreatAsNewFormat()) {
                if ((orderOffer.getOffer().isStackable()) || !order.isHasOrderAdjustments()) {
                    boolean alreadyContainsNotCombinableOfferAtAnyLevel = order.isNotCombinableOfferAppliedAtAnyLevel();
                    applyOrderOffer(order, orderOffer);
                    orderOffersApplied = true;
                    if (!orderOffer.getOffer().isCombinableWithOtherOffers() || alreadyContainsNotCombinableOfferAtAnyLevel) {
                        orderOffersApplied = compareAndAdjustOrderAndItemOffers(order, orderOffersApplied);
                        if (orderOffersApplied) {
                            break;
                        } else {
                            orderOfferIterator.remove();
                        }
                    }
                }
            } else {
                if (!order.containsNotStackableOrderOffer() || !order.isHasOrderAdjustments()) {
                    boolean alreadyContainsTotalitarianOffer = order.isTotalitarianOfferApplied();

                    // TODO:  Add filter for item-subtotal
                    applyOrderOffer(order, orderOffer);
                    orderOffersApplied = true;
                    if (
                            (orderOffer.getOffer().isTotalitarianOffer() != null && orderOffer.getOffer().isTotalitarianOffer()) ||
                                    alreadyContainsTotalitarianOffer
                            ) {
                        orderOffersApplied = compareAndAdjustOrderAndItemOffers(order, orderOffersApplied);
                        if (orderOffersApplied) {
                            break;
                        } else {
                            orderOfferIterator.remove();
                        }
                    } else if (!orderOffer.getOffer().isCombinableWithOtherOffers()) {
                        break;
                    }
                }
            }
        }
        return orderOffersApplied;
    }

    protected boolean compareAndAdjustOrderAndItemOffers(PromotableOrder order, boolean orderOffersApplied) {
        if (order.getAdjustmentPrice().greaterThanOrEqual(order.calculateOrderItemsCurrentPrice())) {
            // item offer is better; remove not combinable order offer and process other order offers
            order.removeAllOrderAdjustments();
            orderOffersApplied = false;
        } else {
            // totalitarian order offer is better; remove all item offers
            order.removeAllItemAdjustments();
            mergeService.gatherCart(order);
            mergeService.initializeSplitItems(order);
        }
        return orderOffersApplied;
    }

    /**
     * Private method used by applyAllOrderOffers to create an OrderAdjustment from a CandidateOrderOffer
     * and associates the OrderAdjustment to the Order.
     *
     * @param orderOffer a CandidateOrderOffer to apply to an Order
     */
    protected void applyOrderOffer(PromotableOrder order, PromotableCandidateOrderOffer orderOffer) {
        OrderAdjustment orderAdjustment = offerDao.createOrderAdjustment();
        orderAdjustment.init(order.getDelegate(), orderOffer.getOffer(), orderOffer.getOffer().getName());
        PromotableOrderAdjustment promotableOrderAdjustment = promotableItemFactory.createPromotableOrderAdjustment(orderAdjustment, order);
        //add to adjustment
        order.addOrderAdjustments(promotableOrderAdjustment);
    }

    @Override
    public void compileOrderTotal(PromotableOrder order) {
        order.assignOrderItemsFinalPrice();
        order.setSubTotal(order.calculateOrderItemsFinalPrice(true));
    }

    @Override
    public OfferDao getOfferDao() {
        return offerDao;
    }

    @Override
    public void setOfferDao(OfferDao offerDao) {
        this.offerDao = offerDao;
    }

    @Override
    public PromotableItemFactory getPromotableItemFactory() {
        return promotableItemFactory;
    }

    @Override
    public void setPromotableItemFactory(PromotableItemFactory promotableItemFactory) {
        this.promotableItemFactory = promotableItemFactory;
    }
}
