package org.broadleafcommerce.cms.web;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;

public interface URLHandlerFilterExtensionHandler extends ExtensionHandler {

    /**
     * TODO
     *
     * @return
     */
    public ExtensionResultStatusType processPostRedirect();
}
