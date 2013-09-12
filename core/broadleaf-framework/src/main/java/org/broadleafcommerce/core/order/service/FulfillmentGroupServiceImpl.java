/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
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
import org.broadleafcommerce.core.order.domain.FulfillmentGroupFee;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderMultishipOption;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupItemRequest;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupRequest;
import org.broadleafcommerce.core.order.service.type.FulfillmentGroupStatusType;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.core.domain.Address;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

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

    @Override
    @Transactional("blTransactionManager")
    public FulfillmentGroup save(FulfillmentGroup fulfillmentGroup) {
        if (fulfillmentGroup.getSequence() == null) {
            fulfillmentGroup.setSequence(
                    fulfillmentGroupDao.readNextFulfillmentGroupSequnceForOrder(
                            fulfillmentGroup.getOrder()));
        }

        return fulfillmentGroupDao.save(fulfillmentGroup);
    }

    @Override
    public FulfillmentGroup createEmptyFulfillmentGroup() {
        return fulfillmentGroupDao.create();
    }

    @Override
    public FulfillmentGroup findFulfillmentGroupById(Long fulfillmentGroupId) {
        return fulfillmentGroupDao.readFulfillmentGroupById(fulfillmentGroupId);
    }

    @Override
    @Transactional("blTransactionManager")
    public void delete(FulfillmentGroup fulfillmentGroup) {
        fulfillmentGroupDao.delete(fulfillmentGroup);
    }

    @Override
    @Transactional("blTransactionManager")
    public FulfillmentGroup addFulfillmentGroupToOrder(FulfillmentGroupRequest fulfillmentGroupRequest, boolean priceOrder) throws PricingException {
        FulfillmentGroup fg = fulfillmentGroupDao.create();
        fg.setAddress(fulfillmentGroupRequest.getAddress());
        fg.setOrder(fulfillmentGroupRequest.getOrder());
        fg.setPhone(fulfillmentGroupRequest.getPhone());
        fg.setFulfillmentOption(fulfillmentGroupRequest.getOption());
        fg.setType(fulfillmentGroupRequest.getFulfillmentType());

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
    @Transactional("blTransactionManager")
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
    @Transactional("blTransactionManager")
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
    @Transactional("blTransactionManager")
    public Order collapseToOneShippableFulfillmentGroup(Order order, boolean priceOrder) throws PricingException {
        if (order.getFulfillmentGroups() == null || order.getFulfillmentGroups().size() < 2) {
            return order;
        }
        
        List<FulfillmentGroup> shippableFulfillmentGroupList =  new ArrayList<FulfillmentGroup>();
        List<FulfillmentGroup> nonShippableFulfillmentGroupList = new ArrayList<FulfillmentGroup>();
        for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            if(isShippable(fulfillmentGroup.getType())) {
                shippableFulfillmentGroupList.add(fulfillmentGroup);
            } else {
                nonShippableFulfillmentGroupList.add(fulfillmentGroup);
            }
        }
        if (shippableFulfillmentGroupList.size() < 2) {
            return order;
        }

        // Get the default (first) shippable fulfillment group to collapse the others into
        ListIterator<FulfillmentGroup> fgIter = shippableFulfillmentGroupList.listIterator();
        FulfillmentGroup collapsedFg = fgIter.next();
        
        List<FulfillmentGroup> newFulfillmentGroupList = nonShippableFulfillmentGroupList;
        newFulfillmentGroupList.add(collapsedFg);
        order.setFulfillmentGroups(newFulfillmentGroupList);

        // Build out a map representing the default shippable fgs items keyed by OrderItem id
        Map<Long, FulfillmentGroupItem> fgOrderItemMap = new HashMap<Long, FulfillmentGroupItem>();
        for (FulfillmentGroupItem fgi : collapsedFg.getFulfillmentGroupItems()) {
            fgOrderItemMap.put(fgi.getOrderItem().getId(), fgi);
        }
        
        // For all non default shippable fgs, collapse the items into the default shippable fg
        while (fgIter.hasNext()) {
            FulfillmentGroup fg = fgIter.next();
            ListIterator<FulfillmentGroupItem> fgItemIter = fg.getFulfillmentGroupItems().listIterator();
            while (fgItemIter.hasNext()) {
                FulfillmentGroupItem fgi = fgItemIter.next();
                
                Long orderItemId = fgi.getOrderItem().getId();
                FulfillmentGroupItem matchingFgi = fgOrderItemMap.get(orderItemId);
                
                if (matchingFgi == null) {
                    matchingFgi = fulfillmentGroupItemDao.create();
                    matchingFgi.setFulfillmentGroup(collapsedFg);
                    matchingFgi.setOrderItem(fgi.getOrderItem());
                    matchingFgi.setQuantity(fgi.getQuantity());
                    matchingFgi = fulfillmentGroupItemDao.save(matchingFgi);
                    collapsedFg.getFulfillmentGroupItems().add(matchingFgi);
                    fgOrderItemMap.put(orderItemId, matchingFgi);
                } else {
                    matchingFgi.setQuantity(matchingFgi.getQuantity() + fgi.getQuantity());
                }
                
                fulfillmentGroupItemDao.delete(fgi);
                fgItemIter.remove();
            }
            fulfillmentGroupDao.delete(fg);
            fgIter.remove();
        }

        
        return orderService.save(order, priceOrder);
    }
    
    @Override
    @Transactional("blTransactionManager")
    public Order matchFulfillmentGroupsToMultishipOptions(Order order, boolean priceOrder) throws PricingException {
        List<OrderMultishipOption> multishipOptions =  orderMultishipOptionService.findOrderMultishipOptions(order.getId());
        
        // Build map of fulfillmentGroupItemId --> FulfillmentGroupItem.quantity
        // Also build map of addressId:fulfillmentOptionId --> FulfillmentGroup
        Map<Long, Integer> fgItemQuantityMap = new HashMap<Long, Integer>();
        Map<String, FulfillmentGroup> multishipGroups = new HashMap<String, FulfillmentGroup>();
        for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
            if (!isShippable(fg.getType())) {
                continue;
            }
            String key = getKey(fg.getAddress(), fg.getFulfillmentOption(), fg.getType());
            multishipGroups.put(key, fg);
            
            for (FulfillmentGroupItem fgi : fg.getFulfillmentGroupItems()) {
                fgItemQuantityMap.put(fgi.getId(), fgi.getQuantity());
            }
        }
        
        for (OrderMultishipOption option : multishipOptions) {
            String key = getKey(option.getAddress(), option.getFulfillmentOption(), ((DiscreteOrderItem) option.getOrderItem()).getSku().getFulfillmentType());
            FulfillmentGroup fg = multishipGroups.get(key);
            
            // Get or create a fulfillment group that matches this OrderMultishipOption destination
            if (fg == null) {
                FulfillmentGroupRequest fgr = new FulfillmentGroupRequest();
                
                fgr.setOrder(order);
                
                if (option.getAddress() != null) {
                    fgr.setAddress(option.getAddress());
                }
                
                if (option.getFulfillmentOption() != null) {
                    fgr.setOption(option.getFulfillmentOption());
                }
                
                fgr.setFulfillmentType(((DiscreteOrderItem) option.getOrderItem()).getSku().getFulfillmentType());

                fg = addFulfillmentGroupToOrder(fgr, false);
                fg = save(fg);
                order.getFulfillmentGroups().add(fg);
            }
            
            // See if there is a fulfillment group item that matches this OrderMultishipOption
            // OrderItem request
            FulfillmentGroupItem fulfillmentGroupItem = null;
            for (FulfillmentGroupItem fgi : fg.getFulfillmentGroupItems()) {
                if (fgi.getOrderItem().getId() == option.getOrderItem().getId()) {
                    fulfillmentGroupItem = fgi;
                }
            }
            
            // If there is no matching fulfillment group item, create a new one with quantity 1
            if (fulfillmentGroupItem == null) {
                fulfillmentGroupItem = fulfillmentGroupItemDao.create();
                fulfillmentGroupItem.setFulfillmentGroup(fg);
                fulfillmentGroupItem.setOrderItem(option.getOrderItem());
                fulfillmentGroupItem.setQuantity(1);
                fulfillmentGroupItem = fulfillmentGroupItemDao.save(fulfillmentGroupItem);
                fg.getFulfillmentGroupItems().add(fulfillmentGroupItem);
            } else {
                // There are three potential scenarios where a fulfillment group item exists:
                //   1: It has been previously created and exists in the database and
                //      has an id. This means it's in the fgItemQuantityMap. If there is 
                //      remaining quantity in that map, we will decrement it for future
                //      usage. If the quantity is 0 in the map, that means that we have more
                //      items than we did before, and we must simply increment the quantity.
                //      (qty == 0 or qty is not null)
                //   2: It was created in this request but has been saved to the database because
                //      it is a brand new fulfillment group and so it has an id. 
                //      However, it does not have an entry in the fgItemQuantityMap,
                //      so we can simply increment the quantity.
                //      (qty == null)
                //   3: It was created in this request and has not yet been saved to the database.
                //      This is because it was a previously existing fulfillment group that has new
                //      items. Therefore, we simply increment the quantity.
                //      (fulfillmentGroupItem.getId() == null)
                if (fulfillmentGroupItem.getId() != null) {
                    Integer qty = fgItemQuantityMap.get(fulfillmentGroupItem.getId());
                    if (qty == null || qty == 0) {
                        fulfillmentGroupItem.setQuantity(fulfillmentGroupItem.getQuantity() + 1);
                    } else {
                        qty -= 1;
                        fgItemQuantityMap.put(fulfillmentGroupItem.getId(), qty);
                    }
                } else {
                    fulfillmentGroupItem.setQuantity(fulfillmentGroupItem.getQuantity() + 1);
                }
            }
            
            multishipGroups.put(key, fg);
        }
        
        // Go through all of the items in the fgItemQuantityMap. For all items that have a
        // zero quantity, we don't need to do anything because we've already matched them
        // to the newly requested OrderMultishipOption. For items that have a non-zero quantity,
        // there are two possible scenarios:
        //   1: The quantity remaining matches exactly the quantity of a fulfillmentGroupItem.
        //      In this case, we can simply remove the fulfillmentGroupItem.
        //   2: The quantity in the map is greater than what we've found. This means that we
        //      need to subtract the remaining old quantity from the new quantity.
        // Furthermore, delete the empty fulfillment groups.
        for (Entry<Long, Integer> entry : fgItemQuantityMap.entrySet()) {
            if (entry.getValue() > 0) {
                FulfillmentGroupItem fgi = fulfillmentGroupItemDao.readFulfillmentGroupItemById(entry.getKey());
                if (fgi.getQuantity() == entry.getValue()) {
                    FulfillmentGroup fg = fgi.getFulfillmentGroup();
                    fg.getFulfillmentGroupItems().remove(fgi);
                    fulfillmentGroupItemDao.delete(fgi);
                    if (fg.getFulfillmentGroupItems().size() == 0) {
                        order.getFulfillmentGroups().remove(fg);
                        fulfillmentGroupDao.delete(fg);
                    }
                } else { 
                    fgi.setQuantity(fgi.getQuantity() - entry.getValue());
                    fulfillmentGroupItemDao.save(fgi);
                }
            }
        }

        return orderService.save(order, priceOrder);
    }
    
    protected String getKey(Address address, FulfillmentOption option, FulfillmentType fulfillmentType) {
        Long addressKey = (address == null) ? -1 : address.getId();
        Long fulfillmentOptionKey = (option == null) ? -1 : option.getId();
        String fulfillmentTypeKey = (fulfillmentType == null) ? "-1" : fulfillmentType.getType();
        return addressKey + ":" + fulfillmentOptionKey + ":" + fulfillmentTypeKey;
    }
    
    protected FulfillmentGroupItem createFulfillmentGroupItemFromOrderItem(OrderItem orderItem, FulfillmentGroup fulfillmentGroup, int quantity) {
        FulfillmentGroupItem fgi = fulfillmentGroupItemDao.create();
        fgi.setFulfillmentGroup(fulfillmentGroup);
        fgi.setOrderItem(orderItem);
        fgi.setQuantity(quantity);
        return fgi;
    }

    @Override
    @Transactional("blTransactionManager")
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

    @Override
    public FulfillmentGroupFee createFulfillmentGroupFee() {
        return fulfillmentGroupDao.createFulfillmentGroupFee();
    }
    
    @Override
    public List<FulfillmentGroup> findUnfulfilledFulfillmentGroups(int start,
            int maxResults) {
        return fulfillmentGroupDao.readUnfulfilledFulfillmentGroups(start, maxResults);
    }

    @Override
    public List<FulfillmentGroup> findPartiallyFulfilledFulfillmentGroups(
            int start, int maxResults) {
        return fulfillmentGroupDao.readPartiallyFulfilledFulfillmentGroups(start, maxResults);
    }

    @Override
    public List<FulfillmentGroup> findUnprocessedFulfillmentGroups(int start,
            int maxResults) {
        return fulfillmentGroupDao.readUnprocessedFulfillmentGroups(start, maxResults);
    }

    @Override
    public List<FulfillmentGroup> findFulfillmentGroupsByStatus(
            FulfillmentGroupStatusType status, int start, int maxResults,
            boolean ascending) {
        return fulfillmentGroupDao.readFulfillmentGroupsByStatus(status, start, maxResults, ascending);
    }

    @Override
    public List<FulfillmentGroup> findFulfillmentGroupsByStatus(
            FulfillmentGroupStatusType status, int start, int maxResults) {
        return fulfillmentGroupDao.readFulfillmentGroupsByStatus(status, start, maxResults);
    }

    @Override
    public boolean isShippable(FulfillmentType fulfillmentType) {
        if (fulfillmentType.GIFT_CARD.equals(fulfillmentType) || fulfillmentType.DIGITAL.equals(fulfillmentType) || fulfillmentType.PHYSICAL_PICKUP.equals(fulfillmentType)) {
            return false;
        }
        return true;
    }

    /**
     * This method will get the first shippable fulfillment group from an order.
     *
     * @param order
     */
    @Override
    public FulfillmentGroup getFirstShippableFulfillmentGroup(Order order) {
        List<FulfillmentGroup> fulfillmentGroups = order.getFulfillmentGroups();
        if (fulfillmentGroups != null) {
            for (FulfillmentGroup fulfillmentGroup : fulfillmentGroups) {
                if (isShippable(fulfillmentGroup.getType())) {
                    return fulfillmentGroup;
                }
            }
        }
        return null;
    }

}
