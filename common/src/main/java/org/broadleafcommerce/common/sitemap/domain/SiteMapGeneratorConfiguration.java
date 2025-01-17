/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
import org.broadleafcommerce.common.sitemap.service.type.SiteMapGeneratorType;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapPriorityType;

import java.io.Serializable;

/**
 * Sample URL tag generated and controlled by this configuration.
 *
 * <url>
 * <loc>http://www.heatclinic.com/hot-sauces</loc>
 * <lastmod>2009-11-07</lastmod>
 * <changefreq>weekly</changefreq>
 * <priority>0.5</priority>
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
    Long getId();

    /**
     * Sets the SiteMapGeneratorConfiguration Id.
     *
     * @param id
     */
    void setId(Long id);

    /**
     * Returns the "disabled" boolean.
     *
     * @return
     */
    Boolean isDisabled();

    /**
     * Sets the "disabled" boolean.
     *
     * @param disabled
     */
    void setDisabled(Boolean disabled);

    /**
     * Returns the list of SiteMapChangeFreqTypes.
     *
     * @return
     */
    SiteMapChangeFreqType getSiteMapChangeFreq();

    /**
     * Sets the list of SiteMapChangeFreqTypes.
     *
     * @param siteMapChangeFreq
     */
    void setSiteMapChangeFreq(SiteMapChangeFreqType siteMapChangeFreq);

    /**
     * Returns the SiteMapPriority.
     *
     * @return
     */
    SiteMapPriorityType getSiteMapPriority();

    /**
     * Sets the SiteMapPriority.  Must be a two digit value between 0.0 and 1.0.
     *
     * @param siteMapPriority
     */
    void setSiteMapPriority(SiteMapPriorityType siteMapPriority);

    /**
     * Returns the list of SiteMapGeneratorTypes.
     *
     * @return
     */
    SiteMapGeneratorType getSiteMapGeneratorType();

    /**
     * Sets the list of SiteMapGeneratorTypes.
     *
     * @param siteMapGeneratorType
     */
    void setSiteMapGeneratorType(SiteMapGeneratorType siteMapGeneratorType);

    /**
     * Returns the SiteMapConfiguration.
     *
     * @return
     */
    SiteMapConfiguration getSiteMapConfiguration();

    /**
     * Sets the SiteMapConfiguration.
     *
     * @param siteMapConfiguration
     */
    void setSiteMapConfiguration(SiteMapConfiguration siteMapConfiguration);

}
