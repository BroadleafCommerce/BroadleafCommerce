package org.broadleafcommerce.common.sitemap.service;

import java.io.IOException;
import java.util.List;

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
     * Generates a well formed SiteMap.   
     * 
     * Implementation should implement a well formed SiteMap (for example, the default Broadleaf SiteMapImpl
     * returns a SiteMap compatible with this schema.  
     * 
     * http://www.sitemaps.org/schemas/sitemap/0.9/siteindex.xsd
     * 
     * Implementations should utilize the list of SiteMapGenerators that build the actual entries in the sitemap xml
     * files.
     * 
     * @see SiteMapGenerator
     */
    public SiteMapGenerationResponse generateSiteMap() throws IOException;

    /**
     * Returns the list of SiteMapGenerators used to generate the SiteMap.
     * @return
     */
    public List<SiteMapGenerator> getSiteMapGenerators();

    /**
     * Sets the list of SiteMapGeneratorConfigurations.
     * @return
     */
    public void setSiteMapGenerators(List<SiteMapGenerator> siteMapGenerators);
}
