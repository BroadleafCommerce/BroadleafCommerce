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

import org.broadleafcommerce.common.config.domain.ModuleConfiguration;
import org.broadleafcommerce.common.config.service.ModuleConfigurationService;
import org.broadleafcommerce.common.config.service.type.ModuleConfigurationType;
import org.broadleafcommerce.common.file.service.BroadleafFileServiceImpl;
import org.broadleafcommerce.common.file.service.FileServiceProvider;
import org.broadleafcommerce.common.file.service.FileSystemFileServiceProvider;
import org.broadleafcommerce.common.sitemap.domain.SiteMapConfiguration;
import org.broadleafcommerce.common.sitemap.domain.SiteMapConfigurationImpl;
import org.broadleafcommerce.common.sitemap.domain.SiteMapGeneratorConfiguration;
import org.broadleafcommerce.common.sitemap.exception.SiteMapException;
import org.broadleafcommerce.common.web.BaseUrlResolver;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for site map generator tests
 * 
 * @author Joshua Skorton (jskorton)
 */
public class SiteMapGeneratorTest {

    protected SiteMapServiceImpl siteMapService = new SiteMapServiceImpl();
    protected BroadleafFileServiceImpl fileService = new BroadleafFileServiceImpl();
    protected BaseUrlResolver baseUrlResolver = new BaseUrlResolver() {

        @Override
        public String getSiteBaseUrl() {
            return "http://www.heatclinic.com";
        }

        @Override
        public String getAdminBaseUrl() {
            return "http://www.heatclinic.com/admin";
        }
    };

    @Before
    public void setup() {
        FileServiceProvider defaultFileServiceProvider = new FileSystemFileServiceProvider();
        fileService.setDefaultFileServiceProvider(defaultFileServiceProvider);
        siteMapService.broadleafFileService = fileService;
        siteMapService.baseUrlResolver = baseUrlResolver;

    }

    @After
    public void deleteTempFiles() {
        fileService.removeResource("/sitemap_index.xml");
        fileService.removeResource("/sitemap1.xml");
        fileService.removeResource("/sitemap2.xml");
        fileService.removeResource("/sitemap.xml");
    }

    protected void testGenerator(SiteMapGeneratorConfiguration smgc, SiteMapGenerator smg) throws SiteMapException,
            IOException {
        testGenerator(smgc, smg, 2);
    }

    protected void testGenerator(SiteMapGeneratorConfiguration smgc, SiteMapGenerator smg, int maxEntriesPerFile)
            throws SiteMapException, IOException {

        List<SiteMapGeneratorConfiguration> smgcList = new ArrayList<SiteMapGeneratorConfiguration>();
        smgcList.add(smgc);

        SiteMapConfiguration smc = new SiteMapConfigurationImpl();
        smc.setMaximumUrlEntriesPerFile(maxEntriesPerFile);
        smc.setSiteMapGeneratorConfigurations(smgcList);
        smc.setIndexedSiteMapFileName("sitemap_index.xml");
        smgc.setSiteMapConfiguration(smc);

        List<ModuleConfiguration> mcList = new ArrayList<ModuleConfiguration>();
        mcList.add(smc);

        ModuleConfigurationService mcs = EasyMock.createMock(ModuleConfigurationService.class);
        EasyMock.expect(mcs.findActiveConfigurationsByType(ModuleConfigurationType.SITE_MAP)).andReturn(mcList);
        EasyMock.replay(mcs);

        List<SiteMapGenerator> smgList = new ArrayList<SiteMapGenerator>();
        smgList.add(smg);

        siteMapService.setGzipSiteMapFiles(false);
        siteMapService.setModuleConfigurationService(mcs);
        siteMapService.setSiteMapGenerators(smgList);
        SiteMapGenerationResponse smgr = siteMapService.generateSiteMap();

        Assert.assertFalse(smgr.isHasError());
    }

    protected void compareFiles(File file1, String pathToFile2) throws IOException {
        String actualOutput = convertFileToString(file1);
        String expectedOutput = convertFileToString(new File(pathToFile2));
        Assert.assertTrue(actualOutput.equals(expectedOutput));
    }

    protected String convertFileToString(File file) throws IOException {
        FileInputStream fin = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fin));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains("</lastmod>")) {
                continue;
            }
            line = line.replaceAll("\\s+", "");
            sb.append(line);
        }
        return sb.toString();
    }

}