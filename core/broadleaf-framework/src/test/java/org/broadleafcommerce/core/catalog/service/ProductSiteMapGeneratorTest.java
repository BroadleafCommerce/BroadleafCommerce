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
import org.broadleafcommerce.common.sitemap.domain.SiteMapGeneratorConfigurationImpl;
import org.broadleafcommerce.common.sitemap.exception.SiteMapException;
import org.broadleafcommerce.common.sitemap.service.SiteMapGeneratorTest;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapChangeFreqType;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapGeneratorType;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapPriorityType;
import org.broadleafcommerce.core.catalog.dao.ProductDao;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.easymock.EasyMock;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Product site map generator tests
 * 
 * @author Joshua Skorton (jskorton)
 */
public class ProductSiteMapGeneratorTest extends SiteMapGeneratorTest {

    @Test
    public void testProductSiteMapGenerator() throws SiteMapException, IOException {

        Product p1 = new ProductImpl();
        p1.setUrl("/hot-sauces/sudden_death_sauce");
        Product p2 = new ProductImpl();
        p2.setUrl("hot-sauces/sweet_death_sauce");
        Product p3 = new ProductImpl();
        p3.setUrl("/hot-sauces/hoppin_hot_sauce");
        Product p4 = new ProductImpl();
        p4.setUrl("/hot-sauces/day_of_the_dead_chipotle_hot_sauce");

        List<Product> products = new ArrayList<Product>();
        products.add(p1);
        products.add(p2);
        products.add(p3);
        products.add(p4);
        
        ProductDao productDao = EasyMock.createMock(ProductDao.class);
        EasyMock.expect(productDao.readAllActiveProducts(EasyMock.eq(0), EasyMock.eq(5))).andReturn(products);
        EasyMock.replay(productDao);

        ProductSiteMapGenerator psmg = new ProductSiteMapGenerator();
        psmg.setProductDao(productDao);
        psmg.setPageSize(5);

        SiteMapGeneratorConfiguration smgc = new SiteMapGeneratorConfigurationImpl();
        smgc.setDisabled(false);
        smgc.setSiteMapGeneratorType(SiteMapGeneratorType.PRODUCT);
        smgc.setSiteMapChangeFreq(SiteMapChangeFreqType.HOURLY);
        smgc.setSiteMapPriority(SiteMapPriorityType.POINT5);

        testGenerator(smgc, psmg);

        File file1 = fileService.getResource("/sitemap_index.xml");
        File file2 = fileService.getResource("/sitemap1.xml");
        File file3 = fileService.getResource("/sitemap2.xml");

        compareFiles(file1, "src/test/resources/org/broadleafcommerce/sitemap/product/sitemap_index.xml");
        compareFiles(file2, "src/test/resources/org/broadleafcommerce/sitemap/product/sitemap1.xml");
        compareFiles(file3, "src/test/resources/org/broadleafcommerce/sitemap/product/sitemap2.xml");

    }

}