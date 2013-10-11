/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.sitemap.service;

import org.broadleafcommerce.common.config.domain.ModuleConfiguration;
import org.broadleafcommerce.common.config.service.ModuleConfigurationService;
import org.broadleafcommerce.common.config.service.type.ModuleConfigurationType;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.sitemap.domain.SiteMapConfiguration;
import org.broadleafcommerce.common.sitemap.domain.SiteMapGeneratorConfiguration;
import org.broadleafcommerce.common.sitemap.exception.SiteMapException;
import org.broadleafcommerce.common.web.BroadleafRequestContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

import javax.annotation.Resource;

/**
 * Component responsible for generating a sitemap.   Relies on SiteMapGenerators to 
 * produce the actual url entries within the sitemap.
 * 
 * Create a sitemap index file and at least one sitemap file with the URL elements.
 * 
 * @author bpolster
 *
 */
public class SiteMapServiceImpl implements SiteMapService {

    protected int maximumUrlEntriesPerFile = 50000;
    protected List<SiteMapGenerator> siteMapGenerators;
    protected String tempDirectory = System.getProperty("java.io.tmpdir");

    @Resource(name = "blModuleConfigurationService")
    protected ModuleConfigurationService moduleConfigurationService;

    @Override
    public SiteMapGenerationResponse generateSiteMap() throws SiteMapException, IOException {

        // TODO: Create the siteMapBuilder component.

        SiteMapBuilder sitemapBuilder = null;

        //lookup SiteMapConfiguration
        SiteMapConfiguration smc = findActiveSiteMapConfiguration();
        if (smc == null) {
            throw new SiteMapException("No eligible Sitemap configurations were configured.");
        }

        Writer siteMapIndexWriter = createSiteMapIndexWriter(smc);
        writeSiteIndexHeader(siteMapIndexWriter);

        for (SiteMapGeneratorConfiguration currentConfiguration : smc.getSiteMapGeneratorConfigurations()) {
            SiteMapGenerator generator = selectSiteMapGenerator(currentConfiguration);
            generator.addSiteMapEntries(currentConfiguration, sitemapBuilder);
        }

        // TODO: Determine last sequence file.   Append the footer to it.

        writeSiteIndexFooter(siteMapIndexWriter);
        siteMapIndexWriter.close();

        return null;
    }

    protected SiteMapConfiguration findActiveSiteMapConfiguration() {

        List<ModuleConfiguration> configurations = moduleConfigurationService.findActiveConfigurationsByType(ModuleConfigurationType.SITE_MAP);

        SiteMapConfiguration smc = null;
        if (configurations != null && !configurations.isEmpty()) {
            //Try to find a default configuration           
            for (ModuleConfiguration configuration : configurations) {
                if (configuration.getIsDefault()) {
                    smc = (SiteMapConfiguration) configuration;
                    break;
                }
            }
            if (smc == null) {
                //if there wasn't a default one, use the first active one...
                smc = (SiteMapConfiguration) configurations.get(0);
            }
        }

        return smc;
    }

    protected void writeSiteIndexHeader(Writer writer) throws IOException {
        // TODO: write the site index header
    }

    protected void writeSiteIndexFooter(Writer writer) throws IOException {
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
    
    /**
     * Returns the output stream for the siteMap index file
     */
    protected Writer createSiteMapIndexWriter(SiteMapConfiguration smc) throws IOException {
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

        // if file doesn't exists, then create it
        if (!tmpFile.exists()) {
            tmpFile.createNewFile();
        }

        return new PrintWriter(tmpFile);
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
