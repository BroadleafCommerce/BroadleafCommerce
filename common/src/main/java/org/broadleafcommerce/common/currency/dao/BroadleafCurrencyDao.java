package org.broadleafcommerce.common.currency.dao;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;

import java.util.List;

/**
 * Author: jerryocanas
 * Date: 9/6/12
 */
public interface BroadleafCurrencyDao {

    /**
     * Returns the default Broadleaf currency
     * @return The default currency
     */
    public BroadleafCurrency findDefaultBroadleafCurrency();

    /**
     * Returns a Broadleaf currency found by a code
     * @return The currency
     */
    public BroadleafCurrency findCurrencyByCode(String currencyCode);

    /**
     * Returns a list of all the Broadleaf Currencies
     * @return List of currencies
     */
    public List<BroadleafCurrency> getAllCurrencies();

    public BroadleafCurrency save(BroadleafCurrency currency);

}
