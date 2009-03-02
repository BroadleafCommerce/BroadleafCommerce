package org.broadleafcommerce.catalog.dao;

import java.util.List;

import org.broadleafcommerce.catalog.domain.Sku;

public interface SkuDao {

    public Sku readSkuById(Long skuId);

    public Sku maintainSku(Sku sku);

    public Sku readFirstSku();

    public List<Sku> readAllSkus();

    public List<Sku> readSkusById(List<Long> ids);
}
