package org.broadleafcommerce.order.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.order.domain.FullfillmentGroup;
import org.broadleafcommerce.order.domain.FullfillmentGroupItem;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("FullfillmentGroupItemDao")
public class FullfillmentGroupItemDaoJpa implements FullfillmentGroupItemDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @Resource
    private EntityConfiguration entityConfiguration;

    @PersistenceContext
    private EntityManager em;

    @Override
    public void deleteFullfillmentGroupItem(FullfillmentGroupItem fullfillmentGroupItem) {
        em.remove(fullfillmentGroupItem);
    }

    @Override
    public FullfillmentGroupItem maintainFullfillmentGroupItem(
            FullfillmentGroupItem fullfillmentGroupItem) {
        if(fullfillmentGroupItem.getId() == null) {
            em.persist(fullfillmentGroupItem);
        }else{
            fullfillmentGroupItem = em.merge(fullfillmentGroupItem);
        }

        return fullfillmentGroupItem;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FullfillmentGroupItem readFullfillmentGroupItemById(
            Long fullfillmentGroupItemId) {
        return (FullfillmentGroupItem)em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.order.domain.FullfillmentGroupItem"), fullfillmentGroupItemId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<FullfillmentGroupItem> readFullfillmentGroupItemsForFullfillmentGroup(
            FullfillmentGroup fullfillmentGroup) {
        Query query = em.createNamedQuery("READ_FULLFILLMENT_GROUP_ITEM_BY_FULLFILLMENT_GROUP_ID");
        query.setParameter("fullfillmentGroupId", fullfillmentGroup.getId());
        return query.getResultList();
    }

    @Override
    public FullfillmentGroupItem create() {
        return ((FullfillmentGroupItem)entityConfiguration.createEntityInstance("org.broadleafcommerce.order.domain.FullfillmentGroupItem"));
    }



}
