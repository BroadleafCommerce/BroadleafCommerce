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
package org.broadleafcommerce.common.site.dao;

import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.site.domain.CatalogImpl;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.site.domain.SiteCatalogXref;
import org.broadleafcommerce.common.site.domain.SiteImpl;
import org.broadleafcommerce.common.site.service.type.SiteResolutionType;
import org.broadleafcommerce.common.util.dao.TypedQueryBuilder;
import org.hibernate.jpa.QueryHints;
import org.springframework.stereotype.Repository;

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
    public Catalog retrieveCatalogByName(String name) {
        TypedQuery<Catalog> catalogByName = new TypedQueryBuilder<>(Catalog.class, "c")
            .addRestriction("c.name", "=", name)
            .toQuery(em);
        
        List<Catalog> catalogs = catalogByName.getResultList();
        if (CollectionUtils.isNotEmpty(catalogs)) {
            return catalogs.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Catalog createCatalog() {
        return (Catalog) entityConfiguration.createEntityInstance(Catalog.class.getName());
    }

    @Override
    public SiteCatalogXref createSiteCatalog() {
        return (SiteCatalogXref) entityConfiguration.createEntityInstance(SiteCatalogXref.class.getName());
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
        query.setHint(QueryHints.HINT_CACHE_REGION, "blSiteElementsQuery");
        
        return query.getResultList();
    }

    @Override
    public Site retrieveSiteByDomainOrDomainPrefix(String domain, String domainPrefix) {
        if (domain == null) {
            return null;
        }

        List<Site> results = retrieveSitesByPotentialIdentifiers(domain, domainPrefix);
        
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
        }

        return null;
    }
    
    @Override
    public Site retrieveSiteByIdentifier(String identifier) {
        List<Site> sites = retrieveSitesByPotentialIdentifiers(identifier);
        return CollectionUtils.isNotEmpty(sites) ? sites.get(0) : null;
    }
    
    public List<Site> retrieveSitesByPotentialIdentifiers(String... potentialIdentifiers) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Site> criteria = builder.createQuery(Site.class);
        Root<SiteImpl> site = criteria.from(SiteImpl.class);
        criteria.select(site);

        criteria.where(builder.and(site.get("siteIdentifierValue").as(String.class).in(potentialIdentifiers),
                builder.and(
                    builder.or(builder.isNull(site.get("archiveStatus").get("archived").as(String.class)),
                        builder.notEqual(site.get("archiveStatus").get("archived").as(Character.class), 'Y')),
                    builder.or(builder.isNull(site.get("deactivated").as(Boolean.class)),
                        builder.notEqual(site.get("deactivated").as(Boolean.class), true))
                )
            )
        );
        TypedQuery<Site> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "blSiteElementsQuery");
        return query.getResultList();
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
    
    @Override
    public List<Catalog> retrieveAllCatalogs() {
        TypedQuery<Catalog> q = new TypedQueryBuilder<>(Catalog.class, "c")
                .toQuery(em);
        return q.getResultList();
    }

}
