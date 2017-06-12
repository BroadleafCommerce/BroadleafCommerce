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
import org.broadleafcommerce.core.order.domain.BundleOrderItemImpl
import org.broadleafcommerce.core.order.domain.DiscreteOrderItemImpl
import org.broadleafcommerce.core.order.domain.OrderItemImpl
import org.broadleafcommerce.core.order.service.OrderItemService
import org.broadleafcommerce.core.order.service.workflow.CheckUpdateAvailabilityActivity



/*
 * 1a) orderItem instanceOf DiscreteOrderItem
 *     sku is set using DiscreteOrderItem.getSku() CONTINUE
 * 1b) orderItem instanceOf BundleOrderItem 
 *     sku is set using BundleOrderItem.getSku() CONTINUE
 * 1c) LOG.warn() issued and return context EXIT
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
class CheckUpdateAvailabilityActivitySpec extends BaseOrderWorkflowSpec {

    CatalogService mockCatalogService = Mock()
    OrderItemService mockOrderItemService = Mock()
    ContextualInventoryService mockInventoryService = Mock()
    
    
    /*
     * 1) mock for Services, and Sku
     * 2) setup activity
     */
    def setup(){
        activity = Spy(CheckUpdateAvailabilityActivity).with {
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
        context = activity.execute(context)
        
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
        context = activity.execute(context)
        
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
        context = activity.execute(context)
        
        then: "availability is not checked"
        0 * mockOrderItem.getSku() >> mockSku
        0 * mockSku.isAvailable()
    }
    
}
