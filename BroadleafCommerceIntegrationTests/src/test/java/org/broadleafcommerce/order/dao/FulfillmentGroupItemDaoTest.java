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

import org.broadleafcommerce.order.dao.FulfillmentGroupItemDao;
import org.broadleafcommerce.order.dao.OrderDao;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class FulfillmentGroupItemDaoTest extends BaseTest {

    private FulfillmentGroup fulfillmentGroup;
    private Long fulfillmentGroupItemId;

    @Resource
    private FulfillmentGroupItemDao fulfillmentGroupItemDao;

    @Resource
    private CustomerService customerService;

    @Resource
    private OrderDao orderDao;

    @Test(groups = { "createFulfillmentGroupItem" }, dependsOnGroups = { "createOrder", "createDiscreteOrderItem", "createDefaultFulfillmentGroup" })
    @Rollback(false)
    public void createFulfillmentGroupItem() {
        String userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        Order salesOrder = (orderDao.readOrdersForCustomer(customer.getId())).get(0);
        OrderItem orderItem = salesOrder.getOrderItems().get(0);
        fulfillmentGroup = salesOrder.getFulfillmentGroups().get(0);

        assert fulfillmentGroup != null;

        FulfillmentGroupItem fgi = fulfillmentGroupItemDao.create();
        fgi.setFulfillmentGroup(fulfillmentGroup);
        fgi.setOrderItem(orderItem);
        fgi.setQuantity(orderItem.getQuantity());

        assert fgi.getId() == null;
        fgi = fulfillmentGroupItemDao.save(fgi);
        assert fgi.getId() != null;
        fulfillmentGroupItemId = fgi.getId();

    }

    @Test(groups = { "readFulfillmentGroupItemsForFulfillmentGroup" }, dependsOnGroups = { "createFulfillmentGroupItem" })
    public void readFulfillmentGroupItemsForFulfillmentGroup() {
        List<FulfillmentGroupItem> fgis = fulfillmentGroupItemDao.readFulfillmentGroupItemsForFulfillmentGroup(fulfillmentGroup);
        assert fgis != null;
        assert fgis.size() > 0;
    }

    @Test(groups = { "readFulfillmentGroupItemsById" }, dependsOnGroups = { "createFulfillmentGroupItem" })
    public void readFulfillmentGroupItemsById() {
        FulfillmentGroupItem fgi = fulfillmentGroupItemDao.readFulfillmentGroupItemById(fulfillmentGroupItemId);
        assert fgi != null;
        assert fgi.getId() != null;
    }
}
