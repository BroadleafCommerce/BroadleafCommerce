package org.broadleafcommerce.common.pricelist.dao;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.pricelist.domain.PriceList;

public interface PriceListDao {

    /**
     * Returns a pricelist that matches the passed in key
     *
     * @return The pricelist for the passed in key
     */
    PriceList findPriceListByKey(String key);

    /**
     * Returns a pricelist that matches the passed in currency
     *
     * @param currency
     * @return pricelist
     */
    PriceList findPriceListByCurrency(BroadleafCurrency currency);

    /**
     * Returns the default pricelist
     *
     * @return the default pricelist
     */
    PriceList findDefaultPricelist();

}
