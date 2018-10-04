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

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.service.type.OrderStatus;
import org.broadleafcommerce.profile.dao.CustomerDao;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.broadleafcommerce.time.SystemTime;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;

@Repository("blOrderDao")
public class OrderDaoImpl implements org.broadleafcommerce.order.dao.OrderDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Resource(name="blCustomerDao")
    protected CustomerDao customerDao;

    @Resource(name = "webDS")
    protected DataSource dataSource;

    @SuppressWarnings("unchecked")
    public Order readOrderById(Long orderId) {
        return (Order) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.order.domain.Order"), orderId);
    }

    @Override
    public Order readOrderById(final Long orderId, boolean refresh) {
        Order order = readOrderById(orderId);
        if (refresh) {
            em.refresh(order);
        }
        return order;
    }

    public Order save(Order order) {
        if (order.getAuditable() != null) {
            order.getAuditable().setDateUpdated(SystemTime.asDate());
        }
        if (order.getId() == null) {
            em.persist(order);
        } else {
            order = em.merge(order);
        }
        return order;
    }

    public void delete(Order salesOrder) {
        em.remove(salesOrder);
    }

    @SuppressWarnings("unchecked")
    public List<Order> readOrdersForCustomer(Customer customer, OrderStatus orderStatus) {
        if (orderStatus == null) {
            return readOrdersForCustomer(customer.getId());
        } else {
            Query query = em.createNamedQuery("BC_READ_ORDERS_BY_CUSTOMER_ID_AND_STATUS");
            query.setParameter("customerId", customer.getId());
            query.setParameter("orderStatus", orderStatus.getType());
            return query.getResultList();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Order> readOrdersForCustomer(Long customerId) {
        Query query = em.createNamedQuery("BC_READ_ORDERS_BY_CUSTOMER_ID");
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public Order readCartForCustomer(Customer customer) {
        Order order = null;
        Query query = em.createNamedQuery("BC_READ_ORDERS_BY_CUSTOMER_ID_AND_NAME_NULL");
        query.setParameter("customerId", customer.getId());
        query.setParameter("orderStatus", OrderStatus.IN_PROCESS.getType());
        List temp = query.getResultList();
        if (temp.size() > 0) {
            order = (Order) temp.get(0);
        }
        return order;
    }

    public Order createNewCartForCustomer(Customer customer) {
        Order order = create();
        if (customer.getUsername() == null) {
            customer.setUsername(String.valueOf(customer.getId()));
            customer = customerDao.save(customer);
        }
        order.setCustomer(customer);
        order.setEmailAddress(customer.getEmailAddress());
        order.setStatus(OrderStatus.IN_PROCESS);

        order = save(order);

        return order;
    }

    public Order submitOrder(Order cartOrder) {
        cartOrder.setStatus(OrderStatus.SUBMITTED);
        return save(cartOrder);
    }

    public Order create() {
        Order order = ((Order) entityConfiguration.createEntityInstance("org.broadleafcommerce.order.domain.Order"));
        order.getAuditable().setDateCreated(SystemTime.asDate());

        return order;
    }

    public Order updatePrices(Order order) {
        order = em.merge(order);
        if (order.updatePrices()) {
            order = save(order);
        }
        return order;
    }

    @SuppressWarnings("unchecked")
    public Order readNamedOrderForCustomer(Customer customer, String name) {
        Query query = em.createNamedQuery("BC_READ_NAMED_ORDER_FOR_CUSTOMER");
        query.setParameter("customerId", customer.getId());
        query.setParameter("orderStatus", OrderStatus.NAMED.getType());
        query.setParameter("orderName", name);
        List<Order> orders = query.getResultList();
        return orders == null || orders.isEmpty() ? null : orders.get(0);
    }

    @SuppressWarnings("unchecked")
    public Order readOrderByOrderNumber(String orderNumber) {
        if (orderNumber == null || "".equals(orderNumber)) {
            return null;
        }

        Order order = null;
        Query query = em.createNamedQuery("BC_READ_ORDER_BY_ORDER_NUMBER");
        query.setParameter("orderNumber", orderNumber);
        List<Order> result = query.getResultList();
        if (result.size() > 0) {
            order = result.get(0);
        }
        return order;
    }

    @Override
    public boolean acquireLock(Order order) {
        JdbcTemplate template = new JdbcTemplate(dataSource);

        // First, we'll see if there's a record of a lock for this order
        boolean lockExists = template.queryForObject("SELECT COUNT(ORDER_ID) FROM BLC_ORDER_LOCK WHERE ORDER_ID = ?",
                                                     new Object[] { order.getId() }, Integer.class) == 1;

        if (!lockExists) {
            // If there wasn't a lock, we'll try to create one. It's possible that another thread is attempting the
            // same thing at the same time, so we might get a constraint violation exception here. That's ok. If we
            // successfully inserted a record, that means that we are the owner of the lock right now.
            int rowsAffected;
            try {
                rowsAffected = template.update("INSERT INTO BLC_ORDER_LOCK(ORDER_ID, LOCKED) VALUES (?, ?)",
                                               order.getId(), "Y");
            } catch (ConstraintViolationException e) {
                rowsAffected = 0;
            }

            if (rowsAffected == 1) {
                return true;
            }
        }
        // We weren't successful in creating a lock, which means that there was some previously created lock
        // for this order. We'll attempt to update the status from unlocked to locked. If that is succesful,
        // we acquired the lock.
        int rowsAffected = template.update("UPDATE BLC_ORDER_LOCK SET LOCKED = ? WHERE ORDER_ID = ? AND LOCKED = ?",
                                           "Y", order.getId().toString(), "N");
        return rowsAffected == 1;
    }

    @Override
    public boolean releaseLock(Order order) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        int rowsAffected = template.update("UPDATE BLC_ORDER_LOCK SET LOCKED = ? WHERE ORDER_ID = ?",
                                           "N", order.getId().toString());
        return rowsAffected == 1;
    }
}
