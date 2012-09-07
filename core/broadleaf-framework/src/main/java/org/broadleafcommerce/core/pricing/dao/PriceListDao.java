package org.broadleafcommerce.core.pricing.dao;

import org.broadleafcommerce.core.pricing.domain.PriceList;

public interface PriceListDao {


    PriceList findbyKey(String key);
}
