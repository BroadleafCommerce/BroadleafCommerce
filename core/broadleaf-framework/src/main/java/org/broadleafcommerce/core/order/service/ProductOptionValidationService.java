
package org.broadleafcommerce.core.order.service;

import org.broadleafcommerce.core.catalog.domain.ProductOption;

public interface ProductOptionValidationService {

    public abstract Boolean validate(ProductOption productOption, String value);

}