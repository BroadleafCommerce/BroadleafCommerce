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
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryMediaXref;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuMediaXref;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Map;

import javax.annotation.Resource;

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
        return urlResolver.getSiteBaseUrl() + category.getUrl();
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
        try {
            String canonicalUrl = product.getCanonicalUrl();
            if (StringUtils.isEmpty(canonicalUrl)) {
                canonicalUrl = product.getUrl();
            }
            return urlResolver.getSiteBaseUrl() + canonicalUrl;
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return "";
        }
    }
}
