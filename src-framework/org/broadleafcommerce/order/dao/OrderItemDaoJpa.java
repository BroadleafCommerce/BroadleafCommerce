package org.broadleafcommerce.order.dao;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("orderItemDao")
public class OrderItemDaoJpa implements OrderItemDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext(unitName="blPU")
    private EntityManager em;

    @Resource
    private EntityConfiguration entityConfiguration;

    @Override
    public OrderItem maintainOrderItem(OrderItem orderItem) {
        if (orderItem.getId() == null) {
            em.persist(orderItem);
        } else {
            orderItem = em.merge(orderItem);
        }
        return orderItem;
    }

    @SuppressWarnings("unchecked")
    @Override
    public OrderItem readOrderItemById(Long orderItemId) {
        return (OrderItem) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.order.domain.OrderItem"), orderItemId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void deleteOrderItem(OrderItem orderItem) {
        OrderItem deleteItem = (OrderItem) em.getReference(entityConfiguration.lookupEntityClass("org.broadleafcommerce.order.domain.OrderItem"), orderItem.getId());
        em.remove(deleteItem);
    }

    public OrderItem create() {
        return ((OrderItem) entityConfiguration.createEntityInstance("org.broadleafcommerce.order.domain.OrderItem"));
    }
}
