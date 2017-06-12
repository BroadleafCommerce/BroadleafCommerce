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

import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.OrderMultishipOptionService;
import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

@Component("blUdateOrderMultishipOptionActivity")
public class UpdateOrderMultishipOptionActivity extends BaseActivity<ProcessContext<CartOperationRequest>> {
    
    public static final int ORDER = 4000;
    
    @Resource(name = "blOrderMultishipOptionService")
    protected OrderMultishipOptionService orderMultishipOptionService;

    @Resource(name = "blOrderItemService")
    protected OrderItemService orderItemService;
    
    public UpdateOrderMultishipOptionActivity() {
        setOrder(ORDER);
    }
    
    @Override
    public ProcessContext<CartOperationRequest> execute(ProcessContext<CartOperationRequest> context) throws Exception {
        CartOperationRequest request = context.getSeedData();
        Long orderItemId = request.getItemRequest().getOrderItemId();
        
        Integer orderItemQuantityDelta = request.getOrderItemQuantityDelta();
        if (orderItemQuantityDelta < 0) {
            int numToDelete = -1 * orderItemQuantityDelta;
            //find the qty in the default fg
            OrderItem orderItem = request.getOrderItem();
            int qty = 0;
            if (!CollectionUtils.isEmpty(orderItem.getOrder().getFulfillmentGroups())) {
                FulfillmentGroup fg = orderItem.getOrder().getFulfillmentGroups().get(0);
                if (fg.getAddress() == null && fg.getFulfillmentOption() == null) {
                    for (FulfillmentGroupItem fgItem : fg.getFulfillmentGroupItems()) {
                        if (fgItem.getOrderItem().getId().equals(orderItemId)) {
                            qty += fgItem.getQuantity();
                        }
                    }
                }
            }
            if (numToDelete >= qty) {
                request.getMultishipOptionsToDelete().add(new Long[] { orderItemId, (long) (numToDelete - qty) });
            }
        }
        
        return context;
    }

}
