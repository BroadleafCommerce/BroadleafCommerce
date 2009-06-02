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
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.domain.Customer;

/**
 * The Interface OfferService.
 */
public interface OfferService {

    /**
     * Lookup offer by code.
     * @param code the code
     * @return the offer
     */
    public Offer lookupOfferByCode(String code);

    /**
     * Consume offer. This method is used at order submit time to finalize the
     * offers that are used. Until this method is called all offers are not used
     * and may be used again if the order is not submitted and the offers not
     * consumed.
     * @param offer the offer
     * @param customer the customer
     * @return true, if successful
     */
    public boolean consumeOffer(Offer offer, Customer customer);

    /**
     * Lookup valid offers for system. Find the offers for a given system.
     * @param system the system
     * @return the list< offer>
     */
    public List<Offer> lookupValidOffersForSystem(String system);

    /**
     * Lookup code by offer.
     * @param offer the offer
     * @return the offer code
     */
    public OfferCode lookupCodeByOffer(Offer offer);

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


}
