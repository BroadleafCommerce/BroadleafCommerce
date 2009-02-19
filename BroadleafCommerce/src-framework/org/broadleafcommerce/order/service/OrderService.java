package org.broadleafcommerce.order.service;

import java.util.List;

import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.order.domain.DefaultFullfillmentGroup;
import org.broadleafcommerce.order.domain.FullfillmentGroup;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.profile.domain.ContactInfo;
import org.broadleafcommerce.profile.domain.Customer;

public interface OrderService {

    public Order findCurrentBasketForCustomer(Customer customer);

    public DefaultFullfillmentGroup findDefaultFullfillmentGroupForOrder(Order order);

    public List<FullfillmentGroup> findFullfillmentGroupsForOrder(Order order);

    public List<Order> findOrdersForCustomer(Customer customer);

    public List<OrderItem> findItemsForOrder(Order order);

    public Order addContactInfoToOrder(Order order, ContactInfo contactInfo);

    public OrderItem addItemToOrder(Order order, Sku item, int quantity);

    public PaymentInfo addPaymentToOrder(Order order, PaymentInfo payment);

    public FullfillmentGroup addItemToFullfillmentGroup(OrderItem item, FullfillmentGroup fullfillmentGroup, int quantity);

    public FullfillmentGroup addFullfillmentGroupToOrder(Order order, FullfillmentGroup fullfillmentGroup);

    public FullfillmentGroup updateFullfillmentGroup(FullfillmentGroup fullfillmentGroup);

    public OrderItem updateItemInOrder(Order order, OrderItem item);

    public void removeFullfillmentGroupFromOrder(Order order, FullfillmentGroup fullfillmentGroup);

    public Order removeItemFromOrder(Order order, OrderItem item);

    public Order calculateOrderTotal(Order order);

    public Order confirmOrder(Order order);

    public void cancelOrder(Order order);
}
