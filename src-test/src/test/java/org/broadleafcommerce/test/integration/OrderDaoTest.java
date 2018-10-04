/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.test.integration;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.order.dao.OrderDao;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.test.dataprovider.OrderDataProvider;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class OrderDaoTest extends BaseTest {

    String userName = new String();
    Long orderId;

    @Resource
    private OrderDao orderDao;

    @Resource
    private CustomerService customerService;

    @Test(groups = { "createOrder" }, dataProvider = "basicOrder", dataProviderClass = OrderDataProvider.class, dependsOnGroups = { "readCustomer1", "createPhone" })
    @Rollback(false)
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

    @Test(groups = { "readOrdersForCustomer" }, dependsOnGroups = { "readCustomer1", "createOrder" })
    public void readOrdersForCustomer() {
        userName = "customer1";
        Customer user = customerService.readCustomerByUsername(userName);
        List<Order> orders = orderDao.readOrdersForCustomer(user.getId());
        assert orders.size() > 0;
    }

    @Test(groups = {"deleteOrderForCustomer"}, dependsOnGroups = {"createOrder"})
    public void deleteOrderForCustomer(){
        Order order = orderDao.readOrderById(orderId);
        assert order != null;
        assert order.getId() != null;
        orderDao.delete(order);
    }
}
