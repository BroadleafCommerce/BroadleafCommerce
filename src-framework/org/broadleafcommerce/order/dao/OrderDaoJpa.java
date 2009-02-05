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
import org.broadleafcommerce.profile.domain.User;
import org.springframework.stereotype.Repository;

@Repository("orderDao")
public class OrderDaoJpa implements OrderDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext
    private EntityManager em;

    @Override
    public Order readOrderById(Long orderId){
        return em.find(Order.class, orderId);
    }

    @Override
    public Order maintianOrder(Order salesOrder){
        if(salesOrder.getId() == null){
            em.persist(salesOrder);
        }else{
            salesOrder = em.merge(salesOrder);
        }
        return salesOrder;
    }

    @Override
    public void deleteOrderForUser(Order salesOrder) {
        em.remove(salesOrder);
    }

    @Override
    public List<Order> readOrdersForUser(User user){
        return readOrdersForUser(user.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Order> readOrdersForUser(Long userId){
        Query query = em.createQuery("SELECT order FROM org.broadleafcommerce.order.domain.Order order WHERE order.user.id = :userId");
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public BasketOrder readBasketOrderForUser(User user) {
        BasketOrder bo;
        Query query = em.createQuery("SELECT basketOrder FROM org.broadleafcommerce.order.domain.BasketOrder basketOrder WHERE basketOrder.user.id = :userId");
        query.setParameter("userId", user.getId());
        try{
            bo = (BasketOrder)query.getSingleResult();
            return (BasketOrder)query.getSingleResult();
        }catch(NoResultException nre){
            bo = new BasketOrder();
            bo.setUser(user);
            em.persist(bo);
            return bo;
        }
    }

    @Override
    public SubmittedOrder submitOrder(Order basketOrder) {
        SubmittedOrder so = new SubmittedOrder();
        so.setId(basketOrder.getId());
        Query query = em.createQuery("UPDATE org.broadleafcommerce.order.domain.Order orderx SET TYPE = 'SUBMITTED' WHERE orderx.id = :id");
        query.setParameter("id", basketOrder.getId());
        query.executeUpdate();
        return em.find(SubmittedOrder.class, so.getId());
    }




}
