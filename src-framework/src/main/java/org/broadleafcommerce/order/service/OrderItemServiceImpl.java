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
package org.broadleafcommerce.order.service;

import javax.annotation.Resource;

import org.broadleafcommerce.order.dao.OrderItemDao;
import org.broadleafcommerce.order.domain.BundleOrderItem;
import org.broadleafcommerce.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.order.domain.GiftWrapOrderItem;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.domain.PersonalMessage;
import org.broadleafcommerce.order.service.call.BundleOrderItemRequest;
import org.broadleafcommerce.order.service.call.DiscreteOrderItemRequest;
import org.broadleafcommerce.order.service.call.GiftWrapOrderItemRequest;
import org.broadleafcommerce.order.service.call.OrderItemRequest;
import org.broadleafcommerce.order.service.type.OrderItemType;
import org.springframework.stereotype.Service;

@Service("blOrderItemService")
public class OrderItemServiceImpl implements OrderItemService {

    @Resource(name="blOrderItemDao")
    protected OrderItemDao orderItemDao;

    public OrderItem readOrderItemById(Long orderItemId) {
        return orderItemDao.readOrderItemById(orderItemId);
    }

    public OrderItem saveOrderItem(OrderItem orderItem) {
        return orderItemDao.saveOrderItem(orderItem);
    }

    public OrderItem createOrderItem(OrderItemRequest itemRequest) {
        OrderItem orderItem = null;
        if (itemRequest instanceof DiscreteOrderItem) {
            orderItem = createDiscreteOrderItem((DiscreteOrderItemRequest) itemRequest);
        } else if (itemRequest instanceof GiftWrapOrderItem) {
            orderItem = createGiftWrapOrderItem((GiftWrapOrderItemRequest) itemRequest);
        } else if (itemRequest instanceof BundleOrderItemRequest) {
            orderItem = createBundleOrderItem((BundleOrderItemRequest) itemRequest);
        }
        return orderItem;
    }

    public DiscreteOrderItem createDiscreteOrderItem(DiscreteOrderItemRequest itemRequest) {
        DiscreteOrderItem item = (DiscreteOrderItem) orderItemDao.create(OrderItemType.DISCRETE);
        item.setSku(itemRequest.getSku());
        item.setQuantity(itemRequest.getQuantity());
        item.setCategory(itemRequest.getCategory());
        item.setProduct(itemRequest.getProduct());
        item.setSalePrice(itemRequest.getSku().getSalePrice());
        item.setRetailPrice(itemRequest.getSku().getRetailPrice());
        item.assignFinalPrice();
        item.setPersonalMessage(itemRequest.getPersonalMessage());

        return item;
    }

    public GiftWrapOrderItem createGiftWrapOrderItem(GiftWrapOrderItemRequest itemRequest) {
        GiftWrapOrderItem item = (GiftWrapOrderItem) orderItemDao.create(OrderItemType.GIFTWRAP);
        item.setSku(itemRequest.getSku());
        item.setQuantity(itemRequest.getQuantity());
        item.setCategory(itemRequest.getCategory());
        item.setProduct(itemRequest.getProduct());
        item.setSalePrice(itemRequest.getSku().getSalePrice());
        item.setRetailPrice(itemRequest.getSku().getRetailPrice());
        item.assignFinalPrice();
        item.getWrappedItems().addAll(itemRequest.getWrappedItems());
        for (OrderItem orderItem : item.getWrappedItems()) {
            orderItem.setGiftWrapOrderItem(item);
        }

        return item;
    }

    public BundleOrderItem createBundleOrderItem(BundleOrderItemRequest itemRequest) {
        BundleOrderItem item = (BundleOrderItem) orderItemDao.create(OrderItemType.BUNDLE);
        item.setQuantity(itemRequest.getQuantity());
        item.setCategory(itemRequest.getCategory());
        item.setName(itemRequest.getName());

        for (DiscreteOrderItemRequest discreteItemRequest : itemRequest.getDiscreteOrderItems()) {
            DiscreteOrderItem discreteOrderItem = createDiscreteOrderItem(discreteItemRequest);
            discreteOrderItem.setBundleOrderItem(item);
            item.getDiscreteOrderItems().add(discreteOrderItem);
            item.assignFinalPrice();
        }

        return item;
    }

    public void delete(OrderItem item) {
        orderItemDao.delete(item);
    }

    public PersonalMessage createPersonalMessage() {
        return orderItemDao.createPersonalMessage();
    }
}
