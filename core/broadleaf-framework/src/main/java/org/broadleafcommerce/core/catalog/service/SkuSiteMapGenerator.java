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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.file.service.BroadleafFileUtils;
import org.broadleafcommerce.common.sitemap.domain.SiteMapGeneratorConfiguration;
import org.broadleafcommerce.common.sitemap.service.SiteMapBuilder;
import org.broadleafcommerce.common.sitemap.service.SiteMapGenerator;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapGeneratorType;
import org.broadleafcommerce.common.sitemap.wrapper.SiteMapImageWrapper;
import org.broadleafcommerce.common.sitemap.wrapper.SiteMapURLWrapper;
import org.broadleafcommerce.core.catalog.dao.SkuDao;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuMediaXref;
import org.broadleafcommerce.core.util.service.BroadleafSitemapUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

/**
 * Responsible for generating site map entries for Sku.
 * 
 * @author Joshua Skorton (jskorton)
 */
@Component("blSkuSiteMapGenerator")
public class SkuSiteMapGenerator implements SiteMapGenerator {

    @Resource(name = "blSkuDao")
    protected SkuDao skuDao;

    @Value("${sku.site.map.generator.row.limit}")
    protected int pageSize;

    @Override
    public boolean canHandleSiteMapConfiguration(SiteMapGeneratorConfiguration siteMapGeneratorConfiguration) {
        return SiteMapGeneratorType.SKU.equals(siteMapGeneratorConfiguration.getSiteMapGeneratorType());
    }

    @Override
    public void addSiteMapEntries(SiteMapGeneratorConfiguration smgc, SiteMapBuilder siteMapBuilder) {

        int pageNum = 0;
        List<Sku> skus;

        do {
            skus = skuDao.readAllActiveSkus(pageNum++, pageSize);
            for (Sku sku : skus) {
                Product defaultProduct = sku.getDefaultProduct();
                if (defaultProduct != null && CollectionUtils.isNotEmpty(defaultProduct.getAdditionalSkus())) {
                    continue;
                }
                if (defaultProduct instanceof ProductBundle) {
                    continue;
                }
                if (StringUtils.isEmpty(sku.getProduct().getUrl() + sku.getUrlKey())) {
                    continue;
                }
                
                SiteMapURLWrapper siteMapUrl = new SiteMapURLWrapper();

                // location
                siteMapUrl.setLoc(generateUri(siteMapBuilder, sku));

                // change frequency
                siteMapUrl.setChangeFreqType(smgc.getSiteMapChangeFreq());

                // priority
                siteMapUrl.setPriorityType(smgc.getSiteMapPriority());

                // lastModDate
                siteMapUrl.setLastModDate(generateDate(sku));

                constructImageURLs(siteMapBuilder, siteMapUrl, sku);

                siteMapBuilder.addUrl(siteMapUrl);
            }
        } while (skus.size() == pageSize);
    }

    protected void constructImageURLs(SiteMapBuilder siteMapBuilder, SiteMapURLWrapper siteMapUrl, Sku sku) {
        for (SkuMediaXref skuMediaXref : sku.getSkuMediaXref().values()) {
            SiteMapImageWrapper siteMapImage = new SiteMapImageWrapper();

            siteMapImage.setLoc(BroadleafSitemapUtils.generateImageUrl(siteMapBuilder, skuMediaXref.getMedia()));

            siteMapUrl.addImage(siteMapImage);
        }
    }

    protected String generateUri(SiteMapBuilder smb, Sku sku) {
        String uri = null;
        if (sku.getUrlKey() != null) {
            uri = sku.getProduct().getUrl() + sku.getUrlKey();
        } else {
            uri = sku.getProduct().getUrl();
        }
        return BroadleafFileUtils.appendUnixPaths(smb.getBaseUrl(), uri);
    }

    protected Date generateDate(Sku sku) {
        return new Date();
    }
    
    public SkuDao getSkuDao() {
        return skuDao;
    }

    public void setSkuDao(SkuDao skuDao) {
        this.skuDao = skuDao;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

}
