package org.broadleafcommerce.cms.web;

import org.broadleafcommerce.common.extension.ExtensionManager;
import org.springframework.stereotype.Component;

@Component("blURLHandlerFilterExtensionManager")
public class URLHandlerFilterExtensionManager extends ExtensionManager<URLHandlerFilterExtensionHandler> {

    public URLHandlerFilterExtensionManager() {
        super(URLHandlerFilterExtensionHandler.class);
    }
}
