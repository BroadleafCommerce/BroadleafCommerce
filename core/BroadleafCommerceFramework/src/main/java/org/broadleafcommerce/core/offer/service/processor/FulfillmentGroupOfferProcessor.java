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
package org.broadleafcommerce.core.offer.service.processor;

import java.util.List;

import org.broadleafcommerce.core.offer.domain.CandidateFulfillmentGroupOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;

/**
 * 
 * @author jfischer
 *
 */
public interface FulfillmentGroupOfferProcessor extends OrderOfferProcessor {

	public void filterFulfillmentGroupLevelOffer(Order order, List<CandidateFulfillmentGroupOffer> qualifiedFGOffers, List<DiscreteOrderItem> discreteOrderItems, Offer offer);

	public void calculateFulfillmentGroupTotal(Order order);
	
	public boolean applyAllFulfillmentGroupOffers(List<CandidateFulfillmentGroupOffer> qualifiedFGOffers, Order order);
	
}