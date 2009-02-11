package org.broadleafcommerce.order.service;

import java.util.List;

import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.order.domain.BroadleafOrder;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.domain.OrderPayment;
import org.broadleafcommerce.order.domain.OrderShipping;
import org.broadleafcommerce.profile.domain.ContactInfo;
import org.broadleafcommerce.profile.domain.Customer;

public interface OrderService {

    public BroadleafOrder createOrderForCustomer(Customer customer);

    public BroadleafOrder createOrderForCustomer(long userId);

    public BroadleafOrder addContactInfoToOrder(BroadleafOrder order, ContactInfo contactInfo);

    public BroadleafOrder addContactInfoToOrder(Long orderId, Long contactId);

    public OrderItem addItemToOrder(BroadleafOrder order, Sku item, int quantity);

    public OrderItem addItemToOrder(Long orderId, Long itemId, int quantity);

    public OrderPayment addPaymentToOrder(BroadleafOrder order, OrderPayment payment);

    public OrderPayment addPaymentToOrder(Long orderId, Long paymentId);

    public OrderShipping addShippingToOrder(BroadleafOrder order, OrderShipping shipping);

    public OrderShipping addShippingToOrder(Long orderId, Long shippingId) throws Exception;

    public List<BroadleafOrder> getOrdersForCustomer(Customer customer);

    public List<BroadleafOrder> getOrdersForCustomer(Long userId);

    public List<OrderItem> getItemsForOrder(BroadleafOrder order);

    public List<OrderItem> getItemsForOrder(Long orderId);

    public OrderItem updateItemInOrder(BroadleafOrder order, OrderItem item);

    public OrderItem updateItemInOrder(Long orderId, Long itemId, int quantity, double finalPrice);

    public BroadleafOrder removeItemFromOrder(BroadleafOrder order, OrderItem item);

    public BroadleafOrder removeItemFromOrder(Long orderId, Long itemId);

    public BroadleafOrder confirmOrder(BroadleafOrder order);

    public BroadleafOrder confirmOrder(Long orderId);

    public BroadleafOrder calculateOrderTotal(BroadleafOrder order);

    public BroadleafOrder calculateOrderTotal(Long orderId);

    public void cancelOrder(BroadleafOrder order);

    public void cancelOrder(Long orderId);

    public BroadleafOrder getCurrentBasketForCustomer(Customer customer);

    public BroadleafOrder getCurrentBasketForUserId(Long userId);

}
