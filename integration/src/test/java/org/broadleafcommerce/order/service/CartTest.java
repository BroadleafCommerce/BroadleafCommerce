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
import java.util.List;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.service.call.MergeCartResponse;
import org.broadleafcommerce.order.service.type.OrderStatus;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.domain.Customer;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

public class CartTest extends OrderBaseTest {

    @Test(groups = { "testCartAndNamedOrder" })
    @Transactional
    public void testMoveAllItemsToCartFromNamedOrder() throws PricingException {
        Order namedOrder = setUpNamedOrder();
        List<OrderItem> namedOrderItems = new ArrayList<OrderItem>();
        namedOrderItems.addAll(namedOrder.getOrderItems());
        Order cart = cartService.createNewCartForCustomer(namedOrder.getCustomer());
        cart = cartService.moveAllItemsToCartFromNamedOrder(namedOrder);
        assert namedOrderItems.equals(cart.getOrderItems());
        assert namedOrder.getOrderItems().size() == 0;
    }
    
    @Test(groups = { "testCartAndNamedOrder" })
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

    @Test(groups = { "testCartAndNamedOrder" })
    @Transactional
    public void testAddAllItemsToCartFromNamedOrderWithoutExistingCart() throws PricingException {
        Order namedOrder = setUpNamedOrder();
        List<OrderItem> namedOrderItems = new ArrayList<OrderItem>();
        namedOrderItems.addAll(namedOrder.getOrderItems());
        cartService.setMoveNamedOrderItems(false);
        Order cart = cartService.addAllItemsToCartFromNamedOrder(namedOrder);
        assert namedOrderItems.equals(cart.getOrderItems());
        cartService.setMoveNamedOrderItems(true);
    }

    @Test(groups = { "testCartAndNamedOrder" })
    @Transactional
    public void testAddItemToCartFromNamedOrder() throws PricingException {
        Order namedOrder = setUpNamedOrder();
        List<OrderItem> namedOrderItems = new ArrayList<OrderItem>();
        namedOrderItems.addAll(namedOrder.getOrderItems());
        Order cart = cartService.createNewCartForCustomer(namedOrder.getCustomer());
        cartService.setMoveNamedOrderItems(false);
        OrderItem movedItem = cartService.moveItemToCartFromNamedOrder(namedOrder, namedOrderItems.get(0));
        cartService.setMoveNamedOrderItems(true);
        assert movedItem != null;
        assert cart.getOrderItems().size() == 1;
    }

    @Test(groups = { "testCartAndNamedOrder" })
    @Transactional
    public void testMoveItemToCartFromNamedOrder() throws PricingException {
        Order namedOrder = setUpNamedOrder();
        List<OrderItem> namedOrderItems = new ArrayList<OrderItem>();
        namedOrderItems.addAll(namedOrder.getOrderItems());
        Order cart = cartService.createNewCartForCustomer(namedOrder.getCustomer());
        OrderItem movedItem = cartService.moveItemToCartFromNamedOrder(namedOrder, namedOrderItems.get(0));
        
        List<Order> customerNamedOrders = cartService.findOrdersForCustomer(namedOrder.getCustomer(), OrderStatus.NAMED);
        assert customerNamedOrders.size() == 0;
        assert movedItem != null;
        assert cart.getOrderItems().size() == 1;
        assert namedOrder.getOrderItems().size() == 0;
    }

    @Test(groups = { "testCartAndNamedOrder" })
    @Transactional
    public void testMoveItemToCartFromNamedOrderWithoutExistingCart() throws PricingException {
        Order namedOrder = setUpNamedOrder();
        List<OrderItem> namedOrderItems = new ArrayList<OrderItem>();
        namedOrderItems.addAll(namedOrder.getOrderItems());
        OrderItem movedItem = cartService.moveItemToCartFromNamedOrder(namedOrder, namedOrderItems.get(0));
        List<Order> customerNamedOrders = cartService.findOrdersForCustomer(namedOrder.getCustomer(), OrderStatus.NAMED);

        Order cart = cartService.findCartForCustomer(namedOrder.getCustomer());
        assert customerNamedOrders.size() == 0;
        assert movedItem != null;
        assert cart.getOrderItems().size() == 1;
        assert namedOrder.getOrderItems().size() == 0;
    }

    @Test(groups = { "testCartAndNamedOrder" })
    @Transactional
    public void testMoveItemToCartFromNamedOrderByIds() throws PricingException {
        Order namedOrder = setUpNamedOrder();
        List<OrderItem> namedOrderItems = new ArrayList<OrderItem>();
        namedOrderItems.addAll(namedOrder.getOrderItems());
        OrderItem movedItem = cartService.moveItemToCartFromNamedOrder(namedOrder.getCustomer().getId(), 
                namedOrder.getName(), namedOrderItems.get(0).getId(), namedOrderItems.get(0).getQuantity());
        List<Order> customerNamedOrders = cartService.findOrdersForCustomer(namedOrder.getCustomer(), OrderStatus.NAMED);
        
        Order cart = cartService.findCartForCustomer(namedOrder.getCustomer());
        assert customerNamedOrders.size() == 0;
        assert movedItem != null;
        assert cart.getOrderItems().size() == 1;
        assert namedOrder.getOrderItems().size() == 0;
    }

    @Transactional
    @Test(groups = { "testMergeCart" }) 
    public void testMergeToEmptyCart() throws PricingException {
        Order anonymousCart = setUpAnonymousCartWithInactiveSku();
        Customer customer = customerService.saveCustomer(createNamedCustomer());
        MergeCartResponse response = cartService.mergeCart(customer, anonymousCart);
        assert response.getAddedItems().size() == 2;
        assert response.getOrder().getOrderItems().size() == 2;
        assert response.isMerged() == false;
        assert response.getRemovedItems().size() == 2;
    }
    
    @Transactional
    @Test(groups = { "testMergeCart" }) 
    public void testMergeToExistingCart() throws PricingException {
        //sets up anonymous cart with a DiscreteOrderItem, inactive DiscreteOrderItem, BundleOrderItem, and inactive BundleOrderItem
        Order anonymousCart = setUpAnonymousCartWithInactiveSku();
        Customer customer = customerService.saveCustomer(createNamedCustomer());
        
        //sets up existing cart with a DiscreteOrderItem, inactive DiscreteOrderItem, BundleOrderItem, and inactive BundleOrderItem
        setUpExistingCartWithInactiveSkuAndInactiveBundle(customer);
        MergeCartResponse response = cartService.mergeCart(customer, anonymousCart);
        
        assert response.getAddedItems().size() == 2;
        assert response.getOrder().getOrderItems().size() == 4;
        assert response.isMerged();
        assert response.getRemovedItems().size() == 4;
    }
    
}
