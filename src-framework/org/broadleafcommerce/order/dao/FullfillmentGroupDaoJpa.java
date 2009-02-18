package org.broadleafcommerce.order.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.order.domain.DefaultFullfillmentGroup;
import org.broadleafcommerce.order.domain.FullfillmentGroup;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("fullfillmentGroupDao")
public class FullfillmentGroupDaoJpa implements FullfillmentGroupDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @Resource
    private EntityConfiguration entityConfiguration;

    @PersistenceContext
    private EntityManager em;

    @Override
    public FullfillmentGroup maintainFullfillmentGroup(FullfillmentGroup fullfillmentGroup) {
        if (fullfillmentGroup.getId() == null) {
            em.persist(fullfillmentGroup);
        } else {
            fullfillmentGroup = em.merge(fullfillmentGroup);
        }

        return fullfillmentGroup;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FullfillmentGroup readFullfillmentGroupById(Long fullfillmentGroupId) {
        return (FullfillmentGroup) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.order.domain.FullfillmentGroup"), fullfillmentGroupId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<FullfillmentGroup> readFullfillmentGroupsForOrder(Order order) {
        Query query = em.createNamedQuery("READ_FULLFILLMENT_GROUPS_BY_ORDER_ID");
        query.setParameter("orderId", order.getId());
        return query.getResultList();
    }

    @Override
    public DefaultFullfillmentGroup maintainDefaultFullfillmentGroup(DefaultFullfillmentGroup defaultFullfillmentGroup) {
        if (defaultFullfillmentGroup.getId() == null) {
            em.persist(defaultFullfillmentGroup);
        } else {
            defaultFullfillmentGroup = em.merge(defaultFullfillmentGroup);
        }
        return defaultFullfillmentGroup;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DefaultFullfillmentGroup readDefaultFullfillmentGroupById(Long fullfillmentGroupId) {
        return (DefaultFullfillmentGroup) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.order.domain.DefaultFullfillmentGroup"), fullfillmentGroupId);
    }

    @Override
    public DefaultFullfillmentGroup readDefaultFullfillmentGroupForOrder(Order order) {
        Query query = em.createNamedQuery("READ_DEFAULT_FULLFILLMENT_GROUP_BY_ORDER_ID");
        query.setParameter("orderId", order.getId());
        return (DefaultFullfillmentGroup) query.getSingleResult();

    }

    @Override
    public DefaultFullfillmentGroup createDefault() {
        return ((DefaultFullfillmentGroup) entityConfiguration.createEntityInstance("org.broadleafcommerce.order.domain.DefaultFullfillmentGroup"));
    }

    @Override
    public FullfillmentGroup create() {
        return ((FullfillmentGroup) entityConfiguration.createEntityInstance("org.broadleafcommerce.order.domain.FullfillmentGroup"));
    }

}
