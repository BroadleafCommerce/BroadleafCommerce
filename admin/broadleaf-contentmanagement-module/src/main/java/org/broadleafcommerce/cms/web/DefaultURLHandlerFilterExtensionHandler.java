package org.broadleafcommerce.cms.web;

import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;

public class DefaultURLHandlerFilterExtensionHandler extends AbstractExtensionHandler
        implements URLHandlerFilterExtensionHandler {

    @Override
    public ExtensionResultStatusType processPostRedirect() {
        return ExtensionResultStatusType.NOT_HANDLED;
    }
}
