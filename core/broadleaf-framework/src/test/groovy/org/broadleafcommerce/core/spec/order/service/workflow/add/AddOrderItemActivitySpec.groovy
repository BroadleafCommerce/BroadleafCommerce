
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

import org.broadleafcommerce.common.money.Money
import org.broadleafcommerce.core.catalog.domain.CategoryImpl
import org.broadleafcommerce.core.catalog.domain.Product
import org.broadleafcommerce.core.catalog.domain.ProductBundle
import org.broadleafcommerce.core.catalog.domain.ProductBundleImpl
import org.broadleafcommerce.core.catalog.domain.Sku
import org.broadleafcommerce.core.catalog.domain.SkuImpl
import org.broadleafcommerce.core.catalog.service.CatalogService
import org.broadleafcommerce.core.order.domain.BundleOrderItem
import org.broadleafcommerce.core.order.domain.BundleOrderItemImpl
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem
import org.broadleafcommerce.core.order.domain.OrderItem
import org.broadleafcommerce.core.order.domain.OrderItemImpl
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

    
    def setup() {
        activity = Spy(AddOrderItemActivity).with {
            orderService = mockOrderService
            orderItemService = mockOrderItemService
            catalogService = mockCatalogService
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
        Sku testSku = new SkuImpl()
        Product testProduct = new ProductBundleImpl()
        CategoryImpl testCat = new CategoryImpl()
        OrderItem testItem = new OrderItemImpl()


        when: "The activity is executed"
        context = activity.execute(context)

        then: "There is an order item added to the order"
        1 * mockCatalogService.findSkuById(_) >> testSku
        1 * mockCatalogService.findProductById(_) >> testProduct
        1 * mockCatalogService.findCategoryById(_) >> testCat
        1 * mockOrderItemService.createOrderItem(_) >> testItem
        context.seedData.getOrderItem() != null
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
        1 * mockOrderItemService.createOrderItem(_) >> testItem
        context.seedData.getOrderItem() == testItem
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
        Sku testSku = new SkuImpl()
        Product testProduct = new ProductBundleImpl()
        OrderItem testItem = new OrderItemImpl()


        when: "The activity is executed"
        context = activity.execute(context)

        then: "There is an order item added to the order"
        1 * mockCatalogService.findSkuById(_) >> testSku
        1 * mockCatalogService.findProductById(_) >> testProduct
        1 * mockOrderItemService.createOrderItem(_) >> testItem
        context.seedData.getOrderItem() == testItem
        
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
        1 * mockOrderItemService.createDiscreteOrderItem(_) >> testItem
        context.seedData.getOrderItem() == testItem
        
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
        
        CategoryImpl testCategory = Mock(CategoryImpl)
        ProductBundle testProduct = Mock(ProductBundle)
        
        
        BundleOrderItem testItem = Mock(BundleOrderItem)
        
        when: "The activity is executed"
        context = activity.execute(context)
        
        then: "There is an order item added to the order"
        1 * mockCatalogService.findCategoryById(_) >> testCategory
        1 * mockCatalogService.findProductById(_) >> testProduct
        1 * mockOrderItemService.createBundleOrderItem(*_) >> testItem
        context.seedData.getOrderItem() == testItem
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
        context = activity.execute(context)
        
        then: "There is an order item added to the order"
        1 * mockCatalogService.findCategoryById(_) >> testCategory
        1 * mockCatalogService.findProductById(_) >> testProduct
        1 * mockOrderItemService.createBundleOrderItem(*_) >> testItem
        1 * mockOrderItemService.readOrderItemById(_) >> testParent
        context.seedData.getOrderItem() == testItem
        context.seedData.getOrderItem().getParentOrderItem() == testParent
    }
    
    
}
