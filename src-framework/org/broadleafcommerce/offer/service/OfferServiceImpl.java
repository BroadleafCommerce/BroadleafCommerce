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
package org.broadleafcommerce.offer.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.map.LRUMap;
import org.broadleafcommerce.offer.dao.CustomerOfferDao;
import org.broadleafcommerce.offer.dao.OfferCodeDao;
import org.broadleafcommerce.offer.dao.OfferDao;
import org.broadleafcommerce.offer.domain.CandidateFulfillmentGroupOffer;
import org.broadleafcommerce.offer.domain.CandidateFulfillmentGroupOfferImpl;
import org.broadleafcommerce.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.offer.domain.CandidateItemOfferImpl;
import org.broadleafcommerce.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.offer.domain.CandidateOrderOfferImpl;
import org.broadleafcommerce.offer.domain.CustomerOffer;
import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.domain.OfferCode;
import org.broadleafcommerce.offer.domain.OrderAdjustment;
import org.broadleafcommerce.offer.domain.OrderAdjustmentImpl;
import org.broadleafcommerce.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.offer.domain.OrderItemAdjustmentImpl;
import org.broadleafcommerce.offer.service.type.OfferType;
import org.broadleafcommerce.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.service.type.FulfillmentGroupType;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.util.money.Money;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.springframework.stereotype.Service;

/**
 * The Class OfferServiceImpl.
 */
@Service("blOfferService")
public class OfferServiceImpl implements OfferService {

    private static final LRUMap expressionCache = new LRUMap(100);
    private static final StringBuffer functions = new StringBuffer();

    // should be called outside of Offer service after Offer service is executed
    @Resource
    protected CustomerOfferDao customerOfferDao;

    @Resource
    protected OfferCodeDao offerCodeDao;

    @Resource
    protected OfferDao offerDao;

