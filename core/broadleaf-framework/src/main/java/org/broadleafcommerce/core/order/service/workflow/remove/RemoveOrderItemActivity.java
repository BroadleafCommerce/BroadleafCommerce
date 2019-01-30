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
package org.broadleafcommerce.core.order.service.workflow.remove;

import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.stereotype.Component;

import java.util.List;

import javax.annotation.Resource;

/**
 * This class is responsible for determining which OrderItems should be removed from the order, taking into account
 * the fact that removing an OrderItem should also remove all of its child order items.
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Component("blRemoveOrderItemActivity")
public class RemoveOrderItemActivity extends BaseActivity<ProcessContext<CartOperationRequest>> {

    public static final int ORDER = 4000;
    
    @Resource(name = "blOrderService")
    protected OrderService orderService;
    
    @Resource(name = "blOrderItemService")
    protected OrderItemService orderItemService;
    
    public RemoveOrderItemActivity() {
        setOrder(ORDER);
    }
    
    @Override
    public ProcessContext<CartOperationRequest> execute(ProcessContext<CartOperationRequest> context) throws Exception {
        CartOperationRequest request = context.getSeedData();

        OrderItem orderItem = request.getOrderItem();
        removeItemAndChildren(request.getOisToDelete(), orderItem);
        
        return context;
    }
    
    protected void removeItemAndChildren(List<OrderItem> oisToDelete, OrderItem orderItem) {
        if (CollectionUtils.isNotEmpty(orderItem.getChildOrderItems())) {
            for (OrderItem childOrderItem : orderItem.getChildOrderItems()) {
                removeItemAndChildren(oisToDelete, childOrderItem);
            }
        }
        
        oisToDelete.add(orderItem);
    }

}
