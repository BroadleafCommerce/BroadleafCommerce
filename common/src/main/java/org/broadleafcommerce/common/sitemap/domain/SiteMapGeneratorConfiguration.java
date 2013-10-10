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
import org.broadleafcommerce.common.sitemap.service.type.SiteMapChangeFreqType;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapGeneratorType;

import java.math.BigDecimal;
import java.util.List;

/**
 * Sample URL tag generated and controlled by this configuration.
 * 
 * <url>
 *   <loc>http://www.heatclinic.com/hot-sauces</loc>
 *   <lastmod>2009-11-07</lastmod>
 *   <changefreq>weekly</changefreq>
 *   <priority>0.5</priority>
 * </url>
 * 
 * @author bpolster
 */
public interface SiteMapGeneratorConfiguration extends ModuleConfiguration {
    
    /**
     * Returns the list of SiteMapChangeFreqTypes.
     * 
     * @return
     */
    public SiteMapChangeFreqType getSiteMapChangeFreqType();

    /**
     * Sets the list of SiteMapChangeFreqTypes.
     * 
     * @param siteMapChangeFreqType
     */
    public void setSiteMapChangeFreqType(SiteMapChangeFreqType siteMapChangeFreqType);

    /**
     * Returns the SiteMapPriority.
     * 
     * @return
     */
    public BigDecimal getSiteMapPriority();

    /**
     * Sets the SiteMapPriority.  Must be a two digit value between 0.0 and 1.0.  The default priority is 0.5
     * 
     * @param siteMapPriority
     */
    public void setSiteMapPriority(BigDecimal siteMapPriority);

    /**
     * Returns the list of SiteMapGeneratorTypes.
     * 
     * @return
     */
    public SiteMapGeneratorType getSiteMapGeneratorType();
    
    /**
     * Sets the list of SiteMapGeneratorTypes.
     * 
     * @param siteMapGeneratorType
     */
    public void setSiteMapGeneratorType(SiteMapGeneratorType siteMapGeneratorType);
    
    /**
     * Returns a list of custom SiteMapURLEntrys.
     * 
     * @return
     */
    public List<SiteMapURLEntry> getCustomURLEntries();

    /**
     * Sets a list of custom SiteMapURLEntrys.
     * 
     * @param customURLEntries
     */
    public void setCustomURLEntries(List<SiteMapURLEntry> customURLEntries);

    /**
     * Returns the SiteMapConfiguration.
     * 
     * @return
     */
    public SiteMapConfiguration getSiteMapConfiguration();

    /**
     * Sets the SiteMapConfiguration.
     * 
     * @param siteMapConfiguration
     */
    void setSiteMapConfiguration(SiteMapConfiguration siteMapConfiguration);

}
