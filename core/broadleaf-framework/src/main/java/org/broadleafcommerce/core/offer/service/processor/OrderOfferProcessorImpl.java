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

package org.broadleafcommerce.core.offer.service.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferRule;
import org.broadleafcommerce.core.offer.domain.OrderAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.offer.service.discount.CandidatePromotionItems;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateOrderOffer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableFulfillmentGroup;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableItemFactory;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrder;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderAdjustment;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItemAdjustment;
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
import org.broadleafcommerce.core.order.service.CartService;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.manipulation.BundleOrderItemSplitContainer;
import org.broadleafcommerce.core.order.service.manipulation.OrderItemSplitContainer;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.compass.core.util.CollectionUtils;
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

    @Resource(name = "blCartService")
    protected CartService cartService;

    @Resource(name = "blOrderItemService")
    protected OrderItemService orderItemService;

    @Resource(name = "blFulfillmentGroupItemDao")
    protected FulfillmentGroupItemDao fulfillmentGroupItemDao;

    @Resource(name = "blPromotableItemFactory")
    protected PromotableItemFactory promotableItemFactory;

    /* (non-Javadoc)
      * @see org.broadleafcommerce.core.offer.service.processor.OrderOfferProcessor#filterOrderLevelOffer(org.broadleafcommerce.core.order.domain.Order, java.util.List, java.util.List, org.broadleafcommerce.core.offer.domain.Offer)
      */
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

    /**
     * Private method which executes the appliesToOrderRules in the Offer to determine if this offer
     * can be applied to the Order, OrderItem, or FulfillmentGroup.
     *
     * @param offer
     * @param order
     * @return true if offer can be applied, otherwise false
     */
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

    /**
     * Private method that takes a list of sorted CandidateOrderOffers and determines if each offer can be
     * applied based on the restrictions (stackable and/or combinable) on that offer.  OrderAdjustments
     * are create on the Order for each applied CandidateOrderOffer.  An offer with stackable equals false
     * cannot be applied to an Order that already contains an OrderAdjustment.  An offer with combinable
     * equals false cannot be applied to the Order if the Order already contains an OrderAdjustment.
     *
     * @param orderOffers a sorted list of CandidateOrderOffer
     * @param order       the Order to apply the CandidateOrderOffers
     * @return true if order offer applied; otherwise false
     */
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

    public void initializeBundleSplitItems(PromotableOrder order) {
        List<OrderItem> basicOrderItems = order.getDelegate().getOrderItems();
        for (OrderItem basicOrderItem : basicOrderItems) {
            if (basicOrderItem instanceof BundleOrderItem) {
                BundleOrderItem bundleOrderItem = (BundleOrderItem) basicOrderItem;
                List<BundleOrderItem> searchHit = order.searchBundleSplitItems(bundleOrderItem);
                if (searchHit == null) {
                    searchHit = new ArrayList<BundleOrderItem>();
                    BundleOrderItemSplitContainer container = new BundleOrderItemSplitContainer();
                    container.setKey(bundleOrderItem);
                    container.setSplitItems(searchHit);
                    order.getBundleSplitItems().add(container);
                }
                long count = 0L;
                if (bundleOrderItem.getQuantity() > 1) {
                    for (int j = 1; j <= bundleOrderItem.getQuantity(); j++) {
                        count++;
                        BundleOrderItem temp = (BundleOrderItem) bundleOrderItem.clone();
                        temp.setQuantity(1);
                        temp.setId(count);
                        searchHit.add(temp);
                    }
                } else {
                    count++;
                    BundleOrderItem temp = (BundleOrderItem) bundleOrderItem.clone();
                    temp.setId(count);
                    searchHit.add(temp);
                }
            }
        }
    }

    public void initializeSplitItems(PromotableOrder order) {
        List<PromotableOrderItem> items = order.getDiscountableDiscreteOrderItems();
        for (PromotableOrderItem item : items) {
            List<PromotableOrderItem> temp = new ArrayList<PromotableOrderItem>();
            temp.add(item);
            OrderItemSplitContainer container = new OrderItemSplitContainer();
            container.setKey(item.getDelegate());
            container.setSplitItems(temp);
            order.getSplitItems().add(container);
        }
    }

    protected boolean compareAndAdjustOrderAndItemOffers(PromotableOrder order, boolean orderOffersApplied) {
        if (order.getAdjustmentPrice().greaterThanOrEqual(order.calculateOrderItemsCurrentPrice())) {
            // item offer is better; remove not combinable order offer and process other order offers
            order.removeAllOrderAdjustments();
            orderOffersApplied = false;
        } else {
            // totalitarian order offer is better; remove all item offers
            order.removeAllItemAdjustments();
            gatherCart(order);
            initializeSplitItems(order);
        }
        return orderOffersApplied;
    }

    public void gatherCart(PromotableOrder promotableOrder) {
        Order order = promotableOrder.getDelegate();
        try {
            if (!CollectionUtils.isEmpty(order.getFulfillmentGroups())) {
                //stage 1 - gather possible split items - including those inside a bundle order item
                gatherFulfillmentGroupLinkedDiscreteOrderItems(order);
                //stage 2 - gather the bundles themselves
                gatherFulfillmentGroupLinkedBundleOrderItems(order);
            } else {
                //stage 1 - gather possible split items - including those inside a bundle order item
                gatherOrderLinkedDiscreteOrderItems(order);
                //stage 2 - gather the bundles themselves
                gatherOrderLinkedBundleOrderItems(order);
            }

        } catch (PricingException e) {
            throw new RuntimeException("Could not gather the cart", e);
        }
        promotableOrder.resetDiscreteOrderItems();
    }

    protected void gatherOrderLinkedBundleOrderItems(Order order) throws PricingException {
        Map<String, BundleOrderItem> gatherBundle = new HashMap<String, BundleOrderItem>();
        List<BundleOrderItem> bundlesToRemove = new ArrayList<BundleOrderItem>();
        for (OrderItem orderItem : order.getOrderItems()) {
            if (orderItem instanceof BundleOrderItem) {
                String identifier = buildIdentifier(orderItem, null);
                BundleOrderItem retrieved = gatherBundle.get(identifier);
                if (retrieved == null) {
                    gatherBundle.put(identifier, (BundleOrderItem) orderItem);
                    continue;
                }
                retrieved.setQuantity(retrieved.getQuantity() + orderItem.getQuantity());
                bundlesToRemove.add((BundleOrderItem) orderItem);
            }
        }
        for (BundleOrderItem bundleOrderItem : gatherBundle.values()) {
            orderItemService.saveOrderItem(bundleOrderItem);
        }
        for (BundleOrderItem orderItem : bundlesToRemove) {
            cartService.removeItemFromOrder(order, orderItem, false);
        }
    }

    protected void gatherOrderLinkedDiscreteOrderItems(Order order) throws PricingException {
        List<DiscreteOrderItem> itemsToRemove = new ArrayList<DiscreteOrderItem>();
        Map<String, OrderItem> gatheredItem = new HashMap<String, OrderItem>();
        for (OrderItem orderItem : order.getOrderItems()) {
            if (orderItem instanceof BundleOrderItem) {
                for (DiscreteOrderItem discreteOrderItem : ((BundleOrderItem) orderItem).getDiscreteOrderItems()) {
                    gatherOrderLinkedDiscreteOrderItem(itemsToRemove, gatheredItem, discreteOrderItem, String.valueOf(orderItem.getId()));
                }
            } else {
                gatherOrderLinkedDiscreteOrderItem(itemsToRemove, gatheredItem, (DiscreteOrderItem) orderItem, null);
            }

        }
        for (OrderItem orderItem : gatheredItem.values()) {
            orderItemService.saveOrderItem(orderItem);
        }
        for (DiscreteOrderItem orderItem : itemsToRemove) {
            if (orderItem.getBundleOrderItem() == null) {
                cartService.removeItemFromOrder(order, orderItem, false);
            } else {
                cartService.removeItemFromBundle(order, orderItem.getBundleOrderItem(), orderItem, false);
            }
        }
    }

    protected void gatherFulfillmentGroupLinkedBundleOrderItems(Order order) throws PricingException {
        List<BundleOrderItem> bundlesToRemove = new ArrayList<BundleOrderItem>();
        Map<Long, Map<String, Object[]>> gatherBundle = new HashMap<Long, Map<String, Object[]>>();
        for (FulfillmentGroup group : order.getFulfillmentGroups()) {
            Map<String, Object[]> gatheredItem = gatherBundle.get(group.getId());
            if (gatheredItem == null) {
                gatheredItem = new HashMap<String, Object[]>();
                gatherBundle.put(group.getId(), gatheredItem);
            }
            for (FulfillmentGroupItem fgItem : group.getFulfillmentGroupItems()) {
                OrderItem orderItem = fgItem.getOrderItem();
                if (orderItem instanceof BundleOrderItem) {
                    String identifier = buildIdentifier(orderItem, null);
                    Object[] gatheredOrderItem = gatheredItem.get(identifier);
                    if (gatheredOrderItem == null) {
                        gatheredItem.put(identifier, new Object[]{orderItem, fgItem});
                        continue;
                    }
                    ((OrderItem) gatheredOrderItem[0]).setQuantity(((OrderItem) gatheredOrderItem[0]).getQuantity() + orderItem.getQuantity());
                    ((FulfillmentGroupItem) gatheredOrderItem[1]).setQuantity(((FulfillmentGroupItem) gatheredOrderItem[1]).getQuantity() + fgItem.getQuantity());
                    bundlesToRemove.add((BundleOrderItem) orderItem);
                }
            }
        }
        for (Map<String, Object[]> values : gatherBundle.values()) {
            for (Object[] item : values.values()) {
                orderItemService.saveOrderItem((OrderItem) item[0]);
                fulfillmentGroupItemDao.save((FulfillmentGroupItem) item[1]);
            }
        }
        for (BundleOrderItem orderItem : bundlesToRemove) {
            cartService.removeItemFromOrder(order, orderItem, false);
        }
    }

    protected void gatherFulfillmentGroupLinkedDiscreteOrderItems(Order order) throws PricingException {
        List<DiscreteOrderItem> itemsToRemove = new ArrayList<DiscreteOrderItem>();
        Map<Long, Map<String, Object[]>> gatherMap = new HashMap<Long, Map<String, Object[]>>();
        for (FulfillmentGroup group : order.getFulfillmentGroups()) {
            Map<String, Object[]> gatheredItem = gatherMap.get(group.getId());
            if (gatheredItem == null) {
                gatheredItem = new HashMap<String, Object[]>();
                gatherMap.put(group.getId(), gatheredItem);
            }
            for (FulfillmentGroupItem fgItem : group.getFulfillmentGroupItems()) {
                OrderItem orderItem = fgItem.getOrderItem();
                if (orderItem instanceof BundleOrderItem) {
                    for (DiscreteOrderItem discreteOrderItem : ((BundleOrderItem) orderItem).getDiscreteOrderItems()) {
                        gatherFulfillmentGroupLinkedDiscreteOrderItem(itemsToRemove, gatheredItem, fgItem, discreteOrderItem, String.valueOf(orderItem.getId()));
                    }
                } else {
                    gatherFulfillmentGroupLinkedDiscreteOrderItem(itemsToRemove, gatheredItem, fgItem, (DiscreteOrderItem) orderItem, null);
                }
            }
        }
        for (Map<String, Object[]> values : gatherMap.values()) {
            for (Object[] item : values.values()) {
                orderItemService.saveOrderItem((OrderItem) item[0]);
                fulfillmentGroupItemDao.save((FulfillmentGroupItem) item[1]);
            }
        }
        for (DiscreteOrderItem orderItem : itemsToRemove) {
            if (orderItem.getBundleOrderItem() == null) {
                cartService.removeItemFromOrder(order, orderItem, false);
            } else {
                cartService.removeItemFromBundle(order, orderItem.getBundleOrderItem(), orderItem, false);
            }
        }
    }

    protected void gatherOrderLinkedDiscreteOrderItem(List<DiscreteOrderItem> itemsToRemove, Map<String, OrderItem> gatheredItem, DiscreteOrderItem orderItem, String extraIdentifier) {
        if (CollectionUtils.isEmpty(orderItem.getOrderItemAdjustments())) {
            String identifier = buildIdentifier(orderItem, extraIdentifier);

            OrderItem gatheredOrderItem = gatheredItem.get(identifier);
            if (gatheredOrderItem == null) {
                gatheredItem.put(identifier, orderItem);
                return;
            }
            gatheredOrderItem.setQuantity(gatheredOrderItem.getQuantity() + orderItem.getQuantity());
            itemsToRemove.add(orderItem);
        }
    }

    /**
     * Appends the item attributes so that items with different attibutes are not merged together
     * as part of the merge/split logic.
     *
     * @param identifier
     * @param orderItem
     */
    protected void addOptionAttributesToIdentifier(StringBuffer identifier, OrderItem orderItem) {
        if (orderItem.getOrderItemAttributes() != null && orderItem.getOrderItemAttributes().size() > 0) {
            List<String> valueList = new ArrayList<String>();
            for (OrderItemAttribute itemAttribute : orderItem.getOrderItemAttributes().values()) {
                valueList.add(itemAttribute.getName() + "_" + itemAttribute.getValue());
            }
            Collections.sort(valueList);
            identifier.append('_');
            for (String value : valueList) {
                identifier.append(value);
            }
        }
    }

    public String buildIdentifier(OrderItem orderItem, String extraIdentifier) {
        StringBuffer identifier = new StringBuffer();
        if (orderItem.getSplitParentItemId() != null || cartService.getAutomaticallyMergeLikeItems()) {
            if (! cartService.getAutomaticallyMergeLikeItems()) {
                identifier.append(orderItem.getSplitParentItemId());
            } else {
                if (orderItem instanceof BundleOrderItem) {
                    BundleOrderItem bundleOrderItem = (BundleOrderItem) orderItem;
                    if (bundleOrderItem.getSku() != null) {
                        identifier.append(bundleOrderItem.getSku().getId());
                    } else {
                        if (orderItem.getSplitParentItemId() != null) {
                            identifier.append(orderItem.getSplitParentItemId());
                        } else {
                            identifier.append(orderItem.getId());
                        }
                    }
                } else if (orderItem instanceof DiscreteOrderItem) {
                    DiscreteOrderItem discreteOrderItem = (DiscreteOrderItem) orderItem;
                    identifier.append(discreteOrderItem.getSku().getId());
                } else {
                    if (orderItem.getSplitParentItemId() != null) {
                        identifier.append(orderItem.getSplitParentItemId());
                    } else {
                        identifier.append(orderItem.getId());
                    }
                }
            }

            identifier.append('_').append(orderItem.getPrice().stringValue());
            if (extraIdentifier != null) {
                identifier.append('_').append(extraIdentifier);
            }

            addOptionAttributesToIdentifier(identifier, orderItem);
        } else {
            identifier.append(orderItem.getId());
        }
        return identifier.toString();
    }

    protected void gatherFulfillmentGroupLinkedDiscreteOrderItem(List<DiscreteOrderItem> itemsToRemove, Map<String, Object[]> gatheredItem, FulfillmentGroupItem fgItem, DiscreteOrderItem orderItem, String extraIdentifier) {
        if (CollectionUtils.isEmpty(orderItem.getOrderItemAdjustments())) {
            String identifier = buildIdentifier(orderItem, extraIdentifier);


            Object[] gatheredOrderItem = gatheredItem.get(identifier);
            if (gatheredOrderItem == null) {
                gatheredItem.put(identifier, new Object[]{orderItem, fgItem});
                return;
            }
            ((OrderItem) gatheredOrderItem[0]).setQuantity(((OrderItem) gatheredOrderItem[0]).getQuantity() + orderItem.getQuantity());
            ((FulfillmentGroupItem) gatheredOrderItem[1]).setQuantity(((FulfillmentGroupItem) gatheredOrderItem[1]).getQuantity() + fgItem.getQuantity());
            itemsToRemove.add(orderItem);
        }
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

    protected void mergeSplitItems(final PromotableOrder order) {
        try {
            mergeSplitDiscreteOrderItems(order);

            mergeSplitBundleOrderItems(order);

            order.resetDiscreteOrderItems();

            for (PromotableOrderItem myItem : order.getDiscountableDiscreteOrderItems()) {
                //reset adjustment retail and sale values, since their transient values are erased after the above persistence events
                if (myItem.isHasOrderItemAdjustments()) {
                    for (OrderItemAdjustment adjustment : myItem.getDelegate().getOrderItemAdjustments()) {
                        PromotableOrderItemAdjustment promotableOrderItemAdjustment = promotableItemFactory.createPromotableOrderItemAdjustment(adjustment, myItem);
                        myItem.resetAdjustmentPrice();
                        promotableOrderItemAdjustment.computeAdjustmentValues();
                        myItem.computeAdjustmentPrice();
                    }
                }
            }


        } catch (PricingException e) {
            throw new RuntimeException("Could not propagate the items split by the promotion engine into the order", e);
        }
    }

    /**
     * Returns null if the item is not part of a bundle.
     * @return
     */
    private Long getBundleId(OrderItem item) {
        if (item instanceof DiscreteOrderItem) {
            DiscreteOrderItem discreteItem =  (DiscreteOrderItem) item;
            if (discreteItem.getBundleOrderItem() != null) {
                return discreteItem.getBundleOrderItem().getId();
            }
        }
        return null;
    }

    protected void mergeSplitDiscreteOrderItems(PromotableOrder order) throws PricingException {
        //If adjustments are removed - merge split items back together before adding to the cart
        List<PromotableOrderItem> itemsToRemove = new ArrayList<PromotableOrderItem>();
        List<DiscreteOrderItem> delegatesToRemove = new ArrayList<DiscreteOrderItem>();
        Iterator<PromotableOrderItem> finalItems = order.getDiscountableDiscreteOrderItems().iterator();
        Map<String, PromotableOrderItem> allItems = new HashMap<String, PromotableOrderItem>();
        while (finalItems.hasNext()) {
            PromotableOrderItem nextItem = finalItems.next();
            List<PromotableOrderItem> mySplits = order.searchSplitItems(nextItem);
            if (!CollectionUtils.isEmpty(mySplits)) {
                PromotableOrderItem cloneItem = nextItem.clone();
                cloneItem.clearAllDiscount();
                cloneItem.clearAllQualifiers();
                cloneItem.removeAllAdjustments();
                cloneItem.setQuantity(0);
                Iterator<PromotableOrderItem> splitItemIterator = mySplits.iterator();
                while (splitItemIterator.hasNext()) {
                    PromotableOrderItem splitItem = splitItemIterator.next();
                    if (!splitItem.isHasOrderItemAdjustments()) {
                        cloneItem.setQuantity(cloneItem.getQuantity() + splitItem.getQuantity());
                        splitItemIterator.remove();
                    }
                }
                if (cloneItem.getQuantity() > 0) {
                    String identifier = String.valueOf(cloneItem.getSku().getId());
                    Long bundleItemId = getBundleId(cloneItem.getDelegate());
                    if (bundleItemId != null) {
                        identifier += bundleItemId;
                    }
                    if (allItems.containsKey(identifier)) {
                        PromotableOrderItem savedItem = allItems.get(identifier);
                        savedItem.setQuantity(savedItem.getQuantity() + cloneItem.getQuantity());
                    } else {
                        allItems.put(identifier, cloneItem);
                        mySplits.add(cloneItem);
                    }
                }

                if (nextItem.getDelegate().getBundleOrderItem() == null) {
                    if (mySplits.contains(nextItem)) {
                        mySplits.remove(nextItem);
                    } else {
                        itemsToRemove.add(nextItem);
                        delegatesToRemove.add(nextItem.getDelegate());
                    }
                } else {
                    itemsToRemove.add(nextItem);
                    delegatesToRemove.add(nextItem.getDelegate());
                }
            }
        }

        for (OrderItemSplitContainer key : order.getSplitItems()) {
            List<PromotableOrderItem> mySplits = key.getSplitItems();
            if (!CollectionUtils.isEmpty(mySplits)) {
                PromotableFulfillmentGroup targetGroup = getTargetFulfillmentGroup(order, key.getKey());
                for (PromotableOrderItem myItem : mySplits) {
                    myItem.assignFinalPrice();
                    DiscreteOrderItem delegateItem = myItem.getDelegate();
                    Long delegateItemBundleItemId = getBundleId(delegateItem);
                    if (delegateItemBundleItemId == null) {
                        delegateItem = (DiscreteOrderItem) cartService.addOrderItemToOrder(order.getDelegate(), delegateItem, false);
                        if (targetGroup != null) {
                            cartService.addItemToFulfillmentGroup(delegateItem, targetGroup.getDelegate(), false);
                        }
                    } else {
                        delegateItem = (DiscreteOrderItem) cartService.addOrderItemToBundle(order.getDelegate(), delegateItem.getBundleOrderItem(), delegateItem, false);
                    }
                    myItem.setDelegate(delegateItem);
                }
            }
        }

        //compile a list of any gift wrap items that we're keeping
        List<GiftWrapOrderItem> giftWrapItems = new ArrayList<GiftWrapOrderItem>();
        for (DiscreteOrderItem discreteOrderItem : order.getDelegate().getDiscreteOrderItems()) {
            if (discreteOrderItem instanceof GiftWrapOrderItem) {
                if (!delegatesToRemove.contains(discreteOrderItem)) {
                    giftWrapItems.add((GiftWrapOrderItem) discreteOrderItem);
                } else {
                    Iterator<OrderItem> wrappedItems = ((GiftWrapOrderItem) discreteOrderItem).getWrappedItems().iterator();
                    while (wrappedItems.hasNext()) {
                        OrderItem wrappedItem = wrappedItems.next();
                        wrappedItem.setGiftWrapOrderItem(null);
                        wrappedItems.remove();
                    }
                }
            }
        }

        for (PromotableOrderItem itemToRemove : itemsToRemove) {
            DiscreteOrderItem delegateItem = itemToRemove.getDelegate();

            mergeSplitGiftWrapOrderItems(order, giftWrapItems, itemToRemove, delegateItem);

            if (delegateItem.getBundleOrderItem() == null) {
                cartService.removeItemFromOrder(order.getDelegate(), itemToRemove.getDelegate(), false);
            } else {
                delegateItem.getBundleOrderItem().getDiscreteOrderItems().remove(itemToRemove.getDelegate());
            }
        }
    }

    protected void mergeSplitGiftWrapOrderItems(PromotableOrder order, List<GiftWrapOrderItem> giftWrapItems, PromotableOrderItem itemToRemove, DiscreteOrderItem delegateItem) {
        for (GiftWrapOrderItem giftWrapOrderItem : giftWrapItems) {
            List<OrderItem> newItems = new ArrayList<OrderItem>();
            Iterator<OrderItem> wrappedItems = giftWrapOrderItem.getWrappedItems().iterator();
            boolean foundItems = false;
            while (wrappedItems.hasNext()) {
                OrderItem wrappedItem = wrappedItems.next();
                if (wrappedItem.equals(delegateItem)) {
                    foundItems = true;
                    //add in the new wrapped items (split or not)
                    List<PromotableOrderItem> searchHits = order.searchSplitItems(itemToRemove);
                    if (!CollectionUtils.isEmpty(searchHits)) {
                        for (PromotableOrderItem searchHit : searchHits) {
                            newItems.add(searchHit.getDelegate());
                            searchHit.getDelegate().setGiftWrapOrderItem(giftWrapOrderItem);
                        }
                    }
                    //eradicate the old wrapped items
                    delegateItem.setGiftWrapOrderItem(null);
                    wrappedItems.remove();
                }
            }
            if (foundItems) {
                giftWrapOrderItem.getWrappedItems().addAll(newItems);
                orderItemService.saveOrderItem(giftWrapOrderItem);
            }
        }
    }

    protected void mergeSplitBundleOrderItems(PromotableOrder order) throws PricingException {
        List<BundleOrderItemSplitContainer> bundleContainers = order.getBundleSplitItems();
        for (BundleOrderItemSplitContainer bundleContainer : bundleContainers) {
            PromotableFulfillmentGroup targetGroup = getTargetFulfillmentGroup(order, bundleContainer.getKey());
            List<BundleOrderItem> bundleOrderItems = bundleContainer.getSplitItems();
            Map<String, BundleOrderItem> gatheredBundleItems = new HashMap<String, BundleOrderItem>();
            for (BundleOrderItem bundleOrderItem : bundleOrderItems) {
                bundleOrderItem.assignFinalPrice();
                String hash = bundleOrderItem.getPrice().stringValue();
                if (!gatheredBundleItems.containsKey(hash)) {
                    gatheredBundleItems.put(hash, bundleOrderItem);
                } else {
                    BundleOrderItem temp = gatheredBundleItems.get(hash);
                    temp.setQuantity(temp.getQuantity() + 1);
                }
            }
            cartService.removeItemFromOrder(order.getDelegate(), bundleContainer.getKey(), false);
            for (BundleOrderItem vals : gatheredBundleItems.values()) {
                vals.setId(null);
                BundleOrderItem temp = (BundleOrderItem) cartService.addOrderItemToOrder(order.getDelegate(), vals, false);
                if (targetGroup != null) {
                    cartService.addItemToFulfillmentGroup(temp, targetGroup.getDelegate(), false);
                }
            }
        }
    }

    protected PromotableFulfillmentGroup getTargetFulfillmentGroup(PromotableOrder order, OrderItem key) {
        //find fulfillment group for original order item
        PromotableFulfillmentGroup targetGroup = null;
        checkGroups:
        {
            for (PromotableFulfillmentGroup fg : order.getFulfillmentGroups()) {
                for (FulfillmentGroupItem fgItem : fg.getDelegate().getFulfillmentGroupItems()) {
                    if (fgItem.getOrderItem().equals(key)) {
                        targetGroup = fg;
                        break checkGroups;
                    }
                }
            }
        }
        return targetGroup;
    }

    public void compileOrderTotal(PromotableOrder order) {
        order.assignOrderItemsFinalPrice();
        order.setSubTotal(order.calculateOrderItemsFinalPrice(true));
    }

    public OfferDao getOfferDao() {
        return offerDao;
    }

    public void setOfferDao(OfferDao offerDao) {
        this.offerDao = offerDao;
    }

    public CartService getCartService() {
        return cartService;
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    public OrderItemService getOrderItemService() {
        return orderItemService;
    }

    public void setOrderItemService(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    public FulfillmentGroupItemDao getFulfillmentGroupItemDao() {
        return fulfillmentGroupItemDao;
    }

    public void setFulfillmentGroupItemDao(
            FulfillmentGroupItemDao fulfillmentGroupItemDao) {
        this.fulfillmentGroupItemDao = fulfillmentGroupItemDao;
    }

    public PromotableItemFactory getPromotableItemFactory() {
        return promotableItemFactory;
    }

    public void setPromotableItemFactory(PromotableItemFactory promotableItemFactory) {
        this.promotableItemFactory = promotableItemFactory;
    }

}
