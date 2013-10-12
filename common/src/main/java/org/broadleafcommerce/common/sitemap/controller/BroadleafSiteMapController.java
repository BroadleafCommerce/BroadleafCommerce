/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.sitemap.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.sitemap.exception.SiteMapException;
import org.broadleafcommerce.common.sitemap.service.SiteMapGenerationResponse;
import org.broadleafcommerce.common.sitemap.service.SiteMapService;
import org.springframework.ui.Model;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller to generate and retrieve site map files.
 * 
 * @author Joshua Skorton (jskorton)
 */
public class BroadleafSiteMapController {

    private static final Log LOG = LogFactory.getLog(BroadleafSiteMapController.class);

    @Resource(name = "blSiteMapService")
    protected SiteMapService siteMapService;
    
    /**
     * Generates site map
     * 
     * @param request
     * @param response
     * @param model
     * @return
     * @throws SiteMapException
     * @throws IOException
     */
    public String generateSiteMap(HttpServletRequest request, HttpServletResponse response, Model model) throws SiteMapException, IOException {
        SiteMapGenerationResponse siteMapGenResponse = siteMapService.generateSiteMap();
        if (siteMapGenResponse.isHasError()) {
            return "<html>Site map generation error:  " + siteMapGenResponse.getErrorCode() + "</html>";
        }
        return "<html>Site map " + siteMapGenResponse.getSitemapIndexFileName() + " generated!</html>";
    }

    /**
     * Retrieves site map
     * 
     * @param request
     * @param response
     * @param model
     * @return
     */
    public String retrieveSiteMap(HttpServletRequest request, HttpServletResponse response, Model model) {
        return "";
    }

}
