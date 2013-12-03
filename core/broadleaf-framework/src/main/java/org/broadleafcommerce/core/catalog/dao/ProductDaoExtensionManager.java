package org.broadleafcommerce.core.catalog.dao;

import org.broadleafcommerce.common.extension.ExtensionManager;
import org.springframework.stereotype.Service;

/**
 * @author Jeff Fischer
 */
@Service("blProductDaoExtensionManager")
public class ProductDaoExtensionManager extends ExtensionManager<ProductDaoExtensionHandler> {

    public ProductDaoExtensionManager() {
        super(ProductDaoExtensionHandler.class);
    }

}
