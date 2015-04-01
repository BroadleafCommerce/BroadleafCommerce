/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.common.currency.domain;

/**
 * The BroadleafCurrencyResolver can be implemented to set the currency (e.g. CurrencyToUse).   
 * 
 * This may differ from the currency that was requested (e.g. from the locale, etc.)   
 * 
 * By storing the desired currency, we have the opportunity for a later module (like PriceLists) to 
 * check the DesiredCurrency and possibly alter the currency for the request. 
 * 
 * @author bpolster
 *
 */
public class BroadleafRequestedCurrencyDto {

    BroadleafCurrency currencyToUse;
    BroadleafCurrency requestedCurrency;

    public BroadleafRequestedCurrencyDto(BroadleafCurrency currencyToUse, BroadleafCurrency requestedCurrency) {
        super();
        this.currencyToUse = currencyToUse;
        this.requestedCurrency = requestedCurrency;
    }

    /**
     * @return the currencyToUse
     */
    public BroadleafCurrency getCurrencyToUse() {
        return currencyToUse;
    }

    /**
     * @return the requestedCurrency
     */
    public BroadleafCurrency getRequestedCurrency() {
        return requestedCurrency;
    }

}
