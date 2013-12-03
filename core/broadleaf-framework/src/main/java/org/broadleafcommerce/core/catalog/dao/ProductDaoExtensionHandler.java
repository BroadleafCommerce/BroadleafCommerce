package org.broadleafcommerce.core.catalog.dao;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;

/**
 * @author Jeff Fischer
 */
public interface ProductDaoExtensionHandler extends ExtensionHandler {

    public ExtensionResultStatusType findProductByURI(String uri, ExtensionResultHolder resultHolder);

}
