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

package org.broadleafcommerce.core.offer.service.discount.domain;

import org.broadleafcommerce.core.offer.domain.CandidateFulfillmentGroupOffer;
import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.core.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.core.offer.domain.FulfillmentGroupAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;

public interface PromotableItemFactory {

    public PromotableOrder createPromotableOrder(Order order);

    public PromotableCandidateOrderOffer createPromotableCandidateOrderOffer(CandidateOrderOffer candidateOrderOffer, PromotableOrder order);

    public PromotableOrderAdjustment createPromotableOrderAdjustment(OrderAdjustment orderAdjustment, PromotableOrder order);

    public PromotableOrderItem createPromotableOrderItem(DiscreteOrderItem orderItem,PromotableOrder order);

    public PromotableCandidateItemOffer createPromotableCandidateItemOffer(CandidateItemOffer candidateItemOffer);

    public PromotableOrderItemAdjustment createPromotableOrderItemAdjustment(OrderItemAdjustment orderItemAdjustment,PromotableOrderItem orderItem);

    public PromotableFulfillmentGroup createPromotableFulfillmentGroup(FulfillmentGroup fulfillmentGroup, PromotableOrder order);

    public PromotableCandidateFulfillmentGroupOffer createPromotableCandidateFulfillmentGroupOffer(CandidateFulfillmentGroupOffer candidateFulfillmentGroupOffer, PromotableFulfillmentGroup fulfillmentGroup);
    
    public PromotableFulfillmentGroupAdjustment createPromotableFulfillmentGroupAdjustment(FulfillmentGroupAdjustment fulfillmentGroupAdjustment, PromotableFulfillmentGroup fulfillmentGroup);

}