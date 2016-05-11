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
        
}
