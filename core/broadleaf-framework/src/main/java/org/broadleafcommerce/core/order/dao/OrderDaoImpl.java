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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.util.StreamCapableTransactionalOperationAdapter;
import org.broadleafcommerce.common.util.StreamingTransactionCapableUtil;
import org.broadleafcommerce.common.util.dao.TypedQueryBuilder;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.order.domain.NullOrderImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderImpl;
import org.broadleafcommerce.core.order.domain.OrderLock;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.hibernate.jpa.AvailableSettings;
import org.hibernate.jpa.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import javax.annotation.Resource;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Repository("blOrderDao")
public class OrderDaoImpl implements OrderDao {

    private static final Log LOG = LogFactory.getLog(OrderDaoImpl.class);
    private static final String ORDER_LOCK_KEY = UUID.randomUUID().toString();

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Resource(name = "blOrderDaoExtensionManager")
    protected OrderDaoExtensionManager extensionManager;

    @Resource(name = "blStreamingTransactionCapableUtil")
    protected StreamingTransactionCapableUtil transUtil;
    
    @Override
    public Order readOrderById(final Long orderId) {
        return em.find(OrderImpl.class, orderId);
    }

    @Override
    public Order readOrderByIdIgnoreCache(final Long orderId) {
        Map<String, Object> m = new HashMap<>();
        m.put(AvailableSettings.SHARED_CACHE_RETRIEVE_MODE, CacheRetrieveMode.BYPASS);
        return em.find(OrderImpl.class, orderId, m);
    }

