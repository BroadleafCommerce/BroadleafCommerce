package org.broadleafcommerce.order.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.springframework.stereotype.Repository;

@Repository("orderItemDao")
public class OrderItemDaoJpa implements OrderItemDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext
    private EntityManager em;

    @Override
    public OrderItem maintainOrderItem(OrderItem orderItem) {
        if (orderItem.getId() == null) {
            em.persist(orderItem);
        } else {
            orderItem = em.merge(orderItem);
        }
        return orderItem;
    }

    @Override
    public OrderItem readOrderItemById(Long orderItemId) {
        return em.find(OrderItem.class, orderItemId);
    }

    @Override
    public void deleteOrderItem(OrderItem orderItem) {
        OrderItem deleteItem = em.getReference(OrderItem.class, orderItem.getId());
        em.remove(deleteItem);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<OrderItem> readOrderItemsForOrder(Order order) {
        Query query = em.createNamedQuery("READ_ORDER_ITEMS_BY_ORDER_ID");
        query.setParameter("orderId", order.getId());
        return query.getResultList();
    }
}
