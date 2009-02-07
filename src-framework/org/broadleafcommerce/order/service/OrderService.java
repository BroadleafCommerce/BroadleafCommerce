package org.broadleafcommerce.order.service;

import java.util.List;

import org.broadleafcommerce.catalog.domain.SellableItem;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.domain.OrderPayment;
import org.broadleafcommerce.order.domain.OrderShipping;
import org.broadleafcommerce.profile.domain.ContactInfo;
import org.broadleafcommerce.profile.domain.Customer;

public interface OrderService {

    public Order createOrderForCustomer(Customer customer);

    public Order createOrderForCustomer(long userId);

    public Order addContactInfoToOrder(Order order, ContactInfo contactInfo);

    public Order addContactInfoToOrder(Long orderId, Long contactId);

    public OrderItem addItemToOrder(Order order, SellableItem item, int quantity);

    public OrderItem addItemToOrder(Long orderId, Long itemId, int quantity);

    public OrderPayment addPaymentToOrder(Order order, OrderPayment payment);

    public OrderPayment addPaymentToOrder(Long orderId, Long paymentId);

    public OrderShipping addShippingToOrder(Order order, OrderShipping shipping);

    public OrderShipping addShippingToOrder(Long orderId, Long shippingId) throws Exception;

    public List<Order> getOrdersForCustomer(Customer customer);

    public List<Order> getOrdersForCustomer(Long userId);

    public List<OrderItem> getItemsForOrder(Order order);

    public List<OrderItem> getItemsForOrder(Long orderId);

    public OrderItem updateItemInOrder(Order order, OrderItem item);

    public OrderItem updateItemInOrder(Long orderId, Long itemId, int quantity, double finalPrice);

    public Order removeItemFromOrder(Order order, OrderItem item);

    public Order removeItemFromOrder(Long orderId, Long itemId);

    public Order confirmOrder(Order order);

    public Order confirmOrder(Long orderId);

    public Order calculateOrderTotal(Order order);

    public Order calculateOrderTotal(Long orderId);

    public void cancelOrder(Order order);

    public void cancelOrder(Long orderId);

    public Order getCurrentBasketForCustomer(Customer customer);

    public Order getCurrentBasketForUserId(Long userId);

}
