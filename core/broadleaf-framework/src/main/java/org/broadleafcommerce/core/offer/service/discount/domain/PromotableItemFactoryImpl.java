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
