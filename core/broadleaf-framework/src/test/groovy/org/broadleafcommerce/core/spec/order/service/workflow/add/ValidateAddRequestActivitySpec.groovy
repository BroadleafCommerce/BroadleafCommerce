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

import org.broadleafcommerce.common.currency.domain.BroadleafCurrencyImpl
import org.broadleafcommerce.core.catalog.domain.SkuImpl
import org.broadleafcommerce.core.catalog.service.CatalogService
import org.broadleafcommerce.core.order.service.OrderItemService
import org.broadleafcommerce.core.order.service.OrderService
import org.broadleafcommerce.core.order.service.ProductOptionValidationService
import org.broadleafcommerce.core.order.service.call.NonDiscreteOrderItemRequestDTO
import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest
import org.broadleafcommerce.core.order.service.workflow.add.ValidateAddRequestActivity


/*
 * execute:
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
 */

class ValidateAddRequestActivitySpec extends BaseAddItemActivitySpec {
    
    
    
    OrderService mockOrderService = Mock()
    OrderItemService mockOrderItemService = Mock()
    CatalogService mockCatalogService = Mock()
    ProductOptionValidationService mockProductOptionValidationService = Mock()
    
    def setup() {
        activity = Spy(ValidateAddRequestActivity).with {
            orderService = mockOrderService
            orderItemService = mockOrderItemService
            catalogService = mockCatalogService
            productOptionValidationService = mockProductOptionValidationService
            it
        }
    }
    
    
    
    /*
     * Quantity = null -> context.stopProcess() occurs
     */
    def "If a null quantity is given, the process is stopped"(){
        setup: "Setup the quantity to be null"
        context.seedData.itemRequest.setQuantity(null)
        
        when: "The activity is executed"
        context = activity.execute(context)
        
        then: "The process is stopped"
        context.isStopped() == true
        
        
    }
    
    /*
     * Quantity = 0 -> IllegalArgumentException
     */
    def "If a zero quantity is given, the process is stopped"(){
        setup: "Setup the quantity to be 0"
        context.seedData.itemRequest.setQuantity(0)
        
        when: "The activity is executed"
        context = activity.execute(context)
        
        then: "The process is stopped"
        context.isStopped() == true
    }
    
    /*
     * Quantity < 0 -> context.stopProcess() occurs
     */
    def "If a negative quantity is given, an IllegalArgumentException is thrown"() {
        setup: "Setup the quantity to be negative"
        context.seedData.itemRequest.setQuantity(-1)
        
        when: "The activity is executed"
        context = activity.execute(context)
        
        then: "An IllegalArgumentException is thrown"
        IllegalArgumentException e = thrown()
        e.message == "Quantity cannot be negative"
    }
    
    /*
     * request.getOrder() = null -> IllegalArgumentException
     */
    def "If there is no order given with a request, an IllegalArgumentException is thrown"(){
        setup: "Setup the order to be null"
        context.seedData.setOrder(null)
        
        when: "The activity is executed"
        context = activity.execute(context)
        
        then: "An IllegalArgumentException is thrown"
        IllegalArgumentException e = thrown()
        e.message == "Order is required when adding item to order"
        
    }
    
    /*
     * ProductId != null, catalog.getProductById = null -> IllegalArgumentException
     */
    def "If productId is not null, and there is a null value for that productId in the catalog, then an IllegalArgumentException is thrown"(){
        setup: "Setup the productId to be a number"
        context.seedData.itemRequest.setProductId(1)
        
        when: "The activity is executed"
        context = activity.execute(context)
        
        then: "An IllegalArgumentException is thrown"
        1 * activity.catalogService.findProductById(_) >> null
        IllegalArgumentException e = thrown()
        e.message == "Product was specified but no matching product was found for productId 1"
        
    }
    
    /*
     * sku == null && orderItemRequestDTO not NonDiscrete -> IllegalArgumentException
     */
    def "If we cannot find a sku, and itemRequest is not NonDiscrete, we throw an IllegalArgumentException"(){
        setup: "Setup the itemRequest to not be nondiscrete and productId is null"
        context.seedData.itemRequest.setProductId(null)
        
        when: "The activity is executed"
        context = activity.execute(context)
        
        then: "An IllegalArgumentException is thrown"
        IllegalArgumentException e = thrown()
    }
    
    /*
     * sku == null && orderItemRequestDTO is NonDiscrete && itemName isBlank -> IllegalArgumentException
     */
    def "If we cannot find a sku, and itemRequest is NonDiscrete, and itemName is blank, we throw an IllegalArgumentException"(){
        setup: "Setup the itemRequest to be nondiscrete and productId is null"
        context.seedData.itemRequest = Mock(NonDiscreteOrderItemRequestDTO)
        context.seedData.itemRequest.getQuantity() >> 1
        context.seedData.itemRequest.getProductId() >> null
        context.seedData.itemRequest.getItemName() >> "  "
        
        
        when: "The activity is executed"
        context = activity.execute(context)
        
        then: "An IllegalArgumentException is thrown"
        IllegalArgumentException e = thrown()
        e.message == "Item name is required for non discrete order item add requests"
    }
    
