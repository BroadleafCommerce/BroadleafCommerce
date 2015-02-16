/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
 * %%
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
 * #L%
 */
package org.broadleafcommerce.core.spec.order.service.workflow.add

import org.broadleafcommerce.core.catalog.service.CatalogService
import org.broadleafcommerce.core.order.service.OrderItemService
import org.broadleafcommerce.core.order.service.OrderService
import org.broadleafcommerce.core.order.service.ProductOptionValidationService
import org.broadleafcommerce.core.order.service.workflow.add.ValidateAddRequestActivity

/*
 * Tests:
 * 1) Quantity = null -> context.stopProcess() occurs
 * 2) Quantity = 0 -> context.stopProcess() occurs
 * 3) Quantity < 0 -> IllegalArgumentException
 * 4) request.getOrder() = null -> IllegalArgumentException
 * 5) ProductId != null, catalog.getProductById = null -> IllegalArgumentException
 * 6) sku == null && orderItemRequestDTO not NonDiscrete -> IllegalArgumentException
 * 7) sku == null && orderItemRequestDTO is NonDiscrete && itemName isBlank -> IllegalArgumentException
 * 8) sku == null && orderItemRequestDTO is NonDiscrete && retail&sale price null -> IllegalArgumentException
 * 9) !sku.isActive() -> IllegalArgumentException
 * 10) pass all above -> itemRequest.skuId set to sku.getId()
 * 11) orderItemRequestDTO not NonDiscrete && getOrder.getCurrency != null && sku.getCurrency != null && getOrder.currency!=sku.currency -> IllegalArgumentException
 * 12) parentOrderItemId != null && orderItemService.readOrderItemById == null -> IllegalArgumentException
 * 13) all pass
 * 
 */

class ValidateAddRequestActivitySpec extends BaseAddItemActivitySpec {
    
    
    
    OrderService mockOrderService = Mock()
    OrderItemService mockOrderItemService = Mock()
    CatalogService mockCatalogService = Mock()
    ProductOptionValidationService mockProductOptionValidationService = Mock()
    
    def setup() {
        activity = new ValidateAddRequestActivity().with {
            orderService = mockOrderService
            orderItemService = mockOrderItemService
            catalogService = mockCatalogService
            productOptionValidationService = mockProductOptionValidationService
            it
        }
        
    }
    
    
}
