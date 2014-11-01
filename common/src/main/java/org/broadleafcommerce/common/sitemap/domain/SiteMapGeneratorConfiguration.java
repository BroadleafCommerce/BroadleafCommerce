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
import org.broadleafcommerce.common.sitemap.service.type.SiteMapGeneratorType;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapPriorityType;

import java.io.Serializable;

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
public interface SiteMapGeneratorConfiguration extends Serializable {
    
    /**
     * Returns the SiteMapGeneratorConfiguration Id.
     * 
     * @return
     */
    public Long getId();

    /**
     * Sets the SiteMapGeneratorConfiguration Id.
     * 
     * @param id
     */
    public void setId(Long id);

    /**
     * Returns the "disabled" boolean.
     * 
     * @return
     */
    public Boolean isDisabled();

    /**
     * Sets the "disabled" boolean.
     * 
     * @param disabled
     */
    public void setDisabled(Boolean disabled);

    /**
     * Returns the list of SiteMapChangeFreqTypes.
     * 
     * @return
     */
    public SiteMapChangeFreqType getSiteMapChangeFreq();

    /**
     * Sets the list of SiteMapChangeFreqTypes.
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
    public void setSiteMapConfiguration(SiteMapConfiguration siteMapConfiguration);

}
