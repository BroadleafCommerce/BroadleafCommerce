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

import org.apache.solr.client.solrj.response.QueryResponse;
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

    protected QueryResponse queryResponse;

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
        return ((products == null || products.size() == 0) && (skus == null || skus.size() == 0)) ? 0 : ((page - 1) * pageSize) + 1;
    }
    
    public Integer getEndResult() {
        return Math.min(page * pageSize, totalResults);
    }
    
    public Integer getTotalPages() {
        return ((products == null || products.size() == 0) && (skus == null || skus.size() == 0)) ? 1 : (int) Math.ceil(totalResults * 1.0 / pageSize);
    }

    public QueryResponse getQueryResponse() {
        return queryResponse;
    }

    public void setQueryResponse(QueryResponse queryResponse) {
        this.queryResponse = queryResponse;
    }
}
