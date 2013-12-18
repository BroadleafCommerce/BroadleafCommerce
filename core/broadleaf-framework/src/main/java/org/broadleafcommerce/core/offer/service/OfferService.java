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
     * Apply offers to order.
     * @param offers the offers
     * @param order the order
     */
    public void applyOffersToOrder(List<Offer> offers, Order order) throws PricingException;

    /**
     * Create a list of offers that applies to this order
     * @param order
     * @return
     */
    public List<Offer> buildOfferListForOrder(Order order);

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
    public boolean verifyMaxCustomerUsageThreshold(@Nonnull Customer customer, @Nonnull Offer offer);
    
    /**
     * <p>Validates that the given code is underneath the max uses for that code. This method will also delegate to
     * {@link #verifyMaxCustomerUsageThreshold(Customer, Offer)} for the code's offer and the passed in customer</p>
     * 
     * @param customer the customer attempting to use the code
     * @param code the code to check
     * @return <b>true</b> if it is ok for the customer to use this offer with their current order, <b>false</b> if not.
     */
    public boolean verifyMaxCustomerUsageThreshold(@Nonnull Customer customer, @Nonnull OfferCode code);
    
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
}
