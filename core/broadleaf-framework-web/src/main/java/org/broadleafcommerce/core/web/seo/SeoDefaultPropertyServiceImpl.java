/*
 * #%L
 * broadleaf-enterprise
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
package org.broadleafcommerce.core.web.seo;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.media.domain.Media;
import org.broadleafcommerce.common.page.dto.PageDTO;
import org.broadleafcommerce.common.web.BaseUrlResolver;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.util.BroadleafUrlParamUtils;
import org.broadleafcommerce.core.catalog.dao.CategoryDao;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryMediaXref;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuMediaXref;
import org.broadleafcommerce.core.search.service.SearchService;
import org.broadleafcommerce.core.web.service.SearchFacetDTOService;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Chris Kittrell (ckittrell)
 */
@Service("blSeoDefaultPropertyService")
@ConditionalOnTemplating
public class SeoDefaultPropertyServiceImpl implements SeoDefaultPropertyService {

    protected static final Log LOG = LogFactory.getLog(SeoDefaultPropertyServiceImpl.class);

    @Autowired
    protected Environment env;

    @Resource(name = "blBaseUrlResolver")
    protected BaseUrlResolver urlResolver;

    @Resource(name = "blSearchFacetDTOService")
    protected SearchFacetDTOService facetService;

    @Resource(name = "blSearchService")
    protected SearchService searchService;

    @Resource(name = "blCategoryDao")
    protected CategoryDao categoryDao;

    @Override
    public String getProductTitlePattern(Product product) {
        try {
            Category category = product.getCategory();
            String pattern = category.getProductTitlePatternOverride();
            if (StringUtils.isEmpty(pattern)) {
                pattern = env.getProperty("seo.product.title.pattern");
            }
            return pattern;
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return "";
        }
    }

    @Override
    public String getCategoryTitlePattern() {
        return env.getProperty("seo.category.title.pattern");
    }

    @Override
    public String getTitle(PageDTO page) {
        Map<String, String> pageAttributes = page.getPageAttributes();

        return pageAttributes.get("title");
    }

    @Override
    public String getType(Product product) {
        return "product";
    }

    @Override
    public String getType(Category category) {
        return "article";
    }

    @Override
    public String getType(PageDTO page) {
        return "article";
    }

    @Override
    public String getProductDescriptionPattern(Product product) {
        try {
            Category category = product.getCategory();
            String pattern = category.getProductDescriptionPatternOverride();
            if (StringUtils.isEmpty(pattern)) {
                pattern = env.getProperty("seo.product.description.pattern");
            }
            return pattern;
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return "";
        }
    }

    @Override
    public String getCategoryDescriptionPattern() {
        return env.getProperty("seo.category.description.pattern");
    }

    @Override
    public String getDescription(PageDTO page) {
        Map<String, String> pageAttributes = page.getPageAttributes();

        return pageAttributes.get("metaDescription");
    }

    @Override
    public String getUrl(Product product) {
        return getCanonicalUrl(product);
    }

    @Override
    public String getUrl(Category category) {
        return getCanonicalUrl(category);
    }

    @Override
    public String getUrl(PageDTO page) {
        return urlResolver.getSiteBaseUrl() + page.getUrl();
    }

    @Override
    public String getImage(Product product) {
        Sku defaultSku = product.getDefaultSku();
        Map<String, SkuMediaXref> mediaXrefs = defaultSku.getSkuMediaXref();
        SkuMediaXref primaryMediaXref = mediaXrefs.get("primary");

        if (primaryMediaXref != null) {
            Media primaryMedia = primaryMediaXref.getMedia();
            return urlResolver.getSiteBaseUrl() + primaryMedia.getUrl();
        }

        return null;
    }

    @Override
    public String getImage(Category category) {
        Map<String, CategoryMediaXref> categoryMediaXrefs = category.getCategoryMediaXref();
        CategoryMediaXref primaryMediaXref = categoryMediaXrefs.get("primary");

        if (primaryMediaXref != null) {
            Media primaryMedia = primaryMediaXref.getMedia();
            return urlResolver.getSiteBaseUrl() + primaryMedia.getUrl();
        }

        return null;
    }

    @Override
    public String getImage(PageDTO page) {
        return null;
    }

    @Override
    public String getCanonicalUrl(Product product) {
        Integer pageNumber = getCurrentProductPageNumber();

        return getCanonicalUrl(product, pageNumber);
    }

    @Override
    public String getPaginationPrevUrl(Product product) {
        Integer pageNumber = getCurrentProductPageNumber() - 1;

        if (!productPaginationIsEnabled() || !isValidPrevPageNumber(pageNumber)) {
            return null;
        }

        return getCanonicalUrl(product, pageNumber);
    }

    @Override
    public String getPaginationNextUrl(Product product) {
        Integer pageNumber = getCurrentProductPageNumber() + 1;

        if (!productPaginationIsEnabled() || !isValidNextPageNumber(product, pageNumber)) {
            return null;
        }

        return getCanonicalUrl(product, pageNumber);
    }

