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

package org.broadleafcommerce.core.order.service.workflow;

import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.core.order.dao.FulfillmentGroupItemDao;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.OrderMultishipOptionService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

/**
 * As of Broadleaf version 3.1.0, saves of individual aspects of an Order (such as OrderItems and FulfillmentGroupItems) no
 * longer happen in their respective activities. Instead, we will now handle these saves in this activity exclusively.
 * 
 * This provides the ability for an implementation to not require a transactional wrapper around the entire workflow and
 * instead only requires it around this particular activity. This is only recommended if there are long running steps in
 * the workflow, such as an external service call to check availability.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class PriceOrderIfNecessaryActivity extends BaseActivity<ProcessContext<CartOperationRequest>> {
    
    @Resource(name = "blOrderService")
    protected OrderService orderService;
    
    @Resource(name = "blOrderItemService")
    protected OrderItemService orderItemService;

    @Resource(name = "blFulfillmentGroupItemDao")
    protected FulfillmentGroupItemDao fgItemDao;

    @Resource(name = "blOrderMultishipOptionService")
    protected OrderMultishipOptionService orderMultishipOptionService;
    
    @Override
    public ProcessContext<CartOperationRequest> execute(ProcessContext<CartOperationRequest> context) throws Exception {
        CartOperationRequest request = context.getSeedData();
        Order order = request.getOrder();

        // If the UpdateOrderMultishipOptionActivity identified that we should delete order item multiship options,
        // go ahead and carry out that delete here.
        if (CollectionUtils.isNotEmpty(request.getMultishipOptionsToDelete())) {
            for (Long[] pack : request.getMultishipOptionsToDelete()) {
                if (pack[1] == null) {
                    orderMultishipOptionService.deleteOrderItemOrderMultishipOptions(pack[0]);
                } else {
                    orderMultishipOptionService.deleteOrderItemOrderMultishipOptions(pack[0], pack[1].intValue());
                }
            }
        }
        
        // We potentially have some FulfillmentGroupItems that were identified in the FulfillmentGroupItemStrategy as
        // ones that should be deleted. Delete them here.
        if (CollectionUtils.isNotEmpty(request.getFgisToDelete())) {
            for (FulfillmentGroupItem fgi : request.getFgisToDelete()) {
                for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
                    ListIterator<FulfillmentGroupItem> fgItemIter = fg.getFulfillmentGroupItems().listIterator();
                    while (fgItemIter.hasNext()) {
                        FulfillmentGroupItem fgi2 = fgItemIter.next();
                        if (fgi2 == fgi) {
                            fgItemIter.remove();
                            fgItemDao.delete(fgi2);
                        }
                    }
                }
            }
        }
        
        // We now need to delete any OrderItems that were marked as such, including their children, if any
        for (OrderItem oi : request.getOisToDelete()) {
            order.getOrderItems().remove(oi);
            orderItemService.delete(oi);
            
            if (oi.getParentOrderItem() != null) {
                OrderItem parentItem = oi.getParentOrderItem();
                parentItem.getChildOrderItems().remove(oi);
            }
        }
        
        
        // We need to build up a map of OrderItem to which FulfillmentGroupItems reference that particular OrderItem.
        // This is so we are able to update them appropriately on the FulfillmentGroupItem once the OrderItem is saved.
        Map<OrderItem, List<FulfillmentGroupItem>> oiFgiMap = new HashMap<OrderItem, List<FulfillmentGroupItem>>();
        for (OrderItem oi : order.getOrderItems()) {
            List<FulfillmentGroupItem> fgis = new ArrayList<FulfillmentGroupItem>();

            for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
                for (FulfillmentGroupItem fgi : fg.getFulfillmentGroupItems()) {
                    if (fgi.getOrderItem().equals(oi)) {
                        fgis.add(fgi);
                    }
                }
            }

            oiFgiMap.put(oi, fgis);
        }
        
        // Save the OrderItems in the Order and then update any FulfillmentGroupItems that referecne those OrderItems to
        // have the newly persisted version of the OrderItem.
        for (Entry<OrderItem, List<FulfillmentGroupItem>> entry : oiFgiMap.entrySet()) {
            order.getOrderItems().remove(entry.getKey());
            OrderItem savedOi = orderItemService.saveOrderItem(entry.getKey());
            order.getOrderItems().add(savedOi);
            
            if (entry.getKey() == request.getOrderItem()) {
                request.setOrderItem(savedOi);
            }

            for (FulfillmentGroupItem fgi : entry.getValue()) {
                fgi.setOrderItem(savedOi);
            }
        }
        
        // If a custom implementation needs to handle additional saves before the parent Order is saved, this method
        // can be overridden to provide that functionality.
        preSaveOperation(request);
        
        // Now that our collection items in our Order have been saved and the state of our Order is in a place where we
        // won't get a transient save exception, we are able to go ahead and save the order with optional pricing.
        order = orderService.save(order, request.isPriceOrder());
        request.setOrder(order);
        
        return context;
    }
    
    /**
     * Intended to be overridden by a custom implementation if there is a requirement to perform additional logic or
     * saves before triggering the main Order save with pricing.
     * 
     * @param request
     */
    protected void preSaveOperation(CartOperationRequest request) {
        // Broadleaf implementation does nothing here
    }

}
