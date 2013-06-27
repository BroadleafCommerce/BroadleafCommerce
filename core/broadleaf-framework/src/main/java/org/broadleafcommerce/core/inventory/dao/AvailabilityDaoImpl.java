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

package org.broadleafcommerce.core.inventory.dao;

import org.broadleafcommerce.common.util.dao.BatchRetrieveDao;
import org.broadleafcommerce.core.inventory.domain.SkuAvailability;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * 
 * @deprecated This is no longer required and is instead implemented as a third-party inventory module
 * 
 */
@Deprecated
@Repository("blAvailabilityDao")
public class AvailabilityDaoImpl extends BatchRetrieveDao implements AvailabilityDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Override
    public List<SkuAvailability> readSKUAvailability(List<Long> skuIds, boolean realTime) {
        Query query = em.createNamedQuery("BC_READ_SKU_AVAILABILITIES_BY_SKU_IDS");
        if (! realTime) {
            query.setHint(QueryHints.HINT_CACHEABLE, true);
        }
        return batchExecuteReadQuery(query, skuIds, "skuIds");
    }

    @Override
    public List<SkuAvailability> readSKUAvailabilityForLocation(List<Long> skuIds, Long locationId, boolean realTime) {
        Query query = em.createNamedQuery("BC_READ_SKU_AVAILABILITIES_BY_LOCATION_ID_AND_SKU_IDS");
        if (! realTime) {
            query.setHint(QueryHints.HINT_CACHEABLE, true);
        }
        query.setParameter("locationId", locationId);
        return batchExecuteReadQuery(query, skuIds, "skuIds");
    }

    @Override
    public void save(SkuAvailability skuAvailability) {
        em.merge(skuAvailability);
    }

}