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
package org.broadleafcommerce.core.order.service.workflow.update.options;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.call.DiscreteOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("blUpdateProductOptionsOrderItemActivity")
public class UpdateProductOptionsOrderItemActivity extends BaseActivity<ProcessContext<CartOperationRequest>> {
    
    public static final int ORDER = 2000;
    
    @Resource(name = "blOrderService")
    protected OrderService orderService;

    @Resource(name = "blOrderItemService")
    protected OrderItemService orderItemService;
    
    public UpdateProductOptionsOrderItemActivity() {
        setOrder(ORDER);
    }

    @Override
    public ProcessContext<CartOperationRequest> execute(ProcessContext<CartOperationRequest> context) throws Exception {
        CartOperationRequest request = context.getSeedData();
        OrderItemRequestDTO orderItemRequestDTO = request.getItemRequest();
        Order order = request.getOrder();
        
        if (orderItemService.readOrderItemById(Long.valueOf(orderItemRequestDTO.getOrderItemId())) != null) {
            DiscreteOrderItemRequest itemRequest = new DiscreteOrderItemRequest();
            itemRequest.setItemAttributes(orderItemRequestDTO.getItemAttributes());
            orderItemService.updateDiscreteOrderItem(orderItemService.readOrderItemById(Long.valueOf(orderItemRequestDTO.getOrderItemId())), itemRequest);

        }

        order = orderService.save(order, false);
        request.setOrder(order);

        return context;
    }

}
