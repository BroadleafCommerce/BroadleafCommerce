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

import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.site.domain.SiteCatalogXref;

import java.util.List;

public interface SiteDao {

    /**
     * Creates an instance of Site based on the class matching the bean id of 
     * "org.broadleafcommerce.common.site.domain.Site"
     * 
     * @return
     */
    public Site create();

    /**
     * Finds a site by its id.
     * @param id
     * @return
     */
    public Site retrieve(Long id);

    /**
     * Finds a site by its domain or domain prefix.
     * @param domain
     * @param prefix
     * @return
     */
    public Site retrieveSiteByDomainOrDomainPrefix(String domain, String prefix);
    
    /**
     * Finds a site by its {@link Site#getSiteIdentifierValue()}.
     * @param identifier
     * @return
     */
    public Site retrieveSiteByIdentifier(String identifier);

    /**
     * Persists the site changes.
     * @param site
     * @return
     */
    public Site save(Site site);

    /**
     * Returns a default site.   This method returns null in the out of box implementation of Broadleaf.
     * Extend for implementation specific behavior. 
     * 
     * @return
     */
    public Site retrieveDefaultSite();

    /**
     * @return a List of all sites in the system
     */
    public List<Site> readAllActiveSites();

    /**
     * Finds a catalog by its id.
     * 
     * @param id
     * @return the catalog
     */
    public Catalog retrieveCatalog(Long id);
    
    Catalog retrieveCatalogByName(String name);

    /**
     * Creates a catalog using {@link org.broadleafcommerce.common.persistence.EntityConfiguration}.
     *
     * @return the catalog
     */
    public Catalog createCatalog();

    /**
     * Creates a new instance of {@code SiteCatalogXref} using {@link org.broadleafcommerce.common.persistence.EntityConfiguration}.
     *
     * @return the site catalog
     */
    public SiteCatalogXref createSiteCatalog();
    
    public Catalog save(Catalog catalog);

    /**
     * Retrieves all catalogs
     * 
     * @return the list of catalogs
     */
    public List<Catalog> retrieveAllCatalogs();
}
