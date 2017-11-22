package org.broadleafcommerce.admin.server.service.persistence.module.provider.extension;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.catalog.domain.Product;

public interface ProductUrlFieldPersistenceProviderExtensionHandler extends ExtensionHandler {

    ExtensionResultStatusType modifyUrl(String url, Product product, ExtensionResultHolder<String> holder);
}
