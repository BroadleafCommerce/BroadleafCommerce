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
package org.broadleafcommerce.inventory.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.inventory.domain.SkuAvailability;
import org.springframework.stereotype.Repository;

@Repository("blAvailabilityDao")
public class AvailabilityDaoImpl implements AvailabilityDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    protected String queryCacheableKey = "org.hibernate.cacheable";

    @SuppressWarnings("unchecked")
    public List<SkuAvailability> readSKUAvailability(List<Long> skuIds, boolean realTime) {
        Query query = em.createNamedQuery("BC_READ_SKU_AVAILABILITIES_BY_SKU_IDS");
        if (! realTime) {
            query.setHint(getQueryCacheableKey(), true);
        }
        query.setParameter("skuIds", skuIds);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<SkuAvailability> readSKUAvailabilityForLocation(List<Long> skuIds, Long locationId, boolean realTime) {
        Query query = em.createNamedQuery("BC_READ_SKU_AVAILABILITIES_BY_LOCATION_ID_AND_SKU_IDS");
        if (! realTime) {
            query.setHint(getQueryCacheableKey(), true);
        }
        query.setParameter("skuIds", skuIds);
        query.setParameter("locationId", locationId);
        return query.getResultList();
    }

    public void save(SkuAvailability skuAvailability) {
        em.merge(skuAvailability);
    }

    public String getQueryCacheableKey() {
        return queryCacheableKey;
    }

    public void setQueryCacheableKey(String queryCacheableKey) {
        this.queryCacheableKey = queryCacheableKey;
    }
}