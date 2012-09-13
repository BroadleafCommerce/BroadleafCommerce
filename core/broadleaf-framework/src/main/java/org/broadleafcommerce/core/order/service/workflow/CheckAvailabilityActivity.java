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

package org.broadleafcommerce.core.order.service.workflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.exception.InventoryUnavailableException;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;

import javax.annotation.Resource;

public class CheckAvailabilityActivity extends BaseActivity {
    private static Log LOG = LogFactory.getLog(CheckAvailabilityActivity.class);
    
    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;
    
    @Resource(name = "blOrderItemService")
    protected OrderItemService orderItemService;

    public ProcessContext execute(ProcessContext context) throws Exception {
        CartOperationRequest request = ((CartOperationContext) context).getSeedData();
        
        Sku sku = null;
        if (request.getItemRequest().getSkuId() != null) {
        	sku = catalogService.findSkuById(request.getItemRequest().getSkuId());
        } else { 
        	OrderItem orderItem = orderItemService.readOrderItemById(request.getItemRequest().getOrderItemId());
        	if (orderItem instanceof DiscreteOrderItem) {
        		sku = ((DiscreteOrderItem) orderItem).getSku();
        		request.getItemRequest().setSkuId(sku.getId());
        	}
        }
        
        if (sku == null || !sku.isActive()) {
        	throw new InventoryUnavailableException("The requested SKU is no longer active");
        }
        
        return context;
    }

}
