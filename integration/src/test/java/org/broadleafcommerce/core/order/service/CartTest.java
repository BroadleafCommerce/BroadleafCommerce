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

package org.broadleafcommerce.core.order.service;

import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class CartTest extends OrderBaseTest {
	
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
    
}
