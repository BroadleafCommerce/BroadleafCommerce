/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.order.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupFee;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.type.FulfillmentGroupStatusType;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository("blFulfillmentGroupDao")
public class FulfillmentGroupDaoImpl implements FulfillmentGroupDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public FulfillmentGroup save(final FulfillmentGroup fulfillmentGroup) {
        return em.merge(fulfillmentGroup);
    }

    @Override
    public FulfillmentGroup readFulfillmentGroupById(final Long fulfillmentGroupId) {
        return em.find(FulfillmentGroupImpl.class, fulfillmentGroupId);
    }

    @Override
    public FulfillmentGroupImpl readDefaultFulfillmentGroupForOrder(final Order order) {
        final Query query = em.createNamedQuery("BC_READ_DEFAULT_FULFILLMENT_GROUP_BY_ORDER_ID");
        query.setParameter("orderId", order.getId());
        @SuppressWarnings("unchecked")
        List<FulfillmentGroupImpl> fulfillmentGroups = query.getResultList();
        return fulfillmentGroups == null || fulfillmentGroups.isEmpty() ? null : fulfillmentGroups.get(0);
    }

    @Override
    public void delete(FulfillmentGroup fulfillmentGroup) {
        if (!em.contains(fulfillmentGroup)) {
            fulfillmentGroup = readFulfillmentGroupById(fulfillmentGroup.getId());
        }
        em.remove(fulfillmentGroup);
    }

    @Override
    public FulfillmentGroup createDefault() {
        final FulfillmentGroup fg = ((FulfillmentGroup) entityConfiguration.createEntityInstance("org.broadleafcommerce.core.order.domain.FulfillmentGroup"));
        fg.setPrimary(true);
        return fg;
    }

    @Override
    public FulfillmentGroup create() {
        final FulfillmentGroup fg =  ((FulfillmentGroup) entityConfiguration.createEntityInstance("org.broadleafcommerce.core.order.domain.FulfillmentGroup"));
        return fg;
    }

    @Override
    public FulfillmentGroupFee createFulfillmentGroupFee() {
        return ((FulfillmentGroupFee) entityConfiguration.createEntityInstance("org.broadleafcommerce.core.order.domain.FulfillmentGroupFee"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FulfillmentGroup> readUnfulfilledFulfillmentGroups(int start,
            int maxResults) {
        Query query = em.createNamedQuery("BC_READ_UNFULFILLED_FULFILLMENT_GROUP_ASC");
        query.setFirstResult(start);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FulfillmentGroup> readPartiallyFulfilledFulfillmentGroups(int start,
            int maxResults) {
        Query query = em.createNamedQuery("BC_READ_PARTIALLY_FULFILLED_FULFILLMENT_GROUP_ASC");
        query.setFirstResult(start);
        query.setMaxResults(maxResults);
        
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FulfillmentGroup> readUnprocessedFulfillmentGroups(int start,
            int maxResults) {
        Query query = em.createNamedQuery("BC_READ_UNPROCESSED_FULFILLMENT_GROUP_ASC");
        query.setFirstResult(start);
        query.setMaxResults(maxResults);
        
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FulfillmentGroup> readFulfillmentGroupsByStatus(
            FulfillmentGroupStatusType status, int start, int maxResults, boolean ascending) {
        Query query = null;
        if (ascending) {
            query = em.createNamedQuery("BC_READ_FULFILLMENT_GROUP_BY_STATUS_ASC");
        } else {
            query = em.createNamedQuery("BC_READ_FULFILLMENT_GROUP_BY_STATUS_DESC");
        }
        query.setParameter("status", status.getType());
        query.setFirstResult(start);
        query.setMaxResults(maxResults);
        
        return query.getResultList();
    }

    @Override
    public List<FulfillmentGroup> readFulfillmentGroupsByStatus(
            FulfillmentGroupStatusType status, int start, int maxResults) {
        return readFulfillmentGroupsByStatus(status, start, maxResults, true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Integer readNextFulfillmentGroupSequnceForOrder(Order order) {
        Query query = em.createNamedQuery("BC_READ_MAX_FULFILLMENT_GROUP_SEQUENCE");
        query.setParameter("orderId", order.getId());
        List<Integer> max = query.getResultList();
        if (max != null && !max.isEmpty()) {
            Integer maxNumber = max.get(0);
            if (maxNumber == null) {
                return 1;
            }
            return maxNumber + 1;
        }
        return 1;
    }
}