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
import java.util.Map;

import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.order.dao.FulfillmentGroupItemDao;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.CartService;
import org.broadleafcommerce.core.order.service.OrderItemService;

/**
 * 
 * @author jfischer
 *
 */
public interface OrderOfferProcessor extends BaseProcessor {

	public void filterOrderLevelOffer(Order order, List<CandidateOrderOffer> qualifiedOrderOffers, List<DiscreteOrderItem> discreteOrderItems, Offer offer);

	public OfferDao getOfferDao();

	public void setOfferDao(OfferDao offerDao);
	
	public Boolean executeExpression(String expression, Map<String, Object> vars);
	
	public boolean couldOfferApplyToOrder(Offer offer, Order order);
	
	public List<CandidateOrderOffer> removeTrailingNotCombinableOrderOffers(List<CandidateOrderOffer> candidateOffers);
	
	public boolean applyAllOrderOffers(List<CandidateOrderOffer> orderOffers, Order order);
	
	public void compileOrderTotal(Order order);
	
	public List<OrderItem> getAllSplitItems(Order order);
    
    public void initializeSplitItems(Order order, List<OrderItem> items);
	
    public CartService getCartService();

	public void setCartService(CartService cartService);
	
	public void gatherCart(Order order);
	
	public OrderItemService getOrderItemService();

	public void setOrderItemService(OrderItemService orderItemService);

	public FulfillmentGroupItemDao getFulfillmentGroupItemDao();

	public void setFulfillmentGroupItemDao(FulfillmentGroupItemDao fulfillmentGroupItemDao);
	
}