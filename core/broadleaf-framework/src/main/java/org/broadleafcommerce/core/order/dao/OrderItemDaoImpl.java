/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.core.order.domain.GiftWrapOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderImpl;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemImpl;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetail;
import org.broadleafcommerce.core.order.domain.OrderItemQualifier;
import org.broadleafcommerce.core.order.domain.PersonalMessage;
import org.broadleafcommerce.core.order.service.type.OrderItemType;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.hibernate.jpa.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository("blOrderItemDao")
public class OrderItemDaoImpl implements OrderItemDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
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
        PersonalMessage personalMessage = (PersonalMessage) entityConfiguration.createEntityInstance(
                PersonalMessage.class.getName()
        );
        return personalMessage;
    }

    @Override
    @Transactional("blTransactionManager")
    public OrderItem saveOrderItem(final OrderItem orderItem) {
        return em.merge(orderItem);
    }

    @Override
    public OrderItemPriceDetail createOrderItemPriceDetail() {
        return (OrderItemPriceDetail) entityConfiguration.createEntityInstance(OrderItemPriceDetail.class.getName());
    }

    @Override
    public OrderItemQualifier createOrderItemQualifier() {
        return (OrderItemQualifier) entityConfiguration.createEntityInstance(OrderItemQualifier.class.getName());
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

    @Override
    public Long readNumberOfOrderItems() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        criteria.select(builder.count(criteria.from(OrderItemImpl.class)));
        TypedQuery<Long> query = em.createQuery(criteria);
        return query.getSingleResult();
    }

    @Override
    public List<OrderItem> readBatchOrderItems(int start, int count, List<OrderStatus> statuses) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<OrderItem> criteria = builder.createQuery(OrderItem.class);
        Root<OrderImpl> order = criteria.from(OrderImpl.class);
        Join<Order, OrderItem> orderItems = order.join("orderItems");
        criteria.select(orderItems);

        List<Predicate> restrictions = new ArrayList<>();
        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));

        if (CollectionUtils.isNotEmpty(statuses)) {
            // We only want results that match the orders with the correct status
            ArrayList<String> statusStrings = new ArrayList<>();
            for (OrderStatus status : statuses) {
                statusStrings.add(status.getType());
            }
            criteria.where(order.get("status").as(String.class).in(statusStrings));
        }

        TypedQuery<OrderItem> query = em.createQuery(criteria);
        query.setFirstResult(start);
        query.setMaxResults(count);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Order");

        return query.getResultList();
    }

}
