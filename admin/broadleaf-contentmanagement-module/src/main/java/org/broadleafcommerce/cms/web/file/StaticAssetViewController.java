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
package org.broadleafcommerce.cms.web.file;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.common.AssetNotFoundException;
import org.broadleafcommerce.cms.file.service.StaticAssetStorageService;
import org.broadleafcommerce.cms.file.service.operation.NamedOperationComponent;
import org.broadleafcommerce.cms.file.service.operation.NamedOperationManager;
import org.broadleafcommerce.cms.file.service.operation.StaticMapNamedOperationComponent;
import org.broadleafcommerce.common.classloader.release.ThreadLocalManager;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.BroadleafSiteResolver;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by jfischer
 */
public class StaticAssetViewController extends AbstractController {

    private static final Log LOG = LogFactory.getLog(StaticAssetViewController.class);
    
    protected String assetServerUrlPrefix;
    protected String viewResolverName;

    @Resource(name="blStaticAssetStorageService")
    protected StaticAssetStorageService staticAssetStorageService;

    @Resource(name = "blSiteResolver")
    protected BroadleafSiteResolver siteResolver;

    @Resource
    protected NamedOperationManager namedOperationManager;

    @PostConstruct
    protected void init() {
        if (getAllowUnnamedImageManipulation()) {
            LOG.warn("Allowing image manipulation strictly through URL parameters that the application does not know about"
                    + " is not recommended and can be used maliciously for nefarious purposes. Instead, you should set up"
                    + " a map of known operations and the transformations associated with each operation. This behavior will"
                    + " default to false starting with Broadleaf 3.2.0-GA. For more information"
                    + " see the docs at http://www.broadleafcommerce.com/docs/core/current/broadleaf-concepts/additional-configuration/asset-server-configuration");
        }
    }
    
    /**
     * Converts the given request parameter map into a single key-value map. This will also strip parameters that do not
     * conform to existing application-configured named operations according to {@link #allowUnnamedImageManipulation} that
     * appear in {@link NamedOperationManager#getNamedOperationComponents()}
     * @param parameterMap
     * @return
     */
    protected Map<String, String> convertParameterMap(Map<String, String[]> parameterMap) {
        Map<String, String> convertedMap = new LinkedHashMap<String, String>(parameterMap.size());
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            if (isAllowedUrlParameter(entry.getKey())) {
                convertedMap.put(entry.getKey(), StringUtils.join(entry.getValue(), ','));
            } else {
                // we didn't find it in the list of named operations, lets see if we allow that to happen
                if (getAllowUnnamedImageManipulation()) {
                    convertedMap.put(entry.getKey(), StringUtils.join(entry.getValue(), ','));
                } else {
                    LOG.debug("Stripping URL image manipulation parameter " + entry.getKey() + " as it is not a known named"
                            + " operation.");
                }
            }
        }

        return convertedMap;
    }
    
    protected boolean isAllowedUrlParameter(String parameter) {
        boolean parameterWithinNamedOperations = false;
        for (NamedOperationComponent component : namedOperationManager.getNamedOperationComponents()) {
            if (component.getClass().isAssignableFrom(StaticMapNamedOperationComponent.class)) {
                parameterWithinNamedOperations = ((StaticMapNamedOperationComponent) component).getNamedOperations().containsKey(parameter);
            }
            if (parameterWithinNamedOperations) {
                break;
            }
        }
        return parameterWithinNamedOperations;
    }

    /**
     * Process the static asset request by determining the asset name.
     * Checks the current sandbox for a matching asset.   If not found, checks the
     * production sandbox.
     *
     * The view portion will be handled by a component with the name "blStaticAssetView" This is
     * intended to be the specific class StaticAssetView.
     *
     * @see StaticAssetView
     *
     * @see #handleRequest
     */
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String fullUrl = removeAssetPrefix(request.getRequestURI());

        // Static Assets don't typically go through the Spring Security pipeline but they may need access 
        // to the site 
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        context.setSite(siteResolver.resolveSite(new ServletWebRequest(request, response)));
        try {
            Map<String, String> model = staticAssetStorageService.getCacheFileModel(fullUrl, convertParameterMap(request.getParameterMap()));
            return new ModelAndView(viewResolverName, model);
        } catch (AssetNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        } catch (Exception e) {
            LOG.error("Unable to retrieve static asset", e);
            throw new RuntimeException(e);
        } finally {
            ThreadLocalManager.remove();
        }
    }
    
    protected String removeAssetPrefix(String requestURI) {
        String fileName = requestURI;
        if (assetServerUrlPrefix != null) {
            int pos = fileName.indexOf(assetServerUrlPrefix);
            fileName = fileName.substring(pos+assetServerUrlPrefix.length());

            if (! fileName.startsWith("/")) {
                fileName = "/"+fileName;
            }
        }

        return fileName;
        
    }
    
    public boolean getAllowUnnamedImageManipulation() {
        boolean allowUnnamedImageManipulation = 
                BLCSystemProperty.resolveBooleanSystemProperty("asset.server.allow.unnamed.image.manipulation");
        return allowUnnamedImageManipulation;
    }

    public String getAssetServerUrlPrefix() {
        return assetServerUrlPrefix;
    }

    public void setAssetServerUrlPrefix(String assetServerUrlPrefix) {        
        this.assetServerUrlPrefix = assetServerUrlPrefix;
    }

    public String getViewResolverName() {
        return viewResolverName;
    }

    public void setViewResolverName(String viewResolverName) {
        this.viewResolverName = viewResolverName;
    }
}
