/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.web.filter;

import org.apache.commons.collections4.iterators.IteratorEnumeration;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.exception.SiteNotFoundException;
import org.broadleafcommerce.common.persistence.TargetModeType;
import org.broadleafcommerce.common.security.service.StaleStateProtectionService;
import org.broadleafcommerce.common.security.service.StaleStateProtectionServiceImpl;
import org.broadleafcommerce.common.security.service.StaleStateServiceException;
import org.broadleafcommerce.common.web.BroadleafSiteResolver;
import org.broadleafcommerce.common.web.BroadleafWebRequestProcessor;
import org.broadleafcommerce.common.web.filter.FilterOrdered;
import org.broadleafcommerce.openadmin.security.ClassNameRequestParamValidationService;
import org.broadleafcommerce.openadmin.server.service.persistence.Persistable;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceThreadManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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

    @Autowired
    @Qualifier("blAdminRequestProcessor")
    protected BroadleafWebRequestProcessor requestProcessor;

    @Autowired
    @Qualifier("blPersistenceThreadManager")
    protected PersistenceThreadManager persistenceThreadManager;

    @Autowired
    @Qualifier("blClassNameRequestParamValidationService")
    protected ClassNameRequestParamValidationService validationService;

    @Autowired
    @Qualifier("blStaleStateProtectionService")
    protected StaleStateProtectionService staleStateProtectionService;

    @Override
    public void doFilterInternalUnlessIgnored(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws IOException, ServletException {

        if (!validateClassNameParams(request)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

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
                        if (!staleStateProtectionService.sendRedirectOnStateChange(
                                response,
                                BroadleafAdminRequestProcessor.SANDBOX_REQ_PARAM,
                                BroadleafAdminRequestProcessor.CATALOG_REQ_PARAM,
                                BroadleafAdminRequestProcessor.PROFILE_REQ_PARAM
                        )) {
                            filterChain.doFilter(request, response);
                        }
                        return null;
                    } catch (Exception e) {
                        throw ExceptionHelper.refineException(e);
                    }
                }
            });
        } catch (SiteNotFoundException e) {
            LOG.warn("Could not resolve a site for the given request, returning not found");
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (StaleStateServiceException e) {
            //catch state change attempts from a stale page
            forwardToConflictDestination(request,response);
        } finally {
            requestProcessor.postProcess(new ServletWebRequest(request, response));
        }
    }

    protected boolean validateClassNameParams(HttpServletRequest request) {
        String ceilingEntityClassname = request.getParameter("ceilingEntityClassname");
        String ceilingEntity = request.getParameter("ceilingEntity");
        String ceilingEntityFullyQualifiedClassname = request.getParameter("fields['ceilingEntityFullyQualifiedClassname'].value");
        String originalType = request.getParameter("fields['__originalType'].value");
        String entityType = request.getParameter("entityType");
        Map<String, String> params = new HashMap<>(2);
        params.put("ceilingEntityClassname", ceilingEntityClassname);
        params.put("entityType", entityType);
        params.put("ceilingEntity", ceilingEntity);
        params.put("ceilingEntityFullyQualifiedClassname", ceilingEntityFullyQualifiedClassname);
        params.put("__originalType", originalType);
        return validationService.validateClassNameParams(params, "blPU");
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

    @Override
    public int getOrder() {
        return FilterOrdered.POST_SECURITY_HIGH;
    }
}
