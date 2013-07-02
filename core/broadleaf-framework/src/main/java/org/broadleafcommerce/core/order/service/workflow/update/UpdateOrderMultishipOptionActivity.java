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

package org.broadleafcommerce.core.order.service.workflow.update;

import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.OrderMultishipOptionService;
import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

public class UpdateOrderMultishipOptionActivity extends BaseActivity<ProcessContext<CartOperationRequest>> {
    
    @Resource(name = "blOrderMultishipOptionService")
    protected OrderMultishipOptionService orderMultishipOptionService;

    @Resource(name = "blOrderItemService")
    protected OrderItemService orderItemService;

    @Override
    public ProcessContext<CartOperationRequest> execute(ProcessContext<CartOperationRequest> context) throws Exception {
        CartOperationRequest request = context.getSeedData();
        Long orderItemId = request.getItemRequest().getOrderItemId();
        
        Integer orderItemQuantityDelta = request.getOrderItemQuantityDelta();
        if (orderItemQuantityDelta < 0) {
            int numToDelete = -1 * orderItemQuantityDelta;
            //find the qty in the default fg
            OrderItem orderItem = orderItemService.readOrderItemById(orderItemId);
            int qty = 0;
            if (!CollectionUtils.isEmpty(orderItem.getOrder().getFulfillmentGroups())) {
                FulfillmentGroup fg = orderItem.getOrder().getFulfillmentGroups().get(0);
                if (fg.getAddress() == null && fg.getFulfillmentOption() == null) {
                    for (FulfillmentGroupItem fgItem : fg.getFulfillmentGroupItems()) {
                        if (fgItem.getOrderItem().getId() == orderItemId) {
                            qty += fgItem.getQuantity();
                        }
                    }
                }
            }
            if (numToDelete >= qty) {
                orderMultishipOptionService.deleteOrderItemOrderMultishipOptions(orderItemId, numToDelete - qty);
            }
        }
        
        return context;
    }

}
