package org.broadleafcommerce.order.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("fulfillmentGroupItemDao")
public class FulfillmentGroupItemDaoJpa implements FulfillmentGroupItemDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext
    private EntityManager em;

    @Resource
    private EntityConfiguration entityConfiguration;

    @Override
    public void deleteFulfillmentGroupItem(FulfillmentGroupItem fulfillmentGroupItem) {
        em.remove(fulfillmentGroupItem);
    }

    @Override
    public FulfillmentGroupItem maintainFulfillmentGroupItem(FulfillmentGroupItem fulfillmentGroupItem) {
        if (fulfillmentGroupItem.getId() == null) {
            em.persist(fulfillmentGroupItem);
        } else {
            fulfillmentGroupItem = em.merge(fulfillmentGroupItem);
        }

        return fulfillmentGroupItem;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FulfillmentGroupItem readFulfillmentGroupItemById(Long fulfillmentGroupItemId) {
        return (FulfillmentGroupItem) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.order.domain.FulfillmentGroupItem"), fulfillmentGroupItemId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<FulfillmentGroupItem> readFulfillmentGroupItemsForFulfillmentGroup(FulfillmentGroup fulfillmentGroup) {
        Query query = em.createNamedQuery("READ_FULFILLMENT_GROUP_ITEM_BY_FULFILLMENT_GROUP_ID");
        query.setParameter("fulfillmentGroupId", fulfillmentGroup.getId());
        return query.getResultList();
    }

    @Override
    public FulfillmentGroupItem create() {
        return ((FulfillmentGroupItem) entityConfiguration.createEntityInstance("org.broadleafcommerce.order.domain.FulfillmentGroupItem"));
    }

}
