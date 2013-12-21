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
import org.broadleafcommerce.common.config.domain.ModuleConfiguration;
import org.broadleafcommerce.common.config.service.ModuleConfigurationService;
import org.broadleafcommerce.common.config.service.type.ModuleConfigurationType;
import org.broadleafcommerce.common.file.domain.FileWorkArea;
import org.broadleafcommerce.common.file.service.BroadleafFileService;
import org.broadleafcommerce.common.file.service.BroadleafFileUtils;
import org.broadleafcommerce.common.sitemap.domain.SiteMapConfiguration;
import org.broadleafcommerce.common.sitemap.domain.SiteMapGeneratorConfiguration;
import org.broadleafcommerce.common.sitemap.exception.SiteMapException;
import org.broadleafcommerce.common.util.DateUtil;
import org.broadleafcommerce.common.web.BaseUrlResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

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
@Service("blSiteMapService")
public class SiteMapServiceImpl implements SiteMapService {

    protected static final Log LOG = LogFactory.getLog(SiteMapServiceImpl.class);

    @Value("${gzip.site.map.files}")
    protected boolean gzipSiteMapFiles = true;

    protected Long siteMapTimeout = DateUtil.ONE_WEEK_MILLIS;

    @Resource(name = "blModuleConfigurationService")
    protected ModuleConfigurationService moduleConfigurationService;

    @Resource(name = "blSiteMapGenerators")
    protected List<SiteMapGenerator> siteMapGenerators = new ArrayList<SiteMapGenerator>();

    @Resource(name = "blFileService")
    protected BroadleafFileService broadleafFileService;

    @Resource(name = "blBaseUrlResolver")
    protected BaseUrlResolver baseUrlResolver;

    @Override
    public SiteMapGenerationResponse generateSiteMap() throws SiteMapException, IOException {
        SiteMapGenerationResponse smgr = new SiteMapGenerationResponse();
        SiteMapConfiguration smc = findActiveSiteMapConfiguration();
        if (smc == null) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("No SiteMap generated since no active configuration was found.");
            }
            smgr.setHasError(true);
            smgr.setErrorCode("No SiteMap Configuration Found");
            return smgr;
        }

        FileWorkArea fileWorkArea = broadleafFileService.initializeWorkArea();
        SiteMapBuilder siteMapBuilder = new SiteMapBuilder(smc, fileWorkArea, baseUrlResolver.getSiteBaseUrl(), gzipSiteMapFiles);

        if (LOG.isTraceEnabled()) {
            LOG.trace("File work area initalized with path " + fileWorkArea.getFilePathLocation());
        }
        for (SiteMapGeneratorConfiguration currentConfiguration : smc.getSiteMapGeneratorConfigurations()) {
            if (currentConfiguration.isDisabled()) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Skipping disabled sitemap generator configuration" + currentConfiguration.getId());
                }
                continue;
            }
            SiteMapGenerator generator = selectSiteMapGenerator(currentConfiguration);
            if (generator != null) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("SiteMapGenerator found, adding entries" + generator.getClass());
                }
                generator.addSiteMapEntries(currentConfiguration, siteMapBuilder);
            } else {
                LOG.warn("No site map generator found to process generator configuration for " + currentConfiguration.getSiteMapGeneratorType());
            }
        }

        siteMapBuilder.persistSiteMap();
        if (gzipSiteMapFiles) {
            gzipAndDeleteFiles(fileWorkArea, siteMapBuilder.getIndexedFileNames());
        }


        // Move the generated files to their permanent location
        broadleafFileService.addOrUpdateResources(fileWorkArea, true);
        broadleafFileService.closeWorkArea(fileWorkArea);

        return smgr;
    }

    @Override
    public File getSiteMapFile(String fileName) throws SiteMapException, IOException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Method getSiteMapFile() invoked for " + fileName);
        }
        File siteMapFile = broadleafFileService.getResource(fileName, getSiteMapTimeout());
        if (siteMapFile.exists()) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Returning existing SiteMap");
            }
            return siteMapFile;
        } else {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Generating SiteMap");
            }
            generateSiteMap();
            siteMapFile = broadleafFileService.getResource(fileName, getSiteMapTimeout());
            if (siteMapFile.exists()) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Returning SiteMap file " + fileName);
                }
            } else {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Sitemap file " + fileName + " not found after call to generate siteMap.xml");
                }
            }
            return siteMapFile;
        }        
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

    /**
     * Returns the siteMapGenerator most qualified to handle the given configuration.     
     * 
     * @param smgc
     * @return
     */
    protected SiteMapGenerator selectSiteMapGenerator(SiteMapGeneratorConfiguration smgc) {
        for (SiteMapGenerator siteMapGenerator : siteMapGenerators) {
            if (siteMapGenerator.canHandleSiteMapConfiguration(smgc)) {
                return siteMapGenerator;
            }
        }
        return null;
    }

    /**
     * Gzip a file and then delete the file
     * 
     * @param fileName
     */
    protected void gzipAndDeleteFiles(FileWorkArea fileWorkArea, List<String> fileNames) {
        for (String fileName : fileNames) {
            try {
                String fileNameWithPath = BroadleafFileUtils.buildFilePath(fileWorkArea.getFilePathLocation(), fileName);

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

                File originalFile = new File(fileNameWithPath);
                originalFile.delete();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<SiteMapGenerator> getSiteMapGenerators() {
        return siteMapGenerators;
    }

    public void setSiteMapGenerators(List<SiteMapGenerator> siteMapGenerators) {
        this.siteMapGenerators = siteMapGenerators;
    }

    public ModuleConfigurationService getModuleConfigurationService() {
        return moduleConfigurationService;
    }

    public void setModuleConfigurationService(ModuleConfigurationService moduleConfigurationService) {
        this.moduleConfigurationService = moduleConfigurationService;
    }

    public Long getSiteMapTimeout() {
        return siteMapTimeout;
    }

    public void setSiteMapTimeout(Long siteMapTimeout) {
        this.siteMapTimeout = siteMapTimeout;
    }

    public boolean isGzipSiteMapFiles() {
        return gzipSiteMapFiles;
    }

    public void setGzipSiteMapFiles(boolean gzipSiteMapFiles) {
        this.gzipSiteMapFiles = gzipSiteMapFiles;
    }
}
