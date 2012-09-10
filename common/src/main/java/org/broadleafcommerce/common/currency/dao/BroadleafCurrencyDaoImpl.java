package org.broadleafcommerce.common.currency.dao;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Author: jerryocanas
 * Date: 9/6/12
 */

@Repository("blCurrencyDao")
public class BroadleafCurrencyDaoImpl implements BroadleafCurrencyDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public BroadleafCurrency findDefaultBroadleafCurrency() {
        Query query = em.createNamedQuery("BC_READ_DEFAULT_CURRENCY");
        List<BroadleafCurrency> currencyList = (List<BroadleafCurrency>) query.getResultList();
        if (currencyList.size() >= 1) {
            return currencyList.get(0);
        }
        return null;
    }

    /**
     * @return The locale for the passed in code
     */
    @Override
    public BroadleafCurrency findCurrencyByCode(String currencyCode) {
        Query query = em.createNamedQuery("BC_READ_CURRENCY_BY_CODE");
        query.setParameter("currencyCode", currencyCode);
        List<BroadleafCurrency> currencyList = (List<BroadleafCurrency>) query.getResultList();
        if (currencyList.size() >= 1) {
            return currencyList.get(0);
        }
        return null;
    }

    @Override
    public List<BroadleafCurrency> getAllCurrencies() {
        Query query = em.createNamedQuery("BC_READ_ALL_CURRENCIES");
        return (List<BroadleafCurrency>) query.getResultList();
    }

    @Override
    public BroadleafCurrency save(BroadleafCurrency currency) {
        return em.merge(currency);
    }
}
