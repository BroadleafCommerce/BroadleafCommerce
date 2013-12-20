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

package org.broadleafcommerce.common.sitemap.service;

import org.broadleafcommerce.common.sitemap.domain.SiteMapGeneratorConfiguration;


/**
 * Responsible for generating site map entries.   
 * 
 * Each SiteMapGenerator can generate 
 * 
 * @author bpolster
 *
 */
public interface SiteMapGenerator {
    
    /**
     * Returns true if this SiteMapGenerator is able to process the passed in siteMapGeneratorConfiguration.   
     * 
     * @param siteMapGeneratorConfiguration
     * @return
     */
    public boolean canHandleSiteMapConfiguration(SiteMapGeneratorConfiguration siteMapGeneratorConfiguration);
    
    /**
     * Typically, the generator will loop through and build a list of URLs to add to the sitemap by calling 
     * methods on the SiteMapBuilder.
     * 
     * @param siteMapGeneratorConfiguration
     * @param siteMapBuilder
     */
    public void addSiteMapEntries(SiteMapGeneratorConfiguration siteMapGeneratorConfiguration, SiteMapBuilder siteMapBuilder);

}