    static {
        // load static mvel functions into SB
        InputStream is = OfferServiceImpl.class.getResourceAsStream("/org/broadleafcommerce/offer/service/mvelFunctions.mvel");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                functions.append(line);
            }
            functions.append(" ");
        } catch(Exception e){
            throw new RuntimeException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e){}
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.offer.service.OfferService#consumeOffer(org.broadleafcommerce.offer.domain.Offer, org.broadleafcommerce.profile.domain.Customer)
     */
    @Override
    public boolean consumeOffer(Offer offer, Customer customer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Offer lookupOfferByCode(String code) {
        Offer offer = null;
        OfferCode offerCode = offerCodeDao.readOfferCodeByCode(code);
        if (offerCode != null) {
            offer = offerCode.getOffer();
        }
        return offer;
    }


    // Retrieves all the offers that can be applied to this order
    // The same offer cannot be applied more than one time
    public List<Offer> buildOfferListForOrder(Order order) {
        List<Offer> offers = new ArrayList<Offer>();
        List<CustomerOffer> customerOffers = lookupOfferCustomerByCustomer(order.getCustomer());
        for (CustomerOffer customerOffer : customerOffers) {
            if (!offers.contains(customerOffer.getOffer())) {
                offers.add(customerOffer.getOffer());
            }
        }
        List<OfferCode> orderOfferCodes = order.getAddedOfferCodes();
        orderOfferCodes = removeOutOfDateOfferCodes(orderOfferCodes);
        for (OfferCode orderOfferCode : orderOfferCodes) {
            if (!offers.contains(orderOfferCode.getOffer())) {
                offers.add(orderOfferCode.getOffer());
            }
        }
        List<Offer> globalOffers = lookupGlobalOffers();
        for (Offer globalOffer : globalOffers) {
            if (!offers.contains(globalOffer)) {
                offers.add(globalOffer);
            }
        }
        return offers;
    }


    private List<CustomerOffer> lookupOfferCustomerByCustomer(Customer customer) {
        List<CustomerOffer> offerCustomers = customerOfferDao.readCustomerOffersByCustomer(customer);
        return offerCustomers;
    }

    private List<Offer> lookupGlobalOffers() {
        List<Offer> globalOffers = offerDao.readOffersByAutomaticDeliveryType();
        return globalOffers;
    }


    /*
     *
     * Offers Logic:
     * 1) Remove all existing offers in the Order (order, item, and fulfillment)
     * 2) Check and remove offers
     *    a) Remove out of date offers
     *    b) Remove offers that do not apply to this customer
     * 3) Loop through offers
     *    a) Verifies type of offer (order, order item, fulfillment)
     *    b) Verifies if offer can be applies
     *    c) Assign offer to type (order, order item, or fulfillment)
     * 4) Sort the order, item and fulfillment offers list by discount and priority (priority out ranks discount)
     * 5) Identify the best offers to apply to order item and create adjustments for each item offer
     * 6) Compare order item adjustment price to sales price, and remove adjustments if sale price is better
     * 7) Identify the best offers to apply to the order and create adjustments for each order offer (assume order subtotal represents retail price)
     * 8) If item contains non-Combinable offers or order contains non-Stackable offers, remove the item or order adjustments based on value
     * 9) Set final order item prices and reapply order offers
     * 10)
     *
     * Assumptions:
     * 1) % off all items will be created as an item offer with no expression
     * 2) $ off order will be created as an order offer
     * 3) Order offers applies to the best price for each item (not just retail price)
     * 4) Fulfillment offers apply to best price for each item (not just retail price)
     * 5) Stackable only applies to the same offer type (i.e. a not stackable order offer can be used with item offers)
     * 6) Fulfillment offers cannot be not combinable
     */
    @SuppressWarnings("unchecked")
    public void applyOffersToOrder(List<Offer> offers, Order order) throws PricingException {
        // we assume that all offers that can be applied to this order exists in the offers list
        // The evaluation on customer need to be executed first
        List<CandidateOrderOffer> qualifiedOrderOffers = new ArrayList<CandidateOrderOffer>();

        List<CandidateItemOffer> qualifiedItemOffers = new ArrayList<CandidateItemOffer>();

        order.removeAllCandidateOffers();
        order.removeAllAdjustments();

        if (offers != null && !offers.isEmpty()) {
            // set order subtotal price to total item price without adjustments
            order.setSubTotal(order.calculateCurrentSubTotal());

            List<Offer> filteredOffers = removeOutOfDateOffers(offers);

            filteredOffers = removeInvalidCustomerOffers(filteredOffers, order);

            List<DiscreteOrderItem> discreteOrderItems = order.getDiscountableDiscreteOrderItems();


            if (filteredOffers != null && !filteredOffers.isEmpty()) {

                for (Offer offer : filteredOffers) {
                    //
                    // . Evaluate all offers and compute their discount amount as if they were the only offer on the order
                    //

                    //TODO: change code so the computing discount only happens in the CandidateItemOffer or CandidateOrderOffer objects
                    if(offer.getType().equals(OfferType.ORDER)){
                        if (couldOfferApplyToOrder(offer, order)) {
                            CandidateOrderOffer candidateOffer = new CandidateOrderOfferImpl(order, offer);
                            // Why do we add offers here when we set the sorted list later
                            order.addCandidateOrderOffer(candidateOffer);
                            qualifiedOrderOffers.add(candidateOffer);
                        }
                    } else if(offer.getType().equals(OfferType.ORDER_ITEM)){
                        for (DiscreteOrderItem discreteOrderItem : discreteOrderItems) {
                            if(couldOfferApplyToOrder(offer, order, discreteOrderItem)) {
                                CandidateItemOffer candidateOffer = new CandidateItemOfferImpl(discreteOrderItem, offer);
                                discreteOrderItem.addCandidateItemOffer(candidateOffer);
                                qualifiedItemOffers.add(candidateOffer);
                            }
                        }
                    } else if(offer.getType().equals(OfferType.FULFILLMENT_GROUP)){
                        // TODO: Handle Offer calculation for offer type of fullfillment group
                        // how to verify if offer applies for fulfillment?
                        for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
                            if(couldOfferApplyToOrder(offer, order, fulfillmentGroup)) {
                                CandidateFulfillmentGroupOffer candidateOffer = new CandidateFulfillmentGroupOfferImpl(fulfillmentGroup, offer);
                                fulfillmentGroup.addCandidateFulfillmentGroupOffer(candidateOffer);
                            }
                        }
                    }
                }
                //
                // . Create a sorted list sorted by priority asc then amount desc
                // TODO: verify if discountPrice is the adjusted price or discount amount(not use reverse comparator)
                //
                Collections.sort(qualifiedOrderOffers, new BeanComparator("discountedPrice"));
                Collections.sort(qualifiedOrderOffers, new BeanComparator("priority"));

                Collections.sort(qualifiedItemOffers, new BeanComparator("discountedPrice"));
                Collections.sort(qualifiedItemOffers, new BeanComparator("priority"));
                // qualifiedItemOffers list is sorted but the candidate offer list on the item is not sorted.  what do we use?


                // Determine if the offers should be applied to this line item; may want to move to its own method
                // Iterate through the collection of CandiateItemOffers. Remember that each one is an offer that may apply to a
                // particular OrderItem.  Multiple CandidateItemOffers may contain a reference to the same OrderItem object.
                //
                // isCombinableWithOtherOffers - not combinable with any offers in the order
                // isStackable - cannot be stack on top of an existing item offer back, other offers can be stack of top of it
                //
                // List<Adjustment> itemAdjustments = new ArrayList<Adjustment>(); // used to calculate total discount applied to order items
                boolean isItemAdjustmentApplied = false;
                for (CandidateItemOffer itemOffer : qualifiedItemOffers) {
                    OrderItem orderItem = itemOffer.getOrderItem();
                    if ((itemOffer.getOffer().isCombinableWithOtherOffers()) || (isItemAdjustmentApplied)) {
                        // check to see if this offer can be applied to the order
                        // if no offer has been applied to any of the items, or an offer has been applied but the new offer can be combined
                        if ((itemOffer.getOffer().isStackable()) || orderItem.getOrderItemAdjustments().size() == 0) {
                            // check to see if this offer can be applied to the order item
                            // the offer needs to be stackable or no adjustment has been applied to this line item
                            //if (doesItemOfferApply(itemOffer, order, itemAdjustments)) {
                                applyOrderItemOffer(itemOffer);
                                isItemAdjustmentApplied = true;
                                if (!itemOffer.getOffer().isCombinableWithOtherOffers()) {
                                    // Offer applied is not combinable with other offers, ignore other offers
                                    break;
                                }
                            //}
                        }
                    } else {
                        // no code here; offer cannot be combined with another offer so move to the next offer
                    }
                }

                // compare adjustment price to sales price
                for (DiscreteOrderItem discreteOrderItem : discreteOrderItems) {
                    Money itemPrice = discreteOrderItem.getRetailPrice();
                    if (discreteOrderItem.getSalePrice() != null) {
                        itemPrice = discreteOrderItem.getSalePrice();
                    }
                    if ((discreteOrderItem.getAdjustmentPrice() != null) && (discreteOrderItem.getAdjustmentPrice().greaterThanOrEqual(itemPrice))) {
                        // adjustment price is not best price, remove adjustments for this item
                        discreteOrderItem.removeAllAdjustments();
                    }
                }

                // Determine which order offers apply to the order
                // If order offer is not combinable, first verify order adjustment is zero, if zero, compare item discount total vs this offer's total
                // Non stackable offers will need to be check at a later time since other offers can be stack on top
                for (CandidateOrderOffer orderOffer : qualifiedOrderOffers) {
                    if (order.getOrderAdjustments().size() == 0) {
                        if (!orderOffer.getOffer().isCombinableWithOtherOffers()) {
                            //if (doesOrderOfferApply(orderOffer, order, orderAdjustments)) {
                                applyOrderOffer(orderOffer);
                                // check to see if this not combinable offer has a greater value than all the item offers
                                if (order.getAdjustmentPrice().greaterThanOrEqual(order.calculateCurrentSubTotal())) {
                                    // item offers has more value; remove order offer
                                    order.removeAllAdjustments();
                                } else {
                                    order.removeAllItemAdjustments();
                                    isItemAdjustmentApplied = false;
                                }
                            //}
                        } else {
                            //if (doesOrderOfferApply(orderOffer, order, orderAdjustments)) {
                                applyOrderOffer(orderOffer);
                            //}
                        }
                    } else if (orderOffer.getOffer().isCombinableWithOtherOffers()) {
                        // check to see if this offer can be applied to the order
                        // if no offer has been applied to any of the items, or an offer has been applied but the new offer can be combined
                        if (orderOffer.getOffer().isStackable()) {
                            // check to see if this offer can be applied to the order item
                            // the offer needs to be stackable or no adjustment has been applied to this line item
                            //if (doesOrderOfferApply(orderOffer, order, orderAdjustments)) {
                                applyOrderOffer(orderOffer);
                                if (!orderOffer.getOffer().isCombinableWithOtherOffers()) {
                                    // Offer applied is not combinable with other offers, ignore other offers
                                    break;
                                }
                            //}
                        }
                    } else {
                        // no code here; offer cannot be combined with another offer so move to the next offer
                    }
                }

                // if order contains a non-Combinable item offer determine if the order offer or item offer
                // should apply
                if ((order.getOrderAdjustments().size() > 0) && (isItemAdjustmentApplied)) {
                    if (order.containsNotCombinableItemOffer()) {
                        if (order.getAdjustmentPrice().greaterThanOrEqual(order.calculateCurrentSubTotal())) {
                            // remove all order adjustments
                            order.removeAllAdjustments();
                        } else {
                            order.removeAllItemAdjustments();
                        }
                    }
                }
            }
        }
        order.assignOrderItemsFinalPrice();
        order.setSubTotal(order.calculateFinalSubTotal());
        if (!order.getOrderAdjustments().isEmpty()) {
            order.reapplyOrderAdjustments();
        }
    }


    private void applyOrderItemOffer(CandidateItemOffer itemOffer) {
        OrderItemAdjustment itemAdjustment = new OrderItemAdjustmentImpl(itemOffer.getOrderItem(), itemOffer.getOffer(), itemOffer.getOffer().getName());
        //add to adjustment
        itemOffer.getOrderItem().addOrderItemAdjustment(itemAdjustment); //This is how we can tell if an item has been discounted
    }

    private void applyOrderOffer(CandidateOrderOffer orderOffer) {
        OrderAdjustment orderAdjustment = new OrderAdjustmentImpl(orderOffer.getOrder(), orderOffer.getOffer(), orderOffer.getOffer().getName());
        //add to adjustment
        orderOffer.getOrder().addOrderAdjustments(orderAdjustment); //This is how we can tell if an item has been discounted
    }

    /**
     * Removes all out of date offers.  If an offer does not have a start date, or the start
     * date is a later date, that offer will be removed.  Offers without a start date should
     * not be processed.  If the offer has a end date that has already passed, that offer
     * will be removed.  Offers without a end date will be processed if the start date
     * is prior to the transaction date.
     *
     * @param offers
     * @return
     */
    private List<Offer> removeOutOfDateOffers(List<Offer> offers){
        Date now = new Date();
        List<Offer> offersToRemove = new ArrayList<Offer>();
        for (Offer offer : offers) {
            if ((offer.getStartDate() == null) || (offer.getStartDate().after(now))){
                offersToRemove.add(offer);
            } else if (offer.getEndDate() != null && offer.getEndDate().before(now)){
                offersToRemove.add(offer);
            }
        }
        // remove all offers in the offersToRemove list from original offers list
        for (Offer offer : offersToRemove) {
            offers.remove(offer);
        }
        return offers;
    }

    /**
     * Removes all out of date offerCodes based on the offerCode and its offer's start and end
     * date.  If an offerCode has a later start date, that offerCode will be removed.
     * OfferCodes without a start date will still be processed. If the offerCode
     * has a end date that has already passed, that offerCode will be removed.  OfferCodes
     * without a end date will be processed.  The start and end dates on the offer will
     * still need to be evaluated.
     *
     * @param offers
     * @return
     */
    private List<OfferCode> removeOutOfDateOfferCodes(List<OfferCode> offerCodes){
        Date now = new Date();
        List<OfferCode> offerCodesToRemove = new ArrayList<OfferCode>();
        for (OfferCode offerCode : offerCodes) {
            if ((offerCode.getStartDate() != null) && (offerCode.getStartDate().after(now))){
                offerCodesToRemove.add(offerCode);
            } else if (offerCode.getEndDate() != null && offerCode.getEndDate().before(now)){
                offerCodesToRemove.add(offerCode);
            }
        }
        // remove all offers in the offersToRemove list from original offers list
        for (OfferCode offerCode : offerCodesToRemove) {
            offerCodes.remove(offerCode);
        }
        return offerCodes;
    }

    private List<Offer> removeInvalidCustomerOffers(List<Offer> offers, Order order){
        List<Offer> offersToRemove = new ArrayList<Offer>();
        for (Offer offer : offers) {
            if (!couldOfferApplyToCustomer(offer, order)) {
                offersToRemove.add(offer);
            }
        }
        // remove all offers in the offersToRemove list from original offers list
        for (Offer offer : offersToRemove) {
            offers.remove(offer);
        }
        return offers;
    }

    private boolean couldOfferApplyToOrder(Offer offer, Order order) {
        return couldOfferApplyToOrder(offer, order, null, null);
    }

    private boolean couldOfferApplyToOrder(Offer offer, Order order, DiscreteOrderItem discreteOrderItem) {
        return couldOfferApplyToOrder(offer, order, discreteOrderItem, null);
    }

    private boolean couldOfferApplyToOrder(Offer offer, Order order, FulfillmentGroup fulfillmentGroup) {
        return couldOfferApplyToOrder(offer, order, null, fulfillmentGroup);
    }

    private boolean couldOfferApplyToOrder(Offer offer, Order order, DiscreteOrderItem discreteOrderItem, FulfillmentGroup fulfillmentGroup) {
        boolean appliesToItem = false;

        if (offer.getAppliesToOrderRules() != null && offer.getAppliesToOrderRules().length() != 0) {

            HashMap<String, Object> vars = new HashMap<String, Object>();
            vars.put("doMark", Boolean.FALSE); //We never want to mark offers when we are checking if they could apply.
            vars.put("order", order);
            vars.put("offer", offer);
//            if (fulfillmentGroup != null) {
//                vars.put("currentfulfillmentGroup", fulfillmentGroup);
//            }
            if (discreteOrderItem != null) {
                vars.put("discreteOrderItem", discreteOrderItem);
            }
            Boolean expressionOutcome = (Boolean)executeExpression(offer.getAppliesToOrderRules(), vars);
            if (expressionOutcome != null && expressionOutcome) {
                appliesToItem = true;
            }
        } else {
            appliesToItem = true;
        }

        return appliesToItem;
    }

    private boolean couldOfferApplyToCustomer(Offer offer, Order order) {
        boolean appliesToCustomer = false;

        if (offer.getAppliesToCustomerRules() != null && offer.getAppliesToCustomerRules().length() != 0) {

            HashMap<String, Object> vars = new HashMap<String, Object>();
            vars.put("customer", order.getCustomer());
            Boolean expressionOutcome = (Boolean)executeExpression(offer.getAppliesToCustomerRules(), vars);
            if (expressionOutcome != null && expressionOutcome) {
                appliesToCustomer = true;
            }
        } else {
            appliesToCustomer = true;
        }

        return appliesToCustomer;
    }


    private Object executeExpression(String expression, Map<String, Object> vars) {
        Serializable exp = (Serializable)expressionCache.get(expression);
        if (exp == null) {
            ParserContext context = new ParserContext();
            context.addImport("OfferType", OfferType.class);
            context.addImport("FulfillmentGroupType", FulfillmentGroupType.class);
            StringBuffer completeExpression = new StringBuffer(functions.toString());
            completeExpression.append(" ").append(expression);
            exp = MVEL.compileExpression(completeExpression.toString(), context);
        }
        expressionCache.put(expression, exp);

        return MVEL.executeExpression(exp, vars);

    }

    // Is this needed?  Why would we ever need to look up a code from an offer?  An offer can have more than one code.
    @Override
    public OfferCode lookupCodeByOffer(Offer offer) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.offer.service.OfferService#lookupValidOffersForSystem(java.lang.String)
     */
    @Override
    public List<Offer> lookupValidOffersForSystem(String system) {
        // TODO Auto-generated method stub
        return null;
    }

}
