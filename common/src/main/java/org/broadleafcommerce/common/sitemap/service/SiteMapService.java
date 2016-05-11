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

package org.broadleafcommerce.common.sitemap.service;

import org.broadleafcommerce.common.sitemap.exception.SiteMapException;

import java.io.File;
import java.io.IOException;

/**
 * Class responsible for generating the SiteMap.xml and related files.  
 * 
 * This service generates the structure of the SiteMap file.  It assumes the use of SiteMap indexes 
 * and follows the convention siteMap#.xml 
 *
 * @author bpolster
 * 
 */
public interface SiteMapService {

    /**
     * Generates a well formed SiteMap.   When {@link #getSiteMapFile(String)} is called, if no file is found then
     * it will invoke this method.    Typically, an implementation will setup scheduled jobs to create the
     * siteMap.xml.
     * 
     * Implementation should implement a well formed SiteMap (for example, the default Broadleaf SiteMapImpl
     * returns a SiteMap compatible with this schema.  
     * 
     * http://www.sitemaps.org/schemas/sitemap/0.9/siteindex.xsd
     * 
     * Implementations should utilize the list of SiteMapGenerators that build the actual entries in the sitemap.xml
     * files.
     * @throws SiteMapException 
     * 
     * @see SiteMapGenerator
     */
    SiteMapGenerationResponse generateSiteMap() throws IOException, SiteMapException;

    /**
     * Returns the File object that can be used to retrieve the SiteMap.xml file
     * @throws IOException 
     * @throws SiteMapException 
     */
    File getSiteMapFile(String fileName) throws SiteMapException, IOException;
}
