package org.broadleafcommerce.core.order.service;

import org.broadleafcommerce.core.catalog.dao.ProductOptionValueDao;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

@Repository("blProductOptionValueService")
public class ProductOptionValueServiceImpl implements ProductOptionValueService {

    @Resource(name = "blProductOptionValueDao")
    protected ProductOptionValueDao productOptionValueDao;

    @Override
    public Map<ProductOption, Set<ProductOptionValue>> findApplicableProductOptionsAndValuesForProduct(Long productId) {
        return productOptionValueDao.readApplicableProductOptionsAndValuesForProduct(productId);
    }

    @Override
    public Sku findSkuForProductOptionsAndValues(Long productId, Map<String, String> attributeNameValuePair) {
        return productOptionValueDao.readSkuForProductOptionsAndValues(productId, attributeNameValuePair);
    }

    @Override
    public List<ProductOptionValue> findMatchingProductOptionsForValues(Long productId, Map<String, String> attributeNameValuePair) {
        return productOptionValueDao.readMatchingProductOptionsForValues(productId, attributeNameValuePair);
    }

}