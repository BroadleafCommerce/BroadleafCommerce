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

import org.broadleafcommerce.common.sitemap.service.type.SiteMapChangeFreqType;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapPriorityType;

import java.io.Serializable;
import java.util.Date;

/**
 * Sample URL entry
 * 
 * <url>
 *   <loc>http://www.heatclinic.com/hot-sauces</loc>
 *   <lastmod>2009-11-07</lastmod>
 *   <changefreq>weekly</changefreq>
 *   <priority>0.5</priority>
 * </url>
 * 
 * @author Josh
 */
public interface SiteMapUrlEntry extends Serializable {

    /**
     * Returns the SiteMapURLEntry Id.
     * 
     * @return
     */
    public Long getId();

    /**
     * Sets the SiteMapURLEntry Id.
     * 
     * @param id
     */
    public void setId(Long id);

    /**
     * Returns the URL location.
     * 
     * @return
     */
    public String getLocation();
    
    /**
     * Sets the URL location.
     * 
     * @param location
     */
    public void setLocation(String location);
    
    /**
     * Returns the last modified date.
     * 
     * @return
     */
    public Date getLastMod();
    
    /**
     * Sets the last modified date.
     * 
     * @param date
     */
    public void setLastMod(Date date);

    /**
     * Returns the SiteMapChangeFreqType.
     * 
     * @return
     */
    public SiteMapChangeFreqType getSiteMapChangeFreq();
    
    /**
     * Sets the SiteMapChangeFreqType.
     * 
     * @param siteMapChangeFreq
     */
    public void setSiteMapChangeFreq(SiteMapChangeFreqType siteMapChangeFreq);
    
    /**
     * Returns the SiteMapPriority.
     * 
     * @return
     */
    public SiteMapPriorityType getSiteMapPriority();

    /**
     * Sets the SiteMapPriority.  Must be a two digit value between 0.0 and 1.0.
     * 
     * @param siteMapPriority
     */
    public void setSiteMapPriority(SiteMapPriorityType siteMapPriority);

    /**
     * Returns the SiteMapGeneratorConfiguration.
     * 
     * @return
     */
    public CustomUrlSiteMapGeneratorConfiguration getCustomUrlSiteMapGeneratorConfiguration();

    /**
     * Sets the SiteMapGeneratorConfiguration.
     * 
     * 
     * @param siteMapGeneratorConfiguration
     */
    public void setCustomUrlSiteMapGeneratorConfiguration(CustomUrlSiteMapGeneratorConfiguration customUrlSiteMapGeneratorConfiguration);

}
