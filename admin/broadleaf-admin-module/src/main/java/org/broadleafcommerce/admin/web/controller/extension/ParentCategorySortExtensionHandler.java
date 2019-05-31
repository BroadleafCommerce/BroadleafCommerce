package org.broadleafcommerce.admin.web.controller.extension;

import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.broadleafcommerce.openadmin.web.service.AbstractFormBuilderExtensionHandler;
import org.broadleafcommerce.openadmin.web.service.FormBuilderExtensionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service("blParentCategorySortExtensionHandler")
public class ParentCategorySortExtensionHandler extends AbstractFormBuilderExtensionHandler {


    @Value("${allow.product.parent.category.sorting:false}")
    protected boolean allowProductParentCategorySorting = false;

    @Resource(name = "blFormBuilderExtensionManager")
    protected FormBuilderExtensionManager extensionManager;

    @PostConstruct
    public void init() {
        if (isEnabled()) {
            extensionManager.registerHandler(this);
        }
    }

    @Override
    public ExtensionResultStatusType modifyListGrid(String className, ListGrid listGrid) {
        if (Product.class.getName().equals(className) && !allowProductParentCategorySorting) {
            for (Field f : listGrid.getHeaderFields()) {
                if (f.getName().equals("defaultCategory")) {
                    f.setFilterSortDisabled(true);
                }
            }
            return ExtensionResultStatusType.HANDLED;
        }
        return ExtensionResultStatusType.NOT_HANDLED;
    }
}
