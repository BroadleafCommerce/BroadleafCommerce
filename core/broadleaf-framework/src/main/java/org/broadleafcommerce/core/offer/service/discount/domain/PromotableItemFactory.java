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

public interface PromotableItemFactory {

    PromotableOrder createPromotableOrder(Order order, boolean includeOrderAndItemAdjustments);

    PromotableCandidateOrderOffer createPromotableCandidateOrderOffer(PromotableOrder promotableOrder, Offer offer);

    PromotableCandidateOrderOffer createPromotableCandidateOrderOffer(PromotableOrder promotableOrder,
            Offer offer, Money potentialSavings);

    PromotableOrderAdjustment createPromotableOrderAdjustment(
            PromotableCandidateOrderOffer promotableCandidateOrderOffer,
            PromotableOrder order);

    PromotableOrderAdjustment createPromotableOrderAdjustment(
            PromotableCandidateOrderOffer promotableCandidateOrderOffer,
            PromotableOrder order, Money value);

    PromotableOrderItem createPromotableOrderItem(OrderItem orderItem, PromotableOrder order,
            boolean includeAdjustments);

    PromotableOrderItemPriceDetail createPromotableOrderItemPriceDetail(PromotableOrderItem promotableOrderItem,
            int quantity);

    PromotableCandidateItemOffer createPromotableCandidateItemOffer(PromotableOrder promotableOrder, Offer offer);

    PromotableOrderItemPriceDetailAdjustment createPromotableOrderItemPriceDetailAdjustment(
            PromotableCandidateItemOffer promotableCandidateItemOffer,
            PromotableOrderItemPriceDetail promotableOrderItemPriceDetail);

    PromotableFulfillmentGroup createPromotableFulfillmentGroup(FulfillmentGroup fulfillmentGroup, PromotableOrder order);

    PromotableCandidateFulfillmentGroupOffer createPromotableCandidateFulfillmentGroupOffer(
            PromotableFulfillmentGroup fulfillmentGroup,
            Offer offer);
    
    PromotableFulfillmentGroupAdjustment createPromotableFulfillmentGroupAdjustment(
            PromotableCandidateFulfillmentGroupOffer promotableCandidateFulfillmentGroupOffer,
            PromotableFulfillmentGroup fulfillmentGroup);
}