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

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;

import java.util.List;

/**
 * Author: jerryocanas
 * Date: 9/6/12
 */
public interface BroadleafCurrencyService {

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
    
    public BroadleafCurrency create();
}
