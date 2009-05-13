package org.broadleafcommerce.order.service;

import java.util.List;

import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.order.service.call.BundleOrderItemRequest;
import org.broadleafcommerce.order.service.call.DiscreteOrderItemRequest;
import org.broadleafcommerce.order.service.call.FulfillmentGroupRequest;
import org.broadleafcommerce.order.service.exception.ItemNotFoundException;
import org.broadleafcommerce.order.service.type.OrderStatus;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.domain.Customer;

public interface OrderService {

    public Order createNamedOrderForCustomer(String name, Customer customer);

    public Order findOrderById(Long orderId);

    public List<Order> findOrdersForCustomer(Customer customer);

    public List<Order> findOrdersForCustomer(Customer customer, OrderStatus status);

    public Order findNamedOrderForCustomer(String name, Customer customer);

    public FulfillmentGroup findDefaultFulfillmentGroupForOrder(Order order);

    public OrderItem addDiscreteItemToOrder(Order order, DiscreteOrderItemRequest itemRequest) throws PricingException;

    public OrderItem addBundleItemToOrder(Order order, BundleOrderItemRequest itemRequest) throws PricingException;

    public PaymentInfo addPaymentToOrder(Order order, PaymentInfo payment);

    public PaymentInfo addPaymentToOrder(Order order, PaymentInfo payment, Referenced securePaymentInfo);

    public FulfillmentGroup addFulfillmentGroupToOrder(FulfillmentGroupRequest fulfillmentGroupRequest) throws PricingException;

    public FulfillmentGroup addFulfillmentGroupToOrder(Order order, FulfillmentGroup fulfillmentGroup) throws PricingException;

    public FulfillmentGroup addItemToFulfillmentGroup(OrderItem item, FulfillmentGroup fulfillmentGroup, int quantity) throws PricingException;

    public Order addOfferToOrder(Order order, String offerCode);

    public OrderItem updateItemInOrder(Order order, OrderItem item) throws ItemNotFoundException, PricingException;

    public List<OrderItem> updateItemsInOrder(Order order, List<OrderItem> orderItems) throws ItemNotFoundException, PricingException;

    public void removeFulfillmentGroupFromOrder(Order order, FulfillmentGroup fulfillmentGroup) throws PricingException;

    public Order removeItemFromOrder(Order order, OrderItem item) throws PricingException;

    public Order removeOfferFromOrder(Order order, Offer offer) throws PricingException;

    public Order removeAllOffersFromOrder(Order order) throws PricingException;

    public void removeNamedOrderForCustomer(String name, Customer customer);

    public Order confirmOrder(Order order);

    public void cancelOrder(Order order);

    public void removeAllFulfillmentGroupsFromOrder(Order order) throws PricingException;

    public List<PaymentInfo> readPaymentInfosForOrder(Order order);

    public OrderItem addSkuToOrder(Long orderId, Long skuId, Long productId, Long categoryId, Integer quantity) throws PricingException;

    public Order removeItemFromOrder(Long orderId, Long itemId) throws PricingException;

    public void removeAllPaymentsFromOrder(Order order);

}
