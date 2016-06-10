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
package org.broadleafcommerce.common.security.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.security.service.ExploitProtectionService;
import org.broadleafcommerce.common.security.service.StaleStateProtectionService;
import org.broadleafcommerce.common.security.service.StaleStateServiceException;
import org.springframework.security.web.util.AntPathRequestMatcher;
import org.springframework.security.web.util.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Checks the validity of the CSRF token on every POST request. Also Checks the validity of the state token on every POST
 * request. Its purpose is to help protect against a page being
 * submitted with stale state. This can occur when key state has changed (either in session, or otherwise) that makes the
 * current POST request no longer viable. See {@link StaleStateProtectionService} for more info on purpose and usage.
 * </p>
 * You can inject excluded Request URI patterns to bypass this filter.
 * This filter uses the AntPathRequestMatcher which compares a pre-defined ant-style pattern against the URL
 * ({@code servletPath + pathInfo}) of an {@code HttpServletRequest}.
 * This allows you to use wildcard matching as well, for example {@code /**} or {@code **}
 *
 * @see AntPathRequestMatcher
 *
 * @author Jeff Fischer
 */
public class SecurityFilter extends GenericFilterBean {

    protected static final Log LOG = LogFactory.getLog(SecurityFilter.class);
    
    @Resource(name="blStaleStateProtectionService")
    protected StaleStateProtectionService staleStateProtectionService;

    @Resource(name="blExploitProtectionService")
    protected ExploitProtectionService exploitProtectionService;

    protected List<String> excludedRequestPatterns;

    @Override
    public void doFilter(ServletRequest baseRequest, ServletResponse baseResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) baseRequest;
        HttpServletResponse response = (HttpServletResponse) baseResponse;

        boolean excludedRequestFound = false;
        if (excludedRequestPatterns != null && excludedRequestPatterns.size() > 0) {
            for (String pattern : excludedRequestPatterns) {
                RequestMatcher matcher = new AntPathRequestMatcher(pattern);
                if (matcher.matches(request)) {
                    excludedRequestFound = true;
                    break;
                }
            }
        }

        // We only validate CSRF tokens on POST
        if (request.getMethod().equals("POST") && !excludedRequestFound) {
            String requestToken = request.getParameter(exploitProtectionService.getCsrfTokenParameter());
            try {
                exploitProtectionService.compareToken(requestToken);
            } catch (ServiceException e) {
                throw new ServletException(e);
            }
        }

        if (staleStateProtectionService.isEnabled()) {
            // We only validate tokens on POST
            // Catch attempts to update form data from a stale page (i.e. a important state change has taken place for this session)
            if (request.getMethod().equals("POST") && !excludedRequestFound) {
                String requestToken = request.getParameter(staleStateProtectionService.getStateVersionTokenParameter());
                try {
                    staleStateProtectionService.compareToken(requestToken);
                } catch (StaleStateServiceException e) {
                    throw new ServletException(e);
                }
            }
        }

        chain.doFilter(request, response);
    }

    public List<String> getExcludedRequestPatterns() {
        return excludedRequestPatterns;
    }

    /**
     * This allows you to declaratively set a list of excluded Request Patterns
     *
     * <bean id="blCsrfFilter" class="org.broadleafcommerce.common.security.handler.CsrfFilter" >
     *     <property name="excludedRequestPatterns">
     *         <list>
     *             <value>/exclude-me/**</value>
     *         </list>
     *     </property>
     * </bean>
     *
     **/
    public void setExcludedRequestPatterns(List<String> excludedRequestPatterns) {
        this.excludedRequestPatterns = excludedRequestPatterns;
    }
}
