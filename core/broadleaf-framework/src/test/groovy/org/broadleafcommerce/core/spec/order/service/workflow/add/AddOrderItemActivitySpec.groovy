
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
import org.broadleafcommerce.core.catalog.domain.ProductBundleImpl
import org.broadleafcommerce.core.catalog.domain.Sku
import org.broadleafcommerce.core.catalog.domain.SkuImpl
import org.broadleafcommerce.core.catalog.service.CatalogService
import org.broadleafcommerce.core.order.domain.OrderItem
import org.broadleafcommerce.core.order.domain.OrderItemImpl
import org.broadleafcommerce.core.order.service.OrderItemService
import org.broadleafcommerce.core.order.service.OrderService
import org.broadleafcommerce.core.order.service.call.NonDiscreteOrderItemRequestDTO
import org.broadleafcommerce.core.order.service.workflow.add.AddOrderItemActivity


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
        activity = new AddOrderItemActivity().with {
            orderService = mockOrderService
            orderItemService = mockOrderItemService
            catalogService = mockCatalogService
            it
        }
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
    }

    def "Test that a non discrete item request is added to order"() {
        setup: "setup message"
        Sku testSku = new SkuImpl()
        Product testProduct = new ProductBundleImpl()
        CategoryImpl testCat = new CategoryImpl()
        OrderItem testItem = new OrderItemImpl()


        when: "The activity is executed"
        context = activity.execute(context)

        then: "There is an order item added to the order"
        1 * activity.catalogService.findSkuById(_) >> testSku
        1 * activity.catalogService.findProductById(_) >> testProduct
        1 * activity.catalogService.findCategoryById(_) >> testCat
        1 * activity.orderItemService.createOrderItem(_) >> testItem
        context.seedData.getOrderItem() != null
    }
    
    def "Test that a non discrete item request is added to order without a sku, product or category given"() {
        setup: "setup message"
        OrderItem testItem = new OrderItemImpl()


        when: "The activity is executed"
        context = activity.execute(context)

        then: "There is an order item added to the order"
        1 * activity.orderItemService.createOrderItem(_) >> testItem
        context.seedData.getOrderItem() != null
    }
    
    def "Test that a non discrete item request without a category is added to order"() {
        setup: "setup message"
        Sku testSku = new SkuImpl()
        Product testProduct = new ProductBundleImpl()
        OrderItem testItem = new OrderItemImpl()


        when: "The activity is executed"
        context = activity.execute(context)

        then: "There is an order item added to the order"
        1 * activity.catalogService.findSkuById(_) >> testSku
        1 * activity.catalogService.findProductById(_) >> testProduct
        1 * activity.orderItemService.createOrderItem(_) >> testItem
        context.seedData.getOrderItem() != null
        
    }
    
    
    
}
