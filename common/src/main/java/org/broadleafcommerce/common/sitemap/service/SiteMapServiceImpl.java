package org.broadleafcommerce.common.sitemap.service;

import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.sitemap.domain.SiteMapConfiguration;
import org.broadleafcommerce.common.sitemap.domain.SiteMapGeneratorConfiguration;
import org.broadleafcommerce.common.web.BroadleafRequestContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Component responsible for generating a sitemap.   Relies on SiteMapGenerators to 
 * produce the actual url entries within the sitemap.
 * 
 * Create a sitemap index file and at least one sitemap file with the URL elements.
 * 
 * @author bpolster
 *
 */
public class SiteMapServiceImpl implements SiteMapUtility, SiteMapService {

    protected int maximumUrlEntriesPerFile = 50000;
    protected List<SiteMapGenerator> siteMapGenerators;
    protected String tempDirectory = System.getProperty("java.io.tmpdir");

    @Override
    public SiteMapGenerationResponse generateSiteMap() throws IOException {

        // TODO:  lookup SiteMapConfiguration from DAO
        SiteMapConfiguration smc = null;
        
        OutputStream siteMapIndexOutputStream = createSiteMapIndexOutputStream(smc);
        writeSiteIndexHeader(siteMapIndexOutputStream);
        
        int currentURLCount = 0;
        for (SiteMapGeneratorConfiguration smgc : smc.getSiteMapGeneratorConfigurations()) {
            SiteMapGenerator generator = selectSiteMapGenerator(smgc);
            int urlsProcessed = generator.generateSiteMapEntries(this, currentURLCount);
            currentURLCount += urlsProcessed;
        }
        
        // TODO: Determine last sequence file.   Append the footer to it.

        writeSiteIndexFooter(siteMapIndexOutputStream);
        siteMapIndexOutputStream.close();

        return null;
    }

    protected void writeSiteIndexHeader(OutputStream os) throws IOException {
        // TODO: write the site index header
    }

    protected void writeSiteIndexFooter(OutputStream os) throws IOException {
        // TODO: write the site index footer
    }

    /**
     * Returns the siteMapGenerator most qualified to handle the given configuration.     
     * 
     * @param smgc
     * @return
     */
    protected SiteMapGenerator selectSiteMapGenerator(SiteMapGeneratorConfiguration smgc) {
        // TODO: Code this
        return null;
    }

    @Override
    public List<SiteMapGenerator> getSiteMapGenerators() {
        return siteMapGenerators;
    }

    @Override
    public void setSiteMapGenerators(List<SiteMapGenerator> siteMapGenerators) {
        this.siteMapGenerators = siteMapGenerators;
    }

    protected OutputStream createNewOutputStream(int currentUrlCount) throws IOException {
        int fileSequenceNumber = currentUrlCount / getMaximumUrlEntriesPerFile();
        
        String sitemapFileName = getSiteMapFileName(fileSequenceNumber);
        File tmpFile = new File(sitemapFileName);
        if (!tmpFile.getParentFile().exists()) {
            if (!tmpFile.getParentFile().mkdirs()) {
                throw new RuntimeException("Unable to create parent directories for file: " + sitemapFileName);
            }
        }
        return new FileOutputStream(tmpFile);
    }

    @Override
    public OutputStream getSiteMapOutputStream(OutputStream currentOutputStream, int currentUrlCount) throws IOException {
        OutputStream returnStream = currentOutputStream;
        if (currentOutputStream == null || (currentUrlCount % getMaximumUrlEntriesPerFile()) == 0) {
            returnStream = createNewOutputStream(currentUrlCount);            
            currentOutputStream.close();
        }
        
        return returnStream;
    }
    
    /**
     * Returns the output stream for the siteMap index file
     */
    protected OutputStream createSiteMapIndexOutputStream(SiteMapConfiguration smc) throws IOException {
        String siteMapIndexFileName = smc.getSiteMapFileName();
        if (siteMapIndexFileName == null) {
            siteMapIndexFileName = "sitemap_index.xml";
        }

        String sitemapIndexFileName = getBaseDirectory() + siteMapIndexFileName;

        File tmpFile = new File(sitemapIndexFileName);
        if (!tmpFile.getParentFile().exists()) {
            if (!tmpFile.getParentFile().mkdirs()) {
                throw new RuntimeException("Unable to create parent directories for file: " + sitemapIndexFileName);
            }
        }
        return new FileOutputStream(tmpFile);
    }    

    /**
     * Returns the maximumUrlEntriesPerFile.   Defaults to 50000 per the sitemap.org schema requirement of
     * a maximum of 50000 per file.   Useful to override for testing purposes.
     * @return
     */
    public int getMaximumUrlEntriesPerFile() {
        return maximumUrlEntriesPerFile;
    }

    /**
     * Sets the maximumUrl Entries per sitemap file.   The sitemap.org contract (version 0.9) says that this number 
     * should be a maximum of 50000 but it may be helpful for some implementations to override the default
     * for testing purposes.
     * 
     * @param maximumUrlEntriesPerFile
     */
    public void setMaximumEntriesPerFile(int maximumUrlEntriesPerFile) {
        this.maximumUrlEntriesPerFile = maximumUrlEntriesPerFile;
    }
    
    /**
     * Returns the siteMap filename as the baseDirectory + "sitemap" + sequency + ".xml".
     * For example:   sitemap1.xml
     * 
     * @param sequence
     * @return
     */
    protected String getSiteMapFileName(int sequence) {
        return getBaseDirectory()+getSiteMapFilePrefix() + sequence +".xml";
    }
    
    protected String getSiteMapFilePrefix() {
        return "sitemap";
    }
    
    /**
     * For multi-site installations, returns the tempDirectory with an additional subdirectory representing the
     * site.
     * 
     * @return
     */
    protected String getBaseDirectory() {
        String baseDirectory = getTempDirectory();
        baseDirectory = fixDirectoryPath(baseDirectory);
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        if (brc != null) {
            Site site = brc.getSite();
            if (site != null) {
                baseDirectory = baseDirectory + "/site-" + site.getId() + '/';
            }
        }
        return baseDirectory;            
    }
        
    /**
     * Ensures that there is a trailing "/"
     * @param path
     * @return
     */
    protected String fixDirectoryPath(String path) {
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        
        return path;
    }

    /**
     * Returns the root directory used to build the sitemap files.
     * Defaults to java.io.tmpdir.
     * 
     * @return
     */
    public String getTempDirectory() {
        return tempDirectory;
    }

    /**
     * Returns the root directory used to build the sitemap files.
     * Defaults to java.io.tmpdir.
     * 
     * @return
     */
    public void setTempDirectory(String tempDirectory) {
        this.tempDirectory = tempDirectory;
    }

}
