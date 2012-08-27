/*
 * Copyright 2012 the original author or authors.
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

import java.util.List;

/**
 * A SearchFacet is an object that represents a particular facet that can be used to guide faceted 
 * searching on a results page.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface SearchFacet {

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
	 * Returns the field associated with this facet. 
	 * 
	 * @return the fieldName
	 */
	public Field getField();

	/**
	 * Sets the field associated with this facet.
	 * 
	 * @see #getFieldName()
	 * @param fieldName
	 */
	public void setField(Field field);

	/**
	 * Gets the label of this SearchFacet. This is the label that will be used for the user-friendly
	 * display name of this facet
	 * 
	 * @return the label
	 */
	public String getLabel();

	/**
	 * Sets the label
	 * 
	 * @see #getLabel()
	 * @param label
	 */
	public void setLabel(String label);

	/**
	 * Gets a boolean that specifies whether or not this SearchFacet should be displayed on search
	 * result pages in addition to category pages
	 * 
	 * @return whether or not to display on search result pages
	 */
	public Boolean getShowOnSearch();

	/**
	 * Sets showOnSearch
	 * 
	 * @see #getShowOnSearch()
	 * @param showOnSearch
	 */
	public void setShowOnSearch(Boolean showOnSearch);

	/**
	 * Gets the display priority of this SearchFacet on search result pages
	 * 
	 * @return the priority
	 */
	public Integer getSearchDisplayPriority();

	/**
	 * Sets the display priority on search result pages
	 * 
	 * @param searchDisplayPriority
	 */
	public void setSearchDisplayPriority(Integer searchDisplayPriority);
	
	/**
	 * Sets whether or not you can multiselect values for this Facet.
	 * 
	 * @param canMultiselect
	 */
	public void setCanMultiselect(Boolean canMultiselect);

	/**
	 * Gets whether or not you can multiselect values for this Facet
	 * 
	 * @return the multiselect flag
	 */
	public Boolean getCanMultiselect();

	/**
	 * Gets the applicable ranges for this search facet, if any are specified. For example, the 
	 * SearchFacet that interacts with "Manufacturers" might not have any ranges defined (as it
	 * would depend on the manufacturers that are in the result list), but a facet on "Price"
	 * might have predefined ranges (such as 0-5, 5-10, 10-20).
	 * 
	 * @return the associated search facet ranges, if any
	 */
	public List<SearchFacetRange> getSearchFacetRanges();
	
	/**
	 * Sets the SearchFacetRanges
	 * 
	 * @see #getSearchFacetRanges()
	 * @param searchFacetRanges
	 */
	public void setSearchFacetRanges(List<SearchFacetRange> searchFacetRanges);

}