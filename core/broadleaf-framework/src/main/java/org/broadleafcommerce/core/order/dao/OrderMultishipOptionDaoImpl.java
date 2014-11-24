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
import org.broadleafcommerce.core.order.domain.OrderMultishipOption;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Repository("blOrderMultishipOptionDao")
public class OrderMultishipOptionDaoImpl implements OrderMultishipOptionDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    @Transactional("blTransactionManager")
    public OrderMultishipOption save(final OrderMultishipOption orderMultishipOption) {
        return em.merge(orderMultishipOption);
    }

    @Override
    public List<OrderMultishipOption> readOrderMultishipOptions(final Long orderId) {
        TypedQuery<OrderMultishipOption> query = em.createNamedQuery("BC_READ_MULTISHIP_OPTIONS_BY_ORDER_ID", OrderMultishipOption.class);
        query.setParameter("orderId", orderId);
        return query.getResultList();
    }
    
    @Override
    public List<OrderMultishipOption> readOrderItemOrderMultishipOptions(final Long orderItemId) {
        TypedQuery<OrderMultishipOption> query = em.createNamedQuery("BC_READ_MULTISHIP_OPTIONS_BY_ORDER_ITEM_ID", OrderMultishipOption.class);
        query.setParameter("orderItemId", orderItemId);
        return query.getResultList();
    }
    
    @Override
    public OrderMultishipOption create() {
        return (OrderMultishipOption) entityConfiguration.createEntityInstance(OrderMultishipOption.class.getName());
    }
    
    @Override
    @Transactional("blTransactionManager")
    public void deleteAll(List<OrderMultishipOption> options) {
        for (OrderMultishipOption option : options) {
            em.remove(option);
        }
    }
}
