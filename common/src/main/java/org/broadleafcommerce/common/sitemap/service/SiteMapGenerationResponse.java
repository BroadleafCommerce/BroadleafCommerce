package org.broadleafcommerce.common.sitemap.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the outcome of a SiteMap generation request.   Can be extended for specific domain objectives but 
 * generally provides the directory location where the SiteMap request is stored.
 * 
 * @author bpolster
 */
public class SiteMapGenerationResponse {

    private String sitemapIndexFileName = "sitemap.xml";
    private List<String> siteMapFilePaths = new ArrayList<String>();
    private boolean hasError = false;
    private String errorCode;

    /**
     * Returns the name of the main SiteMap index file.
     * @return
     */
    public String getSitemapIndexFileName() {
        return sitemapIndexFileName;
    }

    /**
     * Sets the name of the main index file.
     * @param sitemapIndexFileName
     */
    public void setSitemapIndexFileName(String sitemapIndexFileName) {
        this.sitemapIndexFileName = sitemapIndexFileName;
    }

    /**
     * List of files representing the siteMap files.   The default Broadleaf generator will always create 
     * a SiteMap index file with at least one additional Sitemap file.   
     * 
     * @return
     */
    public List<String> getSiteMapFilePaths() {
        return siteMapFilePaths;
    }

    /**
     * Sets the individual sitemap files.
     * @param siteMapFilePaths
     */
    public void setSiteMapFilePaths(List<String> siteMapFilePaths) {
        this.siteMapFilePaths = siteMapFilePaths;
    }

    /**
     * Returns true if the sitemap generation process resulted in an error.
     * @return
     */
    public boolean isHasError() {
        return hasError;
    }

    /**
     * Sets that this response represents an error when generating the SiteMap.
     * @return
     */

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    /**
     * Returns a code representing the error.   Undefined if called in the context of a response with no error.
     * @return
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the error code associated with this SiteMap Generation.
     * @param errorCode
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
