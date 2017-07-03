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
package org.broadleafcommerce.common.site.service;

import org.broadleafcommerce.common.site.dao.SiteDao;
import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.util.StreamCapableTransactionalOperationAdapter;
import org.broadleafcommerce.common.util.StreamingTransactionCapableUtil;
import org.broadleafcommerce.common.util.TransactionUtils;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

@Service("blSiteService")
public class SiteServiceImpl implements SiteService {

    @Resource(name="blStreamingTransactionCapableUtil")
    protected StreamingTransactionCapableUtil transUtil;

    @Resource(name = "blSiteDao")
    protected SiteDao siteDao;

    @Resource(name = "blSiteServiceExtensionManager")
    protected SiteServiceExtensionManager extensionManager;
    
    @Override
    public Site createSite() {
        return siteDao.create();
    }

    @Override
    @Deprecated
    public Site retrieveSiteById(final Long id) {
        return retrieveNonPersistentSiteById(id);
    }
    
    @Override
    public Site retrieveNonPersistentSiteById(final Long id) {
        return retrieveSiteById(id, false);
    }

    @Override
    public Site retrievePersistentSiteById(final Long id) {
        return retrieveSiteById(id, true);
    }
    
    protected Site retrieveSiteById(final Long id, final boolean persistentResult) {
        //Provide an entity manager in view, if we don't already have one, to facilitate a larger scope
        //for the session and avoid lazy init problems. This should only cause a connection borrow from the
        //connection pool if L2 cache is not effective.
        if (id == null) { return null; }
        final Site[] response = new Site[1];
        transUtil.runOptionalEntityManagerInViewOperation(new Runnable() {
            @Override
            public void run() {
                Site site = siteDao.retrieve(id);
                if (persistentResult) {
                    response[0] = site;
                } else {
                    response[0] = getNonPersistentSite(site);
                }
            }
        });

        return response[0];
    }

    @Override
    @Deprecated
    public Site retrieveSiteByDomainName(final String domainName) {
        return retrieveNonPersistentSiteByDomainName(domainName);
    }
    
    @Override
    public Site retrieveNonPersistentSiteByDomainName(final String domainName) {
        return retrieveSiteByDomainName(domainName, false);
    }

    @Override
    public Site retrievePersistentSiteByDomainName(final String domainName) {
        return retrieveSiteByDomainName(domainName, true);
    }
    
    public Site retrieveSiteByDomainName(final String domainName, final boolean persistentResult) {
        //Provide an entity manager in view, if we don't already have one, to facilitate a larger scope
        //for the session and avoid lazy init problems. This should only cause a connection borrow from the
        //connection pool if L2 cache is not effective.
        final Site[] response = new Site[1];
        transUtil.runOptionalEntityManagerInViewOperation(new Runnable() {
            @Override
            public void run() {
                String domainPrefix = null;
                String domain = domainName;
                if (domainName != null) {
                    int pos = domainName.indexOf('.');
                    if (pos >= 0) {
                        domainPrefix = domainName.substring(0, pos);
                        if (stripSubdomain(domainPrefix)) {
                            domain = domainName.substring(domainPrefix.length() + 1);
                        }
                    } else {
                        domainPrefix = domainName;
                    }
                }

                Site site = siteDao.retrieveSiteByDomainOrDomainPrefix(domain, domainPrefix);
                if (persistentResult) {
                    response[0] = site;
                } else {
                    response[0] = getNonPersistentSite(site);
                }
            }
        });

        return response[0];
    }

    /**
     * Checks whether the provided subdomain is one to be stripped/removed from the full domain name
     *
     * @param subDomain
     * @return boolean if subdomain is a candiate to be removed - true indicates it is eligible to be removed
     */
    protected boolean stripSubdomain(String subDomain) {
        if (subDomain != null) {
            String propStripPrefixes = BLCSystemProperty.resolveSystemProperty("site.domain.resolver.strip.subdomains");
            if (propStripPrefixes != null) {
                String[] prefixes = propStripPrefixes.split(",");
                for(String prefix : prefixes) {
                    if (subDomain.equals(prefix)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    @Deprecated
    @Transactional("blTransactionManager")
    public Site save(Site site) {
        return saveAndReturnNonPersisted(site);
    }
    
    @Override
    @Transactional("blTransactionManager")
    public Site saveAndReturnNonPersisted(Site site) {
        return getNonPersistentSite(saveAndReturnPersisted(site));
    }

    @Override
    @Transactional("blTransactionManager")
    public Site saveAndReturnPersisted(Site site) {
        return siteDao.save(site);
    }

    @Override
    public Catalog findCatalogById(Long id) {
        return siteDao.retrieveCatalog(id);
    }

    @Override
    @Deprecated
    public Site retrieveDefaultSite() {
        return retrieveNonPersistentDefaultSite();
    }
    
    @Override
    public Site retrieveNonPersistentDefaultSite() {
        return getNonPersistentSite(retrievePersistentDefaultSite());
    }

    @Override
    public Site retrievePersistentDefaultSite() {
        return retrieveDefaultSite(true);
    }
    
    protected Site retrieveDefaultSite(final boolean persistentResult) {
        //Provide an entity manager in view, if we don't already have one, to facilitate a larger scope
        //for the session and avoid lazy init problems. This should only cause a connection borrow from the
        //connection pool if L2 cache is not effective.
        final Site[] response = new Site[1];
        transUtil.runOptionalEntityManagerInViewOperation(new Runnable() {
            @Override
            public void run() {
                Site defaultSite = siteDao.retrieveDefaultSite();
                if (persistentResult) {
                    response[0] = defaultSite;
                } else {
                    response[0] = getNonPersistentSite(defaultSite);
                }
            }
        });

        return response[0];
    }
    
    @Override
    @Deprecated
    public List<Site> findAllActiveSites() {
        return findAllNonPersistentActiveSites();
    }
    
    @Override
    public List<Site> findAllNonPersistentActiveSites() {
        return findAllSites(false);
    }

    @Override
    public List<Site> findAllPersistentActiveSites() {
        return findAllSites(true);
    }
    
    protected List<Site> findAllSites(final boolean persistentResult) {
        //Provide an entity manager in view, if we don't already have one, to facilitate a larger scope
        //for the session and avoid lazy init problems. This should only cause a connection borrow from the
        //connection pool if L2 cache is not effective.
        final List<Site> response = new ArrayList<Site>();
        transUtil.runOptionalEntityManagerInViewOperation(new Runnable() {
            @Override
            public void run() {
              List<Site> sites = siteDao.readAllActiveSites();
              for (Site site : sites) {
                  if (persistentResult) {
                      response.add(site);
                  } else {
                      response.add(getNonPersistentSite(site));
                  }
              }
          }
        });

        return response;
      }
    
    protected Site getNonPersistentSite(Site persistentSite) {
        if (persistentSite == null) {
            return null;
        }
        NonPersistentSiteTheadLocalCache cache = NonPersistentSiteTheadLocalCache.getSitesCache();
        Site clone = cache.getSites().get(persistentSite.getId());
        if (clone == null) {
            clone = persistentSite.clone();
            extensionManager.getProxy().contributeNonPersitentSiteProperties(persistentSite, clone);
            cache.getSites().put(persistentSite.getId(), clone);
        }
        return clone;
    }
    
    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public Catalog save(Catalog catalog) {
        return siteDao.save(catalog);
    }
    
    @Override
    public List<Catalog> findAllCatalogs() {
        return siteDao.retrieveAllCatalogs();
    }

}
