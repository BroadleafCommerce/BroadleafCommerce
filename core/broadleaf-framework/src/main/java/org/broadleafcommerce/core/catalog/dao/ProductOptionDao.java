/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.catalog.dao;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.dto.AssignedProductOptionDTO;
import org.broadleafcommerce.core.order.domain.OrderItemAttribute;

import java.util.List;

/**
 * @author Phillip Verheyden
 */
public interface ProductOptionDao {

    List<ProductOption> readAllProductOptions();

    ProductOption readProductOptionById(Long id);

    ProductOption saveProductOption(ProductOption option);

    ProductOptionValue readProductOptionValueById(Long id);

    /**
     * Returns a list of {@link org.broadleafcommerce.core.catalog.domain.dto.AssignedProductOptionDTO}
     * found for given the productId.
     *
     * @param productId
     * @return
     */
    List<AssignedProductOptionDTO> findAssignedProductOptionsByProductId(Long productId);

    /**
     * Returns a list of {@link org.broadleafcommerce.core.catalog.domain.dto.AssignedProductOptionDTO}
     * found for given the {@link org.broadleafcommerce.core.catalog.domain.Product}.
     *
     * @param product
     * @return
     */
    List<AssignedProductOptionDTO> findAssignedProductOptionsByProduct(Product product);

    Long countAllowedValuesForProductOptionById(Long productOptionId);

    List<Long> readSkuIdsForProductOptionValues(Long productId, String attributeName, String attributeValue, List<Long> possibleSkuIds);

    Long countProductsUsingProductOptionById(Long productOptionId);

    /**
     * Returns a paginated list of Product Ids that are using the passed in ProductOption ID
     *
     * @param productOptionId
     * @param start
     * @param pageSize
     * @return
     */
    List<Long> findProductIdsUsingProductOptionById(Long productOptionId, int start, int pageSize);

    /**
     * Returns a translated String attribute value for OrderItemAttribute
     *
     * @param itemAttribute
     * @param productOption
     * @return
     */
    String translateItemAttributeValue(OrderItemAttribute itemAttribute, ProductOption productOption);

}
