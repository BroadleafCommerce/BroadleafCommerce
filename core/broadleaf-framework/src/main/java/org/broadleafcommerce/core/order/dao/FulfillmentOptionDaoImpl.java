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
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.order.domain.FulfillmentOptionImpl;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * 
 * @author Phillip Verheyden
 */
@Repository("blFulfillmentOptionDao")
public class FulfillmentOptionDaoImpl implements FulfillmentOptionDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public FulfillmentOption readFulfillmentOptionById(final Long fulfillmentOptionId) {
        return em.find(FulfillmentOptionImpl.class, fulfillmentOptionId);
    }

    @Override
    public FulfillmentOption save(FulfillmentOption option) {
        return em.merge(option);
    }

    @Override
    public List<FulfillmentOption> readAllFulfillmentOptions() {
        TypedQuery<FulfillmentOption> query = em.createNamedQuery("BC_READ_ALL_FULFILLMENT_OPTIONS", FulfillmentOption.class);
        return query.getResultList();
    }

    @Override
    public List<FulfillmentOption> readAllFulfillmentOptionsByFulfillmentType(FulfillmentType type) {
        TypedQuery<FulfillmentOption> query = em.createNamedQuery("BC_READ_ALL_FULFILLMENT_OPTIONS_BY_TYPE", FulfillmentOption.class);
        query.setParameter("fulfillmentType", type.getType());
        return query.getResultList();
    }
}
