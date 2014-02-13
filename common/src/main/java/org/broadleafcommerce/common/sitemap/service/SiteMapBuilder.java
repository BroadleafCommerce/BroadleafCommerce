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

package org.broadleafcommerce.common.sitemap.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.file.domain.FileWorkArea;
import org.broadleafcommerce.common.file.service.BroadleafFileUtils;
import org.broadleafcommerce.common.sitemap.domain.SiteMapConfiguration;
import org.broadleafcommerce.common.sitemap.wrapper.SiteMapIndexWrapper;
import org.broadleafcommerce.common.sitemap.wrapper.SiteMapURLSetWrapper;
import org.broadleafcommerce.common.sitemap.wrapper.SiteMapURLWrapper;
import org.broadleafcommerce.common.sitemap.wrapper.SiteMapWrapper;
import org.broadleafcommerce.common.util.FormatUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * Handles creating the various sitemap files. 
 * 
 * @author bpolster
 */
public class SiteMapBuilder {

    protected static final Log LOG = LogFactory.getLog(SiteMapBuilder.class);
    protected FileWorkArea fileWorkArea;

    protected SiteMapConfiguration siteMapConfig;
    protected SiteMapURLSetWrapper currentURLSetWrapper;
    protected List<String> indexedFileNames = new ArrayList<String>();
    protected String baseUrl;
    protected boolean gzipSiteMapFiles = true;

    public SiteMapBuilder(SiteMapConfiguration siteMapConfig, FileWorkArea fileWorkArea, String baseUrl, boolean gzipSiteMapFiles) {
        this.fileWorkArea = fileWorkArea;
        this.siteMapConfig = siteMapConfig;
        this.currentURLSetWrapper = new SiteMapURLSetWrapper();
        this.baseUrl = baseUrl;
        this.gzipSiteMapFiles = gzipSiteMapFiles;
    }

    /**
     * Returns the SiteMapURLSetWrapper that a Generator should use to add its next URL element.
     * 
     */
    public void addUrl(SiteMapURLWrapper urlWrapper) {
        if (currentURLSetWrapper.getSiteMapUrlWrappers().size() >= siteMapConfig.getMaximumUrlEntriesPerFile()) {
            persistIndexedURLSetWrapper(currentURLSetWrapper);
            currentURLSetWrapper = new SiteMapURLSetWrapper();
        }
        currentURLSetWrapper.getSiteMapUrlWrappers().add(urlWrapper);
    }

    /**
     * Method takes in a valid JAXB object (e.g. has a RootElement) and persists it to 
     * the temporary directory associated with this builder. 
     * 
     * @param fileName
     */
    protected void persistXMLDocument(String fileName, Object xmlObject) {

        try {
            JAXBContext context = JAXBContext.newInstance(xmlObject.getClass());
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.setProperty(Marshaller.JAXB_FRAGMENT, true);

            File file = new File(fileWorkArea.getFilePathLocation() + fileName);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Persisting SiteMap document " + file.getAbsolutePath());
            }

            if (!file.exists()) {
                file.createNewFile();
            }
            Writer writer = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            m.marshal(xmlObject, writer);
            writer.close();
        } catch (IOException ioe) {
            LOG.error("IOException occurred persisting XML Document", ioe);
            throw new RuntimeException("Error persisting XML document when trying to build Sitemap", ioe);
        } catch (JAXBException je) {
            LOG.error("JAXBException occurred persisting XML Document", je);
            throw new RuntimeException("Error persisting XML document when trying to build Sitemap", je);
        }
    }

    /**
     * Save the passed in URL set to a new indexed file. 
     * 
     * @return
     */
    protected void persistIndexedURLSetWrapper(SiteMapURLSetWrapper urlSetWrapper) {
        String indexedFileName = createNextIndexedFileName();
        indexedFileNames.add(indexedFileName);
        persistXMLDocument(indexedFileName, urlSetWrapper);
    }

    /**
     * Save the passed in URL set to a non-indexed file. 
     * 
     * @return
     */
    protected void persistNonIndexedSiteMap() {
        indexedFileNames.add(siteMapConfig.getSiteMapFileName());
        persistXMLDocument(siteMapConfig.getSiteMapFileName(), currentURLSetWrapper);
    }

    /**
     * Save the site map index file. 
     * 
     * @return
     */
    protected void persistIndexedSiteMap() {
        String now = FormatUtil.formatDateUsingW3C(new Date());
        
        // Save the leftover URL set
        persistIndexedURLSetWrapper(currentURLSetWrapper);

        // Build the siteMapIndex
        SiteMapIndexWrapper siteMapIndexWrapper = new SiteMapIndexWrapper();
        for (String fileName : indexedFileNames) {
            SiteMapWrapper siteMapWrapper = new SiteMapWrapper();
            String fileLoc = null;
            if (gzipSiteMapFiles) {
                fileLoc = BroadleafFileUtils.buildFilePath(baseUrl, fileName + ".gz");
            } else {
                fileLoc = BroadleafFileUtils.buildFilePath(baseUrl, fileName);
            }
            siteMapWrapper.setLoc(fileLoc)   ;         
            siteMapWrapper.setLastmod(now);
            siteMapIndexWrapper.getSiteMapWrappers().add(siteMapWrapper);
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("Persisting sitemap.xml file for indexed site map ");
        }
        indexedFileNames.add(siteMapConfig.getIndexedSiteMapFileName());
        persistXMLDocument(siteMapConfig.getIndexedSiteMapFileName(),
                siteMapIndexWrapper);
    }

    /**
     * Create the name of the indexed files.
     * For example, sitemap1.xml, sitemap2.xml, etc.
     * 
     * @return
     */
    protected String createNextIndexedFileName() {
        String pattern = siteMapConfig.getSiteMapIndexFilePattern();
        int indexFileNumber = indexedFileNames.size() + 1;
        String fileName = pattern.replaceFirst("###", String.valueOf(indexFileNumber));
        return fileName;
    }

    protected void persistSiteMap() {
        if (indexedFileNames.size() > 0) {
            persistIndexedSiteMap();
        } else {
            persistNonIndexedSiteMap();
        }
    }

    public List<String> getIndexedFileNames() {
        return indexedFileNames;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

}
