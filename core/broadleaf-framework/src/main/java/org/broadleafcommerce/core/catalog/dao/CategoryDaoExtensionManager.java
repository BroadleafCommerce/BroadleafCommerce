package org.broadleafcommerce.core.catalog.dao;

import org.broadleafcommerce.common.extension.ExtensionManager;
import org.springframework.stereotype.Service;

/**
 * @author Jeff Fischer
 */
@Service("blCategoryDaoExtensionManager")
public class CategoryDaoExtensionManager extends ExtensionManager<CategoryDaoExtensionHandler> {

    public CategoryDaoExtensionManager() {
        super(CategoryDaoExtensionHandler.class);
    }

}
