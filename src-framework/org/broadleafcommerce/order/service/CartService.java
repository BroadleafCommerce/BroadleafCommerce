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

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.service.call.MergeCartResponse;
import org.broadleafcommerce.order.service.call.ReconstructCartResponse;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.domain.Customer;

public interface CartService extends OrderService {

    public Order createNewCartForCustomer(Customer customer);

    public Order findCartForCustomer(Customer customer);

    public Order addAllItemsToCartFromNamedOrder(Order namedOrder, boolean priceOrder) throws PricingException;

    public OrderItem moveItemToCartFromNamedOrder(Order order, OrderItem orderItem, boolean priceOrder) throws PricingException;

    public OrderItem moveItemToCartFromNamedOrder(Long customerId, String orderName, Long orderItemId, Integer quantity, boolean priceOrder) throws PricingException;

    public Order moveAllItemsToCartFromNamedOrder(Order namedOrder, boolean priceOrder) throws PricingException;

    /**
     * Merge the anonymous cart with the customer's cart taking into
     * consideration sku activation
     * @param customer the customer whose cart is to be merged
     * @param anonymousCartId the anonymous cart id
     * @return the response containing the cart, any items added to the cart,
     *         and any items removed from the cart
     */
    public MergeCartResponse mergeCart(Customer customer, Order anonymousCart) throws PricingException;

    /**
     * Reconstruct the cart using previous stored state taking into
     * consideration sku activation
     * @param customer the customer whose cart is to be reconstructed
     * @return the response containing the cart and any items removed from the
     *         cart
     */
    public ReconstructCartResponse reconstructCart(Customer customer) throws PricingException;

    public boolean isMoveNamedOrderItems();

    public void setMoveNamedOrderItems(boolean moveNamedOrderItems);

    public boolean isDeleteEmptyNamedOrders();

    public void setDeleteEmptyNamedOrders(boolean deleteEmptyNamedOrders);
}
