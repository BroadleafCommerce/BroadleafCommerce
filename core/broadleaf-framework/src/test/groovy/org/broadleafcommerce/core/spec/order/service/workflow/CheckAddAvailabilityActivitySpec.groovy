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
package org.broadleafcommerce.core.spec.order.service.workflow

import org.broadleafcommerce.core.catalog.domain.SkuImpl
import org.broadleafcommerce.core.catalog.service.CatalogService
import org.broadleafcommerce.core.inventory.service.ContextualInventoryService
import org.broadleafcommerce.core.inventory.service.InventoryUnavailableException
import org.broadleafcommerce.core.inventory.service.type.InventoryType
import org.broadleafcommerce.core.order.service.OrderItemService
import org.broadleafcommerce.core.order.service.workflow.CheckAddAvailabilityActivity



/*
 * 1a) sku set using catalogService.findSkuById(_) CONTINUE
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
class CheckAddAvailabilityActivitySpec extends BaseOrderWorkflowSpec {

    CatalogService mockCatalogService = Mock()
    OrderItemService mockOrderItemService = Mock()
    ContextualInventoryService mockInventoryService = Mock()
    
    
    /*
     * 1) mock for Services, and Sku
     * 2) setup activity
     */
    def setup(){
        activity = Spy(CheckAddAvailabilityActivity).with {
            catalogService = mockCatalogService
            orderItemService = mockOrderItemService
            inventoryService = mockInventoryService
            it
        }
        
        
    }
    
    def "If the order item id is null, the catalog service is used to find the sku, and that sku is checked for availability"(){
        setup: "order item id is set to be null and catalogService set to return a sku"
        SkuImpl mockSku = Spy(SkuImpl)
        mockSku.getId() >> 1
        
        context.seedData.itemRequest.getQuantity() >> 0
        context.seedData.itemRequest.getOrderItemId() >> null
        context.seedData.itemRequest.getSkuId() >> 1
        
        
        when: "the activity is executed"
        context = activity.execute(context)
        
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
        context = activity.execute(context)
        
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
        context = activity.execute(context)
        
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
        context = activity.execute(context)
        
        then: "that sku is checked for availability"
        1 * mockCatalogService.findSkuById(_) >> mockSku
        1 * mockSku.isAvailable() >> true
        1 * mockInventoryService.isAvailable(*_) >> false
        Exception e = thrown()
        e instanceof InventoryUnavailableException
            
    }
    
    
}
