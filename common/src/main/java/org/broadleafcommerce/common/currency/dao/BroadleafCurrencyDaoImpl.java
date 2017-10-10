/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.currency.dao;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.springframework.stereotype.Repository;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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
        query.setHint(org.hibernate.jpa.QueryHints.HINT_CACHEABLE, true);
        List<BroadleafCurrency> currencyList = query.getResultList();
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
        query.setHint(org.hibernate.jpa.QueryHints.HINT_CACHEABLE, true);
        List<BroadleafCurrency> currencyList = query.getResultList();
        if (currencyList.size() >= 1) {
            return currencyList.get(0);
        }
        return null;
    }

    @Override
    public List<BroadleafCurrency> getAllCurrencies() {
        Query query = em.createNamedQuery("BC_READ_ALL_CURRENCIES");
        query.setHint(org.hibernate.jpa.QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }

    @Override
    public BroadleafCurrency save(BroadleafCurrency currency) {
        return em.merge(currency);
    }
    
    @Override
    public BroadleafCurrency create() {
        return entityConfiguration.createEntityInstance(BroadleafCurrency.class.getName(), BroadleafCurrency.class);
    }    
}
