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
package org.broadleafcommerce.core.spec.order.service.workflow.add

import org.broadleafcommerce.common.dao.GenericEntityDao
import org.broadleafcommerce.common.money.Money
import org.broadleafcommerce.core.catalog.service.CatalogService
import org.broadleafcommerce.core.order.domain.*
import org.broadleafcommerce.core.order.service.OrderItemService
import org.broadleafcommerce.core.order.service.OrderService
import org.broadleafcommerce.core.order.service.call.NonDiscreteOrderItemRequestDTO
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO
import org.broadleafcommerce.core.order.service.workflow.add.AddOrderItemActivity
/**
 * 
 * @author ncrum
 *
 */
class AddOrderItemActivitySpec extends BaseAddItemActivitySpec {

    /*
     * 1) catalogService finds sku, product, and category
     *  a) NonDiscrete -> orderItem has given quantity, retail/sale price, itemname, and order
     *  b) product == null -> category,product,sku,quantity,itemattributes,order,sale/retail price
     *  c) product not ProductBundle -> category,product,sku,quantity,itemattributes,order,sale/retail price
     *      * both are DiscreteOrderItem
     *  d) not a b or c -> BundleOrderItem
     * 2) category == null and product != null -> category is Default
     * 3) parentOrderItemId != null-> item.getParentOrderItem = orderItemService.readOrderItemById()
     * 
     * 
     */

    OrderService mockOrderService = Mock()
    OrderItemService mockOrderItemService = Mock()
    CatalogService mockCatalogService = Mock()
    GenericEntityDao mockGenericDao = Mock()

    
    def setup() {
        activity = Spy(AddOrderItemActivity).with {
            orderService = mockOrderService
            orderItemService = mockOrderItemService
            catalogService = mockCatalogService
            genericEntityDao = mockGenericDao
            it
        }
        
    }

    def "Test that a non discrete item request is added to order"() {
        setup: 
        context.seedData.itemRequest = new NonDiscreteOrderItemRequestDTO().with {
            skuId = 1
            productId = 1
            categoryId = 1
            quantity = 1
            overrideSalePrice = new Money("1.00")
            overrideRetailPrice = new Money("1.50")
            itemName = "test"
            it
        }
        OrderItem testItem = new OrderItemImpl()


        when: "The activity is executed"
        context = activity.execute(context)

        then: "There is an order item added to the order"
        1 * mockOrderItemService.buildOrderItemFromDTO(_,_) >> testItem
        context.seedData.getOrderItem() != null
        context.seedData.getOrderItem() instanceof OrderItem
    }
    
    def "Test that a non discrete item request is added to order without a sku, product or category given"() {
        setup: 
        context.seedData.itemRequest = new NonDiscreteOrderItemRequestDTO().with {
            skuId = 1
            productId = 1
            categoryId = 1
            quantity = 1
            overrideSalePrice = new Money("1.00")
            overrideRetailPrice = new Money("1.50")
            itemName = "test"
            it
        }
        OrderItem testItem = new OrderItemImpl()


        when: "The activity is executed"
        context = activity.execute(context)

        then: "There is an order item added to the order"
        1 * mockOrderItemService.buildOrderItemFromDTO(_,_) >> testItem
        context.seedData.getOrderItem() == testItem
        context.seedData.getOrderItem() instanceof OrderItem
    }
    
    def "Test that a non discrete item request without a category is added to order"() {
        setup: 
        context.seedData.itemRequest = new NonDiscreteOrderItemRequestDTO().with {
            skuId = 1
            productId = 1
            quantity = 1
            overrideSalePrice = new Money("1.00")
            overrideRetailPrice = new Money("1.50")
            itemName = "test"
            it
        }
        OrderItem testItem = new OrderItemImpl()


        when: "The activity is executed"
        context = activity.execute(context)

        then: "There is an order item added to the order"
        1 * mockOrderItemService.buildOrderItemFromDTO(_,_) >> testItem
        context.seedData.getOrderItem() == testItem
        context.seedData.getOrderItem() instanceof OrderItem
        
    }
    
    def "If a DiscreteOrderItemRequest is given and product is null, a discrete order item is added to the order"(){
        setup:
        OrderItemRequestDTO testItemRequest = Spy(OrderItemRequestDTO).with {
            skuId = 1
            productId = 1
            categoryId = 1
            quantity = 1
            overrideSalePrice = new Money("1.00")
            overrideRetailPrice = new Money("1.50")
            it
        }
        
        context.seedData.itemRequest = testItemRequest
        
        DiscreteOrderItem testItem = Mock(DiscreteOrderItem)
        
        when: "The activity is executed"
        context = activity.execute(context)
        
        then: "There is an order item added to the order"
        1 * mockOrderItemService.buildOrderItemFromDTO(_,_) >> testItem
        context.seedData.getOrderItem() == testItem
        context.seedData.getOrderItem() instanceof DiscreteOrderItem
        
    }
    
    def "If product is not null and a ProductBundle is given, a bundle order item is added to the order"() {
        setup:
        OrderItemRequestDTO testItemRequest = Spy(OrderItemRequestDTO).with {
            skuId = 1
            productId = 1
            categoryId = 1
            quantity = 1
            overrideSalePrice = new Money("1.00")
            overrideRetailPrice = new Money("1.50")
            it
        }
        
        context.seedData.itemRequest = testItemRequest
        
        BundleOrderItem testItem = Mock(BundleOrderItem)
        
        when: "The activity is executed"
        context = activity.execute(context)
        
        then: "There is an order item added to the order"
        1 * mockOrderItemService.buildOrderItemFromDTO(_,_) >> testItem
        context.seedData.getOrderItem() == testItem
        context.seedData.getOrderItem() instanceof BundleOrderItem
    }
    
    
}
