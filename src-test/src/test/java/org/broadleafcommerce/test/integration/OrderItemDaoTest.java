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

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.dao.SkuDao;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.order.dao.OrderDao;
import org.broadleafcommerce.order.dao.OrderItemDao;
import org.broadleafcommerce.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.order.domain.GiftWrapOrderItem;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.service.type.OrderStatus;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.test.dataprovider.OrderItemDataProvider;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class OrderItemDaoTest extends BaseTest {

    private Long orderItemId;
    private Long giftWrapItemId;

    @Resource
    private OrderItemDao orderItemDao;

    @Resource
    private OrderDao orderDao;

    @Resource
    private SkuDao skuDao;

    @Resource
    private CustomerService customerService;

    @Test(groups = { "createDiscreteOrderItem" }, dataProvider = "basicDiscreteOrderItem", dataProviderClass = OrderItemDataProvider.class, dependsOnGroups = { "createOrder", "createSku" })
    @Rollback(false)
    public void createDiscreteOrderItem(DiscreteOrderItem orderItem) {
        String userName = "customer1";
        Sku si = skuDao.readFirstSku();
        assert si.getId() != null;
        orderItem.setSku(si);
        Customer customer = customerService.readCustomerByUsername(userName);
        Order so = orderDao.readCartForCustomer(customer);
        assert so.getStatus().equals(OrderStatus.IN_PROCESS);
        assert so.getId() != null;
        assert orderItem.getId() == null;

        orderItem = (DiscreteOrderItem) orderItemDao.save(orderItem);
        assert orderItem.getId() != null;
        orderItemId = orderItem.getId();
    }

    @Test(groups = { "createGiftWrapOrderItem" }, dataProvider = "basicGiftWrapOrderItem", dataProviderClass = OrderItemDataProvider.class, dependsOnGroups = { "readOrderItemsById" })
    @Rollback(false)
    public void createGiftWrapOrderItem(GiftWrapOrderItem orderItem) {
        String userName = "customer1";
        Sku si = skuDao.readFirstSku();
        assert si.getId() != null;
        orderItem.setSku(si);
        Customer customer = customerService.readCustomerByUsername(userName);
        Order so = orderDao.readCartForCustomer(customer);
        assert so.getStatus().equals(OrderStatus.IN_PROCESS);
        assert so.getId() != null;
        assert orderItem.getId() == null;

        OrderItem discreteItem = orderItemDao.readOrderItemById(orderItemId);
        orderItem.getWrappedItems().add(discreteItem);
        discreteItem.setGiftWrapOrderItem(orderItem);

        orderItem = (GiftWrapOrderItem) orderItemDao.save(orderItem);
        assert orderItem.getId() != null;
        giftWrapItemId = orderItem.getId();
    }

    @Test(groups = { "readGiftWrapOrderItemsById" }, dependsOnGroups = { "createGiftWrapOrderItem" })
    public void readGiftWrapOrderItemsById() {
        assert giftWrapItemId != null;
        OrderItem result = orderItemDao.readOrderItemById(giftWrapItemId);
        assert result != null;
        assert result.getId().equals(giftWrapItemId);
        assert ((GiftWrapOrderItem) result).getWrappedItems().get(0).getId().equals(orderItemId);
    }

    @Test(groups = { "deleteGiftWrapOrderItemsById" }, dependsOnGroups = { "readGiftWrapOrderItemsById" })
    @Rollback(false)
    public void deleteGiftWrapOrderItemsById() {
        OrderItem result = orderItemDao.readOrderItemById(giftWrapItemId);
        orderItemDao.delete(result);
        assert orderItemDao.readOrderItemById(giftWrapItemId) == null;
    }

    @Test(groups = { "readOrderItemsById" }, dependsOnGroups = { "createDiscreteOrderItem" })
    public void readOrderItemsById() {
        assert orderItemId != null;
        OrderItem result = orderItemDao.readOrderItemById(orderItemId);
        assert result != null;
        assert result.getId().equals(orderItemId);
    }

    @Test(groups = { "readOrderItemsByIdAfterGiftWrapDeletion" }, dependsOnGroups = { "deleteGiftWrapOrderItemsById" })
    public void readOrderItemsByIdAfterGiftWrapDeletion() {
        assert orderItemId != null;
        OrderItem result = orderItemDao.readOrderItemById(orderItemId);
        assert result != null;
        assert result.getId().equals(orderItemId);
        assert result.getGiftWrapOrderItem() == null;
    }
}
