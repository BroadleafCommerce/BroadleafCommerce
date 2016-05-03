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
package org.broadleafcommerce.common.currency.service;

import org.broadleafcommerce.common.currency.dao.BroadleafCurrencyDao;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.util.TransactionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.List;

/**
 * Author: jerryocanas
 * Date: 9/6/12
 */

@Service("blCurrencyService")
public class BroadleafCurrencyServiceImpl implements BroadleafCurrencyService {

    @Resource(name="blCurrencyDao")
    protected BroadleafCurrencyDao currencyDao;

    /**
     * Returns the default Broadleaf currency
     * @return The default currency
     */
    @Override
    public BroadleafCurrency findDefaultBroadleafCurrency() {
        return currencyDao.findDefaultBroadleafCurrency();
    }

    /**
     * @return The currency for the passed in code
     */
    @Override
    public BroadleafCurrency findCurrencyByCode(String currencyCode) {
        return currencyDao.findCurrencyByCode(currencyCode);
    }

    /**
     * Returns a list of all the Broadleaf Currencies
     *@return List of currencies
     */
    @Override
    public List<BroadleafCurrency> getAllCurrencies() {
        return currencyDao.getAllCurrencies();
    }

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public BroadleafCurrency save(BroadleafCurrency currency) {
        return currencyDao.save(currency);
    }
    
    @Override
    public BroadleafCurrency create() {
        return currencyDao.create();
    }    
}
