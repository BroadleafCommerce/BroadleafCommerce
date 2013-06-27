/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.search.domain;

import java.math.BigDecimal;

/**
 * @author Andre Azzolini (apazzolini)
 */
public interface SearchFacetRange {

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