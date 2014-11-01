/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.order.dao;

import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderImpl;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.profile.core.dao.CustomerDao;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.ListIterator;

@Repository("blOrderDao")
public class OrderDaoImpl implements OrderDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Resource(name = "blCustomerDao")
    protected CustomerDao customerDao;

    @Resource(name = "blOrderDaoExtensionManager")
    protected OrderDaoExtensionManager extensionManager;

    @Override
    public Order readOrderById(final Long orderId) {
        return em.find(OrderImpl.class, orderId);
    }

    @Override
    public Order readOrderById(final Long orderId, boolean refresh) {
        Order order = readOrderById(orderId);
        if (refresh) {
            em.refresh(order);
        }
        return order;
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
        if (customer.getUsername() == null) {
            customer.setUsername(String.valueOf(customer.getId()));
            if (customerDao.readCustomerById(customer.getId()) != null) {
                throw new IllegalArgumentException("Attempting to save a customer with an id (" + customer.getId() + ") " +
                        "that already exists in the database. This can occur when legacy customers have been migrated to " +
                        "Broadleaf customers, but the batchStart setting has not been declared for id generation. In " +
                        "such a case, the defaultBatchStart property of IdGenerationDaoImpl (spring id of " +
                        "blIdGenerationDao) should be set to the appropriate start value.");
            }
            customer = customerDao.save(customer);
        }
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
    @SuppressWarnings("unchecked")
    public Order readNamedOrderForCustomer(final Customer customer, final String name) {
        final Query query = em.createNamedQuery("BC_READ_NAMED_ORDER_FOR_CUSTOMER");
        query.setParameter("customerId", customer.getId());
        query.setParameter("orderStatus", OrderStatus.NAMED.getType());
        query.setParameter("orderName", name);
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
    public Order updatePrices(Order order) {
        order = em.merge(order);
        if (order.updatePrices()) {
            order = save(order);
        }
        return order;
    }

}
