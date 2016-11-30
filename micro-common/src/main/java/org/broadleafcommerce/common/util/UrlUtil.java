/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.common.util;

import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.ESAPI;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

public class UrlUtil {
    public static String generateUrlKey(String toConvert) {
        if (toConvert != null) {
            toConvert = toConvert.replaceAll(" ", "-");
            if (toConvert.matches(".*?\\W.*?")) {
                //remove all non-word characters
                String result = toConvert.replaceAll("[^\\w-]+", "");
                //uncapitalizes the first letter of the url key
                return StringUtils.uncapitalize(result);
            } else {
                return StringUtils.uncapitalize(toConvert);
            }
        }
        return toConvert;
    }
    
    /**
     * If the url does not include "//" then the system will ensure that the
     * application context is added to the start of the URL.
     *
     * @param url
     * @return
     */
    public static String fixRedirectUrl(String contextPath, String url) {
        if (url.indexOf("//") < 0) {

            if (contextPath != null && (!"".equals(contextPath))) {
                if (!url.startsWith("/")) {
                    url = "/" + url;
                }
                if (!url.startsWith(contextPath)) {
                    url = contextPath + url;
                }
            }
        }
        return url;

    }

    /**
     * Returns validated URL. Validation is done by
     * @see org.owasp.esapi.Validator#isValidRedirectLocation(String, String, boolean),
     * which only works on relative URLs.
     *
     * Throws an IOExcption if isValidRedirectLocation is false.
     *
     * @param url
     * @param request
     * @throws IOException
     */
    public static void validateUrl(String url, HttpServletRequest request) throws IOException {
        String serverName = request.getServerName();
        String port = ":" + request.getServerPort();
        String scheme = request.getScheme() + "://";
        String relativeUrl = url.replace(scheme, "").replace(serverName, "").replace(port, "");

        if (!"/".equals(relativeUrl) && !ESAPI.validator().isValidRedirectLocation("Redirect", relativeUrl, false)) {
            throw new IOException("Redirect failed");
        }
    }
        
}
