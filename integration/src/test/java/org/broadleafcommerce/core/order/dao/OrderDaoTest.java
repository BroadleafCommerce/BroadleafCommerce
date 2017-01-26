/*
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.order.dao;

import org.broadleafcommerce.core.order.OrderDataProvider;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.test.TestNGSiteIntegrationSetup;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import java.util.List;

import javax.annotation.Resource;

public class OrderDaoTest extends TestNGSiteIntegrationSetup {

    String userName = new String();
    Long orderId;

    @Resource
    private OrderDao orderDao;

    @Resource
    private CustomerService customerService;

    @Test(groups = { "createOrder" }, dataProvider = "basicOrder", dataProviderClass = OrderDataProvider.class, dependsOnGroups = { "readCustomer", "createPhone" })
    @Rollback(false)
    @Transactional
    public void createOrder(Order order) {
        userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        assert order.getId() == null;
        order.setCustomer(customer);
        order = orderDao.save(order);
        assert order.getId() != null;
        orderId = order.getId();
    }

    @Test(groups = { "readOrder" }, dependsOnGroups = { "createOrder" })
    public void readOrderById() {
        Order result = orderDao.readOrderById(orderId);
        assert result != null;
    }

    @Test(groups = { "readOrdersForCustomer" }, dependsOnGroups = { "readCustomer", "createOrder" })
    @Transactional
    public void readOrdersForCustomer() {
        userName = "customer1";
        Customer user = customerService.readCustomerByUsername(userName);
        List<Order> orders = orderDao.readOrdersForCustomer(user.getId());
        assert orders.size() > 0;
    }

    //FIXME: After the change to cascading the deletion on PaymentResponseItems, this test does not work but for a really
    //strange reason; the list of PaymentResponseItems is getting removed from the Hibernate session for some really weird
    //reason. This only occurs sometimes, so it is probably due to the somewhat random ordering that TestNG puts around tests
//    @Test(groups = {"deleteOrderForCustomer"}, dependsOnGroups = {"readOrder"})
//    @Transactional
//    public void deleteOrderForCustomer(){
//        Order order = orderDao.readOrderById(orderId);
//        assert order != null;
//        assert order.getId() != null;
//        orderDao.delete(order);
//    }
}
