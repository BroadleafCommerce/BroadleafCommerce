package org.broadleafcommerce.order.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderShipping;
import org.springframework.stereotype.Repository;

@Repository("orderShippingDao")
public class OrderShippingDaoJpa implements OrderShippingDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext
    private EntityManager em;


    @Override
    public OrderShipping maintainOrderShipping(
            OrderShipping shipping) {
        if(shipping.getId() == null){
            em.persist(shipping);
        }else{
            shipping = em.merge(shipping);
        }

        return shipping;
    }

    @Override
    public OrderShipping readOrderShippingById(Long shippingId) {
        return em.find(OrderShipping.class, shippingId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<OrderShipping> readOrderShippingForOrder(Order order) {
        Query query = em.createQuery("SELECT orderShipping FROM org.broadleafcommerce.order.domain.OrderShipping orderShipping WHERE orderShipping.order.id = :orderId");
        query.setParameter("orderId", order.getId());
        return query.getResultList();
    }

}