    @Override
    public Order readOrderByExternalId(String orderExternalId) {
        TypedQuery<Order> query = new TypedQueryBuilder<Order>(Order.class, "ord")
                .addRestriction("ord.embeddedOmsOrder.externalId", "=", orderExternalId)
                .toQuery(em);

        try {
            return query.getSingleResult();
            //potentially we can get exception because externalId field is added in oms module that is not mandatory to have
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    @Transactional("blTransactionManager")
    public Order readOrderById(final Long orderId, boolean refresh) {
        Order order = readOrderById(orderId);
        if (refresh) {
            em.refresh(order);
        }
        return order;
    }

    @Override
    public List<Order> readOrdersByIds(List<Long> orderIds) {
        if (orderIds == null || orderIds.size() == 0) {
            return null;
        }
        if (orderIds.size() > 100) {
            LOG.warn("Not recommended to use the readOrdersByIds method for long lists of orderIds, since " +
                    "Hibernate is required to transform the distinct results. The list of requested" +
                    "order ids was (" + orderIds.size() + ") in length.");
        }
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Order> criteria = builder.createQuery(Order.class);
        Root<OrderImpl> order = criteria.from(OrderImpl.class);
        criteria.select(order);

        // We only want results that match the order IDs
        criteria.where(order.get("id").as(Long.class).in(orderIds));

        TypedQuery<Order> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Order");

        return query.getResultList();
    }

    @Override
    public List<Order> readBatchOrders(int start, int pageSize, List<OrderStatus> statuses) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Order> criteria = builder.createQuery(Order.class);
        Root<OrderImpl> order = criteria.from(OrderImpl.class);
        criteria.select(order);

        if (CollectionUtils.isNotEmpty(statuses)) {
            // We only want results that match the orders with the correct status
            ArrayList<String> statusStrings = new ArrayList<String>();
            for (OrderStatus status : statuses) {
                statusStrings.add(status.getType());
            }
            criteria.where(order.get("status").as(String.class).in(statusStrings));
        }

        TypedQuery<Order> query = em.createQuery(criteria);
        query.setFirstResult(start);
        query.setMaxResults(pageSize);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Order");

        return query.getResultList();
    }

    @Override
    public Order save(final Order order) {
        Order response = em.merge(order);
        //em.flush();
        return response;
    }

    @Override
    public void delete(Order salesOrder) {
        if (!em.contains(salesOrder)) {
            salesOrder = readOrderById(salesOrder.getId());
        }

        //need to null out the reference to the Order for all the OrderPayments
        //as they are not deleted but Archived.
        for (OrderPayment payment : salesOrder.getPayments()) {
            payment.setOrder(null);
            payment.setArchived('Y');
            for (PaymentTransaction transaction : payment.getTransactions()) {
                transaction.setArchived('Y');
            }
        }

        em.remove(salesOrder);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Order> readOrdersForCustomer(final Customer customer, final OrderStatus orderStatus) {
        if (orderStatus == null) {
            return readOrdersForCustomer(customer.getId());
        } else {
            final Query query = em.createNamedQuery("BC_READ_ORDERS_BY_CUSTOMER_ID_AND_STATUS");
            query.setParameter("customerId", customer.getId());
            query.setParameter("orderStatus", orderStatus.getType());
            return query.getResultList();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Order> readOrdersForCustomer(final Long customerId) {
        final Query query = em.createNamedQuery("BC_READ_ORDERS_BY_CUSTOMER_ID");
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }

    @Override
    public Order readCartForCustomer(final Customer customer) {
        Order order = null;
        final Query query = em.createNamedQuery("BC_READ_ORDERS_BY_CUSTOMER_ID_AND_NAME_NULL");
        query.setParameter("customerId", customer.getId());
        query.setParameter("orderStatus", OrderStatus.IN_PROCESS.getType());
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Order");
        @SuppressWarnings("rawtypes")
        final List temp = query.getResultList();
        if (temp != null && !temp.isEmpty()) {
            order = (Order) temp.get(0);
        }
        return order;
    }

    @Override
    public Order createNewCartForCustomer(Customer customer) {
        Order order = create();
        order.setCustomer(customer);
        order.setEmailAddress(customer.getEmailAddress());
        order.setStatus(OrderStatus.IN_PROCESS);

        if (BroadleafRequestContext.getBroadleafRequestContext() != null) {
            order.setCurrency(BroadleafRequestContext.getBroadleafRequestContext().getBroadleafCurrency());
            order.setLocale(BroadleafRequestContext.getBroadleafRequestContext().getLocale());
        }

        if (extensionManager != null) {
            extensionManager.getProxy().attachAdditionalDataToNewCart(customer, order);
        }
        
        order = save(order);

        if (extensionManager != null) {
            extensionManager.getProxy().processPostSaveNewCart(customer, order);
        }

        return order;
    }

    @Override
    public Order submitOrder(final Order cartOrder) {
        cartOrder.setStatus(OrderStatus.SUBMITTED);
        return save(cartOrder);
    }

    @Override
    public Order create() {
        final Order order = ((Order) entityConfiguration.createEntityInstance("org.broadleafcommerce.core.order.domain.Order"));

        return order;
    }

    @Override
    public void refresh(Order order) {
        if (order != null && !(order instanceof NullOrderImpl)) {
            em.refresh(order);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Order readNamedOrderForCustomer(final Customer customer, final String name) {
        final Query query = em.createNamedQuery("BC_READ_NAMED_ORDER_FOR_CUSTOMER");
        query.setParameter("customerId", customer.getId());
        query.setParameter("orderStatus", OrderStatus.NAMED.getType());
        query.setParameter("orderName", name);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Order");
        List<Order> orders = query.getResultList();
        
        // Filter out orders that don't match the current locale (if one is set)
        if (BroadleafRequestContext.getBroadleafRequestContext() != null) {
            ListIterator<Order> iter = orders.listIterator();
            while (iter.hasNext()) {
                Locale locale = BroadleafRequestContext.getBroadleafRequestContext().getLocale();
                Order order = iter.next();
                if (locale != null && !locale.equals(order.getLocale())) {
                    iter.remove();
                }
            }
        }
            
        // Apply any additional filters that extension modules have registered
        if (orders != null && !orders.isEmpty() && extensionManager != null) {
            extensionManager.getProxy().applyAdditionalOrderLookupFilter(customer, name, orders);
        }
        
        return orders == null || orders.isEmpty() ? null : orders.get(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Order readOrderByOrderNumber(final String orderNumber) {
        if (orderNumber == null || "".equals(orderNumber)) {
            return null;
        }

        final Query query = em.createNamedQuery("BC_READ_ORDER_BY_ORDER_NUMBER");
        query.setParameter("orderNumber", orderNumber);
        List<Order> orders = query.getResultList();
        return orders == null || orders.isEmpty() ? null : orders.get(0);
    }

    @Override
    public List<Order> readOrdersByDateRange(final Date startDate, final Date endDate) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Order> criteria = builder.createQuery(Order.class);
        Root<OrderImpl> order = criteria.from(OrderImpl.class);
        criteria.select(order);
        criteria.where(builder.between(order.<Date>get("submitDate"), startDate, endDate));
        criteria.orderBy(builder.desc(order.get("submitDate")));

        TypedQuery<Order> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Order");

        return query.getResultList();
    }

    @Override
    public List<Order> readOrdersForCustomersInDateRange(List<Long> customerIds, Date startDate, Date endDate) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Order> criteria = builder.createQuery(Order.class);
        Root<OrderImpl> order = criteria.from(OrderImpl.class);
        criteria.select(order);

        List<Predicate> restrictions = new ArrayList<>();
        restrictions.add(builder.between(order.<Date>get("submitDate"), startDate, endDate));
        restrictions.add(order.get("customer").get("id").in(customerIds));
        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));

        criteria.orderBy(builder.desc(order.get("customer")), builder.asc(order.get("submitDate")));

        TypedQuery<Order> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Order");

        return query.getResultList();
    }

    @Override
    @Transactional("blTransactionManager")
    public Order updatePrices(Order order) {
        order = em.merge(order);
        if (order.updatePrices()) {
            order = save(order);
        }
        return order;
    }

    @Override
    public boolean acquireLock(Order order) {
        String orderLockKey = getOrderLockKey();
        // First, we'll see if there's a record of a lock for this order
        Query q = em.createNamedQuery("BC_ORDER_LOCK_READ");
        q.setParameter("orderId", order.getId());
        q.setParameter("key", orderLockKey);
        q.setHint(QueryHints.HINT_CACHEABLE, false);
        Long count = (Long) q.getSingleResult();
        
        if (count == 0L) {
            // If there wasn't a lock, we'll try to create one. It's possible that another thread is attempting the
            // same thing at the same time, so we might get a constraint violation exception here. That's ok. If we 
            // successfully inserted a record, that means that we are the owner of the lock right now.
            try {
                OrderLock ol = (OrderLock) entityConfiguration.createEntityInstance(OrderLock.class.getName());
                ol.setOrderId(order.getId());
                ol.setLocked(true);
                ol.setKey(orderLockKey);
                ol.setLastUpdated(System.currentTimeMillis());
                em.persist(ol);
                return true;
            } catch (EntityExistsException e) {
                return false;
            }
        }

        // We weren't successful in creating a lock, which means that there was some previously created lock
        // for this order. We'll attempt to update the status from unlocked to locked. If that is successful,
        // we acquired the lock. 
        q = em.createNamedQuery("BC_ORDER_LOCK_ACQUIRE");
        q.setParameter("orderId", order.getId());
        q.setParameter("currentTime", System.currentTimeMillis());
        q.setParameter("key", orderLockKey);
        Long orderLockTimeToLive = getDatabaseOrderLockTimeToLive();
        q.setParameter("timeout", orderLockTimeToLive==-1L?orderLockTimeToLive:System.currentTimeMillis() - orderLockTimeToLive);
        q.setHint(QueryHints.HINT_CACHEABLE, false);
        int rowsAffected = q.executeUpdate();

        return rowsAffected == 1;
    }

    @Override
    public boolean releaseLock(final Order order) {
        final boolean[] response = {false};
        try {
            transUtil.runTransactionalOperation(new StreamCapableTransactionalOperationAdapter() {
                @Override
                public void execute() throws Throwable {
                    Query q = em.createNamedQuery("BC_ORDER_LOCK_RELEASE");
                    q.setParameter("orderId", order.getId());
                    q.setParameter("key", getOrderLockKey());
                    q.setHint(QueryHints.HINT_CACHEABLE, false);
                    int rowsAffected = q.executeUpdate();
                    response[0] = rowsAffected == 1;
                }

                @Override
                public boolean shouldRetryOnTransactionLockAcquisitionFailure() {
                    return true;
                }
            }, RuntimeException.class);
        } catch (RuntimeException e) {
            LOG.error(String.format("Could not release order lock (%s)", order.getId()), e);
        }
        return response[0];
    }

    protected String getOrderLockKey() {
        return getDatabaseOrderLockSessionAffinity()?ORDER_LOCK_KEY:"NO_KEY";
    }

    protected Boolean getDatabaseOrderLockSessionAffinity() {
        return BLCSystemProperty.resolveBooleanSystemProperty("order.lock.database.session.affinity", true);
    }

    protected Long getDatabaseOrderLockTimeToLive() {
        return BLCSystemProperty.resolveLongSystemProperty("order.lock.database.time.to.live", -1L);
    }

    @Override
    public List<Order> readOrdersByEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            return Collections.emptyList();
        }
        TypedQuery<Order> query = em.createNamedQuery("BC_READ_ORDERS_BY_EMAIL", Order.class);
        query.setParameter("email", email);
        List<Order> orders = query.getResultList();
        return orders != null ? orders : new ArrayList<Order>();
    }

    @Override
    public Long readNumberOfOrders() {
    	 CriteriaBuilder builder = em.getCriteriaBuilder();
         CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
         criteria.select(builder.count(criteria.from(OrderImpl.class)));
         TypedQuery<Long> query = em.createQuery(criteria);
         return query.getSingleResult();
    }
}
