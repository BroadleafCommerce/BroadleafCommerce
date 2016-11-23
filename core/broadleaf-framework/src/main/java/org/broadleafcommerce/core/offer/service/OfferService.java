/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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

import org.broadleafcommerce.core.offer.dao.CustomerOfferDao;
import org.broadleafcommerce.core.offer.dao.OfferCodeDao;
import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableItemFactory;
import org.broadleafcommerce.core.offer.service.processor.FulfillmentGroupOfferProcessor;
import org.broadleafcommerce.core.offer.service.processor.ItemOfferProcessor;
import org.broadleafcommerce.core.offer.service.processor.OrderOfferProcessor;
import org.broadleafcommerce.core.offer.service.workflow.VerifyCustomerMaxOfferUsesActivity;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderCustomer;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.core.domain.Customer;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * The Interface OfferService.
 */
public interface OfferService {

    /**
     * Returns all offers
     * @return all offers
     */
    public List<Offer> findAllOffers();

    /**
     * Save a new offer or updates an existing offer
     * @param offer
     * @return the offer
     */
    public Offer save(Offer offer);

    /**
     * Saves a new Offer or updates an existing Offer that belongs to an OfferCode, then saves or updates the OfferCode
     * @param offerCode
     * @return the offerCode
     */
    public OfferCode saveOfferCode(OfferCode offerCode);
    /**
     * Lookup offer by code.
     * @param code the code
     * @return the offer
     */
    public Offer lookupOfferByCode(String code);
    
    /**
     * Lookup an OfferCode by its id
     * @param id the offer id
     * @return the offer
     */
    public OfferCode findOfferCodeById(Long id);

    /**
     * Lookup OfferCode by code.
     * @param code the code
     * @return the offer
     */
    public OfferCode lookupOfferCodeByCode(String code);

    /**
     * Apply offers to order. By default this does not re-price the order.
     * @param offers the offers
     * @param order the order
     * @return
     */
    public Order applyAndSaveOffersToOrder(List<Offer> offers, Order order) throws PricingException;

    /**
     * Apply offers to order. By default this does not re-price the order. This method is deprecated and 
     * should not be used.  The saved order should be returned from this method, which is the case in 
     * applyAndSaveOffersToOrder.
     * @param offers
     * @param order
     * 
     * @throws PricingException
     * @{@link Deprecated} see applyAndSaveOffersToOrder
     */
    @Deprecated
    public void applyOffersToOrder(List<Offer> offers, Order order) throws PricingException;

    /**
     * Lookup all offers by code.
     * @param code the code
     * @return the list of offers
     */
    List<Offer> lookupAllOffersByCode(String code);

    /**
     * Lookup all OfferCodes by code.
     * @param code the code
     * @return the list of offer codes
     */
    List<OfferCode> lookupAllOfferCodesByCode(String code);

    /**
     * Create a list of offers that applies to this order
     * @param order
     * @return
     */
    public List<Offer> buildOfferListForOrder(Order order);

    /**
     * Attempts to resolve a list of offer codes associated explicitly with the customer. 
     * For example, an implementation may choose to associate a specific offer code with a customer 
     * in a custom table or in customer attributes.  This allows you to associate one or more offer codes 
     * with a customer without necessarily having them type it in (e.g. on a URL), or by allowing them to 
     * type it in, but before it has been actually applied to an order.
     * 
     * @param customer
     * @return
     */
    public List<OfferCode> buildOfferCodeListForCustomer(OrderCustomer orderCustomer);

    public CustomerOfferDao getCustomerOfferDao();

    public void setCustomerOfferDao(CustomerOfferDao customerOfferDao);

    public OfferCodeDao getOfferCodeDao();

    public void setOfferCodeDao(OfferCodeDao offerCodeDao);

    public OfferDao getOfferDao();

    public void setOfferDao(OfferDao offerDao);

    public OrderOfferProcessor getOrderOfferProcessor();

    public void setOrderOfferProcessor(OrderOfferProcessor orderOfferProcessor);

    public ItemOfferProcessor getItemOfferProcessor();

    public void setItemOfferProcessor(ItemOfferProcessor itemOfferProcessor);

    public FulfillmentGroupOfferProcessor getFulfillmentGroupOfferProcessor();

    public void setFulfillmentGroupOfferProcessor(FulfillmentGroupOfferProcessor fulfillmentGroupOfferProcessor);
    
    public Order applyAndSaveFulfillmentGroupOffersToOrder(List<Offer> offers, Order order) throws PricingException;

    @Deprecated
    public void applyFulfillmentGroupOffersToOrder(List<Offer> offers, Order order) throws PricingException;

    public PromotableItemFactory getPromotableItemFactory();

    public void setPromotableItemFactory(PromotableItemFactory promotableItemFactory);

    /**
     * <p>Validates that the passed in customer has not exceeded the max uses for the
     * passed in offer.</p>
     *
     * <p>This condition could pass if the system allows two concurrent carts for the same customer.
     * The condition will fail at order submission time when the {@link VerifyCustomerMaxOfferUsesActivity}
     * runs (if that activity is configured as part of the checkout workflow.)</p>
     *
     * <p>This method only checks offers who have a max_customer_uses value that is greater than zero.
     * By default offers can be used as many times as the customer's order qualifies.</p>
     *
     * <p>This method offers no protection against systems that allow customers to create
     * multiple ids in the system.</p>
     *
     * @param customer the customer attempting to use the offer
     * @param offer the offer to check
     * @return <b>true</b> if it is ok for the customer to use this offer with their current order, <b>false</b> if not.
     */
    public boolean verifyMaxCustomerUsageThreshold(@Nonnull OrderCustomer orderCustomer, @Nonnull Offer offer);
    
    /**
     * <p>Validates that the given code is underneath the max uses for that code. This method will also delegate to
     * {@link #verifyMaxCustomerUsageThreshold(Customer, Offer)} for the code's offer and the passed in customer</p>
     * 
     * @param customer the customer attempting to use the code
     * @param code the code to check
     * @return <b>true</b> if it is ok for the customer to use this offer with their current order, <b>false</b> if not.
     */
    public boolean verifyMaxCustomerUsageThreshold(@Nonnull OrderCustomer orderCustomer, @Nonnull OfferCode code);
    
    /**
     * Returns a set of offers that have been used for this order by checking adjustments on the different levels like
     * FulfillmentGroups and OrderItems. This will return all of the unique offers used for instances where an offer can
     * apply to multiple OrderItems or multiple FulfillmentGroups (and show up as different adjustments on each)
     * 
     * @param order
     * @return
     */
    public Set<Offer> getUniqueOffersFromOrder(Order order);
    
    /**
     * Given a list of offer codes and a set of offers, return a map of of offer codes that are keyed by the offer that was
     * applied to the order
     * 
     * @param codes
     * @param appliedOffers
     * @return
     */
    public Map<Offer, OfferCode> getOffersRetrievedFromCodes(List<OfferCode> codes, Set<Offer> appliedOffers);

    /**
     * For a given order, give back a map of all {@link Offer}s that were retrieved from {@link OfferCode}s. More explicitly,
     * this will look at all of the offers that have been used by looking at a given {@link Order}'s adjustments and then
     * match those up with the codes from {@link Order#getAddedOfferCodes()}.
     * 
     * @param order
     * @return a map from {@link Offer} to the {@link OfferCode} that was used to obtain it
     */
    public Map<Offer, OfferCode> getOffersRetrievedFromCodes(Order order);

    public OrderService getOrderService();

    public void setOrderService(OrderService orderService);

    public Boolean deleteOfferCode(OfferCode code);

    public Offer findOfferById(Long offerId);
}
