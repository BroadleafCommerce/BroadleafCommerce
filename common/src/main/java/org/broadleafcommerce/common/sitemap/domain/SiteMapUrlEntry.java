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
