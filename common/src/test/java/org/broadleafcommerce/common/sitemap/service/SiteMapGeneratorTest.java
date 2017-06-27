/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */

package org.broadleafcommerce.common.sitemap.service;

import org.apache.commons.collections.CollectionUtils;
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
import org.broadleafcommerce.common.sitemap.service.SiteMapGenerationResponse;
import org.broadleafcommerce.common.sitemap.service.SiteMapGenerator;
import org.broadleafcommerce.common.sitemap.service.SiteMapServiceImpl;
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
        fileService.removeResource("/sitemap3.xml");
        fileService.removeResource("/sitemap.xml");
    }

    protected void testGenerator(SiteMapGeneratorConfiguration smgc, SiteMapGenerator smg) throws SiteMapException,
            IOException {
        testGenerator(smgc, smg, 2);
    }

    protected void testGenerator(SiteMapGeneratorConfiguration smgc, SiteMapGenerator smg, int maxEntriesPerFile)
            throws SiteMapException, IOException {
        List<SiteMapGeneratorConfiguration> smgcList = new ArrayList<>();
        smgcList.add(smgc);
        testGenerator(smgcList, smg, maxEntriesPerFile);
    }

    protected void testGenerator(List<SiteMapGeneratorConfiguration> smgcList, SiteMapGenerator smg, int maxEntriesPerFile)
            throws SiteMapException, IOException {

        if (CollectionUtils.isNotEmpty(smgcList)) {

            SiteMapConfiguration smc = new SiteMapConfigurationImpl();
            smc.setMaximumUrlEntriesPerFile(maxEntriesPerFile);
            smc.setSiteMapGeneratorConfigurations(smgcList);
            smc.setIndexedSiteMapFileName("sitemap_index.xml");

            for (SiteMapGeneratorConfiguration smgc : smgcList) {
                smgc.setSiteMapConfiguration(smc);
            }

            List<ModuleConfiguration> mcList = new ArrayList<>();
            mcList.add(smc);

            ModuleConfigurationService mcs = EasyMock.createMock(ModuleConfigurationService.class);
            EasyMock.expect(mcs.findActiveConfigurationsByType(ModuleConfigurationType.SITE_MAP)).andReturn(mcList);
            EasyMock.replay(mcs);

            List<SiteMapGenerator> smgList = new ArrayList<>();
            smgList.add(smg);

            siteMapService.setGzipSiteMapFiles(false);
            siteMapService.setModuleConfigurationService(mcs);
            siteMapService.setSiteMapGenerators(smgList);
            SiteMapGenerationResponse smgr = siteMapService.generateSiteMap();

            Assert.assertFalse(smgr.isHasError());
        }

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
        br.close();
        fin.close();
        return sb.toString();
    }

}
