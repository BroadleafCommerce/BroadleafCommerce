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

import org.broadleafcommerce.core.catalog.domain.Product;

import java.util.List;

/**
 * Container that holds the result of a ProductSearch
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class ProductSearchResult {
	
	protected List<Product> products;
	protected List<SearchFacetDTO> facets;

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public List<SearchFacetDTO> getFacets() {
		return facets;
	}

	public void setFacets(List<SearchFacetDTO> facets) {
		this.facets = facets;
	}
	
}
