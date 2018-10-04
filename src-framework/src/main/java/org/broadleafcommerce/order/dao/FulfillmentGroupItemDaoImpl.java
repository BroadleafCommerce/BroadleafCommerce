/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.order.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("blFulfillmentGroupItemDao")
public class FulfillmentGroupItemDaoImpl implements FulfillmentGroupItemDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    public void delete(FulfillmentGroupItem fulfillmentGroupItem) {
        em.remove(fulfillmentGroupItem);
    }

    public FulfillmentGroupItem save(FulfillmentGroupItem fulfillmentGroupItem) {
        if (fulfillmentGroupItem.getId() == null) {
            em.persist(fulfillmentGroupItem);
        } else {
            fulfillmentGroupItem = em.merge(fulfillmentGroupItem);
        }

        return fulfillmentGroupItem;
    }

    @SuppressWarnings("unchecked")
    public FulfillmentGroupItem readFulfillmentGroupItemById(Long fulfillmentGroupItemId) {
        return (FulfillmentGroupItem) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.order.domain.FulfillmentGroupItem"), fulfillmentGroupItemId);
    }

    @SuppressWarnings("unchecked")
    public List<FulfillmentGroupItem> readFulfillmentGroupItemsForFulfillmentGroup(FulfillmentGroup fulfillmentGroup) {
        Query query = em.createNamedQuery("BC_READ_FULFILLMENT_GROUP_ITEM_BY_FULFILLMENT_GROUP_ID");
        query.setParameter("fulfillmentGroupId", fulfillmentGroup.getId());
        return query.getResultList();
    }

    public FulfillmentGroupItem create() {
        return ((FulfillmentGroupItem) entityConfiguration.createEntityInstance("org.broadleafcommerce.order.domain.FulfillmentGroupItem"));
    }
}
