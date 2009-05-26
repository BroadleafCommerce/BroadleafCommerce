package org.broadleafcommerce.order.dao;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.domain.Auditable;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderImpl;
import org.broadleafcommerce.order.service.type.OrderStatus;
import org.broadleafcommerce.profile.dao.CustomerDao;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("orderDao")
public class OrderDaoJpa implements OrderDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource
    protected EntityConfiguration entityConfiguration;

    @Resource
    protected CustomerDao customerDao;

    @Override
    @SuppressWarnings("unchecked")
    public Order readOrderById(Long orderId) {
        return (Order) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.order.domain.Order"), orderId);
    }

    @Override
    public Order save(Order order) {
        if (order.getId() == null) {
            em.persist(order);
        } else {
            order = em.merge(order);
        }
        return order;
    }

    @Override
    public void delete(Order salesOrder) {
        em.remove(salesOrder);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Order> readOrdersForCustomer(Customer customer, OrderStatus orderStatus) {
        if (orderStatus == null) {
            return readOrdersForCustomer(customer.getId());
        } else {
            Query query = em.createNamedQuery("BC_READ_ORDERS_BY_CUSTOMER_ID_AND_STATUS");
            query.setParameter("customerId", customer.getId());
            query.setParameter("orderStatus", orderStatus.getName());
            return query.getResultList();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Order> readOrdersForCustomer(Long customerId) {
        Query query = em.createNamedQuery("BC_READ_ORDERS_BY_CUSTOMER_ID");
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Order readCartForCustomer(Customer customer) {
        Order order = null;
        Query query = em.createNamedQuery("BC_READ_ORDERS_BY_CUSTOMER_ID_AND_NAME_NULL");
        query.setParameter("customerId", customer.getId());
        query.setParameter("orderStatus", OrderStatus.IN_PROCESS.getName());
        List<Order> result = query.getResultList();
        if (result.size() > 0) {
            order = result.get(0);
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
        order.setStatus(OrderStatus.IN_PROCESS.getName());

        order = save(order);

        return order;
    }

    @Override
    public Order submitOrder(Order cartOrder) {
        Order order = create();
        order.setId(cartOrder.getId());
        Query query = em.createNamedQuery("BC_UPDATE_CART_ORDER_TO_SUBMITTED");
        query.setParameter("id", cartOrder.getId());
        query.executeUpdate();
        return em.find(OrderImpl.class, order.getId());
    }

    public Order create() {
        Order order = ((Order) entityConfiguration.createEntityInstance("org.broadleafcommerce.order.domain.Order"));
        Auditable auditable = new Auditable();
        auditable.setDateCreated(new Date());
        order.setAuditable(auditable);

        return order;
    }

    @Override
    public Order readNamedOrderForCustomer(Customer customer, String name) {
        Query query = em.createNamedQuery("BC_READ_NAMED_ORDER_FOR_CUSTOMER");
        query.setParameter("customerId", customer.getId());
        query.setParameter("orderStatus", OrderStatus.NAMED.getName());
        query.setParameter("orderName", name);
        return (Order) query.getSingleResult();
    }
}