    protected boolean shouldIncludeProductPagination(int pageNumber) {
        HttpServletRequest request = BroadleafRequestContext.getBroadleafRequestContext().getRequest();

        String productPaginationParam = getProductPaginationParam();
        String productPaginationParamValue = request.getParameter(productPaginationParam);
        boolean hasPaginationParamValue = StringUtils.isNotBlank(productPaginationParamValue);

        return productPaginationIsEnabled() && (hasPaginationParamValue || (pageNumber > 1));
    }

    protected boolean isValidPrevPageNumber(Integer pageNumber) {
        return (pageNumber > 0);
    }

    /**
     * This method is intended to be overridden to also determine whether or not the {@param pageNumber} is under
     *  the total number of pages for the given {@param product}.
     */
    protected boolean isValidNextPageNumber(Product product, Integer pageNumber) {
        return true;
    }

    protected String getCanonicalUrl(Product product, int pageNumber) {
        String canonicalUrl = product.getCanonicalUrl();
        if (StringUtils.isEmpty(canonicalUrl)) {
            canonicalUrl = urlResolver.getSiteBaseUrl() + product.getUrl();
        }

        if (shouldIncludeProductPagination(pageNumber)) {
            String productPaginationParam = getProductPaginationParam();

            canonicalUrl = BroadleafUrlParamUtils.addPaginationParam(canonicalUrl, productPaginationParam, pageNumber);
        }

        return canonicalUrl;
    }

    protected Integer getCurrentProductPageNumber() {
        try {
            HttpServletRequest request = BroadleafRequestContext.getBroadleafRequestContext().getRequest();

            String productPaginationParam = getProductPaginationParam();
            String productPaginationParamValue = request.getParameter(productPaginationParam);

            return Integer.valueOf(productPaginationParamValue);
        } catch (NumberFormatException e) {
            // Unable to parse page number value. That's fine, return page 1 instead;
            return 1;
        }
    }

    @Override
    public String getCanonicalUrl(Category category) {
        Integer pageNumber = getCurrentCategoryPageNumber();

        return getCanonicalUrl(category, pageNumber);
    }

    @Override
    public String getPaginationPrevUrl(Category category) {
        Integer pageNumber = getCurrentCategoryPageNumber() - 1;

        if (!isValidPrevPageNumber(pageNumber)) {
            return null;
        }

        return getCanonicalUrl(category, pageNumber);
    }

    @Override
    public String getPaginationNextUrl(Category category) {
        Integer pageNumber = getCurrentCategoryPageNumber() + 1;

        if (!isValidNextPageNumber(category, pageNumber)) {
            return null;
        }

        return getCanonicalUrl(category, pageNumber);
    }

    protected boolean shouldIncludeCategoryPagination(int pageNumber) {
        HttpServletRequest request = BroadleafRequestContext.getBroadleafRequestContext().getRequest();

        String categoryPaginationParam = getCategoryPaginationParam();
        String categoryPaginationParamValue = request.getParameter(categoryPaginationParam);
        boolean hasPaginationParamValue = StringUtils.isNotBlank(categoryPaginationParamValue);

        return hasPaginationParamValue || (pageNumber > 1);
    }

    protected boolean isValidNextPageNumber(Category category, Integer pageNumber) {
        return pageNumber <= getPageCount(category);
    }

    protected Integer getPageCount(Category category) {
        long activeCategoryCount = categoryDao.readCountAllActiveProductsByCategory(category);

        return (int) Math.ceil(activeCategoryCount * 1.0 / getPageSize());
    }

    protected String getCanonicalUrl(Category category, int pageNumber) {
        String canonicalUrl = urlResolver.getSiteBaseUrl() + category.getUrl();

        if (shouldIncludeCategoryPagination(pageNumber)) {
            String categoryPaginationParam = getCategoryPaginationParam();

            canonicalUrl = BroadleafUrlParamUtils.addPaginationParam(canonicalUrl, categoryPaginationParam, pageNumber);
        }

        return canonicalUrl;
    }

    protected Integer getCurrentCategoryPageNumber() {
        try {
            HttpServletRequest request = BroadleafRequestContext.getBroadleafRequestContext().getRequest();

            String categoryPaginationParam = getCategoryPaginationParam();
            String categoryPaginationParamValue = request.getParameter(categoryPaginationParam);

            return Integer.valueOf(categoryPaginationParamValue);
        } catch (NumberFormatException e) {
            // Unable to parse page number value. That's fine, return page 1 instead;
            return 1;
        }
    }

    protected boolean productPaginationIsEnabled() {
        return env.getProperty("seo.product.pagination.enabled", boolean.class, false);
    }

    protected String getProductPaginationParam() {
        return env.getProperty("seo.product.pagination.param");
    }

    protected String getCategoryPaginationParam() {
        return env.getProperty("seo.category.pagination.param", "page");
    }

    protected int getPageSize() {
        try {
            HttpServletRequest request = BroadleafRequestContext.getBroadleafRequestContext().getRequest();
            String pageSize = request.getParameter("pageSize");

            if (StringUtils.isBlank(pageSize)) {
                return env.getProperty("web.defaultPageSize", int.class, 40);
            }

            return Integer.parseInt(pageSize);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

}
