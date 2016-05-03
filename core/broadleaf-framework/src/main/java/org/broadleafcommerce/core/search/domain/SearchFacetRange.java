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

import java.math.BigDecimal;

/**
 * @author Andre Azzolini (apazzolini)
 */
public interface SearchFacetRange extends MultiTenantCloneable<SearchFacetRange> {

    /**
     * Returns the internal id
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
     * Gets the minimum value for this SearchFacetRange
     * 
     * Note: The default SearchFacetRangeImpl does not allow this value to be null
     * 
     * @return the min value
     */
    public BigDecimal getMinValue();

    /**
     * Sets the minium value for this SearchFacetRange
     * 
     * @param minValue
     */
    public void setMinValue(BigDecimal minValue);

    /**
     * Gets the maximum value for this SearchFacetRange
     * 
     * Note: The default SearchFacetRangeImpl allows this value to be null
     * 
     * @return the max value
     */
    public BigDecimal getMaxValue();

    /**
     * Sets the maximum value for this SearchFacetRange
     * 
     * @param maxValue
     */
    public void setMaxValue(BigDecimal maxValue);

    /**
     * Gets the associated SearchFacet to this range
     * 
     * @return the associated SearchFacet
     */
    public SearchFacet getSearchFacet();
    
    /**
     * Sets the associated SearchFacet
     * 
     * @param searchFacet
     */
    public void setSearchFacet(SearchFacet searchFacet);

}
