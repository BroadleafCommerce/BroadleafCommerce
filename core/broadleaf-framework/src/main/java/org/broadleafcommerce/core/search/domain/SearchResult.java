/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.search.domain;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;

import java.util.List;

/**
 * Container that holds the result of a ProductSearch or a SkuSearch
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class SearchResult {
    
    protected List<Product> products;
    protected List<Sku> skus;
    protected List<SearchFacetDTO> facets;
    
    protected Integer totalResults;
    protected Integer page;
    protected Integer pageSize;

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Sku> getSkus() {
        return skus;
    }

    public void setSkus(List<Sku> skus) {
        this.skus = skus;
    }

    public List<SearchFacetDTO> getFacets() {
        return facets;
    }

    public void setFacets(List<SearchFacetDTO> facets) {
        this.facets = facets;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
    
    public Integer getStartResult() {
        return (products == null || products.size() == 0) ? 0 : ((page - 1) * pageSize) + 1;
    }
    
    public Integer getEndResult() {
        return Math.min(page * pageSize, totalResults);
    }
    
    public Integer getTotalPages() {
        return (products == null || products.size() == 0) ? 1 : (int) Math.ceil(totalResults * 1.0 / pageSize);
    }

    public Integer getTotalSkuPages() {
        return (skus == null || skus.size() == 0) ? 1 : (int) Math.ceil(totalResults * 1.0 / pageSize);
    }

}
