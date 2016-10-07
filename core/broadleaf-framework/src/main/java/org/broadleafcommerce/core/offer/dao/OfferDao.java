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
package org.broadleafcommerce.core.offer.dao;

import org.broadleafcommerce.core.offer.domain.CandidateFulfillmentGroupOffer;
import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.core.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.core.offer.domain.FulfillmentGroupAdjustment;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferInfo;
import org.broadleafcommerce.core.offer.domain.OrderAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderItemPriceDetailAdjustment;
import org.broadleafcommerce.core.offer.domain.ProratedOrderItemAdjustment;

import java.util.List;

public interface OfferDao {

    ProratedOrderItemAdjustment save(ProratedOrderItemAdjustment adjustment);

    List<Offer> readAllOffers();

    Offer readOfferById(Long offerId);

    List<Offer> readOffersByAutomaticDeliveryType();

    Offer save(Offer offer);

    void delete(Offer offer);

    Offer create();

    CandidateOrderOffer createCandidateOrderOffer();
    
    CandidateItemOffer createCandidateItemOffer();

    CandidateFulfillmentGroupOffer createCandidateFulfillmentGroupOffer();

    OrderItemAdjustment createOrderItemAdjustment();

    OrderItemPriceDetailAdjustment createOrderItemPriceDetailAdjustment();

    OrderAdjustment createOrderAdjustment();

    FulfillmentGroupAdjustment createFulfillmentGroupAdjustment();

    OfferInfo createOfferInfo();

    OfferInfo save(OfferInfo offerInfo);

    void delete(OfferInfo offerInfo);

    /**
     * Returns the number of milliseconds that the current date/time will be cached for queries before refreshing.
     * This aids in query caching, otherwise every query that utilized current date would be different and caching
     * would be ineffective.
     *
     * @return the milliseconds to cache the current date/time
     */
    public Long getCurrentDateResolution();

    /**
     * Sets the number of milliseconds that the current date/time will be cached for queries before refreshing.
     * This aids in query caching, otherwise every query that utilized current date would be different and caching
     * would be ineffective.
     *
     * @param currentDateResolution the milliseconds to cache the current date/time
     */
    public void setCurrentDateResolution(Long currentDateResolution);
}
