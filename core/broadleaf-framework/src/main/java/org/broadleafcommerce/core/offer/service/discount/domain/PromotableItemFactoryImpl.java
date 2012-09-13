/*
 * Copyright 2008-2012 the original author or authors.
 *
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
import org.springframework.stereotype.Service;

@Service("blPromotableItemFactory")
public class PromotableItemFactoryImpl implements PromotableItemFactory {
	
	public PromotableOrder createPromotableOrder(Order order) {
		return new PromotableOrderImpl(order, this);
	}
	
	public PromotableCandidateOrderOffer createPromotableCandidateOrderOffer(CandidateOrderOffer candidateOrderOffer, PromotableOrder order) {
		return new PromotableCandidateOrderOfferImpl(candidateOrderOffer, order);
	}
	
	public PromotableOrderAdjustment createPromotableOrderAdjustment(OrderAdjustment orderAdjustment, PromotableOrder order) {
		return new PromotableOrderAdjustmentImpl(orderAdjustment, order);
	}

	public PromotableOrderItem createPromotableOrderItem(DiscreteOrderItem orderItem, PromotableOrder order) {
		return new PromotableOrderItemImpl(orderItem, order, this);
	}
	
	public PromotableCandidateItemOffer createPromotableCandidateItemOffer(CandidateItemOffer candidateItemOffer) {
		return new PromotableCandidateItemOfferImpl(candidateItemOffer);
	}
	
	public PromotableOrderItemAdjustment createPromotableOrderItemAdjustment(OrderItemAdjustment orderItemAdjustment, PromotableOrderItem orderItem) {
		return new PromotableOrderItemAdjustmentImpl(orderItemAdjustment, orderItem);
	}
	
	public PromotableFulfillmentGroup createPromotableFulfillmentGroup(FulfillmentGroup fulfillmentGroup, PromotableOrder order) {
		return new PromotableFulfillmentGroupImpl(fulfillmentGroup, order, this);
	}
	
	public PromotableCandidateFulfillmentGroupOffer createPromotableCandidateFulfillmentGroupOffer(CandidateFulfillmentGroupOffer candidateFulfillmentGroupOffer, PromotableFulfillmentGroup fulfillmentGroup) {
		return new PromotableCandidateFulfillmentGroupOfferImpl(candidateFulfillmentGroupOffer, fulfillmentGroup);
	}
	
	public PromotableFulfillmentGroupAdjustment createPromotableFulfillmentGroupAdjustment(FulfillmentGroupAdjustment fulfillmentGroupAdjustment, PromotableFulfillmentGroup fulfillmentGroup) {
		return new PromotableFulfillmentGroupAdjustmentImpl(fulfillmentGroupAdjustment, fulfillmentGroup);
	}
}
