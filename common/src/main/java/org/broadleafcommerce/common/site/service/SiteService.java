/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.common.site.service;

import org.broadleafcommerce.common.site.dao.SiteDao;
import org.broadleafcommerce.common.site.dao.SiteDaoImpl;
import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.site.domain.SiteCatalogXref;
import org.broadleafcommerce.common.web.BroadleafSiteResolver;

import java.util.List;

/**
 * <p>
 * Most of the methods below return a {@link Site} which is not attached to a Hibernate session (which is what is referred
 * to as 'non-persistent'). While this prevents LazyInitializationExceptions that might occur if there is not continually a
 * Hibernate session, this also means that while the results of these will return you an instance, you might need to
 * look the entity back up from the database to refresh it.
 *
 * <p>
 * Note also that when resolving sites (via the {@link BroadleafSiteResolver}) the usual case is to return a non-persistent
 * version not attached to a Hibernate session.
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
public interface SiteService {

    /**
     * Creates an instance of Site.   Default implementation delegates to {@link SiteDao#create()}.
     *
     * @return
     */
    Site createSite();

    /**
     * Find a site by its id and returns a non-persistent version
     *
     * @param id
     * @return
     * @deprecated use {@link #retrieveNonPersistentSiteById(Long)} instead
     */
    @Deprecated
    Site retrieveSiteById(Long id);

    /**
     * Retrieves a site by its primary key disconnected from a Hibernate session
     */
    Site retrieveNonPersistentSiteById(Long id);

    /**
     * Retrieves a site by its primary key whose return value is still attached to a Hibernate session
     */
    Site retrievePersistentSiteById(Long id);

    /**
     * Find a site by its domain and returns a non-persistent version
     *
     * @param domain
     * @return
     * @deprecated use {@link #retrieveNonPersistentSiteByDomainName(String)} instead
     */
    @Deprecated
    Site retrieveSiteByDomainName(String domain);

    /**
     * Finds a site by the given domain  name and returns a non-persistent version
     */
    Site retrieveNonPersistentSiteByDomainName(String domain);

    /**
     * Finds a site by the domain name which is still attached to a Hibernate session
     *
     * @param domain
     * @return
     */
    Site retrievePersistentSiteByDomainName(String domain);

    /**
     * Finds a site by its {@link Site#getSiteIdentifierValue()} and return an entity that is not attached to the
     * Hibernate session
     *
     * @param identifier the {@link Site#getSiteIdentifierValue()} to look for
     */
    Site retrieveNonPersistentSiteByIdentifer(String identifier);

    /**
     * Finds a site by its {@link Site#getSiteIdentifierValue()}
     *
     * @param identifier the {@link Site#getSiteIdentifierValue()} to look for
     */
    Site retrievePersistentSiteByIdentifier(String identifier);

    /**
     * Save updates to a site and returns a cloned instance
     *
     * @param site
     * @return
     * @deprecated use {@link #saveAndReturnNonPersisted(Site)} instead
     */
    @Deprecated
    Site save(Site site);

    /**
     * Save updates to a site and returns a non-persistent instance
     *
     * @param site
     * @return
     */
    Site saveAndReturnNonPersisted(Site site);

    /**
     * Save updates to a site and return the persistent instance attached to a Hibernate session
     *
     * @param site
     * @return
     */
    Site saveAndReturnPersisted(Site site);

    /**
     * Returns a clone of the default site.
     *
     * @return
     * @see {@link SiteDaoImpl}
     * @deprecated use {@link #retrieveNonPersistentDefaultSite()} instead
     */
    @Deprecated
    Site retrieveDefaultSite();

    /**
     * Retrieves the non-persistent version of the default site
     */
    Site retrieveNonPersistentDefaultSite();

    /**
     * Retrieves the default site attached to a Hibernate session
     */
    Site retrievePersistentDefaultSite();

    /**
     * @return a List of non-persistent versions of all sites in the system
     * @deprecated use {@link #findAllNonPersistentActiveSites()} instead
     */
    @Deprecated
    List<Site> findAllActiveSites();

    /**
     * Returns all of the active sites in the system and does not attach them to a Hibernate session
     */
    List<Site> findAllNonPersistentActiveSites();

    /**
     * Returns all of the active sites in the system that are still attached to a Hibernate session
     */
    List<Site> findAllPersistentActiveSites();

    /**
     * Finds a catalog by its id.
     *
     * @param id
     * @return the catalog
     */
    Catalog findCatalogById(Long id);

    Catalog findCatalogByName(String name);

    /**
     * Creates an instance of {@code Catalog}.   Default implementation delegates to {@link SiteDao#createCatalog()}.
     *
     * @return the catalog
     */
    Catalog createCatalog();

    /**
     * Creates an instance of {@code SiteCatalogXref}. Default implementation delegates to {@link SiteDao#createSiteCatalog()}
     *
     * @return the site catalog
     */
    SiteCatalogXref createSiteCatalog();

    /**
     * Saves the given <b>catalog</b> and returns the merged instance
     *
     * @param catalog
     * @return
     */
    Catalog save(Catalog catalog);

    /**
     * Finds all catalogs
     *
     * @return the list of catalogs
     */
    List<Catalog> findAllCatalogs();

}
