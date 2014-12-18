package org.broadleafcommerce.core.order.service;

import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.Sku;

import java.util.List;
import java.util.Map;

public interface ProductOptionValueService {

    Sku findSkuForProductOptionsAndValues(Long productId, Map<String, String> attributeValuesForSku);

    List<ProductOptionValue> findMatchingProductOptionsForValues(Long productId, Map<String, String> attributeNameValuePair);

}
