package org.broadleafcommerce.core.order.service;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderCustomer;
import org.broadleafcommerce.core.order.service.call.MergeCartResponse;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

@Service("blOrderCustomerFacadeService")
public class OrderCustomerFacadeServiceImpl implements OrderCustomerFacadeService {

    @Resource(name = "blOrderCustomerService")
    protected OrderCustomerService orderCustomerService;
    
    @Resource(name = "blOrderService")
    protected OrderService orderService;
    
    @Resource(name = "blMergeCartService")
    protected MergeCartService mergeCartService;
    
    @Override
    public List<Order> findOrdersForCustomer(Customer customer, OrderStatus orderStatus) {
        OrderCustomer orderCustomer = findOrCreateOrderCustomerFromCustomer(customer);
        if (orderCustomer == null) {
            return null;
        }
        return orderService.findOrdersForCustomer(orderCustomer, orderStatus);
    }

    @Override
    public Order findCartForCustomer(Customer customer) {
        OrderCustomer orderCustomer = findOrCreateOrderCustomerFromCustomer(customer);
        if (orderCustomer == null) {
            return null;
        }
        return orderService.findCartForCustomer(orderCustomer);
    }

    @Override
    public Order createNamedOrderForCustomer(String name, Customer customer) {
        OrderCustomer orderCustomer = findOrCreateOrderCustomerFromCustomer(customer);
        if (orderCustomer == null) {
            return null;
        }
        return orderService.createNamedOrderForCustomer(name, orderCustomer);
    }

    @Override
    public Order findNamedOrderForCustomer(String name, Customer customer) {
        OrderCustomer orderCustomer = findOrCreateOrderCustomerFromCustomer(customer);
        if (orderCustomer == null) {
            return null;
        }
        return orderService.findNamedOrderForCustomer(name, orderCustomer);
    }
    
    @Override
    public Order createNewCartForCustomer(Customer customer) {
        OrderCustomer orderCustomer = findOrCreateOrderCustomerFromCustomer(customer);
        if (orderCustomer == null) {
            return null;
        }
        return orderService.createNewCartForCustomer(orderCustomer);
    }
    
    @Override
    public MergeCartResponse mergeCart(Customer customer, Order order) throws PricingException, RemoveFromCartException {
        OrderCustomer orderCustomer = findOrCreateOrderCustomerFromCustomer(customer);
        if (orderCustomer == null) {
            return null;
        }
        return mergeCartService.mergeCart(orderCustomer, order);
    }
    
    protected OrderCustomer findOrCreateOrderCustomerFromCustomer(Customer customer) {
        if (customer == null || customer.getId() == null) {
            return null;
        }
        OrderCustomer orderCustomer = orderCustomerService.findOrderCustomerByExternalId(customer.getId());
        if (orderCustomer == null) {
            orderCustomer = orderCustomerService.createOrderCustomer();
            if (StringUtils.isNotBlank(customer.getEmailAddress())) {
                orderCustomer.setEmailAddress(customer.getEmailAddress());
            }
            if (StringUtils.isNotBlank(customer.getFirstName())) {
                orderCustomer.setFirstName(customer.getFirstName());
            }
            if (StringUtils.isNoneBlank(customer.getLastName())) {
                orderCustomer.setLastName(customer.getLastName());
            }
            orderCustomer.setExternalId(customer.getId());
            orderCustomer = orderCustomerService.save(orderCustomer);
        }
        return orderCustomer;
    }

}
