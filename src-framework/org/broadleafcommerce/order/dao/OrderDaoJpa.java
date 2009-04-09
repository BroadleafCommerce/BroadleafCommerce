package org.broadleafcommerce.order.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderImpl;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.broadleafcommerce.type.OrderStatus;
import org.springframework.stereotype.Repository;

@Repository("orderDao")
public class OrderDaoJpa implements OrderDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext(unitName="blPU")
    private EntityManager em;

    @Resource
    private EntityConfiguration entityConfiguration;

    @Override
    @SuppressWarnings("unchecked")
    public Order readOrderById(Long orderId) {
        return (Order) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.order.domain.Order"), orderId);
    }

    @Override
    public Order maintianOrder(Order order) {
        if (order.getId() == null) {
            em.persist(order);
        } else {
            order = em.merge(order);
        }
        return order;
    }

    @Override
    public void deleteOrderForCustomer(Order salesOrder) {
        em.remove(salesOrder);
    }

    @Override 
    @SuppressWarnings("unchecked")
    public List<Order> readOrdersForCustomer(Customer customer, OrderStatus orderStatus) {
        if(orderStatus == null) {
            return readOrdersForCustomer(customer.getId());
        }else {            
            Query query = em.createNamedQuery("BC_READ_ORDERS_BY_CUSTOMER_ID_AND_STATUS");
            query.setParameter("customerId", customer.getId());
            query.setParameter("orderStatus", orderStatus);
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

    @Override
    public Order readCartForCustomer(Customer customer, boolean persist) {
        Order bo = null;
        Query query = em.createNamedQuery("BC_READ_ORDERS_BY_CUSTOMER_ID_AND_NAME_NULL");
        query.setParameter("customerId", customer.getId());
        query.setParameter("orderStatus", OrderStatus.IN_PROCESS);
//        query.setParameter("orderName", null);
        try {
            bo = (Order) query.getSingleResult();
            return (Order) query.getSingleResult();
        } catch (NoResultException nre) {
        	if (persist) {
	            bo = (Order) entityConfiguration.createEntityInstance("org.broadleafcommerce.order.domain.Order");
	            bo.setCustomer(customer);
	            bo.setStatus(OrderStatus.IN_PROCESS);
	            em.persist(bo);
        	}
            return bo;
        }
    }

    @Override
    public Order submitOrder(Order cartOrder) {
        Order so = create();
        so.setId(cartOrder.getId());
        Query query = em.createNamedQuery("BC_UPDATE_CART_ORDER_TO_SUBMITTED");
        query.setParameter("id", cartOrder.getId());
        query.executeUpdate();
        return em.find(OrderImpl.class, so.getId());
    }

    public Order create() {
        return ((Order) entityConfiguration.createEntityInstance("org.broadleafcommerce.order.domain.OrderImpl"));
    }

    @Override
    public Order readNamedOrderForCustomer(Customer customer, String name) {
        Query query = em.createNamedQuery("BC_READ_NAMED_ORDER_FOR_CUSTOMER");
        query.setParameter("customerId", customer.getId());
        query.setParameter("orderStatus", OrderStatus.IN_PROCESS);
        query.setParameter("orderName", name);
        return (Order)query.getSingleResult();
    }

    //   Removed Methods
//    
//    @Override
//    public Order readOrderForCustomer(Long customerId, Long orderId) {
//        Query query = em.createNamedQuery("BC_READ_ORDER_BY_CUSTOMER_ID");
//        query.setParameter("orderId", orderId);
//        query.setParameter("customerId", customerId);
//        return (Order) query.getSingleResult();
//    }
//
//    @Override
//    @SuppressWarnings("unchecked")
//    public List<Order> readSubmittedOrdersForCustomer(Customer customer) {
//        Query query = em.createNamedQuery("BC_READ_ORDERS_BY_CUSTOMER_ID_AND_TYPE");
//        query.setParameter("customerId", customer.getId());
//        query.setParameter("orderType", OrderType.SUBMITTED);
//        return query.getResultList();
//    }
//    
//    @Override
//    @SuppressWarnings("unchecked")
//    public List<Order> readNamedOrdersForcustomer(Customer customer) {
//        Query query = em.createNamedQuery("BC_READ_ORDERS_BY_CUSTOMER_ID_AND_TYPE");
//        query.setParameter("customerId", customer.getId());
//        query.setParameter("orderType", OrderType.NAMED);
//        return query.getResultList();
//    }
//    

}
