package org.broadleafcommerce.core.web.processor;

import org.broadleafcommerce.common.extension.ExtensionManager;
import org.springframework.stereotype.Service;

/**
 * @author Jeff Fischer
 */
@Service("blCategoriesProcessorExtensionManager")
public class CategoriesProcessorExtensionManager extends ExtensionManager<CategoriesProcessorExtensionHandler> {

    public CategoriesProcessorExtensionManager() {
        super(CategoriesProcessorExtensionHandler.class);
    }

}
