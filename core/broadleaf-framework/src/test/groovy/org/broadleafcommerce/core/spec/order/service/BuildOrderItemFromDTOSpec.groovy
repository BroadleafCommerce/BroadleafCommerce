
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
package org.broadleafcommerce.core.spec.order.service

import org.broadleafcommerce.common.money.Money
import org.broadleafcommerce.core.catalog.domain.*
import org.broadleafcommerce.core.catalog.service.CatalogService
import org.broadleafcommerce.core.order.domain.*
import org.broadleafcommerce.core.order.service.OrderItemServiceImpl
import org.broadleafcommerce.core.order.service.call.NonDiscreteOrderItemRequestDTO
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO
/**
 * 
 * @author Chris Kittrell (ckittrell)
 *
 */
class BuildOrderItemFromDTOSpec extends BaseBuildOrderItemFromDTOSpec {

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

    CatalogService mockCatalogService = Mock()

    def setup() {
        orderItemService = Spy(OrderItemServiceImpl).with {
            catalogService = mockCatalogService
            it
        }
        orderItemService.applyAdditionalOrderItemProperties(_) >> null
        
    }

    def "Test that a non discrete item request is created"() {
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
        Sku testSku = new SkuImpl()
        Product testProduct = new ProductBundleImpl()
        CategoryImpl testCat = new CategoryImpl()
        OrderItem testItem = new OrderItemImpl()

        when: "The activity is executed"
        OrderItem orderItem = orderItemService.buildOrderItemFromDTO(context.seedData.order, context.seedData.itemRequest)

        then: "There is an order item created"
        1 * mockCatalogService.findSkuById(_) >> testSku
        1 * mockCatalogService.findProductById(_) >> testProduct
        1 * mockCatalogService.findCategoryById(_) >> testCat
        1 * orderItemService.createOrderItem(_) >> testItem
        orderItem != null
    }

    def "Test that a non discrete item request is created without a sku, product or category given"() {
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
        OrderItem orderItem = orderItemService.buildOrderItemFromDTO(context.seedData.order, context.seedData.itemRequest)

        then: "There is an order item created"
        1 * orderItemService.createOrderItem(_) >> testItem
        orderItem == testItem
    }

    def "Test that a non discrete item request without a category is created"() {
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
        Sku testSku = new SkuImpl()
        Product testProduct = new ProductBundleImpl()
        OrderItem testItem = new OrderItemImpl()

        when: "The activity is executed"
        OrderItem orderItem = orderItemService.buildOrderItemFromDTO(context.seedData.order, context.seedData.itemRequest)

        then: "There is an order item created"
        1 * mockCatalogService.findSkuById(_) >> testSku
        1 * mockCatalogService.findProductById(_) >> testProduct
        1 * orderItemService.createOrderItem(_) >> testItem
        orderItem == testItem
    }

    def "If a DiscreteOrderItemRequest is given and product is null, a discrete order item is created"(){
        setup:
        context.seedData.itemRequest = Spy(OrderItemRequestDTO).with {
            skuId = 1
            productId = 1
            categoryId = 1
            quantity = 1
            overrideSalePrice = new Money("1.00")
            overrideRetailPrice = new Money("1.50")
            it
        }
        DiscreteOrderItem testItem = Mock(DiscreteOrderItem)

        when: "The activity is executed"
        OrderItem orderItem = orderItemService.buildOrderItemFromDTO(context.seedData.order, context.seedData.itemRequest)

        then: "There is an order item created"
        1 * orderItemService.createDiscreteOrderItem(_) >> testItem
        orderItem == testItem
    }

    def "If product is not null and a ProductBundle is given, a bundle order item is created"() {
        setup:
        context.seedData.itemRequest = Spy(OrderItemRequestDTO).with {
            skuId = 1
            productId = 1
            categoryId = 1
            quantity = 1
            overrideSalePrice = new Money("1.00")
            overrideRetailPrice = new Money("1.50")
            it
        }
        CategoryImpl testCategory = Mock(CategoryImpl)
        ProductBundle testProduct = Mock(ProductBundle)

        BundleOrderItem testItem = Mock(BundleOrderItem)

        when: "The activity is executed"
        OrderItem orderItem = orderItemService.buildOrderItemFromDTO(context.seedData.order, context.seedData.itemRequest)

        then: "There is an order item created"
        1 * mockCatalogService.findCategoryById(_) >> testCategory
        1 * mockCatalogService.findProductById(_) >> testProduct
        1 * orderItemService.createBundleOrderItem(*_) >> testItem
        orderItem == testItem
    }

    def "If the item request finds its parent order item id is not null, the new order item has its parent set to that order item"(){
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

        CategoryImpl testCategory = Mock(CategoryImpl)
        ProductBundle testProduct = Mock(ProductBundle)


        BundleOrderItem testItem = new BundleOrderItemImpl()
        OrderItem testParent = Mock(OrderItem)

        testItemRequest.getParentOrderItemId() >> 1

        when: "The activity is executed"
        OrderItem orderItem = orderItemService.buildOrderItemFromDTO(context.seedData.order, context.seedData.itemRequest)

        then: "There is an order item created"
        1 * mockCatalogService.findCategoryById(_) >> testCategory
        1 * mockCatalogService.findProductById(_) >> testProduct
        1 * orderItemService.createBundleOrderItem(*_) >> testItem
        1 * orderItemService.readOrderItemById(_) >> testParent
        orderItem == testItem
        orderItem.getParentOrderItem() == testParent
    }
}
