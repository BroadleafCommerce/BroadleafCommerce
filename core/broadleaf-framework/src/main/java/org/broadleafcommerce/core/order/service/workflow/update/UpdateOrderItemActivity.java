/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.order.service.workflow.update;

import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.exception.ItemNotFoundException;
import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("blUpdateOrderItemActivity")
public class UpdateOrderItemActivity extends BaseActivity<ProcessContext<CartOperationRequest>> {

    public static final int ORDER = 3000;
    
    @Resource(name = "blOrderService")
    protected OrderService orderService;
    
    public UpdateOrderItemActivity() {
        setOrder(ORDER);
    }
    
    @Override
    public ProcessContext<CartOperationRequest> execute(ProcessContext<CartOperationRequest> context) throws Exception {
        CartOperationRequest request = context.getSeedData();
        OrderItemRequestDTO orderItemRequestDTO = request.getItemRequest();
        Order order = request.getOrder();
        
        OrderItem orderItem = null;
        for (OrderItem oi : order.getOrderItems()) {
            if (oi.getId().equals(orderItemRequestDTO.getOrderItemId())) {
                orderItem = oi;
            }
        }
        
        if (orderItem == null || !order.getOrderItems().contains(orderItem)) {
            throw new ItemNotFoundException("Order Item (" + orderItemRequestDTO.getOrderItemId() + ") not found in Order (" + order.getId() + ")");
        }
        
        OrderItem itemFromOrder = order.getOrderItems().get(order.getOrderItems().indexOf(orderItem));
        if (orderItemRequestDTO.getQuantity() >= 0) {
            int previousQty = itemFromOrder.getQuantity();
            request.setOrderItemQuantityDelta(orderItemRequestDTO.getQuantity() - itemFromOrder.getQuantity());
            itemFromOrder.setQuantity(orderItemRequestDTO.getQuantity());

            for (OrderItem child : itemFromOrder.getChildOrderItems()) {
                int childQuantity = child.getQuantity();
                childQuantity = childQuantity / previousQty;
                child.setQuantity(childQuantity * orderItemRequestDTO.getQuantity());
            }

            // Update any additional attributes of the passed in request
            if (itemFromOrder instanceof DiscreteOrderItem) {
                DiscreteOrderItem discreteOrderItem = (DiscreteOrderItem) itemFromOrder;
                discreteOrderItem.getAdditionalAttributes().putAll(orderItemRequestDTO.getAdditionalAttributes());
            }

            request.setOrderItem(itemFromOrder);
        }

        return context;
    }

}
