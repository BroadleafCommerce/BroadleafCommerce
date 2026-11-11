/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2026 Broadleaf Commerce
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
package org.broadleafcommerce.core.order.service;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.workflow.ActivityMessages;

import java.util.List;
import java.util.Map;

public interface ProductOptionValidationService {

    Boolean validate(ProductOption productOption, String value);

    /**
     * For each submitted attribute that corresponds to a product option on the product, validates that the submitted
     * value is one the option allows. A value that is not one of an enumerated option's
     * {@link ProductOption#getAllowedValues() allowed values} is rejected. Non-enumerated option types (free entry,
     * e.g. text or date) are excluded, since their value is not chosen from a list. An attribute that does not match a
     * product option is left alone, since order item attributes are a generic key-value store also used for custom,
     * non-option attributes.
     *
     * @param product         the product the attributes are being validated against
     * @param attributeValues the submitted item attribute name/value pairs
     */
    void validateProductOptionAttributes(Product product, Map<String, String> attributeValues);

    boolean isSubmitType(ProductOption productOption);

    boolean hasProductOptionValidationStrategy(ProductOption productOption);

    boolean isAddOrNoneType(ProductOption productOption);

    void validateWithoutException(ProductOption productOption, String attributeValue, ActivityMessages messages);

    List<Long> findSkuIdsForProductOptionValues(Long id, String key, String value, List<Long> possibleSkuIds);

}
