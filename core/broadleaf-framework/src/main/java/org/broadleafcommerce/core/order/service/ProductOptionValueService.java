package org.broadleafcommerce.core.order.service;

import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.Sku;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ProductOptionValueService {
    Map<ProductOption, Set<ProductOptionValue>> findApplicableProductOptionsAndValuesForProduct(Long productId);

    Sku findSkuForProductOptionsAndValues(Long productId, Map<String, String> attributeValuesForSku);

    List<ProductOptionValue> findMatchingProductOptionsForValues(Long productId, String attributeName,
                                                                 String attributeValue);
}
