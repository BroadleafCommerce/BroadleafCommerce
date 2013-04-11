/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.order.service.legacy;

import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.call.BundleOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.DiscreteOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupRequest;
import org.broadleafcommerce.core.order.service.call.GiftWrapOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.exception.ItemNotFoundException;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.Customer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This legacy interface should no longer be used as of 2.0
 * 
 * The new interface and implementation are OrderService and OrderServiceImpl
 * 
 * @deprecated
 */
@Deprecated
public interface LegacyOrderService extends OrderService {

    public FulfillmentGroup findDefaultFulfillmentGroupForOrder(Order order);

    /**
     * Note: This method will automatically associate the given <b>order</b> to the given <b>itemRequest</b> such that
     * then resulting {@link OrderItem} will already have an {@link Order} associated to it.
     * 
     * @param order
     * @param itemRequest
     * @return
     * @throws PricingException
     */
    public OrderItem addGiftWrapItemToOrder(Order order, GiftWrapOrderItemRequest itemRequest) throws PricingException;

    /**
     * Used to create dynamic bundles groupings of order items.
     * Typically not used with ProductBundles which should instead
     * call addProductToOrder.
     *
     * Prices the order after adding the bundle.
     *
     * @param order
     * @param itemRequest
     * @return
     * @throws PricingException
     */
    public OrderItem addBundleItemToOrder(Order order, BundleOrderItemRequest itemRequest) throws PricingException;

    /**
     * Used to create dynamic bundles groupings of order items.
     * Typically not used with ProductBundles which should instead
     * call addProductToOrder.
     *
     * Prices the order after adding the bundle if priceOrder = true.  Clients
     * may wish to perform many cart operations without pricing and
     * then use priceOrder = true on the last operation to avoid
     * exercising the pricing engine in a batch order update mode.
     * 
     * NOTE: this will automatically associate the given <b>order</b> to the given <b>itemRequest</b> such that the
     * resulting {@link OrderItem} will already have the {@link Order} associated to it
     *
     * @param order
     * @param itemRequest
     * @param priceOrder
     * @return
     * @throws PricingException
     */
    public OrderItem addBundleItemToOrder(Order order, BundleOrderItemRequest itemRequest, boolean priceOrder) throws PricingException;

    public PaymentInfo addPaymentToOrder(Order order, PaymentInfo payment);

    public FulfillmentGroup addFulfillmentGroupToOrder(FulfillmentGroupRequest fulfillmentGroupRequest) throws PricingException;
    
    public FulfillmentGroup addFulfillmentGroupToOrder(FulfillmentGroupRequest fulfillmentGroupRequest, boolean priceOrder) throws PricingException;

    public FulfillmentGroup addFulfillmentGroupToOrder(Order order, FulfillmentGroup fulfillmentGroup) throws PricingException;
    
    public FulfillmentGroup addFulfillmentGroupToOrder(Order order, FulfillmentGroup fulfillmentGroup, boolean priceOrder) throws PricingException;

    public FulfillmentGroup addItemToFulfillmentGroup(OrderItem item, FulfillmentGroup fulfillmentGroup, int quantity) throws PricingException;
    
    public FulfillmentGroup addItemToFulfillmentGroup(OrderItem item, FulfillmentGroup fulfillmentGroup, int quantity, boolean priceOrder) throws PricingException;

    public FulfillmentGroup addItemToFulfillmentGroup(OrderItem item, FulfillmentGroup fulfillmentGroup) throws PricingException;
    
    public FulfillmentGroup addItemToFulfillmentGroup(OrderItem item, FulfillmentGroup fulfillmentGroup, boolean priceOrder) throws PricingException;

    public FulfillmentGroup addItemToFulfillmentGroup(Order order, OrderItem item, FulfillmentGroup fulfillmentGroup, int quantity, boolean priceOrder) throws PricingException;
    /**
     * Delegates to the fully parametrized method with priceOrder = true.
     *
     * @param order
     * @param item
     * @throws ItemNotFoundException
     * @throws PricingException
     */
    public void updateItemQuantity(Order order, OrderItem item) throws ItemNotFoundException, PricingException;

    /**
     * Updates the quantity and reprices the order.
     * Removes the orderItem if the quantity is updated to 0 (or less).
     *
     *
     * @param order
     * @param item
     * @param priceOrder
     * @throws ItemNotFoundException
     * @throws PricingException
     */
    public void updateItemQuantity(Order order, OrderItem item, boolean priceOrder) throws ItemNotFoundException, PricingException;
    
    /**
     * From the given OrderItemRequestDTO object, this will look through the order's DiscreteOrderItems
     * to find the item with the matching orderItemId and update this item's quantity with the value of 
     * the quantity field in the OrderItemRequestDTO.
     * 
     * @param order
     * @param orderItemRequestDTO
     * @throws ItemNotFoundException
     * @throws PricingException
     */
    public void updateItemQuantity(Order order, OrderItemRequestDTO orderItemRequestDTO) throws ItemNotFoundException, PricingException;

    public void removeFulfillmentGroupFromOrder(Order order, FulfillmentGroup fulfillmentGroup) throws PricingException;
    
    public void removeFulfillmentGroupFromOrder(Order order, FulfillmentGroup fulfillmentGroup, boolean priceOrder) throws PricingException;

    public Order removeItemFromOrder(Order order, OrderItem item) throws PricingException;
    
    public Order removeItemFromOrder(Order order, OrderItem item, boolean priceOrder) throws PricingException;

    public void removeNamedOrderForCustomer(String name, Customer customer);

    public void removeAllFulfillmentGroupsFromOrder(Order order) throws PricingException;

    public void removeAllFulfillmentGroupsFromOrder(Order order, boolean priceOrder) throws PricingException;

    public List<PaymentInfo> readPaymentInfosForOrder(Order order);

    public Order removeItemFromOrder(Long orderId, Long itemId) throws PricingException;
    
    public Order removeItemFromOrder(Long orderId, Long itemId, boolean priceOrder) throws PricingException;

    public FulfillmentGroup createDefaultFulfillmentGroup(Order order, Address address);

    /**
     * Adds an item to the passed in order.
     *
     * The orderItemRequest can be sparsely populated.
     *
     * When priceOrder is false, the system will not reprice the order.   This is more performant in
     * cases such as bulk adds where the repricing could be done for the last item only.
     *
     * @see OrderItemRequestDTO
     * @param orderItemRequestDTO
     * @param priceOrder
     * @return
     */
    public Order addItemToOrder(Long orderId, OrderItemRequestDTO orderItemRequestDTO, boolean priceOrder) throws PricingException;


    /**
     * Not typically used in versions since 1.7.
     * See: {@link #addItemToOrder(Long, OrderItemRequestDTO, boolean)}
     * 
     * @param orderId
     * @param skuId
     * @param productId
     * @param categoryId
     * @param quantity
     * @return
     */
    public DiscreteOrderItemRequest createDiscreteOrderItemRequest(Long orderId, Long skuId, Long productId, Long categoryId, Integer quantity);    

    /**
     * Adds the passed in name/value pair to the order-item.    If the
     * attribute already exists, then it is updated with the new value.   
     * 
     * If the value passed in is null or empty string and the attribute exists, it is removed
     * from the order item.
     *
     * You may wish to set priceOrder to false if performing set of
     * cart operations to avoid the expense of exercising the pricing engine
     * until you are ready to finalize pricing after adding the last item.
     *
     * @param order
     * @param item
     * @param attributeValues
     * @param priceOrder
     * @return
     */
    public Order addOrUpdateOrderItemAttributes(Order order, OrderItem item, Map<String,String> attributeValues, boolean priceOrder) throws ItemNotFoundException, PricingException;
    
    /**
     * Adds the passed in name/value pair to the order-item.    If the
     * attribute already exists, then it is updated with the new value.   
     * 
     * If the value passed in is null and the attribute exists, it is removed
     * from the order item.
     *
     * You may wish to set priceOrder to false if performing set of
     * cart operations to avoid the expense of exercising the pricing engine
     * until you are ready to finalize pricing after adding the last item.
     *
     * @param order
     * @param item
     * @param attributeName
     * @param priceOrder
     * @return
     */
    public Order removeOrderItemAttribute(Order order, OrderItem item, String attributeName, boolean priceOrder) throws ItemNotFoundException, PricingException;

    /**
     * @deprecated Call addItemToOrder(Long orderId, OrderItemRequestDTO orderItemRequestDTO, boolean priceOrder)
     * @param order
     * @param itemRequest
     * @return
     * @throws PricingException
     */
    @Deprecated
    public OrderItem addDiscreteItemToOrder(Order order, DiscreteOrderItemRequest itemRequest) throws PricingException;

    /**
     * @deprecated Call addItemToOrder(Long orderId, OrderItemRequestDTO orderItemRequestDTO, boolean priceOrder)
     *
     * Due to cart merging and gathering requirements, the item returned is not an
     * actual cart item.
     *
     * NOTE: this will automatically associate the given <b>order</b> to the given <b>itemRequest</b> such that the
     * resulting {@link OrderItem} will already have the {@link Order} associated to it
     *
     * @param order
     * @param itemRequest
     * @param priceOrder
     * @return
     * @throws PricingException
     */
    @Deprecated
    public OrderItem addDiscreteItemToOrder(Order order, DiscreteOrderItemRequest itemRequest, boolean priceOrder) throws PricingException;

