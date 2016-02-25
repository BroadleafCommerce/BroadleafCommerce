/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.service;

import org.broadleafcommerce.core.search.domain.SearchCriteria;
import org.broadleafcommerce.core.search.domain.SearchFacetDTO;
import org.broadleafcommerce.core.search.domain.SearchFacetResultDTO;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * Provides methods that facilitate interactions with SearchFacetDTOs and SearchFacetResultDTOs
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface SearchFacetDTOService {

    /**
     * Given a servlet request and a list of available facets for this request (could be search or category based),
     * this method will build out a SearchCriteria object to be used by the ProductSearchService. It will
     * perform translations from query string parameters to the SearchCriteria.
     * 
     * @param availableFacets
     * @param request
     * @return the SearchCriteria
     */
    public SearchCriteria buildSearchCriteria(HttpServletRequest request);

    /**
     * Sets the "active" boolean on a given SearchFacetResultDTO as determined by the current request
     * 
     * @param facets
     * @param request
     */
    public void setActiveFacetResults(List<SearchFacetDTO> facets, HttpServletRequest request);

    /**
     * Returns whether or not the SearchFacetResultDTO's key/value pair is present in the servlet request
     * 
     * @param result
     * @param request
     * @return if the result is active
     */
    public boolean isActive(SearchFacetResultDTO result, HttpServletRequest request);
    
    /**
     * Gets the url abbreviation associated with a given SearchFacetResultDTO.
     * 
     * @param result
     * @return the key associated with a SearchFacetResultDTO
     */
    public String getUrlKey(SearchFacetResultDTO result);

    /**
     * Gets the value of the given SearchFacetResultDTO.
     * The default Broadleaf implementation will return the String value of the result if the value
     * is not empty, or "range[<min-value>:<max-value>]" if the value was empty.
     * 
     * @param result
     * @return the value of the SearchFacetResultDTO
     */
    public String getValue(SearchFacetResultDTO result);


    public String getUrlKey(SearchFacetDTO result);

}
