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

package org.broadleafcommerce.core.order.service.workflow.update;

import org.broadleafcommerce.core.order.service.OrderMultishipOptionService;
import org.broadleafcommerce.core.order.service.workflow.CartOperationContext;
import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;

import javax.annotation.Resource;

public class UpdateOrderMultishipOptionActivity extends BaseActivity {
    
    @Resource(name = "blOrderMultishipOptionService")
    protected OrderMultishipOptionService orderMultishipOptionService;

    public ProcessContext execute(ProcessContext context) throws Exception {
        CartOperationRequest request = ((CartOperationContext) context).getSeedData();
        Long orderItemId = request.getItemRequest().getOrderItemId();
        
		Integer orderItemQuantityDelta = request.getOrderItemQuantityDelta();
		if (orderItemQuantityDelta < 0) {
			int numToDelete = -1 * orderItemQuantityDelta;
			orderMultishipOptionService.deleteOrderItemOrderMultishipOptions(orderItemId, numToDelete);
		}
        
        return context;
    }

}
