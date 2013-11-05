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

import org.broadleafcommerce.common.sitemap.domain.CustomUrlSiteMapGeneratorConfiguration;
import org.broadleafcommerce.common.sitemap.domain.CustomUrlSiteMapGeneratorConfigurationImpl;
import org.broadleafcommerce.common.sitemap.domain.SiteMapUrlEntry;
import org.broadleafcommerce.common.sitemap.domain.SiteMapUrlEntryImpl;
import org.broadleafcommerce.common.sitemap.exception.SiteMapException;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapChangeFreqType;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapGeneratorType;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapPriorityType;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Custom URL site map generator tests
 * 
 * @author Joshua Skorton (jskorton)
 */
public class CustomSiteMapGeneratorTest extends SiteMapGeneratorTest {

    @Test
    public void testCustomUrlSiteMapGenerator() throws SiteMapException, IOException {

        SiteMapUrlEntry urlEntry1 = new SiteMapUrlEntryImpl();
        urlEntry1.setLastMod(new Date());
        urlEntry1.setLocation("http://www.heatclinic.com/1");
        urlEntry1.setSiteMapChangeFreq(SiteMapChangeFreqType.HOURLY);
        urlEntry1.setSiteMapPriority(SiteMapPriorityType.POINT5);

        SiteMapUrlEntry urlEntry2 = new SiteMapUrlEntryImpl();
        urlEntry2.setLastMod(new Date());
        urlEntry2.setLocation("http://www.heatclinic.com/2");
        urlEntry2.setSiteMapChangeFreq(SiteMapChangeFreqType.HOURLY);
        urlEntry2.setSiteMapPriority(SiteMapPriorityType.POINT5);

        SiteMapUrlEntry urlEntry3 = new SiteMapUrlEntryImpl();
        urlEntry3.setLastMod(new Date());
        urlEntry3.setLocation("http://www.heatclinic.com/3");
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

        testGenerator(smgc, new CustomUrlSiteMapGenerator());

        compareFiles(siteMapService.getTempDirectory() + "sitemap_index.xml", "src/test/resources/org/broadleafcommerce/sitemap/custom/sitemap_index.xml");
        compareFiles(siteMapService.getTempDirectory() + "sitemap.xml", "src/test/resources/org/broadleafcommerce/sitemap/custom/sitemap.xml");
        compareFiles(siteMapService.getTempDirectory() + "sitemap1.xml", "src/test/resources/org/broadleafcommerce/sitemap/custom/sitemap1.xml");

    }

}
