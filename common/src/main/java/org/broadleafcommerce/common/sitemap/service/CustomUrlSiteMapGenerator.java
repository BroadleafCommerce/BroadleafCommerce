/*
 * #%L
 * BroadleafCommerce Common Libraries
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
            return BroadleafFileUtils.buildFilePath(smb.getBaseUrl(), url);
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
