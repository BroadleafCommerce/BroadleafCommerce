package org.broadleafcommerce.core.pricelist.service;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.core.pricing.domain.PriceList;

public class PriceListDaoImpl implements PriceListDao{
    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public PriceList findbyKey(String key) {
    Query query;
    query = em.createNamedQuery("BC_READ_PRICE_LIST");
    query.setParameter("key", key);

    @SuppressWarnings("unchecked")
    List<PriceList> results = query.getResultList();
    if (results != null && !results.isEmpty()) {
        return results.get(0);
    } else {
        return null;
    }
    }
}