    /**
     * @deprecated Call addItemToOrder(Long orderId, OrderItemRequestDTO orderItemRequestDTO, boolean priceOrder)
     * @param orderId
     * @param skuId
     * @param productId
     * @param categoryId
     * @param quantity
     * @return
     * @throws PricingException
     */
    @Deprecated
    public OrderItem addSkuToOrder(Long orderId, Long skuId, Long productId, Long categoryId, Integer quantity) throws PricingException;

    /**
     * @deprecated Call addItemToOrder(Long orderId, OrderItemRequestDTO orderItemRequestDTO, boolean priceOrder)
     * @param orderId
     * @param skuId
     * @param productId
     * @param categoryId
     * @param quantity
     * @param orderItemAttributes
     * @return
     * @throws PricingException
     */
    @Deprecated
    public OrderItem addSkuToOrder(Long orderId, Long skuId, Long productId, Long categoryId, Integer quantity, Map<String,String> orderItemAttributes) throws PricingException;

    /**
     * @deprecated Call addItemToOrder(Long orderId, OrderItemRequestDTO orderItemRequestDTO, boolean priceOrder)
     * @param orderId
     * @param skuId
     * @param productId
     * @param categoryId
     * @param quantity
     * @param priceOrder
     * @return
     * @throws PricingException
     */
    @Deprecated
    public OrderItem addSkuToOrder(Long orderId, Long skuId, Long productId, Long categoryId, Integer quantity, boolean priceOrder) throws PricingException;

    /**
     * @deprecated Call addItemToOrder(Long orderId, OrderItemRequestDTO orderItemRequestDTO, boolean priceOrder)
     * @param orderId
     * @param skuId
     * @param productId
     * @param categoryId
     * @param quantity
     * @param priceOrder
     * @param orderItemAttributes
     * @return
     * @throws PricingException
     */
    @Deprecated
    public OrderItem addSkuToOrder(Long orderId, Long skuId, Long productId, Long categoryId, Integer quantity, boolean priceOrder, Map<String,String> orderItemAttributes) throws PricingException;
    
    /**
     * @deprecated Call addItemToOrder(Long orderId, OrderItemRequestDTO orderItemRequestDTO, boolean priceOrder)
     * @param order
     * @param newOrderItem
     * @return
     * @throws PricingException
     */
    @Deprecated
    public OrderItem addOrderItemToOrder(Order order, OrderItem newOrderItem) throws PricingException;

    /**
     * @deprecated Call addItemToOrder(Long orderId, OrderItemRequestDTO orderItemRequestDTO, boolean priceOrder)
     * @param order
     * @param newOrderItem
     * @param priceOrder
     * @return
     * @throws PricingException
     */
    @Deprecated
    public OrderItem addOrderItemToOrder(Order order, OrderItem newOrderItem, boolean priceOrder) throws PricingException;
    
    /**
     * @deprecated Call addItemToOrder(Long orderId, OrderItemRequestDTO orderItemRequestDTO, boolean priceOrder)
     * @param order
     * @param itemRequest
     * @param skuPricingConsiderations
     * @return
     * @throws PricingException
     */
    @Deprecated
    public OrderItem addDynamicPriceDiscreteItemToOrder(Order order, DiscreteOrderItemRequest itemRequest, @SuppressWarnings("rawtypes") HashMap skuPricingConsiderations) throws PricingException;

    /**
     * @deprecated Call addItemToOrder(Long orderId, OrderItemRequestDTO orderItemRequestDTO, boolean priceOrder)
     * @param order
     * @param itemRequest
     * @param skuPricingConsiderations
     * @param priceOrder
     * @return
     * @throws PricingException
     */
    @Deprecated
    public OrderItem addDynamicPriceDiscreteItemToOrder(Order order, DiscreteOrderItemRequest itemRequest, @SuppressWarnings("rawtypes") HashMap skuPricingConsiderations, boolean priceOrder) throws PricingException;

    OrderItem addOrderItemToBundle(Order order, BundleOrderItem bundle, DiscreteOrderItem newOrderItem, boolean priceOrder) throws PricingException;

    Order removeItemFromBundle(Order order, BundleOrderItem bundle, OrderItem item, boolean priceOrder) throws PricingException;

}
