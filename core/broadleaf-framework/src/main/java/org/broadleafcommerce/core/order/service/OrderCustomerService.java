package org.broadleafcommerce.core.order.service;

import org.broadleafcommerce.core.order.domain.OrderCustomer;

public interface OrderCustomerService {
    
    public OrderCustomer findOrderCustomerByExternalId(Long externalId);
    
    public OrderCustomer createOrderCustomer();
    
    public OrderCustomer save(OrderCustomer orderCustomer);
    
}
