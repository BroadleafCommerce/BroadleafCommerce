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
     * The name of the file that holds the SiteMap.xml.    
     * 
     * If this value was sitemap.xml then your sites robots.txt file would be configured to include the 
     * following line.
     * 
     * The value should include the directory path if applicable.
     * 
     * Sitemap: http://www.yoursite.com/sitemap.xml
     * 
     * @return String representing the filename
     */
    public String getSiteMapFileName();
    
    /**
     * Sets the SiteMap file name.
     * @see #getSiteMapFileName()
     */
    public void setSiteMapFileName(String siteMapFileName);
        
    /**
     * Returns the list of SiteMapGeneratorConfigurations used by this SiteMapConfiguration.
     * @return
     */
    public List<SiteMapGeneratorConfiguration> getSiteMapGeneratorConfigurations();
    
    /**
     * Sets the list of SiteMapGeneratorConfigurations.
     * @return
     */
    public void setSiteMapGeneratorConfigurations(List<SiteMapGeneratorConfiguration> siteMapGeneratorConfigurations);

}
