package org.broadleafcommerce.order.dao;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.broadleafcommerce.type.FulfillmentGroupType;
import org.springframework.stereotype.Repository;

@Repository("fulfillmentGroupDao")
public class FulfillmentGroupDaoJpa implements FulfillmentGroupDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext(unitName = "blPU")
    private EntityManager em;

    @Resource
    private EntityConfiguration entityConfiguration;

    @Override
    public FulfillmentGroup maintainFulfillmentGroup(FulfillmentGroup fulfillmentGroup) {
        return em.merge(fulfillmentGroup);
    }

    @SuppressWarnings("unchecked")
    @Override
    public FulfillmentGroup readFulfillmentGroupById(Long fulfillmentGroupId) {
        return (FulfillmentGroup) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.order.domain.FulfillmentGroup"), fulfillmentGroupId);
    }

    @Override
    public FulfillmentGroupImpl maintainDefaultFulfillmentGroup(FulfillmentGroupImpl defaultFulfillmentGroup) {
        if (defaultFulfillmentGroup.getId() == null) {
            em.persist(defaultFulfillmentGroup);
            em.flush();
        } else {
            defaultFulfillmentGroup = em.merge(defaultFulfillmentGroup);
        }
        return defaultFulfillmentGroup;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FulfillmentGroupImpl readDefaultFulfillmentGroupById(Long fulfillmentGroupId) {
        return (FulfillmentGroupImpl) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.order.domain.FulfillmentGroup"), fulfillmentGroupId);
    }

    @Override
    public FulfillmentGroupImpl readDefaultFulfillmentGroupForOrder(Order order) {
        Query query = em.createNamedQuery("BC_READ_DEFAULT_FULFILLMENT_GROUP_BY_ORDER_ID");
        query.setParameter("orderId", order.getId());
        return (FulfillmentGroupImpl) query.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void removeFulfillmentGroupForOrder(Order order, FulfillmentGroup fulfillmentGroup) {
        fulfillmentGroup = (FulfillmentGroup) em.getReference(entityConfiguration.lookupEntityClass("org.broadleafcommerce.order.domain.FulfillmentGroup"), fulfillmentGroup.getId());
        em.remove(fulfillmentGroup);
    }

    @Override
    public FulfillmentGroupImpl createDefault() {
        FulfillmentGroupImpl fg = ((FulfillmentGroupImpl) entityConfiguration.createEntityInstance("org.broadleafcommerce.order.domain.FulfillmentGroup"));
        fg.setType(FulfillmentGroupType.DEFAULT);
        return fg;
    }

    @Override
    public FulfillmentGroup create() {
        return ((FulfillmentGroup) entityConfiguration.createEntityInstance("org.broadleafcommerce.order.domain.FulfillmentGroup"));
    }
}
