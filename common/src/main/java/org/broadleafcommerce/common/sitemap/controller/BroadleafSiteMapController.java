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

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.sitemap.exception.SiteMapException;
import org.broadleafcommerce.common.sitemap.service.SiteMapGenerationResponse;
import org.broadleafcommerce.common.sitemap.service.SiteMapService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.ui.Model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
            return "Site map generation error:  " + siteMapGenResponse.getErrorCode();
        }
        return "Site map " + siteMapGenResponse.getSitemapIndexFileName() + " generated!";
    }

    /**
     * Retrieves a site map index file in XML format
     * 
     * @param request
     * @param response
     * @param model
     * @param fileName
     * @return
     */

    public FileSystemResource retrieveSiteMapIndex(HttpServletRequest request, HttpServletResponse response, Model model, String fileName) {
        return new FileSystemResource(new File(fixTempDirectory(System.getProperty("java.io.tmpdir")) + fileName));
    }

    /**
     * Retrieves a site map file in gzip format
     * 
     * @param request
     * @param response
     * @param model
     * @param fileName
     * @return
     */
    public void retrieveSiteMap(HttpServletRequest request, HttpServletResponse response, Model model, String fileName) {
        try {

            // get your file as InputStream
            InputStream is = new FileInputStream(new File(fixTempDirectory(System.getProperty("java.io.tmpdir")) + fileName));
            // copy it to response's OutputStream
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ex) {
            //LOG.info("Error writing file to output stream. Filename was '" + fileName + "'");
            throw new RuntimeException("IOError writing file to output stream");
        }
    }

    // Ensure that the temp directory ends with a "/"
    protected String fixTempDirectory(String tempDirectory) {
        assert tempDirectory != null;
        if (tempDirectory.endsWith("/")) {
            return tempDirectory + "/";
        }
        return tempDirectory;
    }

}
