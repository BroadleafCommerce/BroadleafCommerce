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
package org.broadleafcommerce.offer.dao;

import java.util.List;

import org.broadleafcommerce.offer.domain.CandidateFulfillmentGroupOffer;
import org.broadleafcommerce.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.offer.domain.FulfillmentGroupAdjustment;
import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.domain.OfferInfo;
import org.broadleafcommerce.offer.domain.OrderAdjustment;
import org.broadleafcommerce.offer.domain.OrderItemAdjustment;

public interface OfferDao {

    public List<Offer> readAllOffers();

    public Offer readOfferById(Long offerId);

    public List<Offer> readOffersByAutomaticDeliveryType();

    public Offer save(Offer offer);

    public void delete(Offer offer);

    public Offer create();

    public CandidateOrderOffer createCandidateOrderOffer();

    public CandidateItemOffer createCandidateItemOffer();

    public CandidateFulfillmentGroupOffer createCandidateFulfillmentGroupOffer();

    public OrderItemAdjustment createOrderItemAdjustment();

    public OrderAdjustment createOrderAdjustment();

    public FulfillmentGroupAdjustment createFulfillmentGroupAdjustment();

    public OfferInfo createOfferInfo();

    public OfferInfo save(OfferInfo offerInfo);

    public void delete(OfferInfo offerInfo);

}
