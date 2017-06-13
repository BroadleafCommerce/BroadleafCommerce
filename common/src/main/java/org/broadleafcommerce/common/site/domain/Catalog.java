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
package org.broadleafcommerce.common.site.domain;

import org.broadleafcommerce.common.persistence.Status;

import java.io.Serializable;
import java.util.List;
import java.lang.Comparable;

/**
 * @author Jeff Fischer
 */
public interface Catalog extends Status, Serializable, Comparable<Catalog> {

    Long getId();

    void setId(Long id);

    String getName();

    void setName(String name);

    @Deprecated
    List<Site> getSites();

    @Deprecated
    void setSites(List<Site> sites);
    
    public List<SiteCatalogXref> getSiteXrefs();

    public void setSiteXrefs(List<SiteCatalogXref> siteXrefs);

    @Override
    public int compareTo(Catalog other);

    Catalog clone();

}
