/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This filter provides a method to override the default redirect behavior of Spring MVC,
 * which is to deliver a Temp Redirect (302), instead of a permanent redirect (301).
 * This filter is configured with one or more white-space delimited regular expression
 * patterns that match any request URIs whose response status should be checked for 302
 * and converted to 301, if applicable.
 * 
 * This filter should appear before the Spring Dispatch Servlet in the mapping configuration
 * in web.xml
 * 
 * @author jfischer
 *
 */
public class SpringTemporaryRedirectOverrideFilter implements Filter {
    
    private Pattern[] urlPatterns = new Pattern[]{};

    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        //do nothing
    }
    
    public boolean isUrlMatch(String url) {
        for (Pattern pattern : urlPatterns) {
            Matcher matcher = pattern.matcher(url);
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        BroadleafResponseWrapper responseWrapper = new BroadleafResponseWrapper((HttpServletResponse) response);
        chain.doFilter(request, responseWrapper);
        if ( 
                response instanceof BroadleafResponseWrapper && 
                302 == ((BroadleafResponseWrapper) response).getStatus() &&
                isUrlMatch(((HttpServletRequest)request).getRequestURI())
        ) {
            ((HttpServletResponse) response).setStatus(301);
        }                                                                
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig config) throws ServletException {
        String massagedString = config.getInitParameter("urlPatterns").replaceAll("\\s+", " ");
        String[] temp = massagedString.split("\\s");
        urlPatterns = new Pattern[temp.length];
        for (int j=0;j<urlPatterns.length;j++) {
            urlPatterns[j] = Pattern.compile(temp[j]);
        }
    }

}
