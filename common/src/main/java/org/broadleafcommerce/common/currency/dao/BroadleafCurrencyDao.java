/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
    
    public BroadleafCurrency create();

}
