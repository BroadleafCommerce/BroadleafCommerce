package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.search.domain.ProductSearchResult;
import org.broadleafcommerce.core.search.domain.SearchFacetDTO;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "searchResult")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class SearchResultsWrapper extends BaseWrapper implements APIWrapper<ProductSearchResult> {

    @XmlElement
    protected Integer page;

    @XmlElement
    protected Integer pageSize;

    @XmlElement
    protected Integer totalResults;

    @XmlElement
    protected Integer totalPages;

    @XmlElementWrapper(name = "products")
    @XmlElement(name = "product")
    protected List<ProductSummaryWrapper> products;

    @XmlElementWrapper(name = "searchFacets")
    @XmlElement(name = "searchFacet")
    protected List<SearchFacetWrapper> searchFacets;

    @Override
    public void wrap(ProductSearchResult model, HttpServletRequest request) {

        page = model.getPage();
        pageSize = model.getPageSize();
        totalResults = model.getTotalResults();
        totalPages = model.getTotalPages();

        if (model.getProducts() != null) {
            products = new ArrayList<ProductSummaryWrapper>();
            for (Product product : model.getProducts()) {
                ProductSummaryWrapper productSummary = (ProductSummaryWrapper) context.getBean(ProductSummaryWrapper.class.getName());
                productSummary.wrap(product, request);
                this.products.add(productSummary);
            }
        }

        if (model.getFacets() != null) {
            this.searchFacets = new ArrayList<SearchFacetWrapper>();
            for (SearchFacetDTO facet : model.getFacets()) {
                SearchFacetWrapper facetWrapper = (SearchFacetWrapper) context.getBean(SearchFacetWrapper.class.getName());
                facetWrapper.wrap(facet, request);
                this.searchFacets.add(facetWrapper);
            }
        }
    }

}
