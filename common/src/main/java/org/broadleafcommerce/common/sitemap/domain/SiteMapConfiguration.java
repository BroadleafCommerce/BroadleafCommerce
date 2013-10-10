package org.broadleafcommerce.common.sitemap.domain;

import java.util.List;


/**
 * The SiteMapConfiguration is a class that drives the building of the SiteMap.  It contains general properties that drive
 * the creation of the SiteMap such as directory paths, etc.
 * 
 * @author bpolster
 */
public interface SiteMapConfiguration {
    
    // priority
    // enabled/disabled

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
