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
package org.broadleafcommerce.core.offer.service.processor;

import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateItemOffer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateOrderOffer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrder;

import java.util.List;

/**
 * 
 * @author jfischer
 *
 */
public interface ItemOfferProcessor extends OrderOfferProcessor {

    /**
     * Review an item level offer against the list of discountable items from the order. If the
     * offer applies, add it to the qualifiedItemOffers list.
     * 
     * @param order the BLC order
     * @param qualifiedItemOffers the container list for any qualified offers
     * @param discreteOrderItems the order items to evaluate
     * @param offer the offer in question
     */
    public void filterItemLevelOffer(PromotableOrder order, List<PromotableCandidateItemOffer> qualifiedItemOffers, Offer offer);

    /**
     * Private method that takes a list of sorted CandidateItemOffers and determines if each offer can be
     * applied based on the restrictions (stackable and/or combinable) on that offer.  OrderItemAdjustments
     * are create on the OrderItem for each applied CandidateItemOffer.  An offer with stackable equals false
     * cannot be applied to an OrderItem that already contains an OrderItemAdjustment.  An offer with combinable
     * equals false cannot be applied to an OrderItem if that OrderItem already contains an
     * OrderItemAdjustment, unless the offer is the same offer as the OrderItemAdjustment offer.
     *
     * @param itemOffers a sorted list of CandidateItemOffer
     */
    public void applyAllItemOffers(List<PromotableCandidateItemOffer> itemOffers, PromotableOrder order);
    
    public void applyAndCompareOrderAndItemOffers(PromotableOrder order, List<PromotableCandidateOrderOffer> qualifiedOrderOffers, List<PromotableCandidateItemOffer> qualifiedItemOffers);
    
    public void filterOffers(PromotableOrder order, List<Offer> filteredOffers, List<PromotableCandidateOrderOffer> qualifiedOrderOffers, List<PromotableCandidateItemOffer> qualifiedItemOffers);

}