    /*
     * sku == null && orderItemRequestDTO is NonDiscrete && retail&sale price null -> IllegalArgumentException
     */
    def "If we cannot find a sku, and itemRequest is NonDiscrete, and the retail/sale price is null, we throw an IllegalArgumentException"(){
        setup: "Setup the itemRequest to be nondiscrete and productId is null and retail/sale price is null"
        context.seedData.itemRequest = Mock(NonDiscreteOrderItemRequestDTO)
        context.seedData.itemRequest.getQuantity() >> 1
        context.seedData.itemRequest.getProductId() >> null
        context.seedData.itemRequest.getItemName() >> "valid"
        context.seedData.itemRequest.getOverrideRetailPrice() >> null
        context.seedData.itemRequest.getOverrideSalePrice() >> null
        
        
        when: "The activity is executed"
        context = activity.execute(context)
        
        then: "An IllegalArgumentException is thrown"
        IllegalArgumentException e = thrown()
        e.message == "At least one override price is required for non discrete order item add requests"
    }
    
    /*
     * !sku.isActive() -> IllegalArgumentException
     */
    def "If the sku found is not active, we throw an IllegalArgumentException"(){
        setup: "Setup the sku to be inactive"
        context.seedData.itemRequest.setProductId(null)
        context.seedData.itemRequest.setQuantity(1)
        SkuImpl testSku = Mock(SkuImpl)
        activity.determineSku(*_) >> testSku
        testSku.isActive() >> false
        
        when:
        context = activity.execute(context)
        
        then: "IllegalArgumentException is thrown"
        IllegalArgumentException e = thrown()
    }
    
    /*
     * pass all above -> itemRequest.skuId set to sku.getId()
     */
    def "If the sku found is active, we set the itemRequest skuId to sku.getId()"(){
        setup: "Setup the sku to be active"
        context.seedData.itemRequest.setProductId(null)
        context.seedData.itemRequest.setQuantity(1)
        SkuImpl testSku = Mock(SkuImpl)
        activity.determineSku(*_) >> testSku
        testSku.isActive() >> true
        testSku.getId() >> 2
        
        when:
        context = activity.execute(context)
        
        then: "The itemRequest skuId is set to 2"
        context.seedData.itemRequest.skuId == 2
    }
    
    /*
     * orderItemRequestDTO not NonDiscrete && getOrder.getCurrency != null && sku.getCurrency != null && getOrder.currency!=sku.currency -> IllegalArgumentException
     */
    def "If the itemRequest is not NonDiscrete, the order has a non-null currency, the sku has a non-null currency, and the two currency values are not the same, throw an IllegalArgumentException"(){
        setup: "setup the itemRequest to not be NonDiscrete, the order and sku to have non-null and non-equal currency"
        context.seedData.itemRequest.setProductId(null)
        context.seedData.itemRequest.setQuantity(1)
        
        BroadleafCurrencyImpl testCurrency1 = Mock(BroadleafCurrencyImpl)
        testCurrency1.currencyCode = "one"
        context.seedData.getOrder().setCurrency(testCurrency1)
        
        
        SkuImpl testSku = Mock(SkuImpl)
        activity.determineSku(*_) >> testSku
        testSku.isActive() >> true
        testSku.getId() >> 2
        BroadleafCurrencyImpl testCurrency2 = Mock(BroadleafCurrencyImpl)
        testCurrency2.currencyCode = "two"
        testSku.getCurrency() >> testCurrency2
        
        
        when:
        context = activity.execute(context)
        
        then: "An IllegalArgumentException is thrown"
        IllegalArgumentException e = thrown()
        
    }
    
    /*
     * parentOrderItemId != null && orderItemService.readOrderItemById == null -> IllegalArgumentException
     */
    def "If parentOrderItemId is not null and it does not exist in the orderItemService, throw an IllegalArgumentException"(){
        setup: "setup the parentOrderItemId to be null"
        context.seedData.itemRequest.setProductId(null)
        context.seedData.itemRequest.setQuantity(1)
        
        SkuImpl testSku = Mock(SkuImpl)
        activity.determineSku(*_) >> testSku
        testSku.isActive() >> true
        testSku.getId() >> 2
        
        context.seedData.itemRequest.parentOrderItemId = 1
        activity.orderItemService.readOrderItemById(_) >> null
               
        when:
        context = activity.execute(context)
        
        then: "An IllegalArgumentException is thrown"
        IllegalArgumentException e = thrown()
    }
    
    
    
}
