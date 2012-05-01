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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.service.exception.OfferMaxUseExceededException;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.call.BundleOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.DiscreteOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupRequest;
import org.broadleafcommerce.core.order.service.call.GiftWrapOrderItemRequest;
import org.broadleafcommerce.core.order.service.exception.ItemNotFoundException;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.Referenced;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoType;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.Customer;

public interface OrderService {

    public Order createNamedOrderForCustomer(String name, Customer customer);

    public Order save(Order order, Boolean priceOrder) throws PricingException;

    public Order findOrderById(Long orderId);

    public List<Order> findOrdersForCustomer(Customer customer);

    public List<Order> findOrdersForCustomer(Customer customer, OrderStatus status);

    public Order findNamedOrderForCustomer(String name, Customer customer);

    public FulfillmentGroup findDefaultFulfillmentGroupForOrder(Order order);

    public OrderItem addDiscreteItemToOrder(Order order, DiscreteOrderItemRequest itemRequest) throws PricingException;
    
    public OrderItem addDiscreteItemToOrder(Order order, DiscreteOrderItemRequest itemRequest, boolean priceOrder) throws PricingException;

    public OrderItem addGiftWrapItemToOrder(Order order, GiftWrapOrderItemRequest itemRequest) throws PricingException;
    
    public OrderItem addGiftWrapItemToOrder(Order order, GiftWrapOrderItemRequest itemRequest, boolean priceOrder) throws PricingException;

    public OrderItem addBundleItemToOrder(Order order, BundleOrderItemRequest itemRequest) throws PricingException;
    
    public OrderItem addBundleItemToOrder(Order order, BundleOrderItemRequest itemRequest, boolean priceOrder) throws PricingException;

    public PaymentInfo addPaymentToOrder(Order order, PaymentInfo payment);

    public PaymentInfo addPaymentToOrder(Order order, PaymentInfo payment, Referenced securePaymentInfo);

    public FulfillmentGroup addFulfillmentGroupToOrder(FulfillmentGroupRequest fulfillmentGroupRequest) throws PricingException;
    
    public FulfillmentGroup addFulfillmentGroupToOrder(FulfillmentGroupRequest fulfillmentGroupRequest, boolean priceOrder) throws PricingException;

    public FulfillmentGroup addFulfillmentGroupToOrder(Order order, FulfillmentGroup fulfillmentGroup) throws PricingException;
    
    public FulfillmentGroup addFulfillmentGroupToOrder(Order order, FulfillmentGroup fulfillmentGroup, boolean priceOrder) throws PricingException;

    public FulfillmentGroup addItemToFulfillmentGroup(OrderItem item, FulfillmentGroup fulfillmentGroup, int quantity) throws PricingException;
    
    public FulfillmentGroup addItemToFulfillmentGroup(OrderItem item, FulfillmentGroup fulfillmentGroup, int quantity, boolean priceOrder) throws PricingException;

    public FulfillmentGroup addItemToFulfillmentGroup(OrderItem item, FulfillmentGroup fulfillmentGroup) throws PricingException;
    
    public FulfillmentGroup addItemToFulfillmentGroup(OrderItem item, FulfillmentGroup fulfillmentGroup, boolean priceOrder) throws PricingException;

    public void updateItemQuantity(Order order, OrderItem item) throws ItemNotFoundException, PricingException;
    
    public void updateItemQuantity(Order order, OrderItem item, boolean priceOrder) throws ItemNotFoundException, PricingException;

    public void removeFulfillmentGroupFromOrder(Order order, FulfillmentGroup fulfillmentGroup) throws PricingException;
    
    public void removeFulfillmentGroupFromOrder(Order order, FulfillmentGroup fulfillmentGroup, boolean priceOrder) throws PricingException;

    public Order removeItemFromOrder(Order order, OrderItem item) throws PricingException;
    
    public Order removeItemFromOrder(Order order, OrderItem item, boolean priceOrder) throws PricingException;
    
    public Order addOfferCode(Order order, OfferCode offerCode, boolean priceOrder) throws PricingException, OfferMaxUseExceededException;

    public Order removeOfferCode(Order order, OfferCode offerCode, boolean priceOrder) throws PricingException;

    public Order removeAllOfferCodes(Order order, boolean priceOrder) throws PricingException;

    public void removeNamedOrderForCustomer(String name, Customer customer);

    public Order confirmOrder(Order order);

    public void cancelOrder(Order order);

    public void removeAllFulfillmentGroupsFromOrder(Order order) throws PricingException;

    public void removeAllFulfillmentGroupsFromOrder(Order order, boolean priceOrder) throws PricingException;

    public List<PaymentInfo> readPaymentInfosForOrder(Order order);

    public OrderItem addSkuToOrder(Long orderId, Long skuId, Long productId, Long categoryId, Integer quantity) throws PricingException;
    
    public OrderItem addSkuToOrder(Long orderId, Long skuId, Long productId, Long categoryId, Integer quantity, Map<String,String> orderItemAttributes) throws PricingException;
    
    public OrderItem addSkuToOrder(Long orderId, Long skuId, Long productId, Long categoryId, Integer quantity, boolean priceOrder) throws PricingException;
    
    public OrderItem addSkuToOrder(Long orderId, Long skuId, Long productId, Long categoryId, Integer quantity, boolean priceOrder, Map<String,String> orderItemAttributes) throws PricingException;

    public Order removeItemFromOrder(Long orderId, Long itemId) throws PricingException;
    
    public Order removeItemFromOrder(Long orderId, Long itemId, boolean priceOrder) throws PricingException;

    public void removeAllPaymentsFromOrder(Order order);

    public FulfillmentGroup createDefaultFulfillmentGroup(Order order, Address address);

    public OrderItem addOrderItemToOrder(Order order, OrderItem newOrderItem) throws PricingException;
    
    public OrderItem addOrderItemToOrder(Order order, OrderItem newOrderItem, boolean priceOrder) throws PricingException;

    public Order findOrderByOrderNumber (String orderNumber);

    public void removePaymentsFromOrder(Order order, PaymentInfoType paymentInfoType);
    
    public DiscreteOrderItemRequest createDiscreteOrderItemRequest(Long skuId, Long productId, Long categoryId, Integer quantity);
    
    public OrderItem addDynamicPriceDiscreteItemToOrder(Order order, DiscreteOrderItemRequest itemRequest, @SuppressWarnings("rawtypes") HashMap skuPricingConsiderations) throws PricingException;

    public OrderItem addDynamicPriceDiscreteItemToOrder(Order order, DiscreteOrderItemRequest itemRequest, @SuppressWarnings("rawtypes") HashMap skuPricingConsiderations, boolean priceOrder) throws PricingException;
    
    public OrderItem addOrderItemToBundle(Order order, BundleOrderItem bundle, DiscreteOrderItem newOrderItem, boolean priceOrder) throws PricingException;
    
    public Order removeItemFromBundle(Order order, BundleOrderItem bundle, OrderItem item, boolean priceOrder) throws PricingException;

    /**
     * Adds the passed in name/value pair to the order-item.    If the
     * attribute already exists, then it is updated with the new value.   
     * 
     * If the value passed in is null or empty string and the attribute exists, it is removed
     * from the order item.
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
     * @param order
     * @param item
     * @param attributeName
     * @param priceOrder
     * @return
     */
    public Order removeOrderItemAttribute(Order order, OrderItem item, String attributeName, boolean priceOrder) throws ItemNotFoundException, PricingException;
    

}
