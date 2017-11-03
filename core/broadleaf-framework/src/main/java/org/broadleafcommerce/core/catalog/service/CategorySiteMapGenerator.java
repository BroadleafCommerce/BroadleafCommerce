/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.catalog.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.file.service.BroadleafFileUtils;
import org.broadleafcommerce.common.sitemap.domain.SiteMapGeneratorConfiguration;
import org.broadleafcommerce.common.sitemap.service.SiteMapBuilder;
import org.broadleafcommerce.common.sitemap.service.SiteMapGenerator;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapGeneratorType;
import org.broadleafcommerce.common.sitemap.wrapper.SiteMapImageWrapper;
import org.broadleafcommerce.common.sitemap.wrapper.SiteMapURLWrapper;
import org.broadleafcommerce.common.web.util.BroadleafUrlParamUtils;
import org.broadleafcommerce.core.catalog.dao.CategoryDao;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryMediaXref;
import org.broadleafcommerce.core.catalog.domain.CategorySiteMapGeneratorConfiguration;
import org.broadleafcommerce.core.util.service.BroadleafSitemapUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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

    protected static final Log LOG = LogFactory.getLog(CategorySiteMapGenerator.class);

    protected Environment env;

    @Resource(name = "blCategoryDao")
    protected CategoryDao categoryDao;

    @Value("${category.site.map.generator.row.limit}")
    protected int rowLimit;

    public CategorySiteMapGenerator(Environment env) {
        this.env = env;
    }

    @Override
    public boolean canHandleSiteMapConfiguration(SiteMapGeneratorConfiguration siteMapGeneratorConfiguration) {
        return SiteMapGeneratorType.CATEGORY.equals(siteMapGeneratorConfiguration.getSiteMapGeneratorType());
    }

    @Override
    public void addSiteMapEntries(SiteMapGeneratorConfiguration smgc, SiteMapBuilder siteMapBuilder) {

        if (CategorySiteMapGeneratorConfiguration.class.isAssignableFrom(smgc.getClass())) {
            CategorySiteMapGeneratorConfiguration categorySMGC = (CategorySiteMapGeneratorConfiguration) smgc;

            // Recursively construct the category SiteMap URLs
            Long rootCategoryId = categorySMGC.getRootCategory().getId();
            Category rootCategory = categoryDao.readCategoryById(rootCategoryId);
            addCategorySiteMapEntries(rootCategory, 0, categorySMGC, siteMapBuilder);
        }
    }

    protected void addCategorySiteMapEntries(Category parentCategory, int currentDepth, CategorySiteMapGeneratorConfiguration categorySMGC, SiteMapBuilder siteMapBuilder) {
        // If we've reached beyond the ending depth, don't proceed
        if (currentDepth > categorySMGC.getEndingDepth()) {
            return;
        }

        // If we're at or past the starting depth, add this category to the site map
        if (currentDepth >= categorySMGC.getStartingDepth()) {
            constructSiteMapURLs(categorySMGC, siteMapBuilder, parentCategory);
        }

        // Recurse on child categories in batches of size rowLimit
        int rowOffset = 0;
        List<Category> categories;
        do {
            categories = categoryDao.readActiveSubCategoriesByCategory(parentCategory, rowLimit, rowOffset);
            rowOffset += categories.size();
            for (Category category : categories) {
                if (StringUtils.isNotEmpty(category.getUrl())) {
                    addCategorySiteMapEntries(category, currentDepth + 1, categorySMGC, siteMapBuilder);
                } else {
                    LOG.debug("Skipping empty category URL: " + category.getId());
                }
            }
        } while (categories.size() == rowLimit);
    }

    protected void constructSiteMapURLs(CategorySiteMapGeneratorConfiguration categorySMGC, SiteMapBuilder siteMapBuilder, Category category) {
        Integer categoryPageCount = getPageCountForCategory(category);

        for (int pageNumber = 1; pageNumber <= categoryPageCount; pageNumber++) {
            SiteMapURLWrapper siteMapUrl = new SiteMapURLWrapper();

            // location
            siteMapUrl.setLoc(generateUrl(siteMapBuilder, category, pageNumber));

            // change frequency
            siteMapUrl.setChangeFreqType(categorySMGC.getSiteMapChangeFreq());

            // priority
            siteMapUrl.setPriorityType(categorySMGC.getSiteMapPriority());

            // lastModDate
            siteMapUrl.setLastModDate(generateDate(category));

            constructImageURLs(siteMapBuilder, siteMapUrl, category);

            siteMapBuilder.addUrl(siteMapUrl);
        }
    }

    protected void constructImageURLs(SiteMapBuilder siteMapBuilder, SiteMapURLWrapper siteMapUrl, Category category) {
        for (CategoryMediaXref categoryMediaXref : category.getCategoryMediaXref().values()) {
            SiteMapImageWrapper siteMapImage = new SiteMapImageWrapper();

            siteMapImage.setLoc(BroadleafSitemapUtils.generateImageUrl(siteMapBuilder, categoryMediaXref.getMedia()));

            siteMapUrl.addImage(siteMapImage);
        }
    }

    protected Integer getPageCountForCategory(Category category) {
        int activeProductCount = category.getActiveProductXrefs().size();

        return (activeProductCount == 0)? 1 : (int) Math.ceil(activeProductCount * 1.0 / getDefaultPageSize());
    }

    protected String generateUrl(SiteMapBuilder siteMapBuilder, Category category, int pageNumber) {
        String categoryUrl = BroadleafFileUtils.appendUnixPaths(siteMapBuilder.getBaseUrl(), category.getUrl());

        String categoryPaginationParam = getCategoryPaginationParam();
        categoryUrl = BroadleafUrlParamUtils.addPaginationParam(categoryUrl, categoryPaginationParam, pageNumber);

        return categoryUrl;
    }

    protected Date generateDate(Category category) {
        return new Date();
    }

    public CategoryDao getCategoryDao() {
        return categoryDao;
    }

    public void setCategoryDao(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    public int getRowLimit() {
        return rowLimit;
    }

    public void setRowLimit(int rowLimit) {
        this.rowLimit = rowLimit;
    }

    protected int getDefaultPageSize() {
        return env.getProperty("web.defaultPageSize", int.class, 40);
    }

    protected String getCategoryPaginationParam() {
        return env.getProperty("seo.category.pagination.param", "page");
    }

}
