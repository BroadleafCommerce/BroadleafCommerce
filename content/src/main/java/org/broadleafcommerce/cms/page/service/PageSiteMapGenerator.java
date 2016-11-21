/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.page.service;

import org.broadleafcommerce.cms.page.dao.PageDao;
import org.broadleafcommerce.cms.page.domain.Page;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

import javax.annotation.Resource;

/**
 * Responsible for generating site map entries for Page.
 * 
 * @author Joshua Skorton (jskorton)
 */
@Component("blPageSiteMapGenerator")
public class PageSiteMapGenerator  { //TODO microservices - deal with sitemap generation implements SiteMapGenerator {

    @Resource(name = "blPageDao")
    protected PageDao pageDao;

    @Value("${page.site.map.generator.row.limit}")
    protected int rowLimit;
    
//TODO microservices - deal with sitemap generation
//    @Override
//    public boolean canHandleSiteMapConfiguration(SiteMapGeneratorConfiguration siteMapGeneratorConfiguration) {
//        return SiteMapGeneratorType.PAGE.equals(siteMapGeneratorConfiguration.getSiteMapGeneratorType());
//    }
//
//    @Override
//    public void addSiteMapEntries(SiteMapGeneratorConfiguration smgc, SiteMapBuilder siteMapBuilder) {
//
//        int rowOffset = 0;
//        List<Page> pages;
//        String previousUrl = "";
//
//        do {
//            pages = pageDao.readOnlineAndIncludedPages(rowLimit, rowOffset, "fullUrl");
//            rowOffset += pages.size();
//            for (Page page : pages) {
//
//                if (page.getExcludeFromSiteMap()) {
//                    continue;
//                }
//
//                String currentURL = page.getFullUrl();
//
//                if (previousUrl.equals(currentURL)) {
//                    continue;
//                } else {
//                    previousUrl = currentURL;
//                }
//
//                SiteMapURLWrapper siteMapUrl = new SiteMapURLWrapper();
//
//                // location
//                siteMapUrl.setLoc(generateUri(siteMapBuilder, page));
//
//                // change frequency
//                siteMapUrl.setChangeFreqType(smgc.getSiteMapChangeFreq());
//
//                // priority
//                siteMapUrl.setPriorityType(smgc.getSiteMapPriority());
//
//                // lastModDate
//                siteMapUrl.setLastModDate(generateDate(page));
//
//                siteMapBuilder.addUrl(siteMapUrl);
//            }
//        } while (pages.size() == rowLimit);
//    }
//
//    protected String generateUri(SiteMapBuilder smb, Page page) {
//        return BroadleafFileUtils.appendUnixPaths(smb.getBaseUrl(), page.getFullUrl());
//    }

    protected Date generateDate(Page page) {
//        TODO microservices - deal with AdminAudit
//        if (page instanceof AdminAudit) {
//            return ((AdminAudit) page).getDateUpdated();
//        } else {
//            return new Date();
//        }
        return new Date();
    }

    public PageDao getPageDao() {
        return pageDao;
    }

    public void setPageDao(PageDao pageDao) {
        this.pageDao = pageDao;
    }

    public int getRowLimit() {
        return rowLimit;
    }

    public void setRowLimit(int rowLimit) {
        this.rowLimit = rowLimit;
    }

}
