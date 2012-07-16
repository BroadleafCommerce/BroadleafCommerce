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

package org.broadleafcommerce.core.order.service;

import org.broadleafcommerce.core.order.dao.FulfillmentGroupDao;
import org.broadleafcommerce.core.order.dao.FulfillmentGroupItemDao;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderMultishipOption;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupItemRequest;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupRequest;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service("blFulfillmentGroupService")
public class FulfillmentGroupServiceImpl implements FulfillmentGroupService {

    @Resource(name="blFulfillmentGroupDao")
    protected FulfillmentGroupDao fulfillmentGroupDao;
    
    @Resource(name = "blFulfillmentGroupItemDao")
    protected FulfillmentGroupItemDao fulfillmentGroupItemDao;
    
    @Resource(name = "blOrderService")
    protected OrderService orderService;
    
    @Resource(name = "blOrderMultishipOptionService")
    protected OrderMultishipOptionService orderMultishipOptionService;

    public FulfillmentGroup save(FulfillmentGroup fulfillmentGroup) {
        return fulfillmentGroupDao.save(fulfillmentGroup);
    }

    public FulfillmentGroup createEmptyFulfillmentGroup() {
        return fulfillmentGroupDao.create();
    }

    public FulfillmentGroup findFulfillmentGroupById(Long fulfillmentGroupId) {
        return fulfillmentGroupDao.readFulfillmentGroupById(fulfillmentGroupId);
    }

    public void delete(FulfillmentGroup fulfillmentGroup) {
        fulfillmentGroupDao.delete(fulfillmentGroup);
    }

	@Override
	public FulfillmentGroup addFulfillmentGroupToOrder(FulfillmentGroupRequest fulfillmentGroupRequest, boolean priceOrder) throws PricingException {
        FulfillmentGroup fg = fulfillmentGroupDao.create();
        fg.setAddress(fulfillmentGroupRequest.getAddress());
        fg.setOrder(fulfillmentGroupRequest.getOrder());
        fg.setPhone(fulfillmentGroupRequest.getPhone());
        fg.setMethod(fulfillmentGroupRequest.getMethod());
        fg.setService(fulfillmentGroupRequest.getService());
        fg.setFulfillmentOption(fulfillmentGroupRequest.getFulfillmentOption());

        for (int i = 0; i < fulfillmentGroupRequest.getFulfillmentGroupItemRequests().size(); i++) {
            FulfillmentGroupItemRequest request = fulfillmentGroupRequest.getFulfillmentGroupItemRequests().get(i);
            request.setFulfillmentGroup(fg);
            request.setOrder(fulfillmentGroupRequest.getOrder());
            
            boolean shouldPriceOrder = (priceOrder && (i == (fulfillmentGroupRequest.getFulfillmentGroupItemRequests().size() - 1)));
            fg = addItemToFulfillmentGroup(request, shouldPriceOrder);
        }

        return fg;
	}

	@Override
	public FulfillmentGroup addItemToFulfillmentGroup(FulfillmentGroupItemRequest fulfillmentGroupItemRequest, boolean priceOrder) throws PricingException {
		Order order = fulfillmentGroupItemRequest.getOrder();
		OrderItem item = fulfillmentGroupItemRequest.getOrderItem();
		FulfillmentGroup fulfillmentGroup = fulfillmentGroupItemRequest.getFulfillmentGroup();
		
		if (order == null) {
			if (item.getOrder() != null) {
				order = item.getOrder();
			} else {
				throw new IllegalArgumentException("Order must not be null");
			}
		}
		
        // 1) Find the order item's existing fulfillment group, if any
        for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
            Iterator<FulfillmentGroupItem> itr = fg.getFulfillmentGroupItems().iterator();
            while (itr.hasNext()) {
                FulfillmentGroupItem fgItem = itr.next();
                if (fgItem.getOrderItem().equals(item)) {
                    // 2) remove item from it's existing fulfillment group
                    itr.remove();
                    fulfillmentGroupItemDao.delete(fgItem);
                }
            }
        }

        if (fulfillmentGroup == null || fulfillmentGroup.getId() == null) {
            // API user is trying to add an item to a fulfillment group not created
        	fulfillmentGroup = fulfillmentGroupDao.create();
        	FulfillmentGroupRequest fgRequest = new FulfillmentGroupRequest();
        	fgRequest.setOrder(order);
            fulfillmentGroup = addFulfillmentGroupToOrder(fgRequest, false);
            fulfillmentGroup = save(fulfillmentGroup);
            order.getFulfillmentGroups().add(fulfillmentGroup);
        }

        FulfillmentGroupItem fgi = createFulfillmentGroupItemFromOrderItem(item, fulfillmentGroup, fulfillmentGroupItemRequest.getQuantity());
        fgi = fulfillmentGroupItemDao.save(fgi);

