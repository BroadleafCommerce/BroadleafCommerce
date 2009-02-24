package org.broadleafcommerce.order.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.order.domain.DefaultFulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("fulfillmentGroupDao")
public class FulfillmentGroupDaoJpa implements FulfillmentGroupDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext
    private EntityManager em;

    @Resource
    private EntityConfiguration entityConfiguration;

    @Override
    public FulfillmentGroup maintainFulfillmentGroup(FulfillmentGroup fulfillmentGroup) {
        if (fulfillmentGroup.getId() == null) {
            em.persist(fulfillmentGroup);
        } else {
            fulfillmentGroup = em.merge(fulfillmentGroup);
        }

        return fulfillmentGroup;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FulfillmentGroup readFulfillmentGroupById(Long fulfillmentGroupId) {
        return (FulfillmentGroup) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.order.domain.FulfillmentGroup"), fulfillmentGroupId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<FulfillmentGroup> readFulfillmentGroupsForOrder(Order order) {
        Query query = em.createNamedQuery("READ_FULFILLMENT_GROUPS_BY_ORDER_ID");
        query.setParameter("orderId", order.getId());
        return query.getResultList();
    }

    @Override
    public DefaultFulfillmentGroup maintainDefaultFulfillmentGroup(DefaultFulfillmentGroup defaultFulfillmentGroup) {
        if (defaultFulfillmentGroup.getId() == null) {
            em.persist(defaultFulfillmentGroup);
        } else {
            defaultFulfillmentGroup = em.merge(defaultFulfillmentGroup);
        }
        return defaultFulfillmentGroup;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DefaultFulfillmentGroup readDefaultFulfillmentGroupById(Long fulfillmentGroupId) {
        return (DefaultFulfillmentGroup) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.order.domain.DefaultFulfillmentGroup"), fulfillmentGroupId);
    }

    @Override
    public DefaultFulfillmentGroup readDefaultFulfillmentGroupForOrder(Order order) {
        Query query = em.createNamedQuery("READ_DEFAULT_FULFILLMENT_GROUP_BY_ORDER_ID");
        query.setParameter("orderId", order.getId());
        return (DefaultFulfillmentGroup) query.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void removeFulfillmentGroupForOrder(Order order, FulfillmentGroup fulfillmentGroup) {
        fulfillmentGroup.setOrderId(order.getId());
        fulfillmentGroup = (FulfillmentGroup) em.getReference(entityConfiguration.lookupEntityClass("org.broadleafcommerce.order.domain.DefaultFulfillmentGroup"), fulfillmentGroup.getId());
        em.remove(fulfillmentGroup);
    }

    @Override
    public DefaultFulfillmentGroup createDefault() {
        return ((DefaultFulfillmentGroup) entityConfiguration.createEntityInstance("org.broadleafcommerce.order.domain.DefaultFulfillmentGroup"));
    }

    @Override
    public FulfillmentGroup create() {
        return ((FulfillmentGroup) entityConfiguration.createEntityInstance("org.broadleafcommerce.order.domain.FulfillmentGroup"));
    }
}
