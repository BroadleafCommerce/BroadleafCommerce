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
import org.broadleafcommerce.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.offer.domain.CustomerOffer;
import org.broadleafcommerce.offer.domain.FulfillmentGroupAdjustment;
import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.domain.OfferCode;
import org.broadleafcommerce.offer.domain.OrderAdjustment;
import org.broadleafcommerce.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.offer.service.type.OfferType;
import org.broadleafcommerce.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.service.type.FulfillmentGroupType;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.time.SystemTime;
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
    //    private static final StringBuffer functions = new StringBuffer();

    // should be called outside of Offer service after Offer service is executed
    @Resource(name="blCustomerOfferDao")
    protected CustomerOfferDao customerOfferDao;

    @Resource(name="blOfferCodeDao")
    protected OfferCodeDao offerCodeDao;

    @Resource(name="blOfferDao")
    protected OfferDao offerDao;

    /*  Not used for current offer discount types.  Will need to be used to support buy-one-get-one-offers.
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
    } */

    public List<Offer> findAllOffers() {
        return offerDao.readAllOffers();
    }

    public Offer save(Offer offer) {
        return offerDao.save(offer);
    }

    public OfferCode saveOfferCode(OfferCode offerCode) {
        offerCode.setOffer(offerDao.save(offerCode.getOffer()));
        return offerCodeDao.save(offerCode);
    }

    /**
     * Creates a list of offers that applies to this order.  All offers that are assigned to the customer,
     * entered during checkout, or has a delivery type of automatic are added to the list.  The same offer
     * cannot appear more than once in the list.
     *
     * @param order
     * @return a List of offers that may apply to this order
     */
    public Offer lookupOfferByCode(String code) {
        Offer offer = null;
        OfferCode offerCode = offerCodeDao.readOfferCodeByCode(code);
        if (offerCode != null) {
            offer = offerCode.getOffer();
        }
        return offer;
    }

    /**
     * Creates a list of offers that applies to this order.  All offers that are assigned to the customer,
     * entered during checkout, or has a delivery type of automatic are added to the list.  The same offer
     * cannot appear more than once in the list.
     *
     * @param order
     * @return a List of offers that may apply to this order
     */
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
        List<Offer> globalOffers = lookupAutomaticDeliveryOffers();
        for (Offer globalOffer : globalOffers) {
            if (!offers.contains(globalOffer)) {
                offers.add(globalOffer);
            }
        }
        return offers;
    }

    /**
     * Private method used to retrieve all offers assigned to this customer.  These offers
     * have a DeliveryType of MANUAL and are programmatically assigned to the customer.
     *
     * @param customer
     * @return a List of offers assigned to the customer
     */
    protected List<CustomerOffer> lookupOfferCustomerByCustomer(Customer customer) {
        List<CustomerOffer> offerCustomers = customerOfferDao.readCustomerOffersByCustomer(customer);
        return offerCustomers;
    }

    /**
     * Private method used to retrieve all offers with DeliveryType of AUTOMATIC
     *
     * @return a List of automatic delivery offers
     */
    protected List<Offer> lookupAutomaticDeliveryOffers() {
        List<Offer> globalOffers = offerDao.readOffersByAutomaticDeliveryType();
        return globalOffers;
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
     * @return a List of non-expired offers
     */
    protected List<OfferCode> removeOutOfDateOfferCodes(List<OfferCode> offerCodes){
        Date now = SystemTime.asDate();
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
     * 4) Sorts the order and item offers list by priority and then discount
     * 5) Identify the best offers to apply to order item and create adjustments for each item offer
     * 6) Compare order item adjustment price to sales price, and remove adjustments if sale price is better
     * 7) Identify the best offers to apply to the order and create adjustments for each order offer
     * 8) If item contains non-combinable offers remove either the item or order adjustments based on discount value
     * 9) Set final order item prices and reapply order offers
     *
     * Assumptions:
     * 1) % off all items will be created as an item offer with no expression
     * 2) $ off order will be created as an order offer
     * 3) Order offers applies to the best price for each item (not just retail price)
     * 4) Fulfillment offers apply to best price for each item (not just retail price)
     * 5) Stackable only applies to the same offer type (i.e. a not stackable order offer can be used with item offers)
     * 6) Fulfillment offers cannot be not combinable
     * 7) Order offers cannot be FIXED_PRICE
     * 8) FIXED_PRICE offers cannot be stackable
     * 9) Non-combinable offers only apply to the order and order items, fulfillment group offers will always apply
     *
     */
    @SuppressWarnings("unchecked")
    public void applyOffersToOrder(List<Offer> offers, Order order) throws PricingException {
        clearOffersandAdjustments(order);
        List<Offer> filteredOffers = filterOffers(offers, order.getCustomer());

        if ((filteredOffers == null) || (filteredOffers.isEmpty())) {
            order.assignOrderItemsFinalPrice();
            order.setSubTotal(order.calculateOrderItemsFinalPrice());
        } else {
            List<CandidateOrderOffer> qualifiedOrderOffers = new ArrayList<CandidateOrderOffer>();
            List<CandidateItemOffer> qualifiedItemOffers = new ArrayList<CandidateItemOffer>();
            // set order subtotal price to total item price without adjustments
            order.setSubTotal(order.calculateOrderItemsCurrentPrice());
            List<DiscreteOrderItem> discreteOrderItems = order.getDiscountableDiscreteOrderItems();
            for (Offer offer : filteredOffers) {
                if(offer.getType().equals(OfferType.ORDER)){
                    if (couldOfferApplyToOrder(offer, order)) {
                        CandidateOrderOffer candidateOffer = offerDao.createCandidateOrderOffer();
                        candidateOffer.setOrder(order);
                        candidateOffer.setOffer(offer);
                        // Why do we add offers here when we set the sorted list later
                        order.addCandidateOrderOffer(candidateOffer);
                        qualifiedOrderOffers.add(candidateOffer);
                    }
                } else if(offer.getType().equals(OfferType.ORDER_ITEM)){
                    for (DiscreteOrderItem discreteOrderItem : discreteOrderItems) {
                        if(couldOfferApplyToOrder(offer, order, discreteOrderItem)) {
                            CandidateItemOffer candidateOffer = offerDao.createCandidateItemOffer();
                            candidateOffer.setOrderItem(discreteOrderItem);
                            candidateOffer.setOffer(offer);
                            discreteOrderItem.addCandidateItemOffer(candidateOffer);
                            qualifiedItemOffers.add(candidateOffer);
                        }
                    }
                } else if(offer.getType().equals(OfferType.FULFILLMENT_GROUP)){
                    // TODO: Handle Offer calculation for offer type of fullfillment group
                    // how to verify if offer applies for fulfillment?
                    for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
                        if(couldOfferApplyToOrder(offer, order, fulfillmentGroup)) {
                            CandidateFulfillmentGroupOffer candidateOffer = offerDao.createCandidateFulfillmentGroupOffer();
                            candidateOffer.setFulfillmentGroup(fulfillmentGroup);
                            candidateOffer.setOffer(offer);
                            fulfillmentGroup.addCandidateFulfillmentGroupOffer(candidateOffer);
                        }
                    }
                }
            }

            if ((qualifiedItemOffers.isEmpty()) && (qualifiedOrderOffers.isEmpty())) {
                order.assignOrderItemsFinalPrice();
                order.setSubTotal(order.calculateOrderItemsFinalPrice());
            } else {
                if (!qualifiedItemOffers.isEmpty()) {
                    // Sort order item offers by priority and total discount
                    Collections.sort(qualifiedItemOffers, new BeanComparator("discountAmount", Collections.reverseOrder()));
                    Collections.sort(qualifiedItemOffers, new BeanComparator("priority"));
                    applyAllItemOffers(qualifiedItemOffers, discreteOrderItems);
                    // TODO: some notStackable offers may not have applied which changes the total discount of that offer
                    // Do we need to resort the list again?
                }

                if (!qualifiedOrderOffers.isEmpty()) {
                    // Sort order offers by priority and discount
                    Collections.sort(qualifiedOrderOffers, new BeanComparator("discountAmount", Collections.reverseOrder()));
                    Collections.sort(qualifiedOrderOffers, new BeanComparator("priority"));
                    qualifiedOrderOffers = removeTrailingNotCombinableOrderOffers(qualifiedOrderOffers);
                    applyAllOrderOffers(qualifiedOrderOffers, order);
                }

                // calculate subtotal with item offers applied
                order.assignOrderItemsFinalPrice();
                order.setSubTotal(order.calculateOrderItemsFinalPrice());
                if ((!qualifiedOrderOffers.isEmpty()) && (!qualifiedItemOffers.isEmpty())) {
                    List<CandidateOrderOffer> finalQualifiedOrderOffers = new ArrayList<CandidateOrderOffer>();
                    order.removeAllOrderAdjustments();
                    for (CandidateOrderOffer condidateOrderOffer : qualifiedOrderOffers) {
                        // recheck the list of order offers and verify if they still apply with the new subtotal
                        if (couldOfferApplyToOrder(condidateOrderOffer.getOffer(), order)) {
                            finalQualifiedOrderOffers.add(condidateOrderOffer);
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
    }

    protected List<CandidateOrderOffer> removeTrailingNotCombinableOrderOffers(List<CandidateOrderOffer> candidateOffers) {
        List<CandidateOrderOffer> remainingCandidateOffers = new ArrayList<CandidateOrderOffer>();
        int offerCount = 0;
        for (CandidateOrderOffer candidateOffer : candidateOffers) {
            if (offerCount == 0) {
                remainingCandidateOffers.add(candidateOffer);
            } else {
                if (candidateOffer.getOffer().isCombinableWithOtherOffers()) {
                    remainingCandidateOffers.add(candidateOffer);
                }
            }
            offerCount++;
        }
        return remainingCandidateOffers;
    }

    protected List<CandidateItemOffer> removeTrailingNotCombinableItemOffers(List<CandidateItemOffer> candidateOffers) {
        List<CandidateItemOffer> remainingCandidateOffers = new ArrayList<CandidateItemOffer>();
        int offerCount = 0;
        Offer notCombinableOfferApplied = null;
        for (CandidateItemOffer candidateOffer : candidateOffers) {
            if (offerCount == 0) {
                remainingCandidateOffers.add(candidateOffer);
                if (!candidateOffer.getOffer().isCombinableWithOtherOffers()) {
                    notCombinableOfferApplied = candidateOffer.getOffer();
                }
            } else {
                if (candidateOffer.getOffer().isCombinableWithOtherOffers()) {
                    remainingCandidateOffers.add(candidateOffer);
                } else if (candidateOffer.getOffer().equals(notCombinableOfferApplied)) {
                    // Do not remove the same offer applied to different items
                    remainingCandidateOffers.add(candidateOffer);
                }
            }
            offerCount++;
        }
        return remainingCandidateOffers;
    }

    protected List<CandidateOrderOffer> removeOfferFromCandidateOrderOffers(List<CandidateOrderOffer> candidateOffers, Offer offer) {
        List<CandidateOrderOffer> remainingCandidateOffers = new ArrayList<CandidateOrderOffer>();
        for (CandidateOrderOffer candidateOffer : candidateOffers) {
            if (!candidateOffer.getOffer().equals(offer)) {
                remainingCandidateOffers.add(candidateOffer);
            }
        }
        return remainingCandidateOffers;
    }

    protected List<CandidateItemOffer> removeOfferFromCandidateItemOffers(List<CandidateItemOffer> candidateOffers, Offer offer) {
        List<CandidateItemOffer> remainingCandidateOffers = new ArrayList<CandidateItemOffer>();
        for (CandidateItemOffer candidateOffer : candidateOffers) {
            if (!candidateOffer.getOffer().equals(offer)) {
                remainingCandidateOffers.add(candidateOffer);
            }
        }
        return remainingCandidateOffers;
    }

    protected void clearOffersandAdjustments(Order order) {
        order.removeAllCandidateOffers();
        order.removeAllAdjustments();
    }

    protected List<Offer> filterOffers(List<Offer> offers, Customer customer) {
        List<Offer> filteredOffers = null;
        if (offers != null && !offers.isEmpty()) {
            filteredOffers = removeOutOfDateOffers(offers);
            filteredOffers = removeInvalidCustomerOffers(filteredOffers, customer);
        }
        return filteredOffers;
    }

    /**
     * Removes all out of date offers.  If an offer does not have a start date, or the start
     * date is a later date, that offer will be removed.  Offers without a start date should
     * not be processed.  If the offer has a end date that has already passed, that offer
     * will be removed.  Offers without a end date will be processed if the start date
     * is prior to the transaction date.
     *
     * @param offers
     * @return List of Offers with valid dates
     */
    protected List<Offer> removeOutOfDateOffers(List<Offer> offers){
        Date now = SystemTime.asDate();
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
     * Private method that takes in a list of Offers and removes all Offers from the list that
     * does not apply to this customer.
     *
     * @param offers
     * @param customer
     * @return List of Offers that apply to this customer
     */
    protected List<Offer> removeInvalidCustomerOffers(List<Offer> offers, Customer customer){
        List<Offer> offersToRemove = new ArrayList<Offer>();
        for (Offer offer : offers) {
            if (!couldOfferApplyToCustomer(offer, customer)) {
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
     * Private method which executes the appliesToCustomerRules in the Offer to determine if this Offer
     * can be applied to the Customer.
     *
     * @param offer
     * @param customer
     * @return true if offer can be applied, otherwise false
     */
    protected boolean couldOfferApplyToCustomer(Offer offer, Customer customer) {
        boolean appliesToCustomer = false;

        if (offer.getAppliesToCustomerRules() != null && offer.getAppliesToCustomerRules().length() != 0) {

            HashMap<String, Object> vars = new HashMap<String, Object>();
            vars.put("customer", customer);
            Boolean expressionOutcome = executeExpression(offer.getAppliesToCustomerRules(), vars);
            if (expressionOutcome != null && expressionOutcome) {
                appliesToCustomer = true;
            }
        } else {
            appliesToCustomer = true;
        }

        return appliesToCustomer;
    }

    /**
     * Private method that takes a list of sorted CandidateItemOffers and determines if each offer can be
     * applied based on the restrictions (stackable and/or combinable) on that offer.  OrderItemAdjustments
     * are create on the OrderItem for each applied CandidateItemOffer.  An offer with stackable equals false
     * cannot be applied to an OrderItem that already contains an OrderItemAdjustment.  An offer with combinable
     * equals false cannot be applied to an OrderItem if that OrderItem already contains an
     * OrderItemAdjustment, unless the offer is the same offer as the OrderItemAdjustment offer.
     *
     * @param itemOffers a sorted list of CandidateItemOffer
     * @return true if an OrderItemOffer was applied, otherwise false
     */
    protected boolean applyAllItemOffers(List<CandidateItemOffer> itemOffers, List<DiscreteOrderItem> discreteOrderItems) {
        // Iterate through the collection of CandiateItemOffers. Remember that each one is an offer that may apply to a
        // particular OrderItem.  Multiple CandidateItemOffers may contain a reference to the same OrderItem object.
        // The same offer may be applied to different Order Items
        //
        // isCombinableWithOtherOffers - not combinable with any offers in the order
        // isStackable - cannot be stack on top of an existing item offer back, other offers can be stack of top of it
        //
        boolean itemOffersApplied = false;
        int appliedItemOffersCount = 0;
        for (CandidateItemOffer itemOffer : itemOffers) {
            OrderItem orderItem = itemOffer.getOrderItem();
            if (!orderItem.isNotCombinableOfferApplied()) {
                if ((itemOffer.getOffer().isCombinableWithOtherOffers() && itemOffer.getOffer().isStackable()) || !orderItem.isHasOrderItemAdjustments()) {
                    // check if not combinable offer is better than sale price; if no, remove the not combinable offer so
                    // that another offer may be applied to the item
                    applyOrderItemOffer(itemOffer);
                    if (!itemOffer.getOffer().isCombinableWithOtherOffers()) {
                        Money itemPrice = itemOffer.getOrderItem().getRetailPrice();
                        if (itemOffer.getOrderItem().getSalePrice() != null) {
                            itemPrice = itemOffer.getOrderItem().getSalePrice();
                        }
                        if (itemOffer.getOrderItem().getAdjustmentPrice().greaterThanOrEqual(itemPrice)) {
                            // adjustment price is not best price, remove adjustments for this item
                            itemOffer.getOrderItem().removeAllAdjustments();
                            appliedItemOffersCount--;
                        }
                    }
                    appliedItemOffersCount++;
                }
            }
        }
        if (appliedItemOffersCount > 0) {
            // compare adjustment price to sales price and remove adjustments if sales price is better
            for (DiscreteOrderItem discreteOrderItem : discreteOrderItems) {
                if (discreteOrderItem.getAdjustmentPrice() != null) {
                    Money itemPrice = discreteOrderItem.getRetailPrice();
                    if (discreteOrderItem.getSalePrice() != null) {
                        itemPrice = discreteOrderItem.getSalePrice();
                    }
                    if (discreteOrderItem.getAdjustmentPrice().greaterThanOrEqual(itemPrice)) {
                        // adjustment price is not best price, remove adjustments for this item
                        int offersRemoved = discreteOrderItem.removeAllAdjustments();
                        appliedItemOffersCount = appliedItemOffersCount - offersRemoved;
                    }
                }
            }
        }
        if (appliedItemOffersCount > 0) {
            itemOffersApplied = true;
        }
        return itemOffersApplied;
    }

    /**
     * Private method used by applyAllItemOffers to create an OrderItemAdjustment from a CandidateItemOffer
     * and associates the OrderItemAdjustment to the OrderItem.
     *
     * @param itemOffer a CandidateItemOffer to apply to an OrderItem
     */
    protected void applyOrderItemOffer(CandidateItemOffer itemOffer) {
        OrderItemAdjustment itemAdjustment = offerDao.createOrderItemAdjustment();
        itemAdjustment.init(itemOffer.getOrderItem(), itemOffer.getOffer(), itemOffer.getOffer().getName());
        //add to adjustment
        itemOffer.getOrderItem().addOrderItemAdjustment(itemAdjustment); //This is how we can tell if an item has been discounted
    }

    /**
     * Private method that takes a list of sorted CandidateOrderOffers and determines if each offer can be
     * applied based on the restrictions (stackable and/or combinable) on that offer.  OrderAdjustments
     * are create on the Order for each applied CandidateOrderOffer.  An offer with stackable equals false
     * cannot be applied to an Order that already contains an OrderAdjustment.  An offer with combinable
     * equals false cannot be applied to the Order if the Order already contains an OrderAdjustment.
     *
     * @param orderOffers a sorted list of CandidateOrderOffer
     * @param order the Order to apply the CandidateOrderOffers
     * @return true if order offer applied; otherwise false
     */
    protected boolean applyAllOrderOffers(List<CandidateOrderOffer> orderOffers, Order order) {
        // If order offer is not combinable, first verify order adjustment is zero, if zero, compare item discount total vs this offer's total
        boolean orderOffersApplied = false;
        for (CandidateOrderOffer orderOffer : orderOffers) {
            if ((orderOffer.getOffer().isStackable()) || !order.isHasOrderAdjustments()) {
                applyOrderOffer(orderOffer);
                orderOffersApplied = true;
                if (!orderOffer.getOffer().isCombinableWithOtherOffers()) {
                    if (order.getAdjustmentPrice().greaterThanOrEqual(order.calculateOrderItemsCurrentPrice())) {
                        // item offer is better; remove not combinable order offer and process other order offers
                        order.removeAllOrderAdjustments();
                        orderOffersApplied = false;
                    } else {
                        // not combinable order offer is better; remove all item offers
                        order.removeAllItemAdjustments();
                        break;
                    }
                }
            }
        }
        return orderOffersApplied;
    }

    /**
     * Private method used by applyAllOrderOffers to create an OrderAdjustment from a CandidateOrderOffer
     * and associates the OrderAdjustment to the Order.
     *
     * @param orderOffer a CandidateOrderOffer to apply to an Order
     */
    protected void applyOrderOffer(CandidateOrderOffer orderOffer) {
        OrderAdjustment orderAdjustment = offerDao.createOrderAdjustment();
        orderAdjustment.init(orderOffer.getOrder(), orderOffer.getOffer(), orderOffer.getOffer().getName());
        //add to adjustment
        orderOffer.getOrder().addOrderAdjustments(orderAdjustment);
    }

    /**
     * Private method which executes the appliesToOrderRules in the Offer to determine if this offer
     * can be applied to the Order, OrderItem, or FulfillmentGroup.
     *
     * @param offer
     * @param order
     * @return true if offer can be applied, otherwise false
     */
    protected boolean couldOfferApplyToOrder(Offer offer, Order order) {
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
    protected boolean couldOfferApplyToOrder(Offer offer, Order order, DiscreteOrderItem discreteOrderItem) {
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
    protected boolean couldOfferApplyToOrder(Offer offer, Order order, FulfillmentGroup fulfillmentGroup) {
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
    protected boolean couldOfferApplyToOrder(Offer offer, Order order, DiscreteOrderItem discreteOrderItem, FulfillmentGroup fulfillmentGroup) {
        boolean appliesToItem = false;

        if (offer.getAppliesToOrderRules() != null && offer.getAppliesToOrderRules().length() != 0) {

            HashMap<String, Object> vars = new HashMap<String, Object>();
            //vars.put("doMark", Boolean.FALSE); // We never want to mark offers when we are checking if they could apply.
            vars.put("order", order);
            vars.put("offer", offer);
            if (fulfillmentGroup != null) {
                vars.put("fulfillmentGroup", fulfillmentGroup);
            }
            if (discreteOrderItem != null) {
                vars.put("discreteOrderItem", discreteOrderItem);
            }
            Boolean expressionOutcome = executeExpression(offer.getAppliesToOrderRules(), vars);
            if (expressionOutcome != null && expressionOutcome) {
                appliesToItem = true;
            }
        } else {
            appliesToItem = true;
        }

        return appliesToItem;
    }

    /**
     * Private method used by couldOfferApplyToOrder to execute the MVEL expression in the
     * appliesToOrderRules to determine if this offer can be applied.
     *
     * @param expression
     * @param vars
     * @return a Boolean object containing the result of executing the MVEL expression
     */
    protected Boolean executeExpression(String expression, Map<String, Object> vars) {
        Serializable exp = (Serializable)expressionCache.get(expression);
        if (exp == null) {
            ParserContext context = new ParserContext();
            context.addImport("OfferType", OfferType.class);
            context.addImport("FulfillmentGroupType", FulfillmentGroupType.class);
            //            StringBuffer completeExpression = new StringBuffer(functions.toString());
            //            completeExpression.append(" ").append(expression);
            exp = MVEL.compileExpression(expression.toString(), context);
        }
        expressionCache.put(expression, exp);

        return (Boolean)MVEL.executeExpression(exp, vars);

    }

    public void applyFulfillmentGroupsOffers(List<FulfillmentGroup> fulfillmentGroups) {
        for (FulfillmentGroup fulfillmentGroup : fulfillmentGroups) {
            applyFulfillmentGroupOffers(fulfillmentGroup);
        }
    }

    @SuppressWarnings("unchecked")
    public void applyFulfillmentGroupOffers(FulfillmentGroup fulfillmentGroup) {
        List<CandidateFulfillmentGroupOffer> qualifiedFulfillmentGroupOffers = fulfillmentGroup.getCandidateFulfillmentGroupOffers();
        if (qualifiedFulfillmentGroupOffers.size() > 0) {
            Collections.sort(qualifiedFulfillmentGroupOffers, new BeanComparator("discountedPrice"));
            Collections.sort(qualifiedFulfillmentGroupOffers, new BeanComparator("priority"));
            Collections.sort(qualifiedFulfillmentGroupOffers, new BeanComparator("offer.combinableWithOtherOffers", Collections.reverseOrder()));
            applyAllFulfillmentGroupOffers(qualifiedFulfillmentGroupOffers, fulfillmentGroup);
        }
        if (fulfillmentGroup.getAdjustmentPrice() != null) {
            fulfillmentGroup.setShippingPrice(fulfillmentGroup.getAdjustmentPrice());
        } else if (fulfillmentGroup.getSaleShippingPrice() != null) {
            fulfillmentGroup.setShippingPrice(fulfillmentGroup.getSaleShippingPrice());
        } else {
            fulfillmentGroup.setShippingPrice(fulfillmentGroup.getRetailShippingPrice());
        }
    }

    /**
     * Private method that takes a list of sorted CandidateFulfillmentGroupOffer and determines if each offer can be
     * applied based on the restrictions (stackable and/or combinable) on that offer.  FulfillmentGroupAdjustment
     * are create on the FulfillmentGroup for each applied CandidateFulfillmentGroupOffer.  An offer with stackable equals false
     * cannot be applied to an FulfillmentGroup that already contains an FulfillmentGroupAdjustment.  An offer with combinable
     * equals false cannot be applied to the FulfillmentGroup if the FulfillmentGroup already contains an
     * FulfillmentGroupAdjustment.
     *
     * @param fulfillmentGroupOffers a sorted list of CandidateFulfillmentGroupOffer
     * @param fulfillmentGroup the FulfillmentGroup to apply the CandidateOrderOffers
     */
    protected void applyAllFulfillmentGroupOffers(List<CandidateFulfillmentGroupOffer> fulfillmentGroupOffers, FulfillmentGroup fulfillmentGroup) {
        // If order offer is not combinable, first verify order adjustment is zero, if zero, compare item discount total vs this offer's total
        Money appliedDiscounts = new Money(0D);
        for (CandidateFulfillmentGroupOffer fulfillmentGroupOffer : fulfillmentGroupOffers) {
            if ((fulfillmentGroupOffer.getOffer().isStackable()) || fulfillmentGroup.getFulfillmentGroupAdjustments().size() == 0) {
                if (!fulfillmentGroupOffer.getOffer().isCombinableWithOtherOffers()) {
                    if (fulfillmentGroupOffer.getDiscountAmount().greaterThan(appliedDiscounts)) {
                        fulfillmentGroup.removeAllAdjustments();
                        applyFulfillmentGroupOffer(fulfillmentGroupOffer);
                        break;
                    }
                } else {
                    applyFulfillmentGroupOffer(fulfillmentGroupOffer);
                    appliedDiscounts = appliedDiscounts.add(fulfillmentGroupOffer.getDiscountAmount());
                }
            }
        }
    }

    /**
     * Private method used by applyAllFulfillmentGroupOffers to create an FulfillmentGroupAdjustment from a CandidateFulfillmentGroupOffer
     * and associates the FulfillmentGroupAdjustment to the Order.
     *
     * @param fulfillmentGroupOffer a CandidateFulfillmentGroupOffer to apply to an Order
     */
    protected void applyFulfillmentGroupOffer(CandidateFulfillmentGroupOffer fulfillmentGroupOffer) {
        FulfillmentGroupAdjustment fulfillmentGroupAdjustment = offerDao.createFulfillmentGroupAdjustment();
        fulfillmentGroupAdjustment.init(fulfillmentGroupOffer.getFulfillmentGroup(), fulfillmentGroupOffer.getOffer(), fulfillmentGroupOffer.getOffer().getName());
        //add to adjustment
        fulfillmentGroupOffer.getFulfillmentGroup().addFulfillmentGroupAdjustment(fulfillmentGroupAdjustment);
    }

    public OfferCode lookupOfferCodeByCode(String code){
        return offerCodeDao.readOfferCodeByCode(code);
    }


    /*
     *
     * Assumptions:
     * 1) Order and Order Item Offers have been applied and amounts adjusted
     * 2) Fulfillment Offers have been applied and shipping costs adjusted
     * 3) This feature is applicable only if any Fulfillment offer is Non-Combinable.
     *    If not, this code does not execute.
     * 4) The goal is to determine whether applying a non-combinable fulfillment offer
     *    to retail prices is better than applying the order+items offer with retail shipping.
     * 
     * Review Offers Logic:
     * 1) Determine if any fulfillment offer is non-combinable
     * 2) if there are no non-combinable shipping, return. everything has been calculated already.
     * 3) if any shipping offer is non-combinable, continue to next step
     * 4) get total shipping cost without fulfillment offers
     * 5) get total order cost without order and item offers applied
     * 5) get total shipping adjustment cost applying fulfillment offers
     * 6) get order total with offers applied
     * 7) compare (Ord_with_off + Ret_Shipping) and (Ord_with_no_off + Adj_Shipping)
     * 8) if (Ord_with_off + Ret_Shipping) is lesser
     *    - remove fulfillment adjustments and fulfillment candidate offers
     *    - keep order + item offers and applied adjustments as-is
     * 9) if (Ord_with_no_off + Adj_Shipping) is lesser
     *    - remove all order and item offers and adjustments
     *    - remove all fulfillments offers and adjustments
     *    - apply fulfillment candidate offers
     * 10) return a boolean whether review logic was executed or not
     * 11) Iff review logic was executed, the caller of this method (ReviewOffersActivity)
     *     will apply fulfillment offers and calculate fulfillments adjustments.
     * 
     */
    public boolean reviewAllOffersAndApplyBest(Order order)  throws PricingException {

        List<Offer> allFulfillmentOffers = new ArrayList<Offer>();
        boolean atLeastOneFulfillmentOfferNotCombinable = isAnyOneFulfillmentOfferNotCombinable(order, allFulfillmentOffers);

        if (atLeastOneFulfillmentOfferNotCombinable && order.getTotalAdjustmentsValue() != null) {
            Money orderAdjustmentPriceWithOffers = null;
            if (order.getAdjustmentPrice() != null) {
                orderAdjustmentPriceWithOffers = order.getAdjustmentPrice();
            } else {
                orderAdjustmentPriceWithOffers = order.getSubTotal();
            }
            Money totalShippingPriceWithOffers = order.getTotalShipping();

            Money orderAdjustmentPriceWithoutOffers = orderAdjustmentPriceWithOffers.add(order.getTotalAdjustmentsValue());
            Money totalShippingPriceWithoutOffers = order.getShippingPriceWithoutOffers();
            if ((orderAdjustmentPriceWithOffers.add(totalShippingPriceWithoutOffers)).greaterThan((orderAdjustmentPriceWithoutOffers.add(totalShippingPriceWithOffers)))) {
                // remove order and item offers
                // recalculate order and item totals without offers
                // recalculate shipping with fulfillment offers
                applyOffersToOrder(allFulfillmentOffers, order);
            } else {
                // remove shipping offer and keep order and item offers
                // recalculate shipping without fulfillment offers
                for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
                    fulfillmentGroup.removeAllAdjustments();
                    fulfillmentGroup.removeAllCandidateOffers();
                }
            }
            order.setShippingPriceWithoutOffers(new Money(0));
        }

        return (atLeastOneFulfillmentOfferNotCombinable && order.getTotalAdjustmentsValue() != null);
    }

    /**
     * Private method used by reviewAllOffersAndApplyBest to determine whether there is at least one
     * non-combinable fulfillment offer. It also collects all candidate fulfillment offers to be used
     * in case they need to be reapplied.
     *
     * @param order the order
     * @param allOffers a collection to hold all candidate fulfillment offers for use later
     */
    private boolean isAnyOneFulfillmentOfferNotCombinable(Order order, List<Offer> allOffers) {
        List<FulfillmentGroup> groups = order.getFulfillmentGroups();
        boolean hasNonCombinable = false;
        for (FulfillmentGroup group:groups){
            List<CandidateFulfillmentGroupOffer> groupOfferCandidates = group.getCandidateFulfillmentGroupOffers();
            for (CandidateFulfillmentGroupOffer candidate:groupOfferCandidates){
                Offer offer = candidate.getOffer();
                if (!offer.isCombinableWithOtherOffers()) {
                    hasNonCombinable = true;
                }
                allOffers.add(offer);
            }
        }
        return hasNonCombinable;
    }

}
