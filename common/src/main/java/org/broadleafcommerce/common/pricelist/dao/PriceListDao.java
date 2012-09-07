package org.broadleafcommerce.common.pricelist.dao;

import org.broadleafcommerce.common.pricelist.domain.PriceList;

public interface PriceListDao {

    PriceList findPriceListByKey(String key);

}
