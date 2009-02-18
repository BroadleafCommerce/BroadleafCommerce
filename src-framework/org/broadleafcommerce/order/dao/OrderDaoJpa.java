package org.broadleafcommerce.order.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.order.domain.BasketOrder;
import org.broadleafcommerce.order.domain.BroadleafBasketOrder;
import org.broadleafcommerce.order.domain.BroadleafSubmittedOrder;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("orderDao")
public class OrderDaoJpa implements OrderDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @Resource
    private EntityConfiguration entityConfiguration;
    
    @PersistenceContext
    private EntityManager em;

    @Override
    @SuppressWarnings("unchecked")
    public Order readOrderById(Long orderId) {
    	return (Order)em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.order.domain.Order"), orderId);
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
            bo = (BroadleafBasketOrder) query.getSingleResult();
            return (BroadleafBasketOrder) query.getSingleResult();
        } catch (NoResultException nre) {
            bo = (BasketOrder)entityConfiguration.createEntityInstance("org.broadleafcommerce.order.domain.BasketOrder");
            bo.setCustomer(customer);
            em.persist(bo);
            return bo;
        }
    }

    @Override
    public BroadleafSubmittedOrder submitOrder(Order basketOrder) {
        BroadleafSubmittedOrder so = new BroadleafSubmittedOrder();
        so.setId(basketOrder.getId());
        Query query = em.createNamedQuery("UPDATE_BASKET_ORER_TO_SUBMITED");
        query.setParameter("id", basketOrder.getId());
        query.executeUpdate();
        return em.find(BroadleafSubmittedOrder.class, so.getId());
    }
    
    public Order create(){
		return ((Order)entityConfiguration.createEntityInstance("order"));
	}
}
