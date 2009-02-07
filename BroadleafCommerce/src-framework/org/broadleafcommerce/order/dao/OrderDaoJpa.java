package org.broadleafcommerce.order.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.order.domain.BasketOrder;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.SubmittedOrder;
import org.broadleafcommerce.profile.domain.Customer;
import org.springframework.stereotype.Repository;

@Repository("orderDao")
public class OrderDaoJpa implements OrderDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext
    private EntityManager em;

    @Override
    public Order readOrderById(Long orderId) {
        return em.find(Order.class, orderId);
    }

    @Override
    public Order maintianOrder(Order salesOrder) {
        if (salesOrder.getId() == null) {
            em.persist(salesOrder);
        } else {
            salesOrder = em.merge(salesOrder);
        }
        return salesOrder;
    }

    @Override
    public void deleteOrderForCustomer(Order salesOrder) {
        em.remove(salesOrder);
    }

    @Override
    public List<Order> readOrdersForCustomer(Customer customer) {
        return readOrdersForCustomer(customer.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Order> readOrdersForCustomer(Long customerId) {
        Query query = em.createNamedQuery("READ_ORDERS_BY_CUSTOMER_ID");
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }

    @Override
    public BasketOrder readBasketOrderForCustomer(Customer customer) {
        BasketOrder bo;
        Query query = em.createNamedQuery("READ_ORDER_BASKET_FOR_CUSTOMER_ID");
        query.setParameter("customerId", customer.getId());
        try {
            bo = (BasketOrder) query.getSingleResult();
            return (BasketOrder) query.getSingleResult();
        } catch (NoResultException nre) {
            bo = new BasketOrder();
            bo.setCustomer(customer);
            em.persist(bo);
            return bo;
        }
    }

    @Override
    public SubmittedOrder submitOrder(Order basketOrder) {
        SubmittedOrder so = new SubmittedOrder();
        so.setId(basketOrder.getId());
        Query query = em.createNamedQuery("UPDATE_BASKET_ORER_TO_SUBMITED");
        query.setParameter("id", basketOrder.getId());
        query.executeUpdate();
        return em.find(SubmittedOrder.class, so.getId());
    }
}
