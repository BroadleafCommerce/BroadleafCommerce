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

import java.util.List;

import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.service.call.BundleOrderItemRequest;
import org.broadleafcommerce.order.service.call.DiscreteOrderItemRequest;
import org.broadleafcommerce.order.service.call.FulfillmentGroupRequest;
import org.broadleafcommerce.order.service.call.GiftWrapOrderItemRequest;
import org.broadleafcommerce.order.service.call.OrderItemRequest;
import org.broadleafcommerce.order.service.exception.ItemNotFoundException;
import org.broadleafcommerce.order.service.type.OrderStatus;
import org.broadleafcommerce.payment.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.payment.service.type.PaymentInfoType;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.Customer;

public interface OrderService {

    public Order createNamedOrderForCustomer(String name, Customer customer);

    public Order save(Order order, boolean priceOrder) throws PricingException;

    public Order findOrderById(Long orderId);

    public List<Order> findOrdersForCustomer(Customer customer);

    public List<Order> findOrdersForCustomer(Customer customer, OrderStatus status);

    public Order findNamedOrderForCustomer(String name, Customer customer);

    public FulfillmentGroup findDefaultFulfillmentGroupForOrder(Order order);

    public OrderItem addDiscreteItemToOrder(Order order, DiscreteOrderItemRequest itemRequest, boolean priceOrder) throws PricingException;

    public OrderItem addGiftWrapItemToOrder(Order order, GiftWrapOrderItemRequest itemRequest, boolean priceOrder) throws PricingException;

    public OrderItem addBundleItemToOrder(Order order, BundleOrderItemRequest itemRequest, boolean priceOrder) throws PricingException;

    public List<OrderItem> addItemsToOrder(Order order, List<OrderItemRequest> orderItemRequests, boolean priceOrder) throws PricingException;

    public PaymentInfo addPaymentToOrder(Order order, PaymentInfo payment);

    public PaymentInfo addPaymentToOrder(Order order, PaymentInfo payment, Referenced securePaymentInfo);

    public FulfillmentGroup addFulfillmentGroupToOrder(FulfillmentGroupRequest fulfillmentGroupRequest, boolean priceOrder) throws PricingException;

    public FulfillmentGroup addFulfillmentGroupToOrder(Order order, FulfillmentGroup fulfillmentGroup, boolean priceOrder) throws PricingException;

    public FulfillmentGroup addItemToFulfillmentGroup(OrderItem item, FulfillmentGroup fulfillmentGroup, int quantity, boolean priceOrder) throws PricingException;

    public FulfillmentGroup addItemToFulfillmentGroup(OrderItem item, FulfillmentGroup fulfillmentGroup, boolean priceOrder) throws PricingException;

    public FulfillmentGroup addItemsToFulfillmentGroup(List<OrderItem> items, FulfillmentGroup fulfillmentGroup, boolean priceOrder) throws PricingException;

    public Order addOfferToOrder(Order order, String offerCode);

    public void updateItemQuantity(Order order, OrderItem item, boolean priceOrder) throws ItemNotFoundException, PricingException;

    public void removeFulfillmentGroupFromOrder(Order order, FulfillmentGroup fulfillmentGroup, boolean priceOrder) throws PricingException;

    public Order removeItemFromOrder(Order order, OrderItem item, boolean priceOrder) throws PricingException;

    public Order removeItemsFromOrder(Order order, List<OrderItem> items, boolean priceOrder) throws PricingException;

    public Order removeOfferFromOrder(Order order, Offer offer, boolean priceOrder) throws PricingException;

    public Order removeAllOffersFromOrder(Order order, boolean priceOrder) throws PricingException;

    public void removeNamedOrderForCustomer(String name, Customer customer);

    public Order confirmOrder(Order order);

    public void cancelOrder(Order order);

    public void removeAllFulfillmentGroupsFromOrder(Order order, boolean priceOrder) throws PricingException;

    public List<PaymentInfo> readPaymentInfosForOrder(Order order);

    public OrderItem addSkuToOrder(Long orderId, Long skuId, Long productId, Long categoryId, Integer quantity, boolean priceOrder) throws PricingException;

    public Order removeItemFromOrder(Long orderId, Long itemId, boolean priceOrder) throws PricingException;

    public void removeAllPaymentsFromOrder(Order order);

    public FulfillmentGroup createDefaultFulfillmentGroup(Order order, Address address);

    public OrderItem addOrderItemToOrder(Order order, OrderItem newOrderItem, boolean priceOrder) throws PricingException;

    public List<OrderItem> addOrderItemsToOrder(Order order, List<OrderItem> newOrderItems, boolean priceOrder) throws PricingException;

    public Order findOrderByOrderNumber(String orderNumber);

    public void removePaymentsFromOrder(Order order, PaymentInfoType paymentInfoType);

    public Order reloadOrder(Order order);

    public boolean acquireLock(Order order);

    public boolean releaseLock(Order order);
}
