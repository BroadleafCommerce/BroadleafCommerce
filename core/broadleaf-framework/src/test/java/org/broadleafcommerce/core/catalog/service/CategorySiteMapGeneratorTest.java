/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.catalog.service;

import org.broadleafcommerce.common.sitemap.domain.SiteMapGeneratorConfiguration;
import org.broadleafcommerce.common.sitemap.exception.SiteMapException;
import org.broadleafcommerce.common.sitemap.service.SiteMapGeneratorTest;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapChangeFreqType;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapGeneratorType;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapPriorityType;
import org.broadleafcommerce.core.catalog.dao.CategoryDao;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.domain.CategorySiteMapGeneratorConfiguration;
import org.broadleafcommerce.core.catalog.domain.CategorySiteMapGeneratorConfigurationImpl;
import org.easymock.EasyMock;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Category site map generator tests
 * 
 * @author Joshua Skorton (jskorton)
 */
public class CategorySiteMapGeneratorTest extends SiteMapGeneratorTest {

    @Test
    public void testCategorySiteMapGenerator() throws SiteMapException, IOException {

        Category c1 = new CategoryImpl();
        c1.setUrl("/");
        Category c2 = new CategoryImpl();
        c2.setUrl("/hot-sauces");
        Category c3 = new CategoryImpl();
        c3.setUrl("merchandise");
        Category c4 = new CategoryImpl();
        c4.setUrl("/clearance");
        Category c5 = new CategoryImpl();
        c5.setUrl("/mens");
        Category c6 = new CategoryImpl();
        c6.setUrl("/womens");

        List<Category> merchandiseSubcategories = new ArrayList<Category>();
        merchandiseSubcategories.add(c5);
        merchandiseSubcategories.add(c6);

        CategoryDao categoryDao = EasyMock.createMock(CategoryDao.class);
        EasyMock.expect(categoryDao.readActiveSubCategoriesByCategory(c1, 5, 0)).andReturn(new ArrayList<Category>())
                .atLeastOnce();
        EasyMock.expect(categoryDao.readActiveSubCategoriesByCategory(c2, 5, 0)).andReturn(new ArrayList<Category>())
                .atLeastOnce();
        EasyMock.expect(categoryDao.readActiveSubCategoriesByCategory(c3, 5, 0)).andReturn(merchandiseSubcategories)
                .atLeastOnce();
        EasyMock.expect(categoryDao.readActiveSubCategoriesByCategory(c4, 5, 0)).andReturn(new ArrayList<Category>())
                .atLeastOnce();
        EasyMock.replay(categoryDao);

        CategorySiteMapGenerator csmg = new CategorySiteMapGenerator();
        csmg.setCategoryDao(categoryDao);
        csmg.setRowLimit(5);

        List<SiteMapGeneratorConfiguration> smgcList = new ArrayList<SiteMapGeneratorConfiguration>();

        CategorySiteMapGeneratorConfiguration c1CSMGC = new CategorySiteMapGeneratorConfigurationImpl();
        c1CSMGC.setDisabled(false);
        c1CSMGC.setSiteMapGeneratorType(SiteMapGeneratorType.CATEGORY);
        c1CSMGC.setSiteMapChangeFreq(SiteMapChangeFreqType.HOURLY);
        c1CSMGC.setSiteMapPriority(SiteMapPriorityType.POINT5);
        c1CSMGC.setRootCategory(c1);
        c1CSMGC.setStartingDepth(1);
        c1CSMGC.setEndingDepth(1);
        smgcList.add(c1CSMGC);

        CategorySiteMapGeneratorConfiguration c2CSMGC = new CategorySiteMapGeneratorConfigurationImpl();
        c2CSMGC.setDisabled(false);
        c2CSMGC.setSiteMapGeneratorType(SiteMapGeneratorType.CATEGORY);
        c2CSMGC.setSiteMapChangeFreq(SiteMapChangeFreqType.HOURLY);
        c2CSMGC.setSiteMapPriority(SiteMapPriorityType.POINT5);
        c2CSMGC.setRootCategory(c2);
        c2CSMGC.setStartingDepth(1);
        c2CSMGC.setEndingDepth(1);
        smgcList.add(c2CSMGC);

        CategorySiteMapGeneratorConfiguration c3CSMGC = new CategorySiteMapGeneratorConfigurationImpl();
        c3CSMGC.setDisabled(false);
        c3CSMGC.setSiteMapGeneratorType(SiteMapGeneratorType.CATEGORY);
        c3CSMGC.setSiteMapChangeFreq(SiteMapChangeFreqType.HOURLY);
        c3CSMGC.setSiteMapPriority(SiteMapPriorityType.POINT5);
        c3CSMGC.setRootCategory(c3);
        c3CSMGC.setStartingDepth(1);
        c3CSMGC.setEndingDepth(1);
        smgcList.add(c3CSMGC);

        CategorySiteMapGeneratorConfiguration c4CSMGC = new CategorySiteMapGeneratorConfigurationImpl();
        c4CSMGC.setDisabled(false);
        c4CSMGC.setSiteMapGeneratorType(SiteMapGeneratorType.CATEGORY);
        c4CSMGC.setSiteMapChangeFreq(SiteMapChangeFreqType.HOURLY);
        c4CSMGC.setSiteMapPriority(SiteMapPriorityType.POINT5);
        c4CSMGC.setRootCategory(c4);
        c4CSMGC.setStartingDepth(1);
        c4CSMGC.setEndingDepth(1);
        smgcList.add(c4CSMGC);

        testGenerator(smgcList, csmg, 2);

        File file1 = fileService.getResource("/sitemap_index.xml");
        File file2 = fileService.getResource("/sitemap1.xml");
        File file3 = fileService.getResource("/sitemap2.xml");
        File file4 = fileService.getResource("/sitemap3.xml");

        compareFiles(file1, "src/test/resources/org/broadleafcommerce/sitemap/category/sitemap_index.xml");
        compareFiles(file2, "src/test/resources/org/broadleafcommerce/sitemap/category/sitemap1.xml");
        compareFiles(file3, "src/test/resources/org/broadleafcommerce/sitemap/category/sitemap2.xml");
        compareFiles(file4, "src/test/resources/org/broadleafcommerce/sitemap/category/sitemap3.xml");

        testGenerator(smgcList, csmg, 50000);
        File file5 = fileService.getResource("/sitemap.xml");

        compareFiles(file5, "src/test/resources/org/broadleafcommerce/sitemap/category/sitemap.xml");

    }

}