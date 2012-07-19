/*
 * Copyright 2008-2009 the original author or authors.
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

import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.search.domain.CategorySearchFacet;
import org.broadleafcommerce.core.search.domain.ProductSearchCriteria;
import org.broadleafcommerce.core.search.domain.ProductSearchResult;
import org.broadleafcommerce.core.search.domain.SearchFacetDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

@Service("blProductSearchService")
public class DatabaseProductSearchServiceImpl implements ProductSearchService {
	
	@Resource(name = "blCatalogService")
	protected CatalogService catalogService;
	
	@Override
	public ProductSearchResult findProductsByCategory(Category category, ProductSearchCriteria searchCriteria) {
		ProductSearchResult result = new ProductSearchResult();
		List<Product> products = catalogService.findActiveProductsByCategory(category, SystemTime.asDate());
		//List<SearchFacetDTO> facets = getCategoryFacets(category);
		result.setProducts(products);
		//result.setFacets(facets);
		return result;
	}
	
	protected List<SearchFacetDTO> getCategoryFacets(Category category) {
		List<SearchFacetDTO> facets = new ArrayList<SearchFacetDTO>();
		List<CategorySearchFacet> categoryFacets = category.getCumulativeSearchFacets();
		for (CategorySearchFacet facet : categoryFacets) {
			SearchFacetDTO dto = new SearchFacetDTO();
			dto.setFacet(facet);
			dto.setShowQuantity(false);
		}
		return facets;
	}
	
	/*
	protected List<SearchFacetResultDTO> getFacetValues(CategorySearchFacet facet) {
		List<SearchFacetResultDTO> results = new ArrayList<SearchFacetResultDTO>();
		
		
	}
	*/
	

}
