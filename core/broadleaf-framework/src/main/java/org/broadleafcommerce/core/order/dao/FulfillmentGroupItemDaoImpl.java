/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
