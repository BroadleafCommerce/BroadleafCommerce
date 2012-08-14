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

package org.broadleafcommerce.core.search.service;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.search.domain.ProductSearchCriteria;
import org.broadleafcommerce.core.search.domain.ProductSearchResult;
import org.broadleafcommerce.core.search.domain.SearchFacetDTO;

import java.io.IOException;
import java.util.List;

/**
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface SearchService {
	
	/**
	 * Performs a search for products in the given category, taking into consideration the ProductSearchCriteria
	 * 
	 * @param category
	 * @param searchCriteria
	 * @return the result of the search
	 * @throws ServiceException 
	 */
	public ProductSearchResult findProductsByCategory(Category category, ProductSearchCriteria searchCriteria) throws ServiceException;
	
	/**
	 * Performs a search for products across all categories for the given query, taking into consideration
	 * the ProductSearchCriteria
	 * 
	 * @param query
	 * @param searchCriteria
	 * @return the result of the search
	 */
	public ProductSearchResult findProductsByQuery(String query, ProductSearchCriteria searchCriteria);

	/**
	 * Gets all available facets for search results page
	 * 
	 * @return the available facets
	 */
	public List<SearchFacetDTO> getSearchFacets();

	/**
	 * Gets all available facets for a given category
	 * 
	 * @param category
	 * @return the available facets
	 */
	public List<SearchFacetDTO> getCategoryFacets(Category category);
	
	/**
	 * Rebuilds the current index. Note that some search service implementations may not necessarily
	 * support rebuilding an index (such as the DatabaseProductSearchService)
	 * @throws IOException 
	 * @throws ServiceException
	 */
	public void rebuildIndex() throws ServiceException, IOException;

}