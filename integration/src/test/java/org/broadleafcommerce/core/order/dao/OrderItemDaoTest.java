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

import org.broadleafcommerce.core.catalog.dao.SkuDao;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.order.OrderItemDataProvider;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.GiftWrapOrderItem;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.test.BaseTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import javax.annotation.Resource;

public class OrderItemDaoTest extends BaseTest {

    private Long orderItemId;
    private Long giftWrapItemId;

    @Resource
    private OrderItemDao orderItemDao;

    @Resource
    private SkuDao skuDao;

    @Test(groups = { "createDiscreteOrderItem" }, dataProvider = "basicDiscreteOrderItem", dataProviderClass = OrderItemDataProvider.class, dependsOnGroups = { "createOrder", "createSku" })
    @Rollback(false)
    @Transactional
    public void createDiscreteOrderItem(DiscreteOrderItem orderItem) {
        Sku si = skuDao.readFirstSku();
        assert si.getId() != null;
        orderItem.setSku(si);
        assert orderItem.getId() == null;
        
        orderItem = (DiscreteOrderItem) orderItemDao.save(orderItem);
        assert orderItem.getId() != null;
        orderItemId = orderItem.getId();
    }

    @Test(groups = { "createGiftWrapOrderItem" }, dataProvider = "basicGiftWrapOrderItem", dataProviderClass = OrderItemDataProvider.class, dependsOnGroups = { "readOrderItemsById" })
    @Rollback(false)
    @Transactional
    public void createGiftWrapOrderItem(GiftWrapOrderItem orderItem) {
        Sku si = skuDao.readFirstSku();
        assert si.getId() != null;
        orderItem.setSku(si);
        assert orderItem.getId() == null;

        OrderItem discreteItem = orderItemDao.readOrderItemById(orderItemId);
        orderItem.getWrappedItems().add(discreteItem);
        discreteItem.setGiftWrapOrderItem(orderItem);

        orderItem = (GiftWrapOrderItem) orderItemDao.save(orderItem);
        assert orderItem.getId() != null;
        giftWrapItemId = orderItem.getId();
    }

    @Test(groups = { "readGiftWrapOrderItemsById" }, dependsOnGroups = { "createGiftWrapOrderItem" })
    @Transactional
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
