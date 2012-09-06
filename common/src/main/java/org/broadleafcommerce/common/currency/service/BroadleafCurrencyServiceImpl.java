package org.broadleafcommerce.common.currency.service;

import org.broadleafcommerce.common.currency.dao.BroadleafCurrencyDao;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;

import javax.annotation.Resource;
import java.util.List;

/**
 * Author: jerryocanas
 * Date: 9/6/12
 */
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
     * Returns a list of all the Broadleaf Currencies
     *@return List of currencies
     */
    @Override
    public List<BroadleafCurrency> getAllCurrencies() {
        return currencyDao.getAllCurrencies();
    }

    @Override
    public BroadleafCurrency save(BroadleafCurrency currency) {
        return currencyDao.save(currency);
    }
}
