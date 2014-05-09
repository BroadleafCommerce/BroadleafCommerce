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
package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.search.domain.SearchFacetDTO;
import org.broadleafcommerce.core.search.domain.SearchResult;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "searchResults")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class SearchResultsWrapper extends BaseWrapper implements APIWrapper<SearchResult> {

    @XmlElement
    protected Integer page;

    /*
     * Indicates the requested or default page size.
     */
    @XmlElement
    protected Integer pageSize;

    /*
     * Indicates the actual results
     */
    @XmlElement
    protected Integer totalResults;

    /*
     * Indicates the number of pages
     */
    @XmlElement
    protected Integer totalPages;

    /*
     * List of products associated with a search
     */
    @XmlElementWrapper(name = "products")
    @XmlElement(name = "product")
    protected List<ProductWrapper> products;
    
    /*
     * List of products associated with a search
     */
    @XmlElementWrapper(name = "skus")
    @XmlElement(name = "sku")
    protected List<SkuWrapper> skus;

    /*
     * List of available facets to be used for searching
     */
    @XmlElementWrapper(name = "searchFacets")
    @XmlElement(name = "searchFacet")
    protected List<SearchFacetWrapper> searchFacets;

    @Override
    public void wrapDetails(SearchResult model, HttpServletRequest request) {

        page = model.getPage();
        pageSize = model.getPageSize();
        totalResults = model.getTotalResults();
        totalPages = model.getTotalPages();

        if (model.getProducts() != null) {
            products = new ArrayList<ProductWrapper>();
            for (Product product : model.getProducts()) {
                ProductWrapper productSummary = (ProductWrapper) context.getBean(ProductWrapper.class.getName());
                productSummary.wrapSummary(product, request);
                this.products.add(productSummary);
            }
        }
        
        if (model.getSkus() != null) {
            skus = new ArrayList<SkuWrapper>();
            for (Sku sku : model.getSkus()) {
                SkuWrapper skuSummary = (SkuWrapper) context.getBean(SkuWrapper.class.getName());
                skuSummary.wrapSummary(sku, request);
                this.skus.add(skuSummary);
            }
        }

        if (model.getFacets() != null) {
            this.searchFacets = new ArrayList<SearchFacetWrapper>();
            for (SearchFacetDTO facet : model.getFacets()) {
                SearchFacetWrapper facetWrapper = (SearchFacetWrapper) context.getBean(SearchFacetWrapper.class.getName());
                facetWrapper.wrapSummary(facet, request);
                this.searchFacets.add(facetWrapper);
            }
        }
    }

    @Override
    public void wrapSummary(SearchResult model, HttpServletRequest request) {
        wrapDetails(model, request);
    }
}
