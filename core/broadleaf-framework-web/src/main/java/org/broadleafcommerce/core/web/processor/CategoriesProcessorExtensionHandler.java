package org.broadleafcommerce.core.web.processor;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;

/**
 * @author Jeff Fischer
 */
public interface CategoriesProcessorExtensionHandler extends ExtensionHandler {

    public ExtensionResultStatusType findAllPossibleChildCategories(String parentCategory, String maxResults, ExtensionResultHolder resultHolder);

}
