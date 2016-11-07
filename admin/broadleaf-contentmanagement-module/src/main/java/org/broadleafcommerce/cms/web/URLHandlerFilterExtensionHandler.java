package org.broadleafcommerce.cms.web;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface URLHandlerFilterExtensionHandler extends ExtensionHandler {

    /**
     * TODO
     *
     * @param request
     * @param response
     * @return
     */
    public ExtensionResultStatusType processPreRedirect(HttpServletRequest request, HttpServletResponse response, String newURL);
}
