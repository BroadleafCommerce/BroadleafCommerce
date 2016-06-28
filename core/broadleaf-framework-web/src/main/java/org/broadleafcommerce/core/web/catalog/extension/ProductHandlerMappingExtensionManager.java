package org.broadleafcommerce.core.web.catalog.extension;

import org.broadleafcommerce.common.extension.ExtensionManager;
import org.springframework.stereotype.Service;

/**
 * Extension manager for {@link org.broadleafcommerce.core.web.catalog.ProductHandlerMapping}
 *
 * @author Jon Fleschler (jfleschler)
 */
@Service("blProductHandlerMappingExtensionManager")
public class ProductHandlerMappingExtensionManager extends ExtensionManager<ProductHandlerMappingExtensionHandler> {

    public ProductHandlerMappingExtensionManager() {
        super(ProductHandlerMappingExtensionHandler.class);
    }
}
