/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.core.offer.service.processor;

import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateOrderOffer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableItemFactory;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrder;
import org.broadleafcommerce.core.order.dao.OrderItemDao;

import java.util.List;
import java.util.Map;

/**
 * @author jfischer
 */
public interface OrderOfferProcessor extends BaseProcessor {

    void filterOrderLevelOffer(PromotableOrder promotableOrder, List<PromotableCandidateOrderOffer> qualifiedOrderOffers, Offer offer);

    Boolean executeExpression(String expression, Map<String, Object> vars);

    /**
     * Executes the appliesToOrderRules in the Offer to determine if this offer
     * can be applied to the Order, OrderItem, or FulfillmentGroup.
     *
     * @param offer
     * @param promotableOrder
     * @return true if offer can be applied, otherwise false
     */
    boolean couldOfferApplyToOrder(Offer offer, PromotableOrder promotableOrder);

    List<PromotableCandidateOrderOffer> removeTrailingNotCombinableOrderOffers(List<PromotableCandidateOrderOffer> candidateOffers);

    /**
     * Takes a list of sorted CandidateOrderOffers and determines if each offer can be
     * applied based on the restrictions (stackable and/or combinable) on that offer.  OrderAdjustments
     * are create on the Order for each applied CandidateOrderOffer.  An offer with stackable equals false
     * cannot be applied to an Order that already contains an OrderAdjustment.  An offer with combinable
     * equals false cannot be applied to the Order if the Order already contains an OrderAdjustment.
     *
     * @param orderOffers     a sorted list of CandidateOrderOffer
     * @param promotableOrder the Order to apply the CandidateOrderOffers
     */
    void applyAllOrderOffers(List<PromotableCandidateOrderOffer> orderOffers, PromotableOrder promotableOrder);

    PromotableItemFactory getPromotableItemFactory();

    void setPromotableItemFactory(PromotableItemFactory promotableItemFactory);

    /**
     * Takes the adjustments and PriceDetails from the passed in PromotableOrder and transfers them to the
     * actual order first checking to see if they already exist.
     *
     * @param promotableOrder
     */
    void synchronizeAdjustmentsAndPrices(PromotableOrder promotableOrder);

    /**
     * Set the offerDao (primarily for unit testing)
     */
    void setOfferDao(OfferDao offerDao);

    /**
     * Set the orderItemDao (primarily for unit testing)
     */
    void setOrderItemDao(OrderItemDao orderItemDao);

}
