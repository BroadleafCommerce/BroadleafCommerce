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

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.service.type.OrderStatus;
import org.broadleafcommerce.profile.dao.CustomerDao;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("blOrderDao")
public class OrderDaoImpl implements OrderDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Resource(name="blCustomerDao")
    protected CustomerDao customerDao;

    @SuppressWarnings("unchecked")
    public Order readOrderById(Long orderId) {
        return (Order) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.order.domain.Order"), orderId);
    }

    public Order save(Order order) {
        if (order.getAuditable() != null) {
            order.getAuditable().setDateUpdated(new Date());
        }
        return em.merge(order);
    }

    public void delete(Order salesOrder) {
    	if (!em.contains(salesOrder)) {
    		salesOrder = readOrderById(salesOrder.getId());
    	}
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
        order.getAuditable().setDateCreated(new Date());

        return order;
    }

    public Order readNamedOrderForCustomer(Customer customer, String name) {
        Query query = em.createNamedQuery("BC_READ_NAMED_ORDER_FOR_CUSTOMER");
        query.setParameter("customerId", customer.getId());
        query.setParameter("orderStatus", OrderStatus.NAMED.getType());
        query.setParameter("orderName", name);
        return (Order) query.getSingleResult();
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
}
