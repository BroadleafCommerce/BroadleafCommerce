package org.broadleafcommerce.cms.web;

import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultURLHandlerFilterExtensionHandler extends AbstractExtensionHandler
        implements URLHandlerFilterExtensionHandler {

    @Override
    public ExtensionResultStatusType processPreRedirect(HttpServletRequest request, HttpServletResponse response, String newURL) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }
}
