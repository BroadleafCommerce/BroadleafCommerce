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

import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.service.exception.OfferMaxUseExceededException;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.call.MergeCartResponse;
import org.broadleafcommerce.core.order.service.call.OrderItemRequest;
import org.broadleafcommerce.core.order.service.exception.ItemNotFoundException;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.core.domain.Customer;

import java.util.List;

public interface OrderService {

	// Rethink these
    public Order createNewCartForCustomer(Customer customer);
    public Order findOrderById(Long orderId);
    public Order getNullOrder();
    public Order findCartForCustomer(Customer customer);
    public List<Order> findOrdersForCustomer(Customer customer);
    public List<Order> findOrdersForCustomer(Customer customer, OrderStatus status);
    public Order findOrderByOrderNumber(String orderNumber);
    public List<PaymentInfo>findPaymentInfosForOrder(Order order);
    public Order save(Order order, Boolean priceOrder) throws PricingException;
    public boolean cancelOrder(Order order);
    
    public Order addOfferCode(Order order, OfferCode offerCode, boolean priceOrder) throws PricingException, OfferMaxUseExceededException;
    public Order removeOfferCode(Order order, OfferCode offerCode, boolean priceOrder) throws PricingException;
    public Order removeAllOfferCodes(Order order, boolean priceOrder) throws PricingException;
    
    public Order removeItemFromBundle(Order order, BundleOrderItem bundle, OrderItem item, boolean priceOrder) throws PricingException;
    public OrderItem addOrderItemToBundle(Order order, BundleOrderItem bundle, DiscreteOrderItem newOrderItem, boolean priceOrder) throws PricingException;
    
    /**
     * @see #setAutomaticallyMergeLikeItems(boolean)
     * 
     * @return whether or not like-items will be automatically merged
     */
    public boolean getAutomaticallyMergeLikeItems();

    /**
     * When set to true, the system when items are added to the cart, they will
     * automatically be merged.    For example, when a user adds an item to the cart
     * and then adds the item again, the item will have its quantity changed to 2
     * instead of the cart containing two separate items.
     *
     * If this logic needs to be more complex, it is possible to extend the behavior by
     * overriding OrderOfferProcessor.buildIdentifier().
     *
     * @param automaticallyMergeLikeItems
     */
    public void setAutomaticallyMergeLikeItems(boolean automaticallyMergeLikeItems);
    
    /**
     * Merge the anonymous cart with the customer's cart taking into consideration sku activation
     * 
     * @param customer the customer whose cart is to be merged
     * @param anonymousCartId the anonymous cart id
     * @return the response containing the cart, any items added to the cart, and any items removed from the cart
     */
    public MergeCartResponse mergeCart(Customer customer, Order anonymousCart) throws PricingException;
    
    /**
     * Adds an item to the passed in order.
     *
     * Minimum required parameters for OrderItemRequest: productId, quantity
     *
     * When priceOrder is false, the system will not reprice the order.   This is more performant in
     * cases such as bulk adds where the repricing could be done for the last item only.
     *
     * @see OrderItemRequest
     * @param order
     * @param orderItemRequest
     * @param priceOrder
     * @return the order the item was added to
     */
    public Order addItem(Order order, OrderItemRequest orderItemRequest, boolean priceOrder) throws PricingException;
    
    /**
     * From the given OrderItemRequest object, this will look through the order's DiscreteOrderItems
     * to find the item with the matching orderItemId and update this item's quantity.
     * 
     * Minimum required parameters for OrderItemRequest: orderItemId, quantity
     * 
     * @see OrderItemRequest
     * @param order
     * @param orderItemRequest
     * @param priceOrder
     * @return the order the item was added to
     * @throws ItemNotFoundException
     * @throws PricingException
     */
	public Order updateItem(Order order, OrderItemRequest orderItemRequest, boolean priceOrder) throws ItemNotFoundException, PricingException;
	
    /**
     * From the given OrderItemRequest object, this will look through the order's DiscreteOrderItems
     * to find the item with the matching orderItemId and remove it.
     * 
     * Minimum required parameters for OrderItemRequest: orderItemId
     * 
     * @see OrderItemRequest
     * @param order
     * @param orderItemRequest
     * @param priceOrder
     * @return the order the item was added to
     * @throws ItemNotFoundException
     * @throws PricingException
     */
	public Order removeItem(Order order, OrderItemRequest orderItemRequest, boolean priceOrder) throws ItemNotFoundException, PricingException;

}
