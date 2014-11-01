/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.util;

import org.apache.commons.lang.StringUtils;

public class UrlUtil {
    public static String generateUrlKey(String toConvert) {
        if (toConvert.matches(".*?\\W.*?")) {
            //remove all non-word characters
            String result = toConvert.replaceAll("\\W","");
            //uncapitalizes the first letter of the url key
            return StringUtils.uncapitalize(result);
        } else {
            return StringUtils.uncapitalize(toConvert);
        }
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
