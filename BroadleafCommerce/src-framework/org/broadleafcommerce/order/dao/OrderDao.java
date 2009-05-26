package org.broadleafcommerce.order.dao;

import java.util.List;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.profile.domain.Customer;

public interface OrderDao {

    public Order readOrderById(Long orderId);

    public List<Order> readOrdersForCustomer(Customer customer, String orderStatus);

    public List<Order> readOrdersForCustomer(Long id);

    public Order readNamedOrderForCustomer(Customer customer, String name);

    public Order readCartForCustomer(Customer customer);

    public Order save(Order order);

    public void delete(Order order);

    public Order submitOrder(Order cartOrder);

    public Order create();

    public Order createNewCartForCustomer(Customer customer);

    //    removed methods
    //    public List<Order> readNamedOrdersForcustomer(Customer customer);
    //
    //    public Order readOrderForCustomer(Long customerId, Long orderId);
    //
    //    public List<Order> readSubmittedOrdersForCustomer(Customer customer);
    //
}
