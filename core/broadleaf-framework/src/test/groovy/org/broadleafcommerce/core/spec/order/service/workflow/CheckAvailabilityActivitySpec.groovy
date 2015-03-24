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
package org.broadleafcommerce.core.spec.order.service.workflow

import org.broadleafcommerce.core.catalog.domain.SkuImpl
import org.broadleafcommerce.core.catalog.service.CatalogService
import org.broadleafcommerce.core.inventory.service.ContextualInventoryService
import org.broadleafcommerce.core.inventory.service.InventoryUnavailableException
import org.broadleafcommerce.core.inventory.service.type.InventoryType
import org.broadleafcommerce.core.order.domain.BundleOrderItemImpl
import org.broadleafcommerce.core.order.domain.DiscreteOrderItemImpl
import org.broadleafcommerce.core.order.domain.OrderItemImpl
import org.broadleafcommerce.core.order.service.OrderItemService
import org.broadleafcommerce.core.order.service.workflow.CheckAvailabilityActivity



/*
 * 1a) orderItemId != null
 *      a) orderItem instanceOf DiscreteOrderItem
 *          * sku is set using DiscreteOrderItem.getSku() CONTINUE
 *      b) orderItem instanceOf BundleOrderItem 
 *          * sku is set using BundleOrderItem.getSku() CONTINUE
 *      c) LOG.warn() issued and return context EXIT
 * 1b) orderItemId == null
 *      a) sku set using catalogService.findSkuById(_) CONTINUE
 * 
 * 2) !sku.isAvailable()
 *      * throw InventoryUnavailableException EXIT
 * 
 * 3) sku.inventoryType equals InventoryType.CHECK_QUANTITY
 *      a) inventoryService is used to find if it is available
 *          i) !available
 *              * throw InventoryUnavailableException EXIT
 *          ii) available
 *              * nothing happens CONTINUE
 *              
 * 4) return context
 */
class CheckAvailabilityActivitySpec extends BaseOrderWorkflowSpec {

    CatalogService mockCatalogService = Mock()
    OrderItemService mockOrderItemService = Mock()
    ContextualInventoryService mockInventoryService = Mock()
    
    
    /*
     * 1) mock for Services, and Sku
     * 2) setup activity
     */
    def setup(){
        activity = Spy(CheckAvailabilityActivity).with {
            catalogService = mockCatalogService
            orderItemService = mockOrderItemService
            inventoryService = mockInventoryService
            it
        }
        
        
    }
    
    def "If the order item id is non-null, and there is a DiscreteOrderItem, then a sku from that DiscreteOrderItem will be tested for availability"(){
        
        setup: "setup a discrete order item and non-null orderitemId"
        DiscreteOrderItemImpl mockOrderItem = Spy(DiscreteOrderItemImpl)
        SkuImpl mockSku = Spy(SkuImpl)
        mockSku.getId() >> 1
        
        context.seedData.itemRequest.getQuantity() >> 0
        context.seedData.itemRequest.getOrderItemId() >> 1
        mockOrderItemService.readOrderItemById(_) >> mockOrderItem
        
        when: "the activity is executed"
        context = activity.execute(context);
        
        then: "that sku is checked for availability"
        1 * mockOrderItem.getSku() >> mockSku
        1 * mockSku.isAvailable() >> true
    }
    
    def "If the order item id is non-null, and there is a BundleOrderItem, then a sku from that BundleOrderItem will be tested for availability"(){
        setup: "setup a bundle order item and non-null orderitemId"
        BundleOrderItemImpl mockOrderItem = Spy(BundleOrderItemImpl)
        SkuImpl mockSku = Spy(SkuImpl)
        mockSku.getId() >> 1
        
        context.seedData.itemRequest.getQuantity() >> 0
        context.seedData.itemRequest.getOrderItemId() >> 1
        mockOrderItemService.readOrderItemById(_) >> mockOrderItem
        
        when: "the activity is executed"
        context = activity.execute(context);
        
        then: "that sku is checked for availability"
        1 * mockOrderItem.getSku() >> mockSku
        1 * mockSku.isAvailable() >> true
    }
    
