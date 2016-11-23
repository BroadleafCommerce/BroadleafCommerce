package org.broadleafcommerce.core.order.service;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.call.MergeCartResponse;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.core.domain.Customer;

import java.util.List;

public interface OrderCustomerFacadeService {
    
    public List<Order> findOrdersForCustomer(Customer customer, OrderStatus orderStatus);
    
    public Order findCartForCustomer(Customer customer);
    
    public Order createNamedOrderForCustomer(String name, Customer customer);
    
    public Order findNamedOrderForCustomer(String name, Customer customer);
    
    public Order createNewCartForCustomer(Customer customer);
    
    public MergeCartResponse mergeCart(Customer customer, Order order) throws PricingException, RemoveFromCartException;
    
}
