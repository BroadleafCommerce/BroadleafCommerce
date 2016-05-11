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
package org.broadleafcommerce.core.offer.service.discount.domain;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.springframework.stereotype.Service;

@Service("blPromotableItemFactory")
public class PromotableItemFactoryImpl implements PromotableItemFactory {

    public PromotableOrder createPromotableOrder(Order order, boolean includeOrderAndItemAdjustments) {
        return new PromotableOrderImpl(order, this, includeOrderAndItemAdjustments);
    }

    @Override
    public PromotableCandidateOrderOffer createPromotableCandidateOrderOffer(PromotableOrder promotableOrder, Offer offer) {
        return new PromotableCandidateOrderOfferImpl(promotableOrder, offer);
    }
    
    @Override
    public PromotableCandidateOrderOffer createPromotableCandidateOrderOffer(PromotableOrder promotableOrder,
            Offer offer, Money potentialSavings) {
        return new PromotableCandidateOrderOfferImpl(promotableOrder, offer, potentialSavings);
    }

    @Override
    public PromotableOrderAdjustment createPromotableOrderAdjustment(
            PromotableCandidateOrderOffer promotableCandidateOrderOffer, PromotableOrder order) {
        return new PromotableOrderAdjustmentImpl(promotableCandidateOrderOffer, order);
    }
    
    @Override
    public PromotableOrderAdjustment createPromotableOrderAdjustment(
            PromotableCandidateOrderOffer promotableCandidateOrderOffer,
            PromotableOrder order, Money adjustmentValue) {
        return new PromotableOrderAdjustmentImpl(promotableCandidateOrderOffer, order, adjustmentValue);
    }

    @Override
    public PromotableOrderItem createPromotableOrderItem(OrderItem orderItem, PromotableOrder order,
            boolean includeAdjustments) {
        return new PromotableOrderItemImpl(orderItem, order, this, includeAdjustments);
    }
    
    @Override
    public PromotableOrderItemPriceDetail createPromotableOrderItemPriceDetail(PromotableOrderItem promotableOrderItem,
            int quantity) {
        return new PromotableOrderItemPriceDetailImpl(promotableOrderItem, quantity);
    }

    @Override
    public PromotableCandidateItemOffer createPromotableCandidateItemOffer(PromotableOrder promotableOrder, Offer offer) {
        return new PromotableCandidateItemOfferImpl(promotableOrder, offer);
    }
    
    @Override
    public PromotableOrderItemPriceDetailAdjustment createPromotableOrderItemPriceDetailAdjustment(
            PromotableCandidateItemOffer promotableCandidateItemOffer,
            PromotableOrderItemPriceDetail orderItemPriceDetail) {
        return new PromotableOrderItemPriceDetailAdjustmentImpl(promotableCandidateItemOffer, orderItemPriceDetail);
    }
    
    @Override
    public PromotableFulfillmentGroup createPromotableFulfillmentGroup(
            FulfillmentGroup fulfillmentGroup,
            PromotableOrder order) {
        return new PromotableFulfillmentGroupImpl(fulfillmentGroup, order, this);
    }
    
    @Override
    public PromotableCandidateFulfillmentGroupOffer createPromotableCandidateFulfillmentGroupOffer(
            PromotableFulfillmentGroup fulfillmentGroup, Offer offer) {
        return new PromotableCandidateFulfillmentGroupOfferImpl(fulfillmentGroup, offer);
    }
    
    @Override
    public PromotableFulfillmentGroupAdjustment createPromotableFulfillmentGroupAdjustment(
            PromotableCandidateFulfillmentGroupOffer promotableCandidateFulfillmentGroupOffer,
            PromotableFulfillmentGroup fulfillmentGroup) {
        return new PromotableFulfillmentGroupAdjustmentImpl(promotableCandidateFulfillmentGroupOffer, fulfillmentGroup);
    }
}
