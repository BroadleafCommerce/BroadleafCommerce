package org.broadleafcommerce.order.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderPayment;
import org.springframework.stereotype.Repository;

@Repository("orderPaymentDao")
public class OrderPaymentDaoJpa implements OrderPaymentDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext
    private EntityManager em;

    @Override
    public OrderPayment maintainOrderPayment(OrderPayment orderPayment) {
        if (orderPayment.getId() == null) {
            em.persist(orderPayment);
        } else {
            orderPayment = em.merge(orderPayment);
        }
        return orderPayment;
    }

    @Override
    public OrderPayment readOrderPaymentById(Long paymentId) {
        return em.find(OrderPayment.class, paymentId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<OrderPayment> readOrderPaymentsForOrder(Order order) {
        Query query = em.createNamedQuery("READ_ORDERS_PAYMENTS_BY_ORDER_ID");
        query.setParameter("orderId", order.getId());
        return query.getResultList();
    }
}
