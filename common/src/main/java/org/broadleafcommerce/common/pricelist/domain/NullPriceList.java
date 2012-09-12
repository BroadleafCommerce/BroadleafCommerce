package org.broadleafcommerce.common.pricelist.domain;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;

/**
 *   Generates a null entity of PriceList
 */
public class NullPriceList implements PriceList {

    private static final long serialVersionUID = 1L;

    @Override
    public BroadleafCurrency getCurrencyCode() {
        return null;
    }

    @Override
    public void setCurrencyCode(BroadleafCurrency currencyCode) { }

    @Override
    public String getFriendlyName() {
        return null;
    }

    @Override
    public void setFriendlyName(String friendlyName) { }

    @Override
    public Boolean getDefaultFlag() {
        return null;
    }

    @Override
    public void setDefaultFlag(Boolean defaultFlag) { }

    @Override
    public String getPriceKey() {
        return null;
    }

    @Override
    public void setPriceKey(String name) { }

}
