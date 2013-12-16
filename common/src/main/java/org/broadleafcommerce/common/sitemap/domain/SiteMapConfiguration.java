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

package org.broadleafcommerce.common.sitemap.domain;

import org.broadleafcommerce.common.config.domain.ModuleConfiguration;

import java.util.List;


/**
 * The SiteMapConfiguration is a class that drives the building of the SiteMap.  It contains general properties that drive
 * the creation of the SiteMap such as directory paths, etc.
 * 
 * @author bpolster
 */
public interface SiteMapConfiguration extends ModuleConfiguration {

    /**
     * Returns the name of the primary site map file.  Defaults to "sitemap_index.xml".
     * 
     * An index file will be created if there is more than one site map file.  If an index file is created,
     * then the primary file refers to that index file.  If there is no index file, the primary file refers
     * to the only site map file.
     * 
     * If this value was "sitemap.xml" then your sites robots.txt file would be configured to include the 
     * following line.
     * 
     * The value should include the directory path if applicable.
     * 
     * Sitemap: http://www.yoursite.com/sitemap_index.xml
     * 
     * @return String representing the filename
     */
    public String getSiteMapPrimaryFileName();
    
    /**
     * Sets the primary site map file name.
     * 
     * @see #getSiteMapFileName()
     * @param siteMapPrimaryFileName
     */
    public void setSiteMapPrimaryFileName(String siteMapPrimaryFileName);
        
    /**
     * Returns a path to the URL where the site map files will be located.   Typically this is the 
     * production address (e.g. http://www.mysite.com/);
     *  
     * @return
     */
    public String getSiteUrlPath();

    /**
     * Sets a path to the URL where the site map files will be located.   Typically this is the 
     * production address (e.g. http://www.mysite.com/);
     * 
     * @param siteUrlPath
     */
    public void setSiteUrlPath(String siteUrlPath);

    /**
     * Returns the list of SiteMapGeneratorConfigurations used by this SiteMapConfiguration.
     * 
     * @return
     */
    public List<SiteMapGeneratorConfiguration> getSiteMapGeneratorConfigurations();
    
    /**
     * Sets the list of SiteMapGeneratorConfigurations.
     * 
     * @param siteMapGeneratorConfigurations
     */
    public void setSiteMapGeneratorConfigurations(List<SiteMapGeneratorConfiguration> siteMapGeneratorConfigurations);

    /**
     * Returns the maximumUrlEntriesPerFile.   Defaults to 50000 per the sitemap.org schema requirement of
     * a maximum of 50000 per file.   Useful to override for testing purposes.
     * 
     * Will allow values over 50000 but this would be considered invalid for the 0.9 version of the sitemap.org contract.
     * 
     * @return
     */
    public Integer getMaximumUrlEntriesPerFile();

    /**
     * Sets the maximumUrl Entries per sitemap file.   The sitemap.org contract (version 0.9) says that this number 
     * should be a maximum of 50000 but it may be helpful for some implementations to override the default
     * for testing purposes.
     * 
     * @param maximumUrlEntriesPerFile
     */
    public void setMaximumUrlEntriesPerFile(Integer maximumUrlEntriesPerFile);

    /**
     * Ensure that the site URL path does not end with a "/"
     * 
     * @param siteUrlPath
     * @return
     */
    public String fixSiteUrlPath(String siteUrlPath);

}
