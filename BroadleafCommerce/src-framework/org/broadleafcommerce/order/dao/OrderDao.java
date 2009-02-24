package org.broadleafcommerce.order.dao;

import java.util.List;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.profile.domain.Customer;

public interface OrderDao {

    public Order readOrderById(Long orderId);

    public Order maintianOrder(Order order);

    public List<Order> readOrdersForCustomer(Customer customer);

    public List<Order> readOrdersForCustomer(Long id);

    public void deleteOrderForCustomer(Order order);

    public Order readBasketOrdersForCustomer(Customer customer);

    public Order submitOrder(Order basketOrder);

    public Order create();
}
