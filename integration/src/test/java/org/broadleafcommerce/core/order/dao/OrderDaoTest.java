/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.order.dao;

import org.broadleafcommerce.core.order.OrderDataProvider;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.test.BaseTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import java.util.List;

import javax.annotation.Resource;

public class OrderDaoTest extends BaseTest {

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
