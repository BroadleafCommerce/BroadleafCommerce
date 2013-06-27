/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.web.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author apazzolini
 *
 * Utility class for Thymeleaf Processors
 */
public class ProcessorUtils {
    
    /**
     * Gets a UTF-8 URL encoded URL based on the current URL as well as the specified map 
     * of query string parameters
     * 
     * @param baseUrl
     * @param parameters
     * @return the built URL
     */
    public static String getUrl(String baseUrl, Map<String, String[]> parameters) {
        if (baseUrl.contains("?")) {
            throw new IllegalArgumentException("baseUrl contained a ? indicating it is not a base url");
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(baseUrl);
        
        boolean atLeastOneParam = false;
        
        if (parameters != null && parameters.size() > 0) {
            for (Entry<String, String[]> entry : parameters.entrySet()) {
                if (entry.getValue().length > 0) {
                    atLeastOneParam = true;
                }
            }
        }
        
        if (atLeastOneParam) {
            sb.append("?");
        } else {
            return sb.toString();
        }
        
        for (Entry<String, String[]> entry : parameters.entrySet()) {
            String key = entry.getKey();
            for (String value : entry.getValue()) {
                StringBuilder parameter = new StringBuilder();
                try {
                    parameter.append(URLEncoder.encode(key, "UTF-8"));
                    parameter.append("=");
                    parameter.append(URLEncoder.encode(value, "UTF-8"));
                    parameter.append("&");
                } catch (UnsupportedEncodingException e) {
                    parameter = null;
                }
                sb.append(parameter);
            }
        }
        
        String url = sb.toString();
        if (url.charAt(url.length() - 1) == '&') {
            url = url.substring(0, url.length() - 1);
        }
        
        return url;
    }

}
