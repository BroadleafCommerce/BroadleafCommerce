/*
 * #%L
 * BroadleafCommerce Framework
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
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.call.ReconstructCartResponse;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.order.service.exception.ItemNotFoundException;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.order.service.exception.UpdateCartException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/*
 * TODO setup other BLC items to be JMX managed resources like this one. This would include other services, and singleton beans
 * that are configured via Spring and property files (i.e. payment modules, etc...)
 */
/**
 * This legacy implementation should no longer be used as of 2.0
 * 
 * The new interface and implementation are OrderService and OrderServiceImpl
 * 
 * @deprecated
 */
@Deprecated
public class LegacyCartServiceImpl extends LegacyOrderServiceImpl implements LegacyCartService {

    @Resource(name="blCustomerService")
    protected CustomerService customerService;

    public Order addAllItemsToCartFromNamedOrder(Order namedOrder) throws PricingException {
        return addAllItemsToCartFromNamedOrder(namedOrder, true);
    }

    public Order addAllItemsToCartFromNamedOrder(Order namedOrder, boolean priceOrder) throws PricingException {
        Order cartOrder = orderDao.readCartForCustomer(namedOrder.getCustomer());
        if (cartOrder == null) {
            cartOrder = createNewCartForCustomer(namedOrder.getCustomer());
        }
        List<OrderItem> items = new ArrayList<OrderItem>(namedOrder.getOrderItems());
        for (int i = 0; i < items.size(); i++) {
            OrderItem orderItem = items.get(i);

            // only run pricing routines on the last item.
            boolean shouldPriceOrder = (priceOrder && (i == items.size() -1));
            if (moveNamedOrderItems) {
                moveItemToOrder(namedOrder, cartOrder, orderItem, shouldPriceOrder);
            } else {
                addOrderItemToOrder(cartOrder, orderItem, shouldPriceOrder);
            }
            
        }
        return cartOrder;
    }
    
    public OrderItem moveItemToCartFromNamedOrder(Long customerId, String orderName, Long orderItemId, Integer quantity) throws PricingException {
        return moveItemToCartFromNamedOrder(customerId, orderName, orderItemId, quantity, true);
    }

    public OrderItem moveItemToCartFromNamedOrder(Long customerId, String orderName, Long orderItemId, Integer quantity, boolean priceOrder) throws PricingException {
        Order wishlistOrder = findNamedOrderForCustomer(orderName, customerService.createCustomerFromId(customerId));
        OrderItem orderItem = orderItemService.readOrderItemById(orderItemId);
        orderItem.setQuantity(quantity);
        return moveItemToCartFromNamedOrder(wishlistOrder, orderItem, priceOrder);
    }
    
    public OrderItem moveItemToCartFromNamedOrder(Order namedOrder, OrderItem orderItem) throws PricingException {
        return moveItemToCartFromNamedOrder(namedOrder, orderItem, true);
    }

    public OrderItem moveItemToCartFromNamedOrder(Order namedOrder, OrderItem orderItem, boolean priceOrder) throws PricingException {
        Order cartOrder = orderDao.readCartForCustomer(namedOrder.getCustomer());
        if (cartOrder == null) {
            cartOrder = createNewCartForCustomer(namedOrder.getCustomer());
        }
        if (moveNamedOrderItems) {
            moveItemToOrder(namedOrder, cartOrder, orderItem, priceOrder);
            if (namedOrder.getOrderItems().size() == 0 && deleteEmptyNamedOrders) {
                cancelOrder(namedOrder);
            }
        } else {
            orderItem = addOrderItemToOrder(cartOrder, orderItem, priceOrder);
        }
        
        return orderItem;
    }
    
    public Order moveAllItemsToCartFromNamedOrder(Order namedOrder) throws PricingException {
        return moveAllItemsToCartFromNamedOrder(namedOrder, true);
    }

    public Order moveAllItemsToCartFromNamedOrder(Order namedOrder, boolean priceOrder) throws PricingException {
        Order cartOrder = addAllItemsToCartFromNamedOrder(namedOrder, priceOrder);
        if (deleteEmptyNamedOrders) {
            cancelOrder(namedOrder);
        }
        return cartOrder;
    }

    public MergeCartResponse mergeCart(Customer customer, Order anonymousCart) throws PricingException {
        return mergeCart(customer, anonymousCart, true);
    }

    public ReconstructCartResponse reconstructCart(Customer customer) throws PricingException {
        return reconstructCart(customer, true);
    }

    public MergeCartResponse mergeCart(Customer customer, Order anonymousCart, boolean priceOrder) throws PricingException {
        try {
            return mergeCartService.mergeCart(customer, anonymousCart, priceOrder);
        } catch (RemoveFromCartException e) {
            // This should not happen as this service should be configured to use the LegacyMergeCartService, which will
            // not throw this exception
            throw new PricingException(e);
        }
    }
    
    public ReconstructCartResponse reconstructCart(Customer customer, boolean priceOrder) throws PricingException {
        try {
            return mergeCartService.reconstructCart(customer, priceOrder);
        } catch (RemoveFromCartException e) {
            // This should not happen as this service should be configured to use the LegacyMergeCartService, which will
            // not throw this exception
            throw new PricingException(e);
        }
    }

    @Override
    public Order addItem(Long orderId, OrderItemRequestDTO orderItemRequestDTO, boolean priceOrder) throws AddToCartException {
        try {
            return addItemToOrder(orderId, orderItemRequestDTO, priceOrder);
        } catch (PricingException e) {
            throw new AddToCartException("Could not add item", e);
        }
    }

    @Override
    public Order updateItemQuantity(Long orderId, OrderItemRequestDTO orderItemRequestDTO, boolean priceOrder) throws UpdateCartException {
        try {
            Order order = findOrderById(orderId);
            updateItemQuantity(order, orderItemRequestDTO);
            return order;
        } catch (PricingException e) {
            throw new UpdateCartException("Could not update cart", e);
        } catch (ItemNotFoundException e) {
            throw new UpdateCartException("Could not update cart", e);
        }
    }

    @Override
    public Order removeItem(Long orderId, Long orderItemId, boolean priceOrder) throws RemoveFromCartException {
        try {
            return removeItemFromOrder(orderId, orderItemId, priceOrder);
        } catch (PricingException e) {
            throw new RemoveFromCartException("Could not remove item", e);
        }
    }
}
