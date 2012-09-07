package org.broadleafcommerce.common.pricelist.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.pricelist.domain.PriceList;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository("blPriceListDao")
public class PriceListDaoImpl implements PriceListDao{
    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public PriceList findPriceListByKey(String key) {
        Query query = em.createNamedQuery("BC_READ_PRICE_LIST");
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
