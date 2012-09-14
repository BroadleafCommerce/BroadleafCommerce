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

package org.broadleafcommerce.core.order.service.workflow.remove;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.workflow.CartOperationContext;
import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;

import javax.annotation.Resource;

/**
 * This class is responsible for removing OrderItems when requested by the OrderService.
 * Note that this class does not touch the FulfillmentGroupItems and expects that they 
 * have already been remoevd by the time this activity executes. 
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class RemoveOrderItemActivity extends BaseActivity {
    private static Log LOG = LogFactory.getLog(RemoveOrderItemActivity.class);
    
    @Resource(name = "blOrderService")
    protected OrderService orderService;
    
    @Resource(name = "blOrderItemService")
    protected OrderItemService orderItemService;
    
    public ProcessContext execute(ProcessContext context) throws Exception {
        CartOperationRequest request = ((CartOperationContext) context).getSeedData();
        OrderItemRequestDTO orderItemRequestDTO = request.getItemRequest();

        // Find the OrderItem from the database based on its ID
		Order order = request.getOrder();
        OrderItem orderItem = orderItemService.readOrderItemById(orderItemRequestDTO.getOrderItemId());
        
        // Remove the OrderItem from the Order
        OrderItem itemFromOrder = order.getOrderItems().remove(order.getOrderItems().indexOf(orderItem));
        
        // Delete the OrderItem from the database
        itemFromOrder.setOrder(null);
        orderItemService.delete(itemFromOrder);
        
        order = orderService.save(order, false);
        
        request.setOrder(order);
        return context;
    }

}
