/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.common.sitemap.domain.SiteMapGeneratorConfiguration;

/**
 * CategorySiteMapGenerator is controlled by this configuration.
 * 
 * @author Joshua Skorton (jskorton)
 */
public interface CategorySiteMapGeneratorConfiguration extends SiteMapGeneratorConfiguration {

    /**
     * Returns the root category.
     * 
     * @return
     */
    public Category getRootCategory();

    /**
     * Sets the root category.
     * 
     * @param rootCategory
     */
    public void setRootCategory(Category rootCategory);

    /**
     * Returns the starting depth.
     * 
     * @return
     */
    public int getStartingDepth();

    /**
     * Sets the starting depth.
     * 
     * @param startingDepth
     */
    public void setStartingDepth(int startingDepth);

    /**
     * Returns the ending depth.
     * 
     * @return
     */
    public int getEndingDepth();

    /**
     * Sets the ending depth.
     * 
     * @param endingDepth
     */
    public void setEndingDepth(int endingDepth);

}