    def "If the order item id is non-null, and the order item is not a familiar item, then availability is not checked"(){
        setup: "setup a discrete order item and non-null orderitemId"
        OrderItemImpl mockOrderItem = Spy(OrderItemImpl)
        SkuImpl mockSku = Spy(SkuImpl)
        mockSku.getId() >> 1
    
        
        context.seedData.itemRequest.getQuantity() >> 0
        context.seedData.itemRequest.getOrderItemId() >> 1
        mockOrderItemService.readOrderItemById(_) >> mockOrderItem
        
        when: "the activity is executed"
        context = activity.execute(context);
        
        then: "availability is not checked"
        0 * mockOrderItem.getSku() >> mockSku
        0 * mockSku.isAvailable()
    }
    
    def "If the order item id is null, the catalog service is used to find the sku, and that sku is checked for availability"(){
        setup: "order item id is set to be null and catalogService set to return a sku"
        SkuImpl mockSku = Spy(SkuImpl)
        mockSku.getId() >> 1
        
        context.seedData.itemRequest.getQuantity() >> 0
        context.seedData.itemRequest.getOrderItemId() >> null
        context.seedData.itemRequest.getSkuId() >> 1
        
        
        when: "the activity is executed"
        context = activity.execute(context);
        
        then: "that sku is checked for availability"
        1 * mockCatalogService.findSkuById(_) >> mockSku
        1 * mockSku.isAvailable() >> true
    }
    
    def "If a sku is found to not be available, an InventoryUnavailableException is thrown"(){
        setup: "Sku is setup to be found and not available"
        SkuImpl mockSku = Spy(SkuImpl)
        mockSku.getId() >> 1
        
        context.seedData.itemRequest.getQuantity() >> 0
        context.seedData.itemRequest.getOrderItemId() >> null
        context.seedData.itemRequest.getSkuId() >> 1
        
        
        when: "the activity is executed"
        context = activity.execute(context);
        
        then: "InventoryUnavailableException is thrown"
        1 * mockCatalogService.findSkuById(_) >> mockSku
        1 * mockSku.isAvailable() >> false
        Exception e = thrown()
        e instanceof InventoryUnavailableException
    }
    
    def "If a sku has a CHECK_QUANTITY InventoryType, the inventoryService is used to checked availability"(){
        setup: "sku is setup to have a CHECK_QUANITY InventoryType"
        SkuImpl mockSku = Spy(SkuImpl)
        mockSku.getId() >> 1
        
        context.seedData.itemRequest.getQuantity() >> 0
        context.seedData.itemRequest.getOrderItemId() >> null
        context.seedData.itemRequest.getSkuId() >> 1
        
        mockSku.getInventoryType() >> InventoryType.CHECK_QUANTITY
        
        when: "the activity is executed"
        context = activity.execute(context);
        
        then: "that sku is checked for availability"
        1 * mockCatalogService.findSkuById(_) >> mockSku
        1 * mockSku.isAvailable() >> true
        1 * mockInventoryService.isAvailable(*_) >> true
            
    }
    
    def "If a sku is found unavailable using the inventoryService, an InventoryUnavailableException is thrown"(){
        setup: "sku is setup to have a CHECK_QUANITY InventoryType"
        SkuImpl mockSku = Spy(SkuImpl)
        mockSku.getId() >> 1
        
        context.seedData.itemRequest.getQuantity() >> 0
        context.seedData.itemRequest.getOrderItemId() >> null
        context.seedData.itemRequest.getSkuId() >> 1
        
        mockSku.getInventoryType() >> InventoryType.CHECK_QUANTITY
        
        when: "the activity is executed"
        context = activity.execute(context);
        
        then: "that sku is checked for availability"
        1 * mockCatalogService.findSkuById(_) >> mockSku
        1 * mockSku.isAvailable() >> true
        1 * mockInventoryService.isAvailable(*_) >> false
        Exception e = thrown()
        e instanceof InventoryUnavailableException
            
    }
    
    
}
