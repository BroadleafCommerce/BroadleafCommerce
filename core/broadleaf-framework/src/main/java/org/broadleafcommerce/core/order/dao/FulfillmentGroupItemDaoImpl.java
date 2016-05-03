/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.order.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItemImpl;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository("blFulfillmentGroupItemDao")
public class FulfillmentGroupItemDaoImpl implements FulfillmentGroupItemDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    public void delete(FulfillmentGroupItem fulfillmentGroupItem) {
        if (!em.contains(fulfillmentGroupItem)) {
            fulfillmentGroupItem = readFulfillmentGroupItemById(fulfillmentGroupItem.getId());
        }
        em.remove(fulfillmentGroupItem);
    }

    public FulfillmentGroupItem save(final FulfillmentGroupItem fulfillmentGroupItem) {
        return em.merge(fulfillmentGroupItem);
    }

    public FulfillmentGroupItem readFulfillmentGroupItemById(final Long fulfillmentGroupItemId) {
        return (FulfillmentGroupItem) em.find(FulfillmentGroupItemImpl.class, fulfillmentGroupItemId);
    }

    @SuppressWarnings("unchecked")
    public List<FulfillmentGroupItem> readFulfillmentGroupItemsForFulfillmentGroup(final FulfillmentGroup fulfillmentGroup) {
        final Query query = em.createNamedQuery("BC_READ_FULFILLMENT_GROUP_ITEM_BY_FULFILLMENT_GROUP_ID");
        query.setParameter("fulfillmentGroupId", fulfillmentGroup.getId());
        return query.getResultList();
    }

    public FulfillmentGroupItem create() {
        return ((FulfillmentGroupItem) entityConfiguration.createEntityInstance("org.broadleafcommerce.core.order.domain.FulfillmentGroupItem"));
    }
}
