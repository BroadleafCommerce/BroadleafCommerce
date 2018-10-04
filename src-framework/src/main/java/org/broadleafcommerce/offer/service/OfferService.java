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

import java.util.List;

import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.domain.OfferCode;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.pricing.service.exception.PricingException;

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

    /**
     * Apply offers for a fulfillmentGroup
     * @param fulfillmentGroup
     */
    public void applyFulfillmentGroupOffers(FulfillmentGroup fulfillmentGroup);

    /**
     * Apply offers for a List of FulfillmentGroup
     * @param fulfillmentGroups
     */
    public void applyFulfillmentGroupsOffers(List<FulfillmentGroup> fulfillmentGroups);

    /**
     * Visit all offers on an order including order offers, order item offers and fulfillment offers. If any offer is
     * defined non-combinable, then evaluate that offer against the rest combined to finally apply best offer.
     * @param order
     * @return
     * @throws PricingException
     */
    public boolean reviewAllOffersAndApplyBest(Order order) throws PricingException;




}
