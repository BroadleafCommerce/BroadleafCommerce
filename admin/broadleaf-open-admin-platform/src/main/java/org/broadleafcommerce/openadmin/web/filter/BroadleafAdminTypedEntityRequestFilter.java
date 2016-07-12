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
import org.broadleafcommerce.common.admin.domain.TypedEntity;
import org.broadleafcommerce.common.dao.GenericEntityDao;
import org.broadleafcommerce.common.web.BroadleafWebRequestProcessor;
import org.broadleafcommerce.openadmin.server.security.domain.AdminPermission;
import org.broadleafcommerce.openadmin.server.security.domain.AdminRole;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.remote.SecurityVerifier;
import org.broadleafcommerce.openadmin.server.security.service.navigation.AdminNavigationService;
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
 * @author Jon Fleschler (jfleschler)
 */
@Component("blAdminTypedEntityRequestFilter")
public class BroadleafAdminTypedEntityRequestFilter extends AbstractBroadleafAdminRequestFilter {

    private final Log LOG = LogFactory.getLog(BroadleafAdminTypedEntityRequestFilter.class);

    @Resource(name = "blAdminRequestProcessor")
    protected BroadleafWebRequestProcessor requestProcessor;

    @Resource(name="blAdminNavigationService")
    protected AdminNavigationService adminNavigationService;

    @Resource(name = "blGenericEntityDao")
    protected GenericEntityDao genericEntityDao;

    @Resource(name = "blAdminSecurityRemoteService")
    protected SecurityVerifier adminRemoteSecurityService;

    @Override
    public void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws IOException, ServletException {
        if (isRequestForTypedEntity(request, response)) {
            return;
        }
        filterChain.doFilter(request, response);
    }

    @SuppressWarnings("unchecked")
    public boolean isRequestForTypedEntity(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String servletPath = request.getServletPath();
        if (!servletPath.contains(":")) {
            return false;
        }

        // Find the Admin Section for the typed entity.
        String sectionKey = getSectionKeyFromRequest(request);
        AdminSection typedEntitySection = adminNavigationService.findAdminSectionByURI(sectionKey);

        // If the Typed Entity Section does not exist, continue with the filter chain.
        if (typedEntitySection == null) {
            return false;
        }

        // Check if admin user has access to this section.
        if (!adminUserHasAccess(typedEntitySection)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access is denied");
            return true;
        }

        // Add the typed entity admin section to the request.
        request.setAttribute("typedEntitySection", typedEntitySection);

        // Find the type and build the new path.
        String type = getEntityTypeFromRequest(request);
        final String forwardPath = servletPath.replace(type, "");

        // Get the type field name on the Entity for the given section.
        String typedFieldName = getTypeFieldName(typedEntitySection);

        // Build out the new parameter map to be forwarded.
        final Map parameters = new LinkedHashMap(request.getParameterMap());
        if (typedFieldName != null) {
            parameters.put(typedFieldName, new String[]{type.substring(1).toUpperCase()});
        }

        // Build our request wrapper.
        final HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request) {
            @Override
            public String getParameter(String name) {
                Object temp = parameters.get(name);
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
                return parameters;
            }

            @Override
            public Enumeration getParameterNames() {
                return new IteratorEnumeration(parameters.keySet().iterator());
            }

            @Override
            public String[] getParameterValues(String name) {
                return (String[]) parameters.get(name);
            }

            @Override
            public String getServletPath() {
                return forwardPath;
            }
        };
        requestProcessor.process(new ServletWebRequest(wrapper, response));

        // Forward the wrapper to the appropriate path
        wrapper.getRequestDispatcher(wrapper.getServletPath()).forward(wrapper, response);
        return true;
    }

    protected boolean adminUserHasAccess(AdminSection typedEntitySection) {
        AdminUser adminUser = adminRemoteSecurityService.getPersistentAdminUser();
        // Check permissions assigned directly to the user.
        for (AdminPermission permission : adminUser.getAllPermissions()) {
            if (typedEntitySection.getPermissions().contains(permission)) {
                return true;
            }
        }
        // Check all role base permissions.
        for (AdminRole role : adminUser.getAllRoles()) {
            for (AdminPermission permission : role.getAllPermissions()) {
                if (typedEntitySection.getPermissions().contains(permission)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected String getEntityTypeFromRequest(HttpServletRequest request) {
        String uri = request.getServletPath();
        int typeIndex = uri.indexOf(":");
        int endIndex = uri.indexOf("/", typeIndex);
        String type;
        if (endIndex > 0) {
            type = uri.substring(typeIndex, endIndex);
        } else {
            type = uri.substring(typeIndex);
        }
        return type;
    }

    protected String getSectionKeyFromRequest(HttpServletRequest request) {
        String uri = request.getServletPath();
        String sectionKey = uri.replace("/status", "");
        int endIndex = sectionKey.indexOf("/", 1);
        if (endIndex > 0) {
            sectionKey = sectionKey.substring(0, endIndex);
        }
        return sectionKey;
    }

    protected String getTypeFieldName(AdminSection adminSection) {
        try {
            Class<?> implClass = genericEntityDao.getCeilingImplClass(adminSection.getCeilingEntity());
            return ((TypedEntity) implClass.newInstance()).getTypeFieldName();
        } catch (Exception e) {
            return null;
        }
    }
}
