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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.sitemap.domain.SiteMapConfiguration;
import org.broadleafcommerce.common.sitemap.wrapper.SiteMapIndexWrapper;
import org.broadleafcommerce.common.sitemap.wrapper.SiteMapURLSetWrapper;
import org.broadleafcommerce.common.sitemap.wrapper.SiteMapURLWrapper;
import org.broadleafcommerce.common.sitemap.wrapper.SiteMapWrapper;
import org.broadleafcommerce.common.util.FormatUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPOutputStream;

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
    
    protected boolean gzipSiteMap;
    protected boolean gzipSiteMapIndex;

    protected String tempDirectory = System.getProperty("java.io.tmpdir");

    protected SiteMapConfiguration siteMapConfig;
    protected SiteMapURLSetWrapper currentURLSetWrapper;
    protected List<String> indexedFileNames = new ArrayList<String>();

    public SiteMapBuilder(SiteMapConfiguration siteMapConfig, String tempDirectory) {
        this.siteMapConfig = siteMapConfig;
        this.tempDirectory = tempDirectory;
        fixTempDirectory();
        currentURLSetWrapper = new SiteMapURLSetWrapper();
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

            File file = new File(tempDirectory + fileName);
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
        persistXMLDocument(indexedFileName, urlSetWrapper);
        if (isGzipSiteMap()) {
            gzipAndDeleteFile(indexedFileName);
        }
        indexedFileNames.add(indexedFileName + ".gz");
    }

    /**
     * Save the passed in URL set to a non-indexed file. 
     * 
     * @return
     */
    protected void persistNonIndexedSiteMap() {
        persistXMLDocument(siteMapConfig.getSiteMapPrimaryFileName(), currentURLSetWrapper);
        if (isGzipSiteMap()) {
            gzipAndDeleteFile(siteMapConfig.getSiteMapPrimaryFileName());
        }
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
            siteMapWrapper.setLoc(siteMapConfig.getSiteUrlPath() + "/" + fileName);
            siteMapWrapper.setLastmod(now);
            siteMapIndexWrapper.getSiteMapWrappers().add(siteMapWrapper);
        }

        persistXMLDocument(siteMapConfig.getSiteMapPrimaryFileName(), siteMapIndexWrapper);
        if (isGzipSiteMapIndex()) {
            gzipAndDeleteFile(siteMapConfig.getSiteMapPrimaryFileName());
        }
    }

    /**
     * Create the name of the indexed files.
     * For example, sitemap1.xml, sitemap2.xml, etc.
     * 
     * @return
     */
    protected String createNextIndexedFileName() {
        if(indexedFileNames.size() == 0) {
            return "sitemap.xml";
        }
        return "sitemap" + indexedFileNames.size() + ".xml";
    }

    protected void persistSiteMap() {
        if (indexedFileNames.size() > 0) {
            persistIndexedSiteMap();
        } else {
            persistNonIndexedSiteMap();
        }
    }

    // Ensure that the temp directory ends with a "/"
    protected String fixTempDirectory() {
        assert tempDirectory != null;
        if (tempDirectory.endsWith("/")) {
            return tempDirectory + "/";
        }
        return tempDirectory;
    }

    /**
     * Gzip a file and then delete the file
     * 
     * @param fileName
     */
    public void gzipAndDeleteFile(String fileName) {
        try {
            String fileNameWithPath = tempDirectory + fileName;
            FileInputStream fis = new FileInputStream(fileNameWithPath);
            FileOutputStream fos = new FileOutputStream(fileNameWithPath + ".gz");
            GZIPOutputStream gzipOS = new GZIPOutputStream(fos);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                gzipOS.write(buffer, 0, len);
            }
            //close resources
            gzipOS.close();
            fos.close();
            fis.close();

            File file = new File(fileNameWithPath);
            file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean isGzipSiteMap() {
        return gzipSiteMap;
    }

    public void setGzipSiteMap(boolean gzipSiteMap) {
        this.gzipSiteMap = gzipSiteMap;
    }

    public boolean isGzipSiteMapIndex() {
        return gzipSiteMapIndex;
    }

    public void setGzipSiteMapIndex(boolean gzipSiteMapIndex) {
        this.gzipSiteMapIndex = gzipSiteMapIndex;
    }

}
