package org.broadleafcommerce.core.catalog.service;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;

/**
 * @author Jeff Fischer
 */
public interface CatalogServiceExtensionHandler extends ExtensionHandler {

    public ExtensionResultStatusType findCategoryByURI(String uri, ExtensionResultHolder resultHolder);

    public ExtensionResultStatusType findProductByURI(String uri, ExtensionResultHolder resultHolder);

}
