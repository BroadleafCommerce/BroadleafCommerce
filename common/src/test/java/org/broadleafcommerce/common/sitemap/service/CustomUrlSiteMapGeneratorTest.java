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

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.site.domain.SiteImpl;
import org.broadleafcommerce.common.sitemap.domain.CustomUrlSiteMapGeneratorConfiguration;
import org.broadleafcommerce.common.sitemap.domain.CustomUrlSiteMapGeneratorConfigurationImpl;
import org.broadleafcommerce.common.sitemap.domain.SiteMapUrlEntry;
import org.broadleafcommerce.common.sitemap.domain.SiteMapUrlEntryImpl;
import org.broadleafcommerce.common.sitemap.exception.SiteMapException;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapChangeFreqType;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapGeneratorType;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapPriorityType;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Custom URL site map generator tests
 * 
 * @author Joshua Skorton (jskorton)
 */
public class CustomUrlSiteMapGeneratorTest extends SiteMapGeneratorTest {

    @Test
    public void testCustomUrlSiteMapGenerator() throws SiteMapException, IOException {
        CustomUrlSiteMapGeneratorConfiguration smgc = getConfiguration();
        testGenerator(smgc, new CustomUrlSiteMapGenerator());

        File file1 = fileService.getResource("/sitemap_index.xml");
        File file2 = fileService.getResource("/sitemap1.xml");
        File file3 = fileService.getResource("/sitemap2.xml");

        compareFiles(file1, "src/test/resources/org/broadleafcommerce/sitemap/custom/sitemap_index.xml");
        compareFiles(file2, "src/test/resources/org/broadleafcommerce/sitemap/custom/sitemap1.xml");
        compareFiles(file3, "src/test/resources/org/broadleafcommerce/sitemap/custom/sitemap2.xml");

    }
    
    @Test
    public void testSiteMapsWithSiteContext() throws SiteMapException, IOException {
        BroadleafRequestContext brc = new BroadleafRequestContext();
        BroadleafRequestContext.setBroadleafRequestContext(brc);

        Site site = new SiteImpl();
        site.setId(256L);
        brc.setSite(site);
        
        CustomUrlSiteMapGeneratorConfiguration smgc = getConfiguration();
        testGenerator(smgc, new CustomUrlSiteMapGenerator());

        File file1 = fileService.getResource("/sitemap_index.xml");
        File file2 = fileService.getResource("/sitemap1.xml");
        File file3 = fileService.getResource("/sitemap2.xml");
        
        assertThat(file1.getAbsolutePath(), containsString("site-256"));
        assertThat(file2.getAbsolutePath(), containsString("site-256"));
        assertThat(file3.getAbsolutePath(), containsString("site-256"));

        compareFiles(file1, "src/test/resources/org/broadleafcommerce/sitemap/custom/sitemap_index.xml");
        compareFiles(file2, "src/test/resources/org/broadleafcommerce/sitemap/custom/sitemap1.xml");
        compareFiles(file3, "src/test/resources/org/broadleafcommerce/sitemap/custom/sitemap2.xml");
        
        // Remove the request context from thread local so it doesn't get in the way of subsequent tests
        BroadleafRequestContext.setBroadleafRequestContext(null);
    }
    
    public CustomUrlSiteMapGeneratorConfiguration getConfiguration() {
        SiteMapUrlEntry urlEntry1 = new SiteMapUrlEntryImpl();
        urlEntry1.setLastMod(new Date());
        urlEntry1.setLocation("http://www.heatclinic.com/1");
        urlEntry1.setSiteMapChangeFreq(SiteMapChangeFreqType.HOURLY);
        urlEntry1.setSiteMapPriority(SiteMapPriorityType.POINT5);

        SiteMapUrlEntry urlEntry2 = new SiteMapUrlEntryImpl();
        urlEntry2.setLastMod(new Date());
        urlEntry2.setLocation("2");
        urlEntry2.setSiteMapChangeFreq(SiteMapChangeFreqType.HOURLY);
        urlEntry2.setSiteMapPriority(SiteMapPriorityType.POINT5);

        SiteMapUrlEntry urlEntry3 = new SiteMapUrlEntryImpl();
        urlEntry3.setLastMod(new Date());
        urlEntry3.setLocation("/3");
        urlEntry3.setSiteMapChangeFreq(SiteMapChangeFreqType.HOURLY);
        urlEntry3.setSiteMapPriority(SiteMapPriorityType.POINT5);

        List<SiteMapUrlEntry> urlEntries = new ArrayList<SiteMapUrlEntry>();
        urlEntries.add(urlEntry1);
        urlEntries.add(urlEntry2);
        urlEntries.add(urlEntry3);

        CustomUrlSiteMapGeneratorConfiguration smgc = new CustomUrlSiteMapGeneratorConfigurationImpl();
        smgc.setDisabled(false);
        smgc.setSiteMapGeneratorType(SiteMapGeneratorType.CUSTOM);
        smgc.setCustomURLEntries(urlEntries);
        
        return smgc;
    }

}
