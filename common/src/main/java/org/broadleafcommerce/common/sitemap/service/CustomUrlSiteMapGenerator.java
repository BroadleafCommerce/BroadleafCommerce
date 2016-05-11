/*
 * #%L
 * BroadleafCommerce Common Libraries
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

package org.broadleafcommerce.common.sitemap.service;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.file.service.BroadleafFileUtils;
import org.broadleafcommerce.common.sitemap.domain.CustomUrlSiteMapGeneratorConfiguration;
import org.broadleafcommerce.common.sitemap.domain.SiteMapGeneratorConfiguration;
import org.broadleafcommerce.common.sitemap.domain.SiteMapUrlEntry;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapGeneratorType;
import org.broadleafcommerce.common.sitemap.wrapper.SiteMapURLWrapper;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Responsible for generating site map entries.   
 * 
 * Each SiteMapGenerator can generate 
 * 
 * @author bpolster
 *
 */
@Component("blCustomSiteMapGenerator")
public class CustomUrlSiteMapGenerator implements SiteMapGenerator {
    
    /**
     * Returns true if this SiteMapGenerator is able to process the passed in siteMapGeneratorConfiguration.   
     * 
     * @param siteMapGeneratorConfiguration
     * @return
     */
    public boolean canHandleSiteMapConfiguration(SiteMapGeneratorConfiguration siteMapGeneratorConfiguration) {
        return SiteMapGeneratorType.CUSTOM.equals(siteMapGeneratorConfiguration.getSiteMapGeneratorType());
    }

    @Override
    public void addSiteMapEntries(SiteMapGeneratorConfiguration smgc, SiteMapBuilder siteMapBuilder) {
        for (SiteMapUrlEntry urlEntry : ((CustomUrlSiteMapGeneratorConfiguration) smgc).getCustomURLEntries()) {
            if (StringUtils.isEmpty(urlEntry.getLocation())) {
                continue;
            }
            SiteMapURLWrapper siteMapUrl = new SiteMapURLWrapper();

            // location
            siteMapUrl.setLoc(generateUri(siteMapBuilder, urlEntry));

            // change frequency
            if (urlEntry.getSiteMapChangeFreq() != null) {
                siteMapUrl.setChangeFreqType(urlEntry.getSiteMapChangeFreq());
            } else {
                siteMapUrl.setChangeFreqType(smgc.getSiteMapChangeFreq());
            }

            // priority
            if (urlEntry.getSiteMapPriority() != null) {
                siteMapUrl.setPriorityType(urlEntry.getSiteMapPriority());
            } else {
                siteMapUrl.setPriorityType(smgc.getSiteMapPriority());
            }

            // lastModDate
            siteMapUrl.setLastModDate(generateDate(urlEntry));
            
            siteMapBuilder.addUrl(siteMapUrl);
        }
    }
    
    protected String generateUri(SiteMapBuilder smb, SiteMapUrlEntry urlEntry) {
        String url = urlEntry.getLocation();
        if (url.contains("://")) {
            return url;
        } else {
            return BroadleafFileUtils.appendUnixPaths(smb.getBaseUrl(), url);
        }
    }

    protected Date generateDate(SiteMapUrlEntry urlEntry) {
        if(urlEntry.getLastMod() != null) {
            return urlEntry.getLastMod();
        } else {
            return new Date();
        }
    }

}
