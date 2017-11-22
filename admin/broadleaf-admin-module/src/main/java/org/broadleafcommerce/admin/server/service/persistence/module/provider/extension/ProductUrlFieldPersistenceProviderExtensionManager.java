package org.broadleafcommerce.admin.server.service.persistence.module.provider.extension;

import org.broadleafcommerce.common.extension.ExtensionManager;
import org.springframework.stereotype.Service;

@Service("blProductUrlFieldPersistenceProviderExtensionManager")
public class ProductUrlFieldPersistenceProviderExtensionManager
        extends ExtensionManager<ProductUrlFieldPersistenceProviderExtensionHandler> {

    public ProductUrlFieldPersistenceProviderExtensionManager() {
        super(ProductUrlFieldPersistenceProviderExtensionHandler.class);
    }
}
