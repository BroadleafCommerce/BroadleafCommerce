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
import org.broadleafcommerce.common.sandbox.dao.SandBoxDao;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
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
    private static final String SANDBOX_ADMIN_ID_VAR = "blAdminCurrentSandboxId";
    private static final String SANDBOX_ID_VAR = "blSandboxId";
    private String assetServerUrlPrefix;
    private String viewResolverName;

    @Resource(name="blStaticAssetStorageService")
    protected StaticAssetStorageService staticAssetStorageService;

    @Resource(name="blSandBoxDao")
    protected SandBoxDao sandBoxDao;

    protected Map<String, String> convertParameterMap(Map<String, String[]> parameterMap) {
        Map<String, String> convertedMap = new LinkedHashMap<String, String>(parameterMap.size());
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            convertedMap.put(entry.getKey(), StringUtils.join(entry.getValue(), ','));
        }

        return convertedMap;
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

        try {
           Long sandBoxId = (Long) request.getSession().getAttribute(SANDBOX_ID_VAR);
           if (sandBoxId == null) {
               sandBoxId = (Long) request.getSession().getAttribute(SANDBOX_ADMIN_ID_VAR);
           }
           SandBox sandBox = null;
           if (sandBoxId != null) {
               sandBox = sandBoxDao.retrieve(sandBoxId);
           }
           
           try {
               Map<String, String> model = staticAssetStorageService.getCacheFileModel(fullUrl, sandBox, convertParameterMap(request.getParameterMap()));
               return new ModelAndView(viewResolverName, model);
           } catch (AssetNotFoundException e) {
               response.setStatus(HttpServletResponse.SC_NOT_FOUND);
               return null;
           }
       } catch (Exception e) {
           LOG.error("Unable to retrieve static asset", e);
           throw new RuntimeException(e);
       }        
    }
    
    private String removeAssetPrefix(String requestURI) {
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
