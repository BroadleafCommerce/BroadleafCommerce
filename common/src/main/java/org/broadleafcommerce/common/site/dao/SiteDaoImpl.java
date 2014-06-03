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
package org.broadleafcommerce.common.site.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.site.domain.CatalogImpl;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.site.domain.SiteImpl;
import org.broadleafcommerce.common.site.service.type.SiteResolutionType;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Repository("blSiteDao")
public class SiteDaoImpl implements SiteDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public Site create() {
        return (Site) entityConfiguration.createEntityInstance(Site.class.getName());
    }

    @Override
    public Site retrieve(Long id) {
        return em.find(SiteImpl.class, id);
    }
    
    @Override
    public Catalog retrieveCatalog(Long id) {
        return em.find(CatalogImpl.class, id);
    }
    
    @Override
    public List<Site> readAllActiveSites() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Site> criteria = builder.createQuery(Site.class);
        Root<SiteImpl> site = criteria.from(SiteImpl.class);
        criteria.select(site);
        criteria.where(
            builder.and(
                builder.or(builder.isNull(site.get("archiveStatus").get("archived").as(String.class)),
                    builder.notEqual(site.get("archiveStatus").get("archived").as(Character.class), 'Y')),
                builder.or(builder.isNull(site.get("deactivated").as(Boolean.class)),
                    builder.notEqual(site.get("deactivated").as(Boolean.class), true))
            )
        );
        
        TypedQuery<Site> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        
        return query.getResultList();
    }

    @Override
    public Site retrieveSiteByDomainOrDomainPrefix(String domain, String domainPrefix) {
        if (domain == null) {
            return null;
        }

        List<String> siteIdentifiers = new ArrayList<String>();
        siteIdentifiers.add(domain);
        siteIdentifiers.add(domainPrefix);

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Site> criteria = builder.createQuery(Site.class);
        Root<SiteImpl> site = criteria.from(SiteImpl.class);
        criteria.select(site);

        criteria.where(site.get("siteIdentifierValue").as(String.class).in(siteIdentifiers));
        TypedQuery<Site> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);

        List<Site> results = query.getResultList();
        
        for (Site currentSite : results) {
            if (SiteResolutionType.DOMAIN.equals(currentSite.getSiteResolutionType())) {
                if (domain.equals(currentSite.getSiteIdentifierValue())) {
                    return currentSite;
                }
            }

            if (SiteResolutionType.DOMAIN_PREFIX.equals(currentSite.getSiteResolutionType())) {
                if (domainPrefix.equals(currentSite.getSiteIdentifierValue())) {
                    return currentSite;
                }
            }
            
            // We need to forcefully load this collection.
            currentSite.getCatalogXrefs().size();
        }

        return null;
    }

    @Override
    public Site save(Site site) {
        return em.merge(site);
    }

    @Override
    public Site retrieveDefaultSite() {
        return null;
    }
    
    @Override
    public Catalog save(Catalog catalog) {
        return em.merge(catalog);
    }
}
