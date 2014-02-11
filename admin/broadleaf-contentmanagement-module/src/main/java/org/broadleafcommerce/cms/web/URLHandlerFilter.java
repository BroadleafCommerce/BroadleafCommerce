/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.web;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.url.domain.URLHandler;
import org.broadleafcommerce.cms.url.service.URLHandlerService;
import org.broadleafcommerce.cms.url.type.URLRedirectType;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.util.UrlUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Responsible for setting up the site and locale used by Broadleaf Commerce components.
 *
 * @author bpolster
 */
@Component("blURLHandlerFilter")
public class URLHandlerFilter extends OncePerRequestFilter {
    
    @Resource(name = "blURLHandlerService")
    private URLHandlerService urlHandlerService;

    private static final Log LOG = LogFactory.getLog(URLHandlerFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String contextPath = request.getContextPath();
        String requestURIWithoutContext;
        if (request.getContextPath() != null) {
            requestURIWithoutContext = request.getRequestURI().substring(request.getContextPath().length());
        } else {
            requestURIWithoutContext = request.getRequestURI();
        }
        URLHandler handler = urlHandlerService.findURLHandlerByURI(requestURIWithoutContext);
        
        if (handler != null) {
            if (URLRedirectType.FORWARD == handler.getUrlRedirectType()) {              
                request.getRequestDispatcher(handler.getNewURL()).forward(request, response);               
            } else if (URLRedirectType.REDIRECT_PERM == handler.getUrlRedirectType()) {
                String url = UrlUtil.fixRedirectUrl(contextPath, handler.getNewURL());
                url = fixQueryString(request, url);
                response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                response.setHeader( "Location", url);
                response.setHeader( "Connection", "close" );
            } else if (URLRedirectType.REDIRECT_TEMP == handler.getUrlRedirectType()) {
                String url = UrlUtil.fixRedirectUrl(contextPath, handler.getNewURL());
                url = fixQueryString(request, url);
                response.sendRedirect(url);             
            }           
        } else {
            filterChain.doFilter(request, response);
        }
    }
    
    /**
     * If the url does not include "//" then the system will ensure that the application context
     * is added to the start of the URL.
     * 
     * @param url
     * @return
     * @throws Exception 
     */
    protected String fixQueryString(HttpServletRequest request, String url) {
        if (getPreserveQueryStringOnRedirect()) {
            try {
                Map parameterMap = request.getParameterMap();
                if (parameterMap != null && parameterMap.size() > 0) {
                    Set<String> queryParams = getExistingQueryParams(url);

                    String symbol = "?";
                    for (Object keyVal : parameterMap.keySet()) {
                        String key = (String) keyVal;
                        if (!queryParams.contains(key)) {
                            if (url.contains("?")) {
                                symbol = "&";
                            }
                            String param = URLEncoder.encode(key, "UTF-8");
                            String value = URLEncoder.encode(request.getParameter(key), "UTF-8");
                            url = url + symbol + param;
                            if (!StringUtils.isEmpty(value)) {
                                url = url + "=" + value;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOG.error("Error adjusting query string in URLHandlerFilter", e);
            }
            return url;
        }

        return url;
    }

    public static Set<String> getExistingQueryParams(String url) throws UnsupportedEncodingException {
        Set<String> query_params = new HashSet<String>();
        int pos = url.indexOf("?");
        if (pos > 0) {
            String query = url.substring(pos);
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                String param="";
                String value = null;
                if (idx > 0) {
                    param = pair.substring(0, idx);
                } else {
                    param=pair;
                }
                query_params.add(param);
            }
        }
        return query_params;
    }

    protected boolean getPreserveQueryStringOnRedirect() {
        return BLCSystemProperty.resolveBooleanSystemProperty("preserveQueryStringOnRedirect");
    }
}
