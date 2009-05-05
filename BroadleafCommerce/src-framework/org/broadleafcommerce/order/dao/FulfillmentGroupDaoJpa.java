package org.broadleafcommerce.order.dao;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.service.type.FulfillmentGroupType;
import org.broadleafcommerce.profile.util.EntityConfiguration;
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
    public FulfillmentGroup save(FulfillmentGroup fulfillmentGroup) {
        fulfillmentGroup = em.merge(fulfillmentGroup);
        return fulfillmentGroup;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FulfillmentGroup readFulfillmentGroupById(Long fulfillmentGroupId) {
        return (FulfillmentGroup) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.order.domain.FulfillmentGroup"), fulfillmentGroupId);
    }

    @Override
    public FulfillmentGroupImpl readDefaultFulfillmentGroupForOrder(Order order) {
        Query query = em.createNamedQuery("BC_READ_DEFAULT_FULFILLMENT_GROUP_BY_ORDER_ID");
        query.setParameter("orderId", order.getId());
        FulfillmentGroupImpl result;
        try {
            result = (FulfillmentGroupImpl) query.getSingleResult();
        } catch (NoResultException e) {
            result = null;
        }

        return result;
    }

    @Override
    public void delete(FulfillmentGroup fulfillmentGroup) {
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
