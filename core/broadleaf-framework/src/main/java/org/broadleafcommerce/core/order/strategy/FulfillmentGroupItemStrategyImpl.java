/*
 * Copyright 2012 the original author or authors.
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

package org.broadleafcommerce.core.order.strategy;

import org.broadleafcommerce.core.order.dao.FulfillmentGroupItemDao;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupItemRequest;
import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Andre Azzolini (apazzolini)
 */
@Service("blFulfillmentGroupItemStrategy")
public class FulfillmentGroupItemStrategyImpl implements FulfillmentGroupItemStrategy {
	
    @Resource(name = "blFulfillmentGroupService")
    protected FulfillmentGroupService fulfillmentGroupService;
    
    @Resource(name = "blOrderItemService")
    protected OrderItemService orderItemService;
    
    @Resource(name = "blOrderService")
    protected OrderService orderService;
    
    @Resource(name = "blFulfillmentGroupItemDao")
    protected FulfillmentGroupItemDao fgItemDao;
    
    protected boolean removeEmptyFulfillmentGroups = true;
	
	@Override
	public CartOperationRequest onItemAdded(CartOperationRequest request) throws PricingException {
		Order order = request.getOrder();
		OrderItem orderItem = request.getAddedOrderItem();
		
		FulfillmentGroup fulfillmentGroup = null;
		if (order.getFulfillmentGroups().size() == 0) {
			fulfillmentGroup = fulfillmentGroupService.createEmptyFulfillmentGroup();
			fulfillmentGroup.setOrder(order);
			fulfillmentGroup = fulfillmentGroupService.save(fulfillmentGroup);
			order.getFulfillmentGroups().add(fulfillmentGroup);
		} else {
			fulfillmentGroup = order.getFulfillmentGroups().get(0);
		}
		
		FulfillmentGroupItemRequest fulfillmentGroupItemRequest = new FulfillmentGroupItemRequest();
		fulfillmentGroupItemRequest.setOrder(order);
		fulfillmentGroupItemRequest.setOrderItem(orderItem);
		fulfillmentGroupItemRequest.setQuantity(orderItem.getQuantity());
		fulfillmentGroupItemRequest.setFulfillmentGroup(fulfillmentGroup);
		
		fulfillmentGroup = fulfillmentGroupService.addItemToFulfillmentGroup(fulfillmentGroupItemRequest, request.isPriceOrder());
		order =  fulfillmentGroup.getOrder();
		
		request.setOrder(order);
		return request;
	}
	
	@Override
	public CartOperationRequest onItemUpdated(CartOperationRequest request) throws PricingException {
		Order order = request.getOrder();
		OrderItem orderItem = request.getAddedOrderItem();
		Integer orderItemQuantityDelta = request.getOrderItemQuantityDelta();
		boolean done = false;
		
		if (orderItemQuantityDelta == 0) {
			// If the quantity didn't change, nothing needs to happen
			return request;
		} else if (orderItemQuantityDelta > 0) {
			// If the quantity is now greater, we can simply add quantity to the first
			// fulfillment group we find that has that order item in it. 
			for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
				for (FulfillmentGroupItem fgItem : fg.getFulfillmentGroupItems()) {
					if (!done && fgItem.getOrderItem().equals(orderItem)) {
						fgItem.setQuantity(fgItem.getQuantity() + orderItemQuantityDelta);
						done = true;
					}
				}
			}
		} else {
			// The quantity has been decremented. We must ensure that the appropriate number
			// of fulfillment group items are decremented as well.
			int remainingToDecrement = -1 * orderItemQuantityDelta;
			
			for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
				ListIterator<FulfillmentGroupItem> fgItemIter = fg.getFulfillmentGroupItems().listIterator();
				while (fgItemIter.hasNext()) {
					FulfillmentGroupItem fgItem = fgItemIter.next();
					if (fgItem.getOrderItem().equals(orderItem)) {
						if (!done &&fgItem.getQuantity() == remainingToDecrement) {
							// Quantity matches exactly. Simply remove the item.
							fgItemIter.remove();
							fgItemDao.delete(fgItem);
							done = true;
						} else if (!done && fgItem.getQuantity() > remainingToDecrement) {
							// We have enough quantity in this fg item to facilitate the entire requsted update
							fgItem.setQuantity(fgItem.getQuantity() - remainingToDecrement);
							done = true;
						} else if (!done) {
							// We do not have enough quantity. We'll remove this item and continue searching
							// for the remainder.
							remainingToDecrement = remainingToDecrement - fgItem.getQuantity();
							fgItemIter.remove();
							fgItemDao.delete(fgItem);
						}
					}
				}
			}
		}
		
		if (!done) {
			throw new IllegalStateException("Could not find matching fulfillment group item for the given order item");
		}
		
		order = orderService.save(order, request.isPriceOrder());
		request.setOrder(order);
		return request;
	}

	@Override
	public CartOperationRequest onItemRemoved(CartOperationRequest request) {
		Order order = request.getOrder();
        OrderItem orderItem = orderItemService.readOrderItemById(request.getItemRequest().getOrderItemId());
        
        fulfillmentGroupService.removeOrderItemFromFullfillmentGroups(order, orderItem);
        
        
        return request;
	}
	
	@Override
	public CartOperationRequest verify(CartOperationRequest request) throws PricingException {
		Order order = request.getOrder();
		
        if (isRemoveEmptyFulfillmentGroups() && order.getFulfillmentGroups() != null) {
        	ListIterator<FulfillmentGroup> fgIter = order.getFulfillmentGroups().listIterator();
        	while (fgIter.hasNext()) {
        		FulfillmentGroup fg = fgIter.next();
	        	if (fg.getFulfillmentGroupItems() == null || fg.getFulfillmentGroupItems().size() == 0) {
	        		fgIter.remove();
	        		fulfillmentGroupService.delete(fg);
	        	}
	        }
        }
        
        Map<Long, Integer> doiQuantityMap = new HashMap<Long, Integer>();
        for (DiscreteOrderItem doi : order.getDiscreteOrderItems()) {
        	Integer doiQuantity = doiQuantityMap.get(doi.getId());
        	if (doiQuantity == null) {
        		doiQuantity = 0;
        	}
        	doiQuantity += doi.getQuantity();
        	doiQuantityMap.put(doi.getId(), doiQuantity);
        }
        
        for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
        	for (FulfillmentGroupItem fgi : fg.getFulfillmentGroupItems()) {
        		if (fgi.getOrderItem() instanceof DiscreteOrderItem) {
        			Long doiId = fgi.getOrderItem().getId();
        			Integer doiQuantity = doiQuantityMap.get(doiId);
        			
        			if (doiQuantity == null) {
        				throw new IllegalStateException("Fulfillment group items and discrete order items are not in sync. DiscreteOrderItem id: " + doiId);
        			}
        			
        			doiQuantity -= fgi.getQuantity();
        			doiQuantityMap.put(doiId, doiQuantity);
        		}
        	}
        }
        
        for (Entry<Long, Integer> entry : doiQuantityMap.entrySet()) {
        	if (entry.getValue() != 0) {
        		throw new IllegalStateException("Not enough fulfillment group items found for DiscreteOrderItem id: " + entry.getKey());
        	}
        }
        
		return request;
	}

	@Override
	public boolean isRemoveEmptyFulfillmentGroups() {
		return removeEmptyFulfillmentGroups;
	}

	@Override
	public void setRemoveEmptyFulfillmentGroups(boolean removeEmptyFulfillmentGroups) {
		this.removeEmptyFulfillmentGroups = removeEmptyFulfillmentGroups;
	}

	
}
