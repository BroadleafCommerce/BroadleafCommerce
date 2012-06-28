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
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.call.MergeCartResponse;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
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
    public Order findCartForCustomer(Customer customer);
    public List<Order> findOrdersForCustomer(Customer customer);
    public List<Order> findOrdersForCustomer(Customer customer, OrderStatus status);
    public Order findOrderByOrderNumber(String orderNumber);
    public List<PaymentInfo>findPaymentInfosForOrder(Order order);
    public Order save(Order order, Boolean priceOrder) throws PricingException;
    
    /**
     * Deletes the given order. Note that the default implementation in OrderServiceImpl
     * will actually remove the Order instance 
     * @param order
     */
    public void cancelOrder(Order order);
    
    /**
     * Adds the given OfferCode to the order. Optionally prices the order as well
     * 
     * @param order
     * @param offerCode
     * @param priceOrder
     * @return the modified Order
     * @throws PricingException
     * @throws OfferMaxUseExceededException
     */
    public Order addOfferCode(Order order, OfferCode offerCode, boolean priceOrder) throws PricingException, OfferMaxUseExceededException;
    
    /**
     * Remove the given OfferCode for the order. Optionally prices the order as well.
     * 
     * @param order
     * @param offerCode 
     * @param priceOrder
     * @return the modified Order
     * @throws PricingException
     */
    public Order removeOfferCode(Order order, OfferCode offerCode, boolean priceOrder) throws PricingException;
    
    /**
     * Removes all offer codes for the given order. Optionally prices the order as well.
     * 
     * @param order
     * @param priceOrder
     * @return the modified Order
     * @throws PricingException
     */
    public Order removeAllOfferCodes(Order order, boolean priceOrder) throws PricingException;
    
    /**
     * The null order is the default order for all customers when they initially
     * enter the site. Upon the first addition of a product to a cart, a non-null order
     * will be provisioned for the user.
     * 
     * @see org.broadleafcommerce.core.order.domain.NullOrderImpl for more information
     * 
     * @return a shared, static, unmodifiable NullOrder
     */
    public Order getNullOrder();
    
    /**
     * @see #setAutomaticallyMergeLikeItems(boolean)
     * 
     * @return whether or not like-items will be automatically merged
     */
    public boolean getAutomaticallyMergeLikeItems();

    /**
     * When set to true, the system when items are added to the cart, they will
     * automatically be merged. For example, when a user adds an item to the cart
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
     * Merges the anonymous cart with the customer's current cart, taking into consideration the active
     * status of the SKUs to merge. For example, if the customer had a SKU in their anonymous cart that is no longer
     * active, it will not be merged into the new cart.
     * 
     * @param customer the customer whose cart is to be merged
     * @param anonymousCartId the anonymous cart id
     * @return the response containing the cart, any items added to the cart, and any items removed from the cart
     */
    public MergeCartResponse mergeCart(Customer customer, Order anonymousCart) throws PricingException;
    
    /**
     * Initiates the addItem workflow that will attempt to add the given quantity of the specified item
     * to the Order. The item to be added can be determined in a few different ways. For example, the 
     * SKU can be specified directly or it can be determine based on a Product and potentially some
     * specified ProductOptions for that given product.
     *
     * The minimum required parameters for OrderItemRequest are: productId, quantity
     *
     * When priceOrder is false, the system will not reprice the order.   This is more performant in
     * cases such as bulk adds where the repricing could be done for the last item only.
     *
     * @see OrderItemRequestDTO
     * @param orderId
     * @param orderItemRequest
     * @param priceOrder
     * @return the order the item was added to
     */
    public Order addItem(Long orderId, OrderItemRequestDTO orderItemRequestDTO, boolean priceOrder) throws PricingException;
    
    /**
     * Initiates the updateItem workflow that will attempt to update the item quantity for the specified
     * OrderItem in the given Order. The new quantity is specified in the OrderItemRequestDTO
     * 
     * Minimum required parameters for OrderItemRequest: orderItemId, quantity
     * 
     * @see OrderItemRequestDTO
     * @param orderId
     * @param orderItemRequest
     * @param priceOrder
     * @return the order the item was added to
     * @throws ItemNotFoundException
     * @throws PricingException
     */
	public Order updateItemQuantity(Long orderId, OrderItemRequestDTO orderItemRequestDTO, boolean priceOrder) throws ItemNotFoundException, PricingException;
	
    /**
     * Initiates the removeItem workflow that will attempt to remove the specified OrderItem from 
     * the given Order
     * 
     * @see OrderItemRequestDTO
     * @param orderId
     * @param orderItemId
     * @param priceOrder
     * @return the order the item was added to
     * @throws ItemNotFoundException
     * @throws PricingException
     */
	public Order removeItem(Long orderId, Long orderItemId, boolean priceOrder) throws ItemNotFoundException, PricingException;

}