        // 3) add the item to the new fulfillment group
        fulfillmentGroup.addFulfillmentGroupItem(fgi);
        order = orderService.save(order, priceOrder);

        return fulfillmentGroup;
	}
	
	@Override
    public void removeOrderItemFromFullfillmentGroups(Order order, OrderItem orderItem) {
        List<FulfillmentGroup> fulfillmentGroups = order.getFulfillmentGroups();
        for (FulfillmentGroup fulfillmentGroup : fulfillmentGroups) {
            Iterator<FulfillmentGroupItem> itr = fulfillmentGroup.getFulfillmentGroupItems().iterator();
            while (itr.hasNext()) {
                FulfillmentGroupItem fulfillmentGroupItem = itr.next();
                if (fulfillmentGroupItem.getOrderItem().equals(orderItem)) {
                    itr.remove();
                    fulfillmentGroupItemDao.delete(fulfillmentGroupItem);
                } else if (orderItem instanceof BundleOrderItem) {
                    BundleOrderItem bundleOrderItem = (BundleOrderItem) orderItem;
                    for (DiscreteOrderItem discreteOrderItem : bundleOrderItem.getDiscreteOrderItems()) {
                        if (fulfillmentGroupItem.getOrderItem().equals(discreteOrderItem)){
                            itr.remove();
                            fulfillmentGroupItemDao.delete(fulfillmentGroupItem);
                            break;
                        }
                    }
                }
            }
        }
    }
	
	@Override
	public Order splitIntoMultishipGroups(Order order, boolean priceOrder) throws PricingException {
		order = removeAllFulfillmentGroupsFromOrder(order, false);
		List<OrderMultishipOption> multishipOptions =  orderMultishipOptionService.findOrderMultishipOptions(order.getId());
		
		// This map is keyed by a String that follows the pattern "<address.id>:<fulfillmentOption.id>"
		// For example, a key could be "23:3"
		Map<String, FulfillmentGroup> multishipGroups = new HashMap<String, FulfillmentGroup>();
		
		for (OrderMultishipOption option : multishipOptions) {
			String key = option.getAddress().getId() + ":" + option.getFulfillmentOption().getId();
			
			FulfillmentGroup fg = multishipGroups.get(key);
			if (fg == null) {
				FulfillmentGroupRequest fgr = new FulfillmentGroupRequest();
				fgr.setOrder(order);
				fgr.setAddress(option.getAddress());
				fgr.setFulfillmentOption(option.getFulfillmentOption());
				fg = addFulfillmentGroupToOrder(fgr, false);
				fg = save(fg);
				order.getFulfillmentGroups().add(fg);
			}
			
			FulfillmentGroupItem fulfillmentGroupItem = null;
			for (FulfillmentGroupItem fgi : fg.getFulfillmentGroupItems()) {
				if (fgi.getOrderItem().getId() == option.getOrderItem().getId()) {
					fulfillmentGroupItem = fgi;
				}
			}
			
			if (fulfillmentGroupItem == null) {
		        fulfillmentGroupItem = fulfillmentGroupItemDao.create();
		        fulfillmentGroupItem.setFulfillmentGroup(fg);
		        fulfillmentGroupItem.setOrderItem(option.getOrderItem());
		        fulfillmentGroupItem.setQuantity(1);
		        fulfillmentGroupItem = fulfillmentGroupItemDao.save(fulfillmentGroupItem);
		        fg.getFulfillmentGroupItems().add(fulfillmentGroupItem);
			} else {
				fulfillmentGroupItem.setQuantity(fulfillmentGroupItem.getQuantity() + 1);
			}
			
			multishipGroups.put(key, fg);
		}
		
		return orderService.save(order, priceOrder);
	}
	
    protected FulfillmentGroupItem createFulfillmentGroupItemFromOrderItem(OrderItem orderItem, FulfillmentGroup fulfillmentGroup, int quantity) {
        FulfillmentGroupItem fgi = fulfillmentGroupItemDao.create();
        fgi.setFulfillmentGroup(fulfillmentGroup);
        fgi.setOrderItem(orderItem);
        fgi.setQuantity(quantity);
        return fgi;
    }

	@Override
	public Order removeAllFulfillmentGroupsFromOrder(Order order, boolean priceOrder) throws PricingException {
        if (order.getFulfillmentGroups() != null) {
            for (Iterator<FulfillmentGroup> iterator = order.getFulfillmentGroups().iterator(); iterator.hasNext();) {
                FulfillmentGroup fulfillmentGroup = iterator.next();
                iterator.remove();
                fulfillmentGroupDao.delete(fulfillmentGroup);
            }
            order = orderService.save(order, priceOrder);
        }
        return order;
	}
}
