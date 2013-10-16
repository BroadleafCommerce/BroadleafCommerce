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
import org.broadleafcommerce.common.sitemap.service.type.SiteMapChangeFreqType;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapGeneratorType;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapPriorityType;
import org.broadleafcommerce.common.sitemap.wrapper.SiteMapURLWrapper;
import org.broadleafcommerce.core.catalog.dao.CategoryDao;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategorySiteMapGeneratorConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

/**
 * Responsible for generating site map entries for Category.
 * 
 * @author Joshua Skorton (jskorton)
 */
@Component("blCategorySiteMapGenerator")
public class CategorySiteMapGenerator implements SiteMapGenerator {

    @Resource(name = "blCategoryDao")
    protected CategoryDao categoryDao;

    @Value("${category.site.map.generator.row.limit}")
    protected int rowLimit;
    
    SiteMapChangeFreqType siteMapChangeFreq;
    SiteMapPriorityType siteMapPriority;
    
    protected int startingDepth;
    protected int endingDepth;

    SiteMapBuilder siteMapBuilder;

    /**
     * Returns true if this SiteMapGenerator is able to process the passed in siteMapGeneratorConfiguration.   
     * 
     * @param siteMapGeneratorConfiguration
     * @return
     */
    public boolean canHandleSiteMapConfiguration(SiteMapGeneratorConfiguration siteMapGeneratorConfiguration) {
        return SiteMapGeneratorType.CATEGORY.equals(siteMapGeneratorConfiguration.getSiteMapGeneratorType());
    }

    @Override
    public void addSiteMapEntries(SiteMapGeneratorConfiguration siteMapGeneratorConfiguration, SiteMapBuilder siteMapBuilder) {

        CategorySiteMapGeneratorConfiguration categorySMGC = (CategorySiteMapGeneratorConfiguration) siteMapGeneratorConfiguration;
        
        siteMapChangeFreq = categorySMGC.getSiteMapChangeFreqType();
        siteMapPriority = categorySMGC.getSiteMapPriority();
        
        startingDepth = categorySMGC.getStartingDepth();
        endingDepth = categorySMGC.getEndingDepth();
        
        this.siteMapBuilder = siteMapBuilder;

        addCategorySiteMapEntries(categorySMGC.getRootCategory(), 1);
        
    }

    protected void addCategorySiteMapEntries(Category parentCategory, int currentDepth) {
        
        int rowOffset = 0;
        List<Category> categories;
        
        do {
            categories = categoryDao.readActiveSubCategoriesByCategory(parentCategory, rowLimit, rowOffset);
            rowOffset += categories.size();
            for (Category category : categories) {

                if (currentDepth < endingDepth) {
                    addCategorySiteMapEntries(category, currentDepth + 1);
                }

                if(currentDepth < startingDepth) {
                    continue;
                }
                
                SiteMapURLWrapper siteMapUrl = new SiteMapURLWrapper();

                // location
                siteMapUrl.setLoc(category.getUrl());

                // change frequency
                siteMapUrl.setChangeFreqType(siteMapChangeFreq);

                // priority
                siteMapUrl.setPriorityType(siteMapPriority);

                // lastModDate
                siteMapUrl.setLastModDate(new Date());

                siteMapBuilder.addUrl(siteMapUrl);
            }
        } while (categories.size() == rowLimit);

    }

}
