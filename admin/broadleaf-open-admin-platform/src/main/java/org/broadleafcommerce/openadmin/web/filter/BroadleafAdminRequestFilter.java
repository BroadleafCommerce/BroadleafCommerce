/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.web.filter;

import org.apache.commons.collections4.iterators.IteratorEnumeration;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.exception.SiteNotFoundException;
import org.broadleafcommerce.common.security.service.StaleStateProtectionServiceImpl;
import org.broadleafcommerce.common.security.service.StaleStateServiceException;
import org.broadleafcommerce.common.web.BroadleafSiteResolver;
import org.broadleafcommerce.common.web.BroadleafWebRequestProcessor;
import org.broadleafcommerce.openadmin.server.service.persistence.Persistable;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceThreadManager;
import org.broadleafcommerce.openadmin.server.service.persistence.TargetModeType;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * Responsible for setting the necessary attributes on the BroadleafRequestContext
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Component("blAdminRequestFilter")
public class BroadleafAdminRequestFilter extends AbstractBroadleafAdminRequestFilter {

    private final Log LOG = LogFactory.getLog(BroadleafAdminRequestFilter.class);

    @Resource(name = "blAdminRequestProcessor")
    protected BroadleafWebRequestProcessor requestProcessor;

    @Resource(name="blPersistenceThreadManager")
    protected PersistenceThreadManager persistenceThreadManager;

    @Override
    public void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws IOException, ServletException {
        if (!shouldProcessURL(request, request.getRequestURI())) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Process URL not processing URL " + request.getRequestURI());
            }
            filterChain.doFilter(request, response);
            return;
        }

        try {
            persistenceThreadManager.operation(TargetModeType.SANDBOX, new Persistable <Void, RuntimeException>() {
                @Override
                public Void execute() {
                    try {
                        requestProcessor.process(new ServletWebRequest(request, response));
                        filterChain.doFilter(request, response);
                        return null;
                    } catch (Exception e) {
                        throw ExceptionHelper.refineException(e);
                    }
                }
            });
        } catch (SiteNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (StaleStateServiceException e) {
            //catch state change attempts from a stale page
            forwardToConflictDestination(request,response);
        } finally {
            requestProcessor.postProcess(new ServletWebRequest(request, response));
        }
    }

    /**
     * Forward the user to the conflict error page.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void forwardToConflictDestination(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_CONFLICT);
        final Map reducedMap = new LinkedHashMap(request.getParameterMap());
        reducedMap.remove(BroadleafAdminRequestProcessor.CATALOG_REQ_PARAM);
        reducedMap.remove(BroadleafAdminRequestProcessor.PROFILE_REQ_PARAM);
        reducedMap.remove(BroadleafAdminRequestProcessor.SANDBOX_REQ_PARAM);
        reducedMap.remove(StaleStateProtectionServiceImpl.STATEVERSIONTOKENPARAMETER);
        reducedMap.remove(BroadleafSiteResolver.SELECTED_SITE_URL_PARAM);

        final HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request) {
            @Override
            public String getParameter(String name) {
                Object temp = reducedMap.get(name);
                Object[] response = new Object[0];
                if (temp != null) {
                    ArrayUtils.addAll(response, temp);
                }
                if (ArrayUtils.isEmpty(response)) {
                    return null;
                } else {
                    return (String) response[0];
                }
            }

            @Override
            public Map getParameterMap() {
                return reducedMap;
            }

            @Override
            public Enumeration getParameterNames() {
                return new IteratorEnumeration(reducedMap.keySet().iterator());
            }

            @Override
            public String[] getParameterValues(String name) {
                return (String[]) reducedMap.get(name);
            }
        };
        requestProcessor.process(new ServletWebRequest(wrapper, response));
        wrapper.getRequestDispatcher("/sc_conflict").forward(wrapper, response);
    }
}
