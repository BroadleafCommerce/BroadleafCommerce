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
