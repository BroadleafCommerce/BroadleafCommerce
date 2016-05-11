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
