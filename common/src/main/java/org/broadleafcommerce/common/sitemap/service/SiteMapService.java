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

package org.broadleafcommerce.common.sitemap.service;

import org.broadleafcommerce.common.sitemap.exception.SiteMapException;

import java.io.IOException;
import java.util.List;

/**
 * Class responsible for generating the SiteMap.xml and related files.  
 * 
 * This service generates the structure of the SiteMap file.  It assumes the use of SiteMap indexes 
 * and follows the convention siteMap#.xml 
 *
 * @author bpolster
 * 
 */
public interface SiteMapService {

    /**
     * Generates a well formed SiteMap.   
     * 
     * Implementation should implement a well formed SiteMap (for example, the default Broadleaf SiteMapImpl
     * returns a SiteMap compatible with this schema.  
     * 
     * http://www.sitemaps.org/schemas/sitemap/0.9/siteindex.xsd
     * 
     * Implementations should utilize the list of SiteMapGenerators that build the actual entries in the sitemap xml
     * files.
     * @throws SiteMapException 
     * 
     * @see SiteMapGenerator
     */
    public SiteMapGenerationResponse generateSiteMap() throws IOException, SiteMapException;

    /**
     * Returns the list of SiteMapGenerators used to generate the SiteMap.
     * @return
     */
    public List<SiteMapGenerator> getSiteMapGenerators();

    /**
     * Sets the list of SiteMapGeneratorConfigurations.
     * @return
     */
    public void setSiteMapGenerators(List<SiteMapGenerator> siteMapGenerators);

    /**
     * Returns the root directory used to build the sitemap files.
     * Defaults to java.io.tmpdir.
     * 
     * @return
     */
    public String getTempDirectory();

    /**
     * Returns the root directory used to build the sitemap files.
     * Defaults to java.io.tmpdir.
     * 
     * @return
     */
    public void setTempDirectory(String tempDirectory);

    /**
     * Ensure that the temp directory ends with a "/"
     * 
     * @param tempDirectory
     * @return
     */
    public String fixTempDirectory(String tempDirectory);


    /**
     * Return the flag indicating if the site map index file will be gzipped.
     * 
     * @return
     */
    public boolean isGzipSiteMapIndex();

    /**
     * Sets the flag indicating if the site map index file will be gzipped.
     * 
     * @param gzipSiteMapIndex
     */
    public void setGzipSiteMapIndex(boolean gzipSiteMapIndex);

    /**
     * Return the flag indicating if the site map file(s) will be gzipped.
     * 
     * @return
     */
    public boolean isGzipSiteMap();

    /**
     * Sets the flag indicating if the site map file(s) will be gzipped.
     * 
     * @param gzipSiteMap
     */
    public void setGzipSiteMap(boolean gzipSiteMap);

}
