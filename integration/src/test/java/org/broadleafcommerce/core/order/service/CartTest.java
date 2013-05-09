/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.order.service;

import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.call.MergeCartResponse;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class CartTest extends OrderBaseTest {
    
    @Resource(name="blMergeCartService")
    private MergeCartService mergeCartService;
    
    protected boolean cartContainsOnlyTheseItems(Order cart, List<OrderItem> orderItems) {
        List<OrderItem> cartOrderItems = new ArrayList<OrderItem>(cart.getOrderItems());
        
        for (OrderItem item : orderItems) {
            ListIterator<OrderItem> it = cartOrderItems.listIterator();
            while (it.hasNext()) {
                OrderItem otherItem = it.next();
                if (orderItemsSemanticallyEqual((DiscreteOrderItem) item, (DiscreteOrderItem) otherItem)) {
                    it.remove();
                    break;
                }
            }
        }
        
        return cartOrderItems.size() == 0;
    }
    
    protected boolean orderItemsSemanticallyEqual(DiscreteOrderItem one, DiscreteOrderItem two) {
        if ((one == null && two != null) || (one != null && two == null)) {
            return false;
        }
        if (!one.getClass().equals(two.getClass())) {
            return false;
        }
        if (!one.getSku().getId().equals(two.getSku().getId())) {
            return false;
        }
        if (one.getQuantity() != two.getQuantity()) {
            return false;
        }
        if (((one.getProduct() == null && two.getProduct() != null) || 
                (one.getProduct() != null && two.getProduct() == null)) && 
                !one.getProduct().getId().equals(two.getProduct().getId())) {
            return false;
        }
        if (((one.getCategory() == null && two.getCategory() != null) || 
                (one.getCategory() != null && two.getCategory() == null)) && 
                !one.getCategory().getId().equals(two.getCategory().getId())) {
            return false;
        }
        return true;
    }

    @Test(groups = { "testCartAndNamedOrder" })
    @Transactional
    public void testMoveAllItemsToCartFromNamedOrder() throws RemoveFromCartException, AddToCartException {
        Order namedOrder = setUpNamedOrder();
        List<OrderItem> namedOrderItems = new ArrayList<OrderItem>();
        namedOrderItems.addAll(namedOrder.getOrderItems());
        Order cart = orderService.createNewCartForCustomer(namedOrder.getCustomer());
        cart = orderService.addAllItemsFromNamedOrder(namedOrder, true);
        assert cartContainsOnlyTheseItems(cart, namedOrderItems);
        assert namedOrder.getOrderItems().size() == 0;
    }
    
    @Test(groups = { "testCartAndNamedOrder" })
    @Transactional
    public void testAddAllItemsToCartFromNamedOrder() throws RemoveFromCartException, AddToCartException {
        Order namedOrder = setUpNamedOrder();
        List<OrderItem> namedOrderItems = new ArrayList<OrderItem>();
        namedOrderItems.addAll(namedOrder.getOrderItems());
        Order cart = orderService.createNewCartForCustomer(namedOrder.getCustomer());
        orderService.setMoveNamedOrderItems(false);
        cart = orderService.addAllItemsFromNamedOrder(namedOrder, true);
        orderService.setMoveNamedOrderItems(true);
        assert cartContainsOnlyTheseItems(cart, namedOrderItems);
    }

    @Test(groups = { "testCartAndNamedOrder" })
    @Transactional
    public void testAddAllItemsToCartFromNamedOrderWithoutExistingCart() throws RemoveFromCartException, AddToCartException {
        Order namedOrder = setUpNamedOrder();
        List<OrderItem> namedOrderItems = new ArrayList<OrderItem>();
        namedOrderItems.addAll(namedOrder.getOrderItems());
        orderService.setMoveNamedOrderItems(false);
        Order cart = orderService.addAllItemsFromNamedOrder(namedOrder, true);
        orderService.setMoveNamedOrderItems(true);
        assert cartContainsOnlyTheseItems(cart, namedOrderItems);
    }

    @Test(groups = { "testCartAndNamedOrder" })
    @Transactional
    public void testAddItemToCartFromNamedOrder() throws RemoveFromCartException, AddToCartException {
        Order namedOrder = setUpNamedOrder();
        List<OrderItem> namedOrderItems = new ArrayList<OrderItem>();
        namedOrderItems.addAll(namedOrder.getOrderItems());
        List<OrderItem> movedOrderItems = new ArrayList<OrderItem>();
        movedOrderItems.add(namedOrderItems.get(0));
        Order cart = orderService.createNewCartForCustomer(namedOrder.getCustomer());
        orderService.setMoveNamedOrderItems(false);
        cart = orderService.addItemFromNamedOrder(namedOrder, movedOrderItems.get(0), true);
        orderService.setMoveNamedOrderItems(true);
        assert cartContainsOnlyTheseItems(cart, movedOrderItems);
    }

    @Test(groups = { "testCartAndNamedOrder" })
    @Transactional
    public void testMoveItemToCartFromNamedOrder() throws RemoveFromCartException, AddToCartException {
        Order namedOrder = setUpNamedOrder();
        List<OrderItem> namedOrderItems = new ArrayList<OrderItem>();
        namedOrderItems.addAll(namedOrder.getOrderItems());
        List<OrderItem> movedOrderItems = new ArrayList<OrderItem>();
        movedOrderItems.add(namedOrderItems.get(0));
        Order cart = orderService.createNewCartForCustomer(namedOrder.getCustomer());
        cart = orderService.addItemFromNamedOrder(namedOrder, movedOrderItems.get(0), true);
        List<Order> customerNamedOrders = orderService.findOrdersForCustomer(namedOrder.getCustomer(), OrderStatus.NAMED);
        assert customerNamedOrders.size() == 0;
        assert cart.getOrderItems().size() == 1;
        assert namedOrder.getOrderItems().size() == 0;
        assert cartContainsOnlyTheseItems(cart, movedOrderItems);
    }

    @Test(groups = { "testCartAndNamedOrder" })
    @Transactional
    public void testMoveItemToCartFromNamedOrderWithoutExistingCart() throws RemoveFromCartException, AddToCartException {
        Order namedOrder = setUpNamedOrder();
        List<OrderItem> namedOrderItems = new ArrayList<OrderItem>();
        namedOrderItems.addAll(namedOrder.getOrderItems());
        List<OrderItem> movedOrderItems = new ArrayList<OrderItem>();
        movedOrderItems.add(namedOrderItems.get(0));
        Order cart = orderService.addItemFromNamedOrder(namedOrder, movedOrderItems.get(0), true);
        List<Order> customerNamedOrders = orderService.findOrdersForCustomer(namedOrder.getCustomer(), OrderStatus.NAMED);

        assert customerNamedOrders.size() == 0;
        assert cart.getOrderItems().size() == 1;
        assert namedOrder.getOrderItems().size() == 0;
        assert cartContainsOnlyTheseItems(cart, movedOrderItems);
    }
    
    @Transactional
    @Test(groups = { "testMergeCart" }) 
    public void testMergeWithNoAnonymousCart() throws PricingException, RemoveFromCartException, AddToCartException {
        Order anonymousCart = null;
        Order customerCart = setUpCartWithActiveSku();
        Customer customer = customerCart.getCustomer();
        
        MergeCartResponse response = mergeCartService.mergeCart(customer, anonymousCart);
        
        assert response.getOrder().getOrderItems().size() == 1;
        assert response.getOrder().getId().equals(customerCart.getId());
        assert response.isMerged() == false;
    }
    
    @Transactional
    @Test(groups = { "testMergeCart" }) 
    public void testMergeWithNoCustomerCart() throws PricingException, RemoveFromCartException, AddToCartException {
        Order anonymousCart = setUpCartWithActiveSku();
        Order customerCart = null;
        Customer customer = customerService.saveCustomer(createNamedCustomer());
        
        MergeCartResponse response = mergeCartService.mergeCart(customer, anonymousCart);
        
        assert response.getOrder().getOrderItems().size() == 1;
        assert response.getOrder().getId().equals(anonymousCart.getId());
        assert response.isMerged() == false;
    }
    
    @Transactional
    @Test(groups = { "testMergeCart" }) 
    public void testMergeWithBothCarts() throws PricingException, RemoveFromCartException, AddToCartException {
        Order anonymousCart = setUpCartWithActiveSku();
        Order customerCart = setUpCartWithActiveSku();
        
        Customer customer = customerCart.getCustomer();
        
        MergeCartResponse response = mergeCartService.mergeCart(customer, anonymousCart);
        
        assert response.getOrder().getOrderItems().size() == 1;
        assert response.getOrder().getId().equals(anonymousCart.getId());
        assert response.isMerged() == false;
    }
    
    @Transactional
    @Test(groups = { "testMergeCart" }) 
    public void testMergeWithInactiveAnonymousCart() throws PricingException, RemoveFromCartException, AddToCartException {
        Order anonymousCart = null;
        Order customerCart = setUpCartWithInactiveSku();
        
        Customer customer = customerCart.getCustomer();
        
        MergeCartResponse response = mergeCartService.mergeCart(customer, anonymousCart);
        
        assert response.getOrder().getOrderItems().size() == 0;
        assert response.getOrder().getId().equals(customerCart.getId());
        assert response.isMerged() == false;
    }
    
    /*
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
    }
    */
    
    
    
    
    
    
    
    
    
    
    
}
