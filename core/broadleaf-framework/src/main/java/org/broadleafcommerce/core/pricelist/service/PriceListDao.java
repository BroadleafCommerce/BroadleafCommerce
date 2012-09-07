package org.broadleafcommerce.core.pricelist.service;

import org.broadleafcommerce.core.pricing.domain.PriceList;

public interface PriceListDao {


    PriceList findbyKey(String key);
}
