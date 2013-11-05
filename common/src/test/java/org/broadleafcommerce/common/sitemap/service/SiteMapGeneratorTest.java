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
import org.broadleafcommerce.common.sitemap.domain.SiteMapConfiguration;
import org.broadleafcommerce.common.sitemap.domain.SiteMapConfigurationImpl;
import org.broadleafcommerce.common.sitemap.domain.SiteMapGeneratorConfiguration;
import org.broadleafcommerce.common.sitemap.exception.SiteMapException;
import org.easymock.EasyMock;
import org.junit.Assert;

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

    protected void testGenerator(SiteMapGeneratorConfiguration smgc, SiteMapGenerator smg) throws SiteMapException, IOException {

        List<SiteMapGeneratorConfiguration> smgcList = new ArrayList<SiteMapGeneratorConfiguration>();
        smgcList.add(smgc);

        SiteMapConfiguration smc = new SiteMapConfigurationImpl();
        smc.setSiteMapPrimaryFileName("sitemap_index.xml");
        smc.setMaximumUrlEntriesPerFile(2);
        smc.setSiteUrlPath("http://www.heatclinic.com");
        smc.setSiteMapGeneratorConfigurations(smgcList);
        smgc.setSiteMapConfiguration(smc);

        List<ModuleConfiguration> mcList = new ArrayList<ModuleConfiguration>();
        mcList.add(smc);

        ModuleConfigurationService mcs = EasyMock.createMock(ModuleConfigurationService.class);
        EasyMock.expect(mcs.findActiveConfigurationsByType(ModuleConfigurationType.SITE_MAP)).andReturn(mcList);
        EasyMock.replay(mcs);

        List<SiteMapGenerator> smgList = new ArrayList<SiteMapGenerator>();
        smgList.add(smg);

        siteMapService.setGzipSiteMap(false);
        siteMapService.setGzipSiteMapIndex(false);
        siteMapService.setModuleConfigurationService(mcs);
        siteMapService.setSiteMapGenerators(smgList);
        SiteMapGenerationResponse smgr = siteMapService.generateSiteMap();

        Assert.assertFalse(smgr.isHasError());
    }

    protected void compareFiles(String pathToFile1, String pathToFile2) throws IOException {
        String actualOutput = convertFileToString(pathToFile1);
        String expectedOutput = convertFileToString(pathToFile2);
        Assert.assertTrue(actualOutput.equals(expectedOutput));
    }

    protected String convertFileToString(String pathToFile) throws IOException {
        FileInputStream fin = new FileInputStream(new File(pathToFile));
        BufferedReader br = new BufferedReader(new InputStreamReader(fin));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains("</lastmod>")) {
                continue;
            }
            sb.append(line);
        }
        //System.out.println(sb.toString());
        return sb.toString();
    }

}