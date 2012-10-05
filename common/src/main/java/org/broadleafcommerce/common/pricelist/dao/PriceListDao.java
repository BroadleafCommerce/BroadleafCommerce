package org.broadleafcommerce.common.pricelist.dao;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.pricelist.domain.PriceList;

import java.util.List;

public interface PriceListDao {

    /**
     * Returns a pricelist that matches the passed in key
     *
     * @return The pricelist for the passed in key
     */
    public PriceList findPriceListByKey(String key);

    /**
     * Returns a pricelist that matches the passed in currency
     *
     * @param currency
     * @return pricelist
     */
    public PriceList findPriceListByCurrency(BroadleafCurrency currency);

    /**
     * Returns the default pricelist
     *
     * @return the default pricelist
     */
    public PriceList findDefaultPricelist();

    /**
     * @return a list of all currently configured price lists
     */
    public List<PriceList> readAllPriceLists();

}
