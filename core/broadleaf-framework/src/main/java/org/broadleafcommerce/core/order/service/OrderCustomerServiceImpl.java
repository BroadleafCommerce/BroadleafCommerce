package org.broadleafcommerce.core.order.service;

import org.broadleafcommerce.core.order.dao.OrderCustomerDao;
import org.broadleafcommerce.core.order.domain.OrderCustomer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("blOrderCustomerService")
public class OrderCustomerServiceImpl implements OrderCustomerService {
    
    @Resource(name = "blOrderCustomerDao")
    protected OrderCustomerDao orderCustomerDao;
    
    @Override
    public OrderCustomer findOrderCustomerByExternalId(Long externalId) {
        return orderCustomerDao.readOrderCustomerByExternalId(externalId);
    }

    @Override
    public OrderCustomer createOrderCustomer() {
        return orderCustomerDao.createOrderCustomer();
    }

    @Override
    public OrderCustomer save(OrderCustomer orderCustomer) {
        return orderCustomerDao.save(orderCustomer);
    }

}
