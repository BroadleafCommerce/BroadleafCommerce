/*
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.core.order.service.legacy;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.call.MergeCartResponse;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class LegacyCartTest extends LegacyOrderBaseTest {

    @Test(groups = { "testCartAndNamedOrderLegacy" })
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
    
    @Test(groups = { "testCartAndNamedOrderLegacy" })
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

    @Test(groups = { "testCartAndNamedOrderLegacy" })
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

    @Test(groups = { "testCartAndNamedOrderLegacy" })
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

    @Test(groups = { "testCartAndNamedOrderLegacy" })
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

    @Test(groups = { "testCartAndNamedOrderLegacy" })
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

    @Test(groups = { "testCartAndNamedOrderLegacy" })
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
    @Test(groups = { "testMergeCartLegacy" }) 
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
    @Test(groups = { "testMergeCartLegacy" }) 
    public void testMergeToExistingCart() throws PricingException {
        //sets up anonymous cart with a DiscreteOrderItem, inactive DiscreteOrderItem, BundleOrderItem, and inactive BundleOrderItem
        Order anonymousCart = setUpAnonymousCartWithInactiveSku();
        Customer customer = customerService.saveCustomer(createNamedCustomer());
        
        //sets up existing cart with a DiscreteOrderItem, inactive DiscreteOrderItem, BundleOrderItem, and inactive BundleOrderItem
        initializeExistingCartWithInactiveSkuAndInactiveBundle(customer);
        MergeCartResponse response = cartService.mergeCart(customer, anonymousCart);
        
        assert response.getAddedItems().size() == 2;
        assert response.getOrder().getOrderItems().size() == 4;
        assert response.isMerged();
        assert response.getRemovedItems().size() == 4;
    }

    @Transactional
    @Test(groups = { "testMergeCartLegacy" })
    public void testMergeToExistingCartWithGiftWrapOrderItems() throws PricingException {
        //FIXME: This test needs to be fixed
        /*
        //sets up anonymous cart with two DiscreteOrderItems, and a GiftWrapOrderItem
        Order anonymousCart = setUpAnonymousCartWithGiftWrap();
        Customer customer = customerService.saveCustomer(createNamedCustomer());

        //sets up existing cart with two active DiscreteOrderItems
        initializeExistingCart(customer);
        MergeCartResponse response = cartService.mergeCart(customer, anonymousCart);

        assert response.getAddedItems().size() == 3;
        assert response.getOrder().getOrderItems().size() == 5;
        assert response.isMerged();
        assert response.getRemovedItems().size() == 0;

        //sets up anonymous cart with a DiscreteOrderItem, inactive DiscreteOrderItem and inactive GiftWrapOrderItem (due to inactive wrapped item)
        anonymousCart = setUpAnonymousCartWithInactiveGiftWrap();
        customer = customerService.saveCustomer(createNamedCustomer());

        //sets up existing cart with two active DiscreteOrderItems
        initializeExistingCart(customer);
        response = cartService.mergeCart(customer, anonymousCart);

        assert response.getAddedItems().size() == 1;
        assert response.getOrder().getOrderItems().size() == 3;
        assert response.isMerged();
        assert response.getRemovedItems().size() == 2;

        //sets up anonymous cart with a DiscreteOrderItem, inactive DiscreteOrderItem and inactive GiftWrapOrderItem (due to inactive wrapped item) inside a BundleOrderItem
        anonymousCart = setUpAnonymousCartWithInactiveBundleGiftWrap();
        customer = customerService.saveCustomer(createNamedCustomer());

        //sets up existing cart with two active DiscreteOrderItems
        initializeExistingCart(customer);
        response = cartService.mergeCart(customer, anonymousCart);

        assert response.getAddedItems().size() == 0;
        assert response.getOrder().getOrderItems().size() == 2;
        assert response.isMerged();
        assert response.getRemovedItems().size() == 1;

        //sets up anonymous cart with active DiscreteOrderItems, and active GiftWrapOrderItem inside a BundleOrderItem
        anonymousCart = setUpAnonymousCartWithBundleGiftWrap();
        customer = customerService.saveCustomer(createNamedCustomer());

        //sets up existing cart with two active DiscreteOrderItems
        initializeExistingCart(customer);
        response = cartService.mergeCart(customer, anonymousCart);

        assert response.getAddedItems().size() == 1;
        assert response.getOrder().getOrderItems().size() == 3;
        assert response.isMerged();
        assert response.getRemovedItems().size() == 0;

        //sets up anonymous cart with active DiscreteOrderItems, and active GiftWrapOrderItem inside a BundleOrderItem. Active OrderItems are also in the root of the order and the bundled GiftWrapOrderItem wraps the root OrderItems
        anonymousCart = setUpAnonymousCartWithBundleGiftWrapReferringToRootItems();
        customer = customerService.saveCustomer(createNamedCustomer());

        //sets up existing cart with two active DiscreteOrderItems
        initializeExistingCart(customer);
        response = cartService.mergeCart(customer, anonymousCart);

        assert response.getAddedItems().size() == 3;
        assert response.getOrder().getOrderItems().size() == 5;
        assert response.isMerged();
        assert response.getRemovedItems().size() == 0;

        //sets up anonymous cart with two BundleOrderItems, one of which has a GiftWrapOrderItem. The GiftWrapOrderItem wraps the DiscreteOrderItems from the other bundle, which makes its bundle inactive.
        anonymousCart = setUpAnonymousCartWithBundleGiftWrapReferringItemsInAnotherBundle();
        customer = customerService.saveCustomer(createNamedCustomer());

        //sets up existing cart with two active DiscreteOrderItems
        initializeExistingCart(customer);
        response = cartService.mergeCart(customer, anonymousCart);

        assert response.getAddedItems().size() == 1;
        assert response.getOrder().getOrderItems().size() == 3;
        assert response.isMerged();
        assert response.getRemovedItems().size() == 1;
        */
    }
}
