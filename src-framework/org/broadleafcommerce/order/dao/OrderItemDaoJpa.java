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
package org.broadleafcommerce.order.dao;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.order.domain.GiftWrapOrderItem;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.domain.OrderItemImpl;
import org.broadleafcommerce.order.service.type.OrderItemType;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("blOrderItemDao")
public class OrderItemDaoJpa implements OrderItemDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource
    protected EntityConfiguration entityConfiguration;

    @Override
    public OrderItem save(OrderItem orderItem) {
        if (orderItem.getId() == null) {
            em.persist(orderItem);
        } else {
            orderItem = em.merge(orderItem);
        }
        return orderItem;
    }

    @Override
    public OrderItem readOrderItemById(Long orderItemId) {
        return em.find(OrderItemImpl.class, orderItemId);
    }

    @Override
    public void delete(OrderItem orderItem) {
        if (GiftWrapOrderItem.class.isAssignableFrom(orderItem.getClass())) {
            GiftWrapOrderItem giftItem = (GiftWrapOrderItem) orderItem;
            for (OrderItem wrappedItem : giftItem.getWrappedItems()) {
                wrappedItem.setGiftWrapOrderItem(null);
                save(wrappedItem);
            }
        }
        em.remove(orderItem);
    }

    public OrderItem create(OrderItemType orderItemType) {
        return (OrderItem) entityConfiguration.createEntityInstance(orderItemType.getClassName());
    }

    @Override
    public OrderItem saveOrderItem(OrderItem orderItem) {
        return em.merge(orderItem);
    }
}
