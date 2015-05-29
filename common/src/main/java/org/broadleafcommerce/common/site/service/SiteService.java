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
import org.broadleafcommerce.common.site.dao.SiteDaoImpl;
import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.site.domain.Site;
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
    public Site createSite();

    /**
     * Find a site by its id and returns a non-persistent version
     * @param id
     * @return
     * @deprecated use {@link #retrieveNonPersistentSiteById(Long)} instead
     */
    @Deprecated
    public Site retrieveSiteById(Long id);
    
    /**
     * Retrieves a site by its primary key disconnected from a Hibernate session
     */
    public Site retrieveNonPersistentSiteById(Long id);
    
    /**
     * Retrieves a site by its primary key whose return value is still attached to a Hibernate session
     */
    public Site retrievePersistentSiteById(Long id);

    /**
     * Find a site by its domain and returns a non-persistent version
     * @param id
     * @return
     * @deprecated use {@link #retrieveNonPersistentSiteByDomainName(String)} instead
     */
    @Deprecated
    public Site retrieveSiteByDomainName(String domain);

    /**
     * Finds a site by the given domain  name and returns a non-persistent version
     */
    public Site retrieveNonPersistentSiteByDomainName(String domain);

    /**
     * Finds a site by the domain name which is still attached to a Hibernate session
     * @param domain
     * @return
     */
    public Site retrievePersistentSiteByDomainName(String domain);
    
    /**
     * Save updates to a site and returns a cloned instance
     * @param id
     * @return
     * @deprecated use {@link #saveAndReturnNonPersisted(Site)} instead
     */
    @Deprecated
    public Site save(Site site);
    
    /**
     * Save updates to a site and returns a non-persistent instance
     * @param site
     * @return
     */
    public Site saveAndReturnNonPersisted(Site site);
    
    /**
     * Save updates to a site and return the persistent instance attached to a Hibernate session
     * @param site
     * @return
     */
    public Site saveAndReturnPersisted(Site site);
    
    /**
     * Returns a clone of the default site.  
     * 
     * @see {@link SiteDaoImpl}
     * 
     * @param id
     * @return
     * @deprecated use {@link #retrieveNonPersistentDefaultSite()} instead
     */
    @Deprecated
    public Site retrieveDefaultSite();

    /**
     * Retrieves the non-persistent version of the default site
     */
    public Site retrieveNonPersistentDefaultSite();
    
    /**
     * Retrieves the default site attached to a Hibernate session
     */
    public Site retrievePersistentDefaultSite();
    
    /**
     * @return a List of non-persistent versions of all sites in the system
     * @deprecated use {@link #findAllNonPersistentActiveSites()} instead
     */
    @Deprecated
    public List<Site> findAllActiveSites();
    
    /**
     * Returns all of the active sites in the system and does not attach them to a Hibernate session
     */
    public List<Site> findAllNonPersistentActiveSites();
    
    /**
     * Returns all of the active sites in the system that are still attached to a Hibernate session
     */
    public List<Site> findAllPersistentActiveSites();

    /**
     * Finds a catalog by its id.
     * 
     * @param id
     * @return the catalog
     */
    public Catalog findCatalogById(Long id);
    
    /**
     * Saves the given <b>catalog</b> and returns the merged instance
     * @param catalog
     * @return
     */
    public Catalog save(Catalog catalog);

    /**
     * Finds all catalogs
     * 
     * @return the list of catalogs
     */
    public List<Catalog> findAllCatalogs();

}
