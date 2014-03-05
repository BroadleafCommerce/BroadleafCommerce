/*
 * Copyright 2008-2013 the original author or authors.
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

import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.order.dao.FulfillmentGroupItemDao;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupItemRequest;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

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
        Map<FulfillmentType, FulfillmentGroup> fulfillmentGroups = new HashMap<FulfillmentType, FulfillmentGroup>();
        FulfillmentGroup nullFulfillmentTypeGroup = null;
        
        //First, let's organize fulfillment groups according to their fulfillment type
        //We'll use the first of each type that we find. Implementors can choose to move groups / items around later.
        if (order.getFulfillmentGroups() != null) {
            for (FulfillmentGroup group : order.getFulfillmentGroups()) {
                if (group.getType() == null) {
                    if (nullFulfillmentTypeGroup == null) {
                        nullFulfillmentTypeGroup = group;
                    }
                } else {
                    if (fulfillmentGroups.get(group.getType()) == null) {
                        fulfillmentGroups.put(group.getType(), group);
                    }
                }
            }
        }
        
        if (orderItem instanceof BundleOrderItem) {
            //We only care about the discrete order items
            List<DiscreteOrderItem> itemsToAdd = new ArrayList<DiscreteOrderItem>(((BundleOrderItem) orderItem).getDiscreteOrderItems());
            for (DiscreteOrderItem doi : itemsToAdd) {
                FulfillmentGroup fulfillmentGroup = null;
                FulfillmentType type = resolveFulfillmentType(doi);
                if (type == null) {
                    //Use the fulfillment group with a null type
                    fulfillmentGroup = nullFulfillmentTypeGroup;
                } else {
                    if (FulfillmentType.PHYSICAL_PICKUP_OR_SHIP.equals(type)) {
                        //This is really a special case. "PICKUP_OR_SHIP" is convenient to allow a sku to be picked up or shipped.
                        //However, it is ambiguous when actually trying to create a fulfillment group. So we default to "PHYSICAL_SHIP".
                        type = FulfillmentType.PHYSICAL_SHIP;
                    }
                    
                    //Use the fulfillment group with the specified type
                    fulfillmentGroup = fulfillmentGroups.get(type);
                }
                
                //If the null type or specified type, above were null, then we need to create a new fulfillment group
                boolean createdFulfillmentGroup = false;
                if (fulfillmentGroup == null) {
                    fulfillmentGroup = fulfillmentGroupService.createEmptyFulfillmentGroup();
                    //Set the type
                    fulfillmentGroup.setType(type);
                    fulfillmentGroup.setOrder(order);
                    fulfillmentGroup = fulfillmentGroupService.save(fulfillmentGroup);
                    order.getFulfillmentGroups().add(fulfillmentGroup);
                    
                    createdFulfillmentGroup = true;
                }
                
                fulfillmentGroup = addItemToFulfillmentGroup(order, doi, doi.getQuantity() * orderItem.getQuantity(), fulfillmentGroup);
                order = fulfillmentGroup.getOrder();
                
                // If we had to create a new fulfillment group, then ensure that this will operate correctly for the next set
                // of fulfillment groups
                if (createdFulfillmentGroup) {
                    if (type == null) {
                        nullFulfillmentTypeGroup = fulfillmentGroup;
                    } else {
                        fulfillmentGroups.put(type, fulfillmentGroup);
                    }
                }
            }
        } else if (orderItem instanceof DiscreteOrderItem) {
            DiscreteOrderItem doi = (DiscreteOrderItem)orderItem;
            FulfillmentGroup fulfillmentGroup = null;
            FulfillmentType type = resolveFulfillmentType(doi);
            if (type == null) {
                //Use the fulfillment group with a null type
                fulfillmentGroup = nullFulfillmentTypeGroup;
            } else {
                if (FulfillmentType.PHYSICAL_PICKUP_OR_SHIP.equals(type)) {
                    //This is really a special case. "PICKUP_OR_SHIP" is convenient to allow a sku to be picked up or shipped.
                    //However, it is ambiguous when actually trying to create a fulfillment group. So we default to "PHYSICAL_SHIP".
                    type = FulfillmentType.PHYSICAL_SHIP;
                }
                
                //Use the fulfillment group with the specified type
                fulfillmentGroup = fulfillmentGroups.get(type);
            }
            
            //If the null type or specified type, above were null, then we need to create a new fulfillment group
            if (fulfillmentGroup == null) {
                fulfillmentGroup = fulfillmentGroupService.createEmptyFulfillmentGroup();
                //Set the type
                fulfillmentGroup.setType(type);
                fulfillmentGroup.setOrder(order);
                fulfillmentGroup = fulfillmentGroupService.save(fulfillmentGroup);
                order.getFulfillmentGroups().add(fulfillmentGroup);
            }
            
            fulfillmentGroup = addItemToFulfillmentGroup(order, orderItem, fulfillmentGroup);
            order = fulfillmentGroup.getOrder();
        } else {
            FulfillmentGroup fulfillmentGroup = nullFulfillmentTypeGroup;
            if (fulfillmentGroup == null) {
                fulfillmentGroup = fulfillmentGroupService.createEmptyFulfillmentGroup();
                fulfillmentGroup.setOrder(order);
                fulfillmentGroup = fulfillmentGroupService.save(fulfillmentGroup);
                order.getFulfillmentGroups().add(fulfillmentGroup);
            }
            
            fulfillmentGroup = addItemToFulfillmentGroup(order, orderItem, fulfillmentGroup);
            order = fulfillmentGroup.getOrder();
        }
        
        request.setOrder(order);
        return request;
    }
    
    /**
     * Resolves the fulfillment type based on the order item. The OOB implementation uses the {@link DiscreteOrderItem#getSku()}
     * to then invoke {@link #resolveFulfillmentType(Sku)}.
     * 
     * @param discreteOrderItem
     * @return
     */
    protected FulfillmentType resolveFulfillmentType(DiscreteOrderItem discreteOrderItem) {
        return resolveFulfillmentType(discreteOrderItem.getSku());
    }
    
    protected FulfillmentType resolveFulfillmentType(Sku sku) {
        if (sku.getFulfillmentType() != null) {
            return sku.getFulfillmentType();
        }
        if (sku.getDefaultProduct() != null && sku.getDefaultProduct().getDefaultCategory() != null) {
            return sku.getDefaultProduct().getDefaultCategory().getFulfillmentType();
        }
        return null;
    }
    
    protected FulfillmentGroup addItemToFulfillmentGroup(Order order, OrderItem orderItem, FulfillmentGroup fulfillmentGroup) throws PricingException {
        return this.addItemToFulfillmentGroup(order, orderItem, orderItem.getQuantity(), fulfillmentGroup);
    }

    protected FulfillmentGroup addItemToFulfillmentGroup(Order order, OrderItem orderItem, int quantity, FulfillmentGroup fulfillmentGroup) throws PricingException {
        FulfillmentGroupItemRequest fulfillmentGroupItemRequest = new FulfillmentGroupItemRequest();
        fulfillmentGroupItemRequest.setOrder(order);
        fulfillmentGroupItemRequest.setOrderItem(orderItem);
        fulfillmentGroupItemRequest.setQuantity(quantity);
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
                    int quantityPer = oi.getQuantity();
                    order = updateItemQuantity(order, oi, (quantityPer * orderItemQuantityDelta));
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
        List<OrderItem> expandedOrderItems = new ArrayList<OrderItem>();
        
        for (OrderItem oi : order.getOrderItems()) {
            if (oi instanceof BundleOrderItem) {
                for (DiscreteOrderItem doi : ((BundleOrderItem) oi).getDiscreteOrderItems()) {
                    expandedOrderItems.add(doi);
                }
            } else if (oi instanceof DiscreteOrderItem) {
                expandedOrderItems.add(oi);
            } else {
                expandedOrderItems.add(oi);
            }
        }
        
        for (OrderItem oi : expandedOrderItems) {
            Integer oiQuantity = oiQuantityMap.get(oi.getId());
            if (oiQuantity == null) {
                oiQuantity = 0;
            }
            
            if (oi instanceof DiscreteOrderItem && ((DiscreteOrderItem) oi).getBundleOrderItem() != null) {
                oiQuantity += ((DiscreteOrderItem) oi).getBundleOrderItem().getQuantity() * oi.getQuantity();
            } else {
                oiQuantity += oi.getQuantity();
            }
            
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
            if (!entry.getValue().equals(0)) {
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
