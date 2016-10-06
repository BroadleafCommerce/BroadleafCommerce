/*
 * #%L
 * BroadleafCommerce Integration
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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.Resource;

public class CartTest extends OrderBaseTest {
    
    @Resource(name="blMergeCartService")
    private MergeCartService mergeCartService;
    
    protected boolean cartContainsOnlyTheseItems(Order cart, List<OrderItem> orderItems) {
        List<OrderItem> cartOrderItems = new ArrayList<>(cart.getOrderItems());
        
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
        List<OrderItem> namedOrderItems = new ArrayList<>();
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
        List<OrderItem> namedOrderItems = new ArrayList<>();
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
        List<OrderItem> namedOrderItems = new ArrayList<>();
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
        List<OrderItem> namedOrderItems = new ArrayList<>();
        namedOrderItems.addAll(namedOrder.getOrderItems());
        List<OrderItem> movedOrderItems = new ArrayList<>();
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
        List<OrderItem> namedOrderItems = new ArrayList<>();
        namedOrderItems.addAll(namedOrder.getOrderItems());
        List<OrderItem> movedOrderItems = new ArrayList<>();
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
        List<OrderItem> namedOrderItems = new ArrayList<>();
        namedOrderItems.addAll(namedOrder.getOrderItems());
        List<OrderItem> movedOrderItems = new ArrayList<>();
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
    
}
