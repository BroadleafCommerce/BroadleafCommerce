package org.broadleafcommerce.inventory.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.inventory.domain.SkuAvailability;
import org.springframework.stereotype.Repository;

@Repository("availabilityDao")
public class AvailabilityDaoJpa implements AvailabilityDao {

    @PersistenceContext(unitName="blPU")
    private EntityManager em;

    private String queryCacheableKey = "org.hibernate.cacheable";

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
        em.persist(skuAvailability);
    }

    public String getQueryCacheableKey() {
        return queryCacheableKey;
    }

    public void setQueryCacheableKey(String queryCacheableKey) {
        this.queryCacheableKey = queryCacheableKey;
    }
}