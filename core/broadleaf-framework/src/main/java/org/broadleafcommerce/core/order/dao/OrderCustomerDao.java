package org.broadleafcommerce.core.order.dao;

import org.broadleafcommerce.core.order.domain.OrderCustomer;

public interface OrderCustomerDao {
    
    public OrderCustomer readOrderCustomerByExternalId(Long externalId);
    
    public OrderCustomer createOrderCustomer();
    
    public OrderCustomer save(OrderCustomer orderCustomer);

}
