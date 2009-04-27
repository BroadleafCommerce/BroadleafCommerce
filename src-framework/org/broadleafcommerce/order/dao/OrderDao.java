package org.broadleafcommerce.order.dao;

import java.util.List;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.service.type.OrderStatus;
import org.broadleafcommerce.profile.domain.Customer;

public interface OrderDao {

    public Order readOrderById(Long orderId);

    public List<Order> readOrdersForCustomer(Customer customer, OrderStatus orderStatus);
    
    public List<Order> readOrdersForCustomer(Long id);
    
    public Order readNamedOrderForCustomer(Customer customer, String name);
    
    public Order readCartForCustomer(Customer customer, boolean persist);
    
    public Order maintianOrder(Order order);

    public void deleteOrderForCustomer(Order order);

    public Order submitOrder(Order cartOrder);

    public Order create();

//    removed methods
//    public List<Order> readNamedOrdersForcustomer(Customer customer);
//    
//    public Order readOrderForCustomer(Long customerId, Long orderId);
//    
//    public List<Order> readSubmittedOrdersForCustomer(Customer customer);
//    
}
