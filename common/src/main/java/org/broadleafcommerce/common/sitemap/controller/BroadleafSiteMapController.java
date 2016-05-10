/*
 * #%L
 * BroadleafCommerce Common Libraries
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

package org.broadleafcommerce.common.sitemap.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.sitemap.service.SiteMapService;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
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
     * Retrieves a site map index file in XML format
     * 
     * @param request
     * @param response
     * @param model
     * @param fileName
     * @return
     */
    public FileSystemResource retrieveSiteMapFile(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        File siteMapFile = siteMapService.getSiteMapFile(getRequestURIWithoutContext(request));
        if (siteMapFile == null || !siteMapFile.exists()) {
            response.setStatus(404);
            return null;
        }
        return new FileSystemResource(siteMapFile);
    }

    protected String getRequestURIWithoutContext(HttpServletRequest request) {
        if (request.getContextPath() != null) {
            return request.getRequestURI().substring(request.getContextPath().length());
        } else {
            return request.getRequestURI();
        }
    }

}
