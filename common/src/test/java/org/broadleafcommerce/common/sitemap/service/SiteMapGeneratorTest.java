package org.broadleafcommerce.common.sitemap.service;

import org.broadleafcommerce.common.config.domain.ModuleConfiguration;
import org.broadleafcommerce.common.config.service.ModuleConfigurationService;
import org.broadleafcommerce.common.config.service.type.ModuleConfigurationType;
import org.broadleafcommerce.common.sitemap.domain.SiteMapConfiguration;
import org.broadleafcommerce.common.sitemap.domain.SiteMapConfigurationImpl;
import org.broadleafcommerce.common.sitemap.domain.SiteMapGeneratorConfiguration;
import org.broadleafcommerce.common.sitemap.exception.SiteMapException;
import org.easymock.classextension.EasyMock;
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

        List<ModuleConfiguration> configurations = new ArrayList<ModuleConfiguration>();
        configurations.add(smc);

        ModuleConfigurationService mcs = EasyMock.createMock(ModuleConfigurationService.class);
        EasyMock.expect(mcs.findActiveConfigurationsByType(ModuleConfigurationType.SITE_MAP)).andReturn(configurations);
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
            if (line.contains("lastmod")) {
                continue;
            }
            sb.append(line);
        }
        //System.out.println(sb.toString());
        return sb.toString();
    }

}