package org.broadleafcommerce.common.sitemap.service;

import java.io.IOException;
import java.io.OutputStream;

/**
 * SiteMapGenerators are typically unaware of what the actual file they need to write to and depend on this utility 
 * to return them the file.
 * 
 * @author bpolster
 */
public interface SiteMapUtility {

    /**
     * Returns a file to write the next part of the SiteMap.   If passed in fileName is null, assumes that 
     * the fileName is the first file being written to. 
     * 
     */
    public OutputStream getSiteMapOutputStream(OutputStream currentOutputStream, int currentFileCount) throws IOException;

}
