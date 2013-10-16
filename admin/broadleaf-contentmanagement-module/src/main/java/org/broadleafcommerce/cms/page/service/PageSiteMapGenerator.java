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

package org.broadleafcommerce.cms.page.service;

import org.broadleafcommerce.cms.page.dao.PageDao;
import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.common.sitemap.domain.SiteMapGeneratorConfiguration;
import org.broadleafcommerce.common.sitemap.service.SiteMapBuilder;
import org.broadleafcommerce.common.sitemap.service.SiteMapGenerator;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapGeneratorType;
import org.broadleafcommerce.common.sitemap.wrapper.SiteMapURLWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

/**
 * Responsible for generating site map entries for Page.
 * 
 * @author Joshua Skorton (jskorton)
 */
@Component("blPageSiteMapGenerator")
public class PageSiteMapGenerator implements SiteMapGenerator {

    @Resource(name = "blPageDao")
    protected PageDao pageDao;

    @Value("${page.site.map.generator.row.limit}")
    protected int rowLimit;

    @Override
    public boolean canHandleSiteMapConfiguration(SiteMapGeneratorConfiguration siteMapGeneratorConfiguration) {
        return SiteMapGeneratorType.PAGE.equals(siteMapGeneratorConfiguration.getSiteMapGeneratorType());
    }

    @Override
    public void addSiteMapEntries(SiteMapGeneratorConfiguration siteMapGeneratorConfiguration, SiteMapBuilder siteMapBuilder) {

        int rowOffset = 0;
        List<Page> pages;
        String previousUrl = "";

        do {
            pages = pageDao.readOnlinePages(rowLimit, rowOffset, "fullUrl");
            rowOffset += pages.size();
            for (Page page : pages) {

                if (page.getExcludeFromSiteMap()) {
                    continue;
                }

                String currentURL = page.getFullUrl();

                if (previousUrl.equals(currentURL)) {
                    continue;
                } else {
                    previousUrl = currentURL;
                }

                SiteMapURLWrapper siteMapUrl = new SiteMapURLWrapper();

                // location
                siteMapUrl.setLoc(currentURL);

                // change frequency
                siteMapUrl.setChangeFreqType(siteMapGeneratorConfiguration.getSiteMapChangeFreqType());

                // priority
                siteMapUrl.setPriorityType(siteMapGeneratorConfiguration.getSiteMapPriority());

                // lastModDate
                if ((page.getAuditable() != null) && (page.getAuditable().getDateUpdated() != null)) {
                    siteMapUrl.setLastModDate(page.getAuditable().getDateUpdated());
                } else {
                    siteMapUrl.setLastModDate(new Date());
                }

                siteMapBuilder.addUrl(siteMapUrl);
            }
        } while (pages.size() == rowLimit);
    }

}
