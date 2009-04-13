package org.broadleafcommerce.order.service;

import java.util.List;
import java.util.Map;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.type.OrderStatus;

public interface OrderService {

    public Order createNamedOrderForCustomer(String name, Customer customer);

    public Order findOrderById(Long orderId);

    public Order findCartForCustomer(Customer customer, boolean createIfDoesntExist);

    public Order findCartForCustomer(Customer customer);

    public List<Order> findOrdersForCustomer(Customer customer);

    public List<Order> findOrdersForCustomer(Customer customer, OrderStatus status);

    public Order findNamedOrderForCustomer(String name, Customer customer);

    public FulfillmentGroup findDefaultFulfillmentGroupForOrder(Order order);

    public OrderItem addSkuToOrder(Order order, Sku item, int quantity);

    public OrderItem addSkuToOrder(Order order, Sku item, Product product, Category category, int quantity);

    public List<OrderItem> addSkusToOrder(Map<String, Integer> skuIdQtyMap, Order order) throws Exception;

    public OrderItem addItemToCartFromNamedOrder(Order order, Sku item, int quantity);

    public Order addAllItemsToCartFromNamedOrder(Order namedOrder);

    public PaymentInfo addPaymentToOrder(Order order, PaymentInfo payment);

    public FulfillmentGroup addFulfillmentGroupToOrder(Order order, FulfillmentGroup fulfillmentGroup);

    public FulfillmentGroup addItemToFulfillmentGroup(OrderItem item, FulfillmentGroup fulfillmentGroup, int quantity);

    public Order addOfferToOrder(Order order, String offerCode);

    public FulfillmentGroup updateFulfillmentGroup(FulfillmentGroup fulfillmentGroup);

    public OrderItem updateItemInOrder(Order order, OrderItem item);

    public List<OrderItem> updateItemsInOrder(Order order, List<OrderItem> orderItems);

    public OrderItem moveItemToCartFromNamedOrder(Order order, Sku item, int quantity);

    public Order moveAllItemsToCartFromNamedOrder(Order namedOrder, boolean deleteNamedorder);

    public void removeFulfillmentGroupFromOrder(Order order, FulfillmentGroup fulfillmentGroup);

    public Order removeItemFromOrder(Order order, OrderItem item);

    public Order removeItemFromOrder(Order order, long orderItemId);

    public Order removeOfferFromOrder(Order order, Offer offer);

    public void removeNamedOrderForCustomer(String name, Customer customer);

    public Order confirmOrder(Order order);

    public void cancelOrder(Order order);
}
