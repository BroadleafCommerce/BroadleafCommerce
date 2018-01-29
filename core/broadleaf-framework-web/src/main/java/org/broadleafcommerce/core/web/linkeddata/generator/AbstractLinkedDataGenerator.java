/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.linkeddata.generator;

import org.broadleafcommerce.common.breadcrumbs.service.BreadcrumbService;
import org.broadleafcommerce.common.web.BaseUrlResolver;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 
 * 
 * @author Nathan Moore (nathanmoore).
 */
public abstract class AbstractLinkedDataGenerator implements LinkedDataGenerator {
    protected static final String DEFAULT_STRUCTURED_CONTENT_CONTEXT = "http://schema.org/";

    @Autowired
    protected Environment environment;

    @Resource(name = "blBaseUrlResolver")
    protected BaseUrlResolver baseUrlResolver;

    @Resource(name = "blBreadcrumbService")
    protected BreadcrumbService breadcrumbService;
    
    @Resource(name = "blLinkedDataGeneratorExtensionManager")
    protected LinkedDataGeneratorExtensionManager extensionManager;

    @Override
    public abstract boolean canHandle(final HttpServletRequest request);

    @Override
    public void getLinkedDataJSON(final String url, final HttpServletRequest request, final JSONArray schemaObjects) throws JSONException {
        getLinkedDataJsonInternal(url, request, schemaObjects);
    }
    
    protected abstract JSONArray getLinkedDataJsonInternal(final String url, final HttpServletRequest request, 
                                                           final JSONArray schemaObjects) throws JSONException;

    protected String getRequestUri() {
        final HttpServletRequest request = BroadleafRequestContext.getBroadleafRequestContext().getRequest();

        return request.getRequestURI();
    }

    protected static Map<String, String[]> getRequestParams() {
        Map<String, String[]> params = new HashMap<>();

        if (BroadleafRequestContext.getRequestParameterMap() != null) {
            params = new HashMap<>(BroadleafRequestContext.getRequestParameterMap());
        }

        return params;
    }

    protected String getSiteBaseUrl() {
        return baseUrlResolver.getSiteBaseUrl();
    }

    @Override
    public String getStructuredDataContext() {
        return environment.getProperty("structured.data.context", DEFAULT_STRUCTURED_CONTENT_CONTEXT);
    }
}
