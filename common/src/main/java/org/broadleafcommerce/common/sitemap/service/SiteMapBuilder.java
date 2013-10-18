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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
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

    protected String tempDirectory = System.getProperty("java.io.tmpdir");
    protected String siteUrlPath = "http://www.test.com/";

    protected SimpleDateFormat W3C_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    protected SiteMapConfiguration siteMapConfig;
    protected SiteMapURLSetWrapper currentURLSetWrapper;
    protected List<String> indexFileNames = new ArrayList<String>();

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

            File file = new File(tempDirectory + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            Writer writer = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
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
     * @return
     */
    protected void persistIndexedURLSetWrapper(SiteMapURLSetWrapper urlSetWrapper) {
        String indexFileName = createNextIndexFileName();
        persistXMLDocument(indexFileName, urlSetWrapper);
        indexFileNames.add(createNextIndexFileName());
    }

    /**
     * Save the passed in URL set to a new indexed file. 
     * @return
     */
    protected void persistNonIndexedSiteMap() {
        persistXMLDocument(siteMapConfig.getSiteMapFileName(), currentURLSetWrapper);
        indexFileNames.add(createNextIndexFileName());
    }

    /**
     * Save the passed in URL set to a new indexed file. 
     * @return
     */
    protected void persistIndexedSiteMap() {
        String now = W3C_DATE_FORMATTER.format(new Date());
        
        // Save the left over items
        persistIndexedURLSetWrapper(currentURLSetWrapper);

        // Build the siteMapIndex
        SiteMapIndexWrapper siteMapIndexWrapper = new SiteMapIndexWrapper();
        for (String fileName : indexFileNames) {
            SiteMapWrapper siteMapWrapper = new SiteMapWrapper();
            siteMapWrapper.setLoc(getSiteUrlPath() + fileName);
            siteMapWrapper.setLastmod(now);
            siteMapIndexWrapper.getSiteMapWrappers().add(siteMapWrapper);
        }

        persistXMLDocument(siteMapConfig.getSiteMapFileName(), siteMapIndexWrapper);
    }

    /**
     * Returns a path to the URL where the sitemap files will be located.   Typically this is the 
     * production address (e.g. http://www.mysite.com/);
     * @return
     */
    protected String getSiteUrlPath() {
        return siteUrlPath;
    }

    /**
     * Create the name of the indexed files.
     * For example, sitemap1.xml, sitemap2.xml, etc.
     * 
     * @return
     */
    protected String createNextIndexFileName() {
        int fileNumber = indexFileNames.size() + 1;
        return "siteMap" + fileNumber + ".xml";
    }

    protected void persistSiteMap() {
        if (indexFileNames.size() > 0) {
            persistIndexedSiteMap();
        }
        persistNonIndexedSiteMap();
    }

    // Ensure that the temp directory ends with a "/"
    protected String fixTempDirectory() {
        assert tempDirectory != null;
        if (tempDirectory.endsWith("/")) {
            return tempDirectory + "/";
        }
        return tempDirectory;
    }

    private static void compressGzipFile(String file, String gzipFile) {
        try {
            FileInputStream fis = new FileInputStream(file);
            FileOutputStream fos = new FileOutputStream(gzipFile);
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
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
