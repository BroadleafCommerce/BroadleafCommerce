/*
 * #%L
 * BroadleafCommerce Common Libraries
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
     * Returns the list of SiteMapGeneratorConfigurations used by this SiteMapConfiguration.
     * 
     * @return
     */
    List<SiteMapGeneratorConfiguration> getSiteMapGeneratorConfigurations();
    
    /**
     * Sets the list of SiteMapGeneratorConfigurations.
     * 
     * @param siteMapGeneratorConfigurations
     */
    void setSiteMapGeneratorConfigurations(List<SiteMapGeneratorConfiguration> siteMapGeneratorConfigurations);

    /**
     * Returns the maximumUrlEntriesPerFile.   Defaults to 50000 per the sitemap.org schema requirement of
     * a maximum of 50000 per file.   Useful to override for testing purposes.
     * 
     * Will allow values over 50000 but this would be considered invalid for the 0.9 version of the sitemap.org contract.
     * 
     * @return
     */
    Integer getMaximumUrlEntriesPerFile();

    /**
     * Sets the maximumUrl Entries per sitemap file.   The sitemap.org contract (version 0.9) says that this number 
     * should be a maximum of 50000 but it may be helpful for some implementations to override the default
     * for testing purposes.
     * 
     * @param maximumUrlEntriesPerFile
     */
    void setMaximumUrlEntriesPerFile(Integer maximumUrlEntriesPerFile);

    /**
     * Ensure that the site URL path does not end with a "/"
     * 
     * @param siteUrlPath
     * @return
     */
    String fixSiteUrlPath(String siteUrlPath);

    /**
     * The name to use for the primary site map file when it does not contain indexed files.   Note that
     * changing the name of the siteMap file should be reflected in robots.txt (See BroadleafRobotsController)
     * if using Broadleaf to produce the robots.txt file.
     * 
     * 
     * Returns "sitemap.xml" if no value is set.
     * @return
     */
    String getSiteMapFileName();

    /**
     * Sets the value to be returned for the name of the sitemap file.
     * @see #getSiteMapFileName()
     */
    void setSiteMapFileName(String siteMapFileName);

    /**
     * The name to use for the primary site map file when it contains indexed files.
     * 
     * Delegates to {@link #getSiteMapFileName()} if not set.   Be sure to update robots.txt if 
     * changing this value.
     * 
     * @see #getSiteMapFileName()
     * @return
     */
    String getIndexedSiteMapFileName();

    /**
     * Sets the name of the file to use when creating sitemaps and the system requires indexed
     * files.
     */
    void setIndexedSiteMapFileName(String fileName);

    /**
     * The name to use for the indexed sitemap files.
     * 
     * Defaults to the pattern of {@link #getSiteMapFileName()}###.    Where "###" is a token that 
     * will be replaced with the current index.
     * 
     * For example, a file that required 2 indexed files would create the following two files:
     * sitemap1.xml
     * sitemap2.xml
     * 
     * @see #getSiteMapFileName()
     * @return
     */
    String getSiteMapIndexFilePattern();

    /**
     * Sets the name of the file to use when creating sitemaps and the system requires indexed
     * files.
     */
    void setIndexedSiteMapFilePattern(String filePattern);

}
