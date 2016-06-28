package org.broadleafcommerce.core.web.catalog.extension;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.catalog.domain.Product;

/**
 * Extension handler for {@link org.broadleafcommerce.core.web.catalog.ProductHandlerMapping}
 *
 * @author Jon Fleschler (jfleschler)
 */
public interface ProductHandlerMappingExtensionHandler extends ExtensionHandler {

    /**
     * Check product for special attributes to determine if the Product should actually be returned from the Handler Mapping.
     * @param product
     * @param holder
     * @return
     */
    ExtensionResultStatusType checkProductAttributes(Product product, ExtensionResultHolder holder) throws ServiceException;
}
