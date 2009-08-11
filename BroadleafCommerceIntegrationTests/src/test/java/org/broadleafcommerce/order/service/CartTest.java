/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.order.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.CategoryImpl;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.ProductImpl;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.catalog.domain.SkuImpl;
import org.broadleafcommerce.catalog.service.CatalogService;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.test.BaseTest;
import org.broadleafcommerce.util.money.Money;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

public class CartTest extends BaseTest {

    @Resource
    private CartService cartService;
    
    @Resource
    private CustomerService customerService;
    
    @Resource
    private OrderService orderService;
    
    @Resource
    private CatalogService catalogService;

    @Test(groups = { "testNamedOrder" })
    @Transactional
    public void testMoveAllItemsToCartFromNamedOrder() throws PricingException {
    	Order namedOrder = setUpNamedOrder();
    	List<OrderItem> namedOrderItems = new ArrayList<OrderItem>();
    	namedOrderItems.addAll(namedOrder.getOrderItems());
    	Order cart = cartService.createNewCartForCustomer(namedOrder.getCustomer());
    	cart = cartService.addAllItemsToCartFromNamedOrder(namedOrder);
    	assert namedOrderItems.equals(cart.getOrderItems());
    	assert namedOrder.getOrderItems().size() == 0;
    }

    @Test(groups = { "testNamedOrder" })
    @Transactional
    public void testAddAllItemsToCartFromNamedOrder() throws PricingException {
    	Order namedOrder = setUpNamedOrder();
    	List<OrderItem> namedOrderItems = new ArrayList<OrderItem>();
    	namedOrderItems.addAll(namedOrder.getOrderItems());
    	Order cart = cartService.createNewCartForCustomer(namedOrder.getCustomer());
    	cartService.setMoveNamedOrderItems(false);
    	cart = cartService.addAllItemsToCartFromNamedOrder(namedOrder);
    	assert namedOrderItems.equals(cart.getOrderItems());
    	cartService.setMoveNamedOrderItems(true);
    }

    
    private Order setUpNamedOrder() throws PricingException {
        Customer customer = customerService.saveCustomer(customerService.createCustomerFromId(null));

        Category category = new CategoryImpl();
        category.setName("Pants");
        category = catalogService.saveCategory(category);
        Product newProduct = new ProductImpl();

        Calendar activeStartCal = Calendar.getInstance();
        activeStartCal.add(Calendar.DAY_OF_YEAR, -2);
        newProduct.setActiveStartDate(activeStartCal.getTime());

        newProduct.setDefaultCategory(category);
        newProduct.setName("Leather Pants");
        newProduct = catalogService.saveProduct(newProduct);

        Sku newSku = new SkuImpl();
        newSku.setName("Red Leather Pants");
        newSku.setRetailPrice(new Money(44.99));
        newSku.setActiveStartDate(activeStartCal.getTime());
        newSku.setDiscountable(true);
        newSku = catalogService.saveSku(newSku);
        List<Sku> allSkus = new ArrayList<Sku>();
        allSkus.add(newSku);
        newProduct.setAllSkus(allSkus);
        newProduct = catalogService.saveProduct(newProduct);

        Order order = orderService.createNamedOrderForCustomer("Pants Order", customer);

        OrderItem orderItem = orderService.addSkuToOrder(order.getId(), newSku.getId(),
                newProduct.getId(), category.getId(), 2);
        OrderItem quantityNullOrderItem = orderService.addSkuToOrder(order.getId(), newSku.getId(),
                newProduct.getId(), category.getId(), null);
        OrderItem skuNullOrderItem = orderService.addSkuToOrder(order.getId(), null,
                newProduct.getId(), category.getId(), 2);
        OrderItem orderNullOrderItem = orderService.addSkuToOrder(null, newSku.getId(),
                newProduct.getId(), category.getId(), 2);
        OrderItem productNullOrderItem = orderService.addSkuToOrder(order.getId(), newSku.getId(),
                null, category.getId(), 2);
        OrderItem categoryNullOrderItem = orderService.addSkuToOrder(order.getId(), newSku.getId(),
                newProduct.getId(), null, 2);
        
        assert orderItem != null;
        assert skuNullOrderItem == null;
        assert quantityNullOrderItem == null;
        assert orderNullOrderItem == null;
        assert productNullOrderItem != null;
        assert categoryNullOrderItem != null;
        
        return order;
    }
}
