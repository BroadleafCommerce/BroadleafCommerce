package org.broadleafcommerce.core.catalog.dao;

import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.Sku;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ProductOptionValueDao {
    public Map<ProductOption, Set<ProductOptionValue>> readApplicableProductOptionsAndValuesForProduct(
            Long productId);

    public Sku readSkuForProductOptionsAndValues(Long productId, Map<String, String> attributeValuesForSku);

    public List<ProductOptionValue> readMatchingProductOptionsForValues(Long productId, Map<String, String> attributeNameValuePair);

}
