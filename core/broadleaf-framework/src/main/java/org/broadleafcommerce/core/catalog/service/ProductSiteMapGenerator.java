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

package org.broadleafcommerce.core.catalog.service;

import org.broadleafcommerce.common.sitemap.domain.SiteMapGeneratorConfiguration;
import org.broadleafcommerce.common.sitemap.service.SiteMapBuilder;
import org.broadleafcommerce.common.sitemap.service.SiteMapGenerator;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapGeneratorType;
import org.broadleafcommerce.common.sitemap.wrapper.SiteMapURLWrapper;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

/**
 * Responsible for generating site map entries for Product.
 * 
 * @author Joshua Skorton (jskorton)
 */
@Component("blProductSiteMapGenerator")
public class ProductSiteMapGenerator implements SiteMapGenerator {

    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    @Value("${product.site.map.generator.row.limit}")
    protected int pageSize;

    /**
     * Returns true if this SiteMapGenerator is able to process the passed in siteMapGeneratorConfiguration.   
     * 
     * @param siteMapGeneratorConfiguration
     * @return
     */
    public boolean canHandleSiteMapConfiguration(SiteMapGeneratorConfiguration siteMapGeneratorConfiguration) {
        return SiteMapGeneratorType.PRODUCT.equals(siteMapGeneratorConfiguration.getSiteMapGeneratorType());
    }

    @Override
    public void addSiteMapEntries(SiteMapGeneratorConfiguration siteMapGeneratorConfiguration, SiteMapBuilder siteMapBuilder) {

        int pageNum = 0;
        List<Product> products;

        do {
            products = catalogService.readAllActiveProducts(pageNum++, pageSize, new Date());
            for (Product product : products) {
                SiteMapURLWrapper siteMapUrl = new SiteMapURLWrapper();

                // location
                siteMapUrl.setLoc(product.getUrl());

                // change frequency
                siteMapUrl.setChangeFreqType(siteMapGeneratorConfiguration.getSiteMapChangeFreqType());

                // priority
                siteMapUrl.setPriorityType(siteMapGeneratorConfiguration.getSiteMapPriority());

                // lastModDate
                siteMapUrl.setLastModDate(new Date());

                siteMapBuilder.addUrl(siteMapUrl);
            }
        } while (products.size() == pageSize);

    }

}
