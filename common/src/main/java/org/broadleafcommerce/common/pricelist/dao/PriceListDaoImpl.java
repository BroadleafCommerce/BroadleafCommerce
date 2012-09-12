package org.broadleafcommerce.common.pricelist.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.pricelist.domain.PriceList;
import org.springframework.stereotype.Repository;

@Repository("blPriceListDao")
public class PriceListDaoImpl implements PriceListDao{

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    /**
     * Returns a pricelist that matches the passed in key
     *
     * @return The pricelist for the passed in key
     */
    @Override
    public PriceList findPriceListByKey(String priceKey) {
        Query query = em.createNamedQuery("BC_READ_PRICE_LIST");
        query.setParameter("key", priceKey);
        List<PriceList> priceList = query.getResultList();
        if (priceList.size() >= 1) {
            return priceList.get(0);
        }
        return null;
    }

    /**
     * Returns a pricelist that matches the passed in currency
     *
     * @param currency
     * @return pricelist
     */
    @Override
    public PriceList findPriceListByCurrency(BroadleafCurrency currency) {
        Query query = em.createNamedQuery("BC_READ_PRICE_LIST_BY_CURRENCY_CODE");
        query.setParameter("currency", currency);
        List<PriceList> priceList = query.getResultList();
        if (priceList.size() >= 1) {
            return priceList.get(0);
        }
        return null;
    }

    /**
     * Returns the default pricelist
     *
     * @return the default pricelist
     */
    @Override
    public PriceList findDefaultPricelist() {
        Query query = em.createNamedQuery("BC_READ_DEFAULT_PRICE_LIST");
        List<PriceList> priceList = query.getResultList();
        if (priceList.size() >= 1) {
            return priceList.get(0);
        }
        return null;
    }


}
