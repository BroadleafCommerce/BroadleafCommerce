package org.broadleafcommerce.core.order.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.core.order.domain.OrderCustomer;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Repository("blOrderCustomerDao")
public class OrderCustomerDaoImpl implements OrderCustomerDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;
    
    @Override
    public OrderCustomer readOrderCustomerByExternalId(Long externalId) {
        TypedQuery<OrderCustomer> q = em.createQuery("SELECT c FROM " + OrderCustomer.class.getName() + "c WHERE c.externalId = :externalId", OrderCustomer.class);
        q.setParameter("externalId", externalId);
        return q.getSingleResult();
    }

    @Override
    public OrderCustomer createOrderCustomer() {
        return (OrderCustomer) entityConfiguration.createEntityInstance(OrderCustomer.class.getName());
    }

    @Override
    public OrderCustomer save(OrderCustomer orderCustomer) {
        return em.merge(orderCustomer);
    }
}
