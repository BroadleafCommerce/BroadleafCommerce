/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.search.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.core.catalog.domain.Category;

import java.math.BigDecimal;

/**
 * @author Andre Azzolini (apazzolini)
 */
public interface CategoryExcludedSearchFacet extends MultiTenantCloneable<CategoryExcludedSearchFacet>{

    /**
     * Gets the internal id
     * 
     * @return the internal id
     */
    public Long getId();

    /** 
     * Sets the internal id
     * 
     * @param id
     */
    public void setId(Long id);

    /**
     * Gets the associated category
     * 
     * @return the associated category
     */
    public Category getCategory();

    /**
     * Sets the associated category
     * 
     * @param category
     */
    public void setCategory(Category category);

    /**
     * Gets the associated search facet
     * 
     * @return the associated search facet
     */
    public SearchFacet getSearchFacet();

    /**
     * Sets the associated search facet
     * 
     * @param searchFacet
     */
    public void setSearchFacet(SearchFacet searchFacet);

    /**
     * Gets the priority of this search facet in relationship to other search facets in this category
     * 
     * @return the sequence of this search facet
     */
    public BigDecimal getSequence();

    /**
     * Sets the sequence of this search facet
     * 
     * @see #getPosition()
     * @param position
     */
    public void setSequence(BigDecimal sequence);

}
