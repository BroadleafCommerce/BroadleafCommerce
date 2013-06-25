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

package org.broadleafcommerce.common.site.service;

import org.broadleafcommerce.common.site.dao.SiteDao;
import org.broadleafcommerce.common.site.domain.Site;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

@Service("blSiteService")
public class SiteServiceImpl implements SiteService {

    @Resource(name = "blSiteDao")
    protected SiteDao siteDao;

    @Override
    public Site retrieveSiteById(Long id) {
        Site response = siteDao.retrieve(id);
        if (response != null) {
            response = response.clone();
        }

        return response;
    }

    @Override
    @Transactional(value = "blTransactionManager", readOnly = true)
    public Site retrieveSiteByDomainName(String domainName) {
        String domainPrefix = null;
        if (domainName != null) {
            int pos = domainName.indexOf('.');
            if (pos >= 0) {
                domainPrefix = domainName.substring(0, pos);
            } else {
                domainPrefix = domainName;
            }
        }

        Site response = siteDao.retrieveSiteByDomainOrDomainPrefix(domainName, domainPrefix);
        if (response != null) {
            response = response.clone();
        }

        return response;
    }

    @Override
    @Transactional("blTransactionManager")
    public Site save(Site site) {
        return siteDao.save(site).clone();
    }

    @Override
    @Transactional(value = "blTransactionManager", readOnly = true)
    public Site retrieveDefaultSite() {
        return siteDao.retrieveDefaultSite().clone();
    }
    
    @Override
    @Transactional(value = "blTransactionManager", readOnly = true)
    public List<Site> findAllActiveSites() {
        List<Site> response = new ArrayList<Site>();
        List<Site> sites = siteDao.readAllActiveSites();
        for (Site site : sites) {
            response.add(site.clone());
        }
        return response;
    }

}