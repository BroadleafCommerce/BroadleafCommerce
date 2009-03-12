package org.broadleafcommerce.order.service;

import java.util.List;

import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.profile.domain.ContactInfo;
import org.broadleafcommerce.profile.domain.Customer;

public interface OrderService {

    public Order findCurrentCartForCustomer(Customer customer);

    public FulfillmentGroupImpl findDefaultFulfillmentGroupForOrder(Order order);

    public List<FulfillmentGroup> findFulfillmentGroupsForOrder(Order order);

    public List<Order> findOrdersForCustomer(Customer customer);

    public List<OrderItem> findItemsForOrder(Order order);

    public Order addContactInfoToOrder(Order order, ContactInfo contactInfo);

    public OrderItem addItemToOrder(Order order, Sku item, int quantity);

    public PaymentInfo addPaymentToOrder(Order order, PaymentInfo payment);

    public FulfillmentGroup addItemToFulfillmentGroup(OrderItem item, FulfillmentGroup fulfillmentGroup, int quantity);

    public FulfillmentGroup addFulfillmentGroupToOrder(Order order, FulfillmentGroup fulfillmentGroup);

    public FulfillmentGroup updateFulfillmentGroup(FulfillmentGroup fulfillmentGroup);

    public OrderItem updateItemInOrder(Order order, OrderItem item);

    public void removeFulfillmentGroupFromOrder(Order order, FulfillmentGroup fulfillmentGroup);

    public Order removeItemFromOrder(Order order, OrderItem item);

    public Order calculateOrderTotal(Order order);

    public Order addOfferToOrder(Order order, String offerCode);
    
    public Order removeOfferFromOrder(Order order, Offer offer);
    
    public Order confirmOrder(Order order);

    public void cancelOrder(Order order);
}
