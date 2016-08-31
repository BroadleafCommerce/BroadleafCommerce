/*
 * #%L
 * BroadleafCommerce Framework
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

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.core.order.domain.GiftWrapOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderImpl;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemImpl;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetail;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetailImpl;
import org.broadleafcommerce.core.order.domain.OrderItemQualifier;
import org.broadleafcommerce.core.order.domain.OrderItemQualifierImpl;
import org.broadleafcommerce.core.order.domain.PersonalMessage;
import org.broadleafcommerce.core.order.service.type.OrderItemType;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Repository("blOrderItemDao")
public class OrderItemDaoImpl implements OrderItemDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    @Transactional("blTransactionManager")
    public OrderItem save(final OrderItem orderItem) {
        return em.merge(orderItem);
    }

    @Override
    public OrderItem readOrderItemById(final Long orderItemId) {
        return em.find(OrderItemImpl.class, orderItemId);
    }

    @Override
    @Transactional("blTransactionManager")
    public void delete(OrderItem orderItem) {
        if (!em.contains(orderItem)) {
            orderItem = readOrderItemById(orderItem.getId());
        }
        if (GiftWrapOrderItem.class.isAssignableFrom(orderItem.getClass())) {
            final GiftWrapOrderItem giftItem = (GiftWrapOrderItem) orderItem;
            for (OrderItem wrappedItem : giftItem.getWrappedItems()) {
                wrappedItem.setGiftWrapOrderItem(null);
                wrappedItem = save(wrappedItem);
            }
        }
        em.remove(orderItem);
        em.flush();
    }

    @Override
    public OrderItem create(final OrderItemType orderItemType) {
        final OrderItem item = (OrderItem) entityConfiguration.createEntityInstance(orderItemType.getType());
        item.setOrderItemType(orderItemType);
        return item;
    }
    
    @Override
    public PersonalMessage createPersonalMessage() {
        PersonalMessage personalMessage = (PersonalMessage) entityConfiguration.createEntityInstance(PersonalMessage.class.getName());
        return personalMessage;
    }

    @Override
    @Transactional("blTransactionManager")
    public OrderItem saveOrderItem(final OrderItem orderItem) {
        return em.merge(orderItem);
    }

    @Override
    public OrderItemPriceDetail createOrderItemPriceDetail() {
        return new OrderItemPriceDetailImpl();
    }

    @Override
    public OrderItemQualifier createOrderItemQualifier() {
        return new OrderItemQualifierImpl();
    }

    @Override
    public OrderItemPriceDetail initializeOrderItemPriceDetails(OrderItem item) {
        OrderItemPriceDetail detail = createOrderItemPriceDetail();
        detail.setOrderItem(item);
        detail.setQuantity(item.getQuantity());
        detail.setUseSalePrice(item.getIsOnSale());
        item.getOrderItemPriceDetails().add(detail);
        return detail;
    }

    @Override
    public List<OrderItem> readOrderItemsForCustomersInDateRange(List<Long> customerIds, Date startDate, Date endDate) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<OrderItem> criteria = builder.createQuery(OrderItem.class);
        Root<OrderImpl> order = criteria.from(OrderImpl.class);
        Join<Order, OrderItem> orderItems = order.join("orderItems");
        criteria.select(orderItems);

        List<Predicate> restrictions = new ArrayList<>();
        restrictions.add(builder.between(order.<Date>get("submitDate"), startDate, endDate));
        restrictions.add(order.get("customer").get("id").in(customerIds));
        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));

        criteria.orderBy(builder.desc(order.get("customer")), builder.asc(order.get("submitDate")));

        TypedQuery<OrderItem> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Order");

        return query.getResultList();
    }
}
