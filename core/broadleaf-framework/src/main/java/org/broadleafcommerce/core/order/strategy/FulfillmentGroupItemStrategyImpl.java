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
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
		
		if (orderItem instanceof BundleOrderItem) {
		    List<OrderItem> itemsToAdd = new ArrayList<OrderItem>(((BundleOrderItem) orderItem).getDiscreteOrderItems());
		    for (OrderItem oi : itemsToAdd) {
		        fulfillmentGroup = addItemToFulfillmentGroup(order, oi, fulfillmentGroup);
		    }
		} else {
		    fulfillmentGroup = addItemToFulfillmentGroup(order, orderItem, fulfillmentGroup);
		}
		
		order = fulfillmentGroup.getOrder();
		
		request.setOrder(order);
		return request;
	}
	
	protected FulfillmentGroup addItemToFulfillmentGroup(Order order, OrderItem orderItem, FulfillmentGroup fulfillmentGroup) throws PricingException {
		FulfillmentGroupItemRequest fulfillmentGroupItemRequest = new FulfillmentGroupItemRequest();
		fulfillmentGroupItemRequest.setOrder(order);
		fulfillmentGroupItemRequest.setOrderItem(orderItem);
		fulfillmentGroupItemRequest.setQuantity(orderItem.getQuantity());
		fulfillmentGroupItemRequest.setFulfillmentGroup(fulfillmentGroup);
		return fulfillmentGroupService.addItemToFulfillmentGroup(fulfillmentGroupItemRequest, false);
	}
	
	@Override
	public CartOperationRequest onItemUpdated(CartOperationRequest request) throws PricingException {
		Order order = request.getOrder();
		OrderItem orderItem = request.getAddedOrderItem();
		Integer orderItemQuantityDelta = request.getOrderItemQuantityDelta();
		
		if (orderItemQuantityDelta == 0) {
			// If the quantity didn't change, nothing needs to happen
			return request;
		} else {
    		if (orderItem instanceof BundleOrderItem) {
    		    List<OrderItem> itemsToUpdate = new ArrayList<OrderItem>(((BundleOrderItem) orderItem).getDiscreteOrderItems());
    		    for (OrderItem oi : itemsToUpdate) {
    		        int qtyMultiplier = oi.getQuantity() / orderItem.getQuantity();
    		        order = updateItemQuantity(order, oi, (qtyMultiplier * orderItemQuantityDelta));
    		    }
    		} else {
    		    order = updateItemQuantity(order, orderItem, orderItemQuantityDelta);
    		}
		}
		
		request.setOrder(order);
		return request;
	}
		
	protected Order updateItemQuantity(Order order, OrderItem orderItem, Integer orderItemQuantityDelta) throws PricingException {
	    boolean done = false;
	    
		if (orderItemQuantityDelta > 0) {
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
		
		order = orderService.save(order, false);
		return order;
	}

	@Override
	public CartOperationRequest onItemRemoved(CartOperationRequest request) {
		Order order = request.getOrder();
        OrderItem orderItem = orderItemService.readOrderItemById(request.getItemRequest().getOrderItemId());
        
		if (orderItem instanceof BundleOrderItem) {
		    List<OrderItem> itemsToRemove = new ArrayList<OrderItem>(((BundleOrderItem) orderItem).getDiscreteOrderItems());
		    for (OrderItem oi : itemsToRemove) {
		        fulfillmentGroupService.removeOrderItemFromFullfillmentGroups(order, oi);
		    }
		} else {
		    fulfillmentGroupService.removeOrderItemFromFullfillmentGroups(order, orderItem);
		}
        
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
        
        Map<Long, Integer> oiQuantityMap = new HashMap<Long, Integer>();
        for (OrderItem oi : order.getDiscreteOrderItems()) {
        	Integer oiQuantity = oiQuantityMap.get(oi.getId());
        	if (oiQuantity == null) {
        		oiQuantity = 0;
        	}
        	oiQuantity += oi.getQuantity();
        	oiQuantityMap.put(oi.getId(), oiQuantity);
        }
        
        for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
        	for (FulfillmentGroupItem fgi : fg.getFulfillmentGroupItems()) {
    			Long oiId = fgi.getOrderItem().getId();
    			Integer oiQuantity = oiQuantityMap.get(oiId);
    			
    			if (oiQuantity == null) {
    				throw new IllegalStateException("Fulfillment group items and discrete order items are not in sync. DiscreteOrderItem id: " + oiId);
    			}
    			
    			oiQuantity -= fgi.getQuantity();
    			oiQuantityMap.put(oiId, oiQuantity);
        	}
        }
        
        for (Entry<Long, Integer> entry : oiQuantityMap.entrySet()) {
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
