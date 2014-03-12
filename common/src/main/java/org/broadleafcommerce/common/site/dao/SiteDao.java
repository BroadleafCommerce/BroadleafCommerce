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

import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.site.domain.Site;

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
}
