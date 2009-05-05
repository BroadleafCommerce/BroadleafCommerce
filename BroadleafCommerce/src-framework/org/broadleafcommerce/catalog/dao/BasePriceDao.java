package org.broadleafcommerce.catalog.dao;

import org.broadleafcommerce.catalog.domain.BasePrice;

public interface BasePriceDao {

    public BasePrice readBasePriceById(Long basePriceId);

    public BasePrice save(BasePrice basePrice);
}