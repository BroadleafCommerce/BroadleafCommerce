package org.broadleafcommerce.core.catalog.service.extension;

import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.catalog.domain.Product;

public class AbstractPreviewProductUrlModifierExtensionHandler extends AbstractExtensionHandler
        implements PreviewProductUrlModifierExtensionHandler {

    @Override
    public ExtensionResultStatusType modifyUrl(Product product, ExtensionResultHolder<String> holder) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

}
