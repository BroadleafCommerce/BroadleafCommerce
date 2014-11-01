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
package org.broadleafcommerce.core.web.api.endpoint.catalog;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.file.service.StaticAssetPathService;
import org.broadleafcommerce.common.media.domain.Media;
import org.broadleafcommerce.common.security.service.ExploitProtectionService;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryAttribute;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXref;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductAttribute;
import org.broadleafcommerce.core.catalog.domain.RelatedProduct;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuAttribute;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.inventory.service.InventoryService;
import org.broadleafcommerce.core.search.domain.ProductSearchCriteria;
import org.broadleafcommerce.core.search.domain.ProductSearchResult;
import org.broadleafcommerce.core.search.domain.SearchFacetDTO;
import org.broadleafcommerce.core.search.service.SearchService;
import org.broadleafcommerce.core.web.api.BroadleafWebServicesException;
import org.broadleafcommerce.core.web.api.endpoint.BaseEndpoint;
import org.broadleafcommerce.core.web.api.wrapper.CategoriesWrapper;
import org.broadleafcommerce.core.web.api.wrapper.CategoryAttributeWrapper;
import org.broadleafcommerce.core.web.api.wrapper.CategoryWrapper;
import org.broadleafcommerce.core.web.api.wrapper.InventoryWrapper;
import org.broadleafcommerce.core.web.api.wrapper.MediaWrapper;
import org.broadleafcommerce.core.web.api.wrapper.ProductAttributeWrapper;
import org.broadleafcommerce.core.web.api.wrapper.ProductWrapper;
import org.broadleafcommerce.core.web.api.wrapper.RelatedProductWrapper;
import org.broadleafcommerce.core.web.api.wrapper.SearchResultsWrapper;
import org.broadleafcommerce.core.web.api.wrapper.SkuAttributeWrapper;
import org.broadleafcommerce.core.web.api.wrapper.SkuWrapper;
import org.broadleafcommerce.core.web.service.SearchFacetDTOService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

/**
 * This class exposes catalog services as RESTful APIs.  It is dependent on
 * a JAX-RS implementation such as Jersey.  This class must be extended, with appropriate JAX-RS 
 * annotations, such as: <br></br> 
 * 
 * <code>javax.ws.rs.@Scope</code> <br></br> 
 * <code>javax.ws.rs.@Path</code> <br></br> 
 * <code>javax.ws.rs.@Produces</code> <br></br> 
 * <code>javax.ws.rs.@Consumes</code> <br></br> 
 * <code>javax.ws.rs.@Context</code> <br></br> 
 * etc... <br></br>
 * 
 * ... in the subclass.  The subclass must also be a Spring Bean.  The subclass can then override 
 * the methods, and specify custom inputs and outputs.  It will also specify 
 * <code>javax.ws.rs.@Path annotations</code>, <code>javax.ws.rs.@Context</code>, 
 * <code>javax.ws.rs.@PathParam</code>, <code>javax.ws.rs.@QueryParam</code>, 
 * <code>javax.ws.rs.@GET</code>, <code>javax.ws.rs.@POST</code>, etc...  Essentially, the subclass 
 * will override and extend the methods of this class, add new methods, and control the JAX-RS behavior 
 * using annotations according to the JAX-RS specification.
 *
 * User: Kelly Tisdell
 */
public abstract class CatalogEndpoint extends BaseEndpoint {

    @Resource(name="blCatalogService")
    protected CatalogService catalogService;

    @Resource(name = "blSearchService")
    protected SearchService searchService;

    @Resource(name = "blSearchFacetDTOService")
    protected SearchFacetDTOService facetService;

    @Resource(name = "blExploitProtectionService")
    protected ExploitProtectionService exploitProtectionService;

    @Resource(name = "blStaticAssetPathService")
    protected StaticAssetPathService staticAssetPathService;
    
    @Resource(name = "blInventoryService")
    protected InventoryService inventoryService;

    /**
     * Search for {@code Product} by product id
     *
     * @param id the product id
     * @return the product instance with the given product id
     */
    public ProductWrapper findProductById(HttpServletRequest request, Long id) {
        Product product = catalogService.findProductById(id);
        if (product != null) {
            ProductWrapper wrapper;
            wrapper = (ProductWrapper) context.getBean(ProductWrapper.class.getName());
            wrapper.wrapDetails(product, request);
            return wrapper;
        }
        throw BroadleafWebServicesException.build(Response.Status.NOT_FOUND.getStatusCode())
                .addMessage(BroadleafWebServicesException.PRODUCT_NOT_FOUND, id);
    }

    /**
     * This uses Broadleaf's search service to search for products within a category.
     * @param request
     * @param q
     * @param categoryId
     * @param pageSize
     * @param page
     * @return
     */
    public SearchResultsWrapper findProductsByCategoryAndQuery(HttpServletRequest request,
            Long categoryId,
            String q,
            Integer pageSize,
            Integer page) {
        try {
            if (StringUtils.isNotEmpty(q)) {
                q = StringUtils.trim(q);
                q = exploitProtectionService.cleanString(q);
            } else {
                throw BroadleafWebServicesException.build(Response.Status.BAD_REQUEST.getStatusCode())
                        .addMessage(BroadleafWebServicesException.SEARCH_QUERY_EMPTY);
            }
        } catch (ServiceException e) {
            throw BroadleafWebServicesException.build(Response.Status.BAD_REQUEST.getStatusCode())
                    .addMessage(BroadleafWebServicesException.SEARCH_QUERY_MALFORMED, q);
        }

        if (categoryId == null) {
            throw BroadleafWebServicesException.build(Response.Status.BAD_REQUEST.getStatusCode())
                    .addMessage(BroadleafWebServicesException.INVALID_CATEGORY_ID, categoryId);
        }

        Category category = null;
        category = catalogService.findCategoryById(categoryId);
        if (category == null) {
            throw BroadleafWebServicesException.build(Response.Status.BAD_REQUEST.getStatusCode())
                    .addMessage(BroadleafWebServicesException.INVALID_CATEGORY_ID, categoryId);
        }

        List<SearchFacetDTO> availableFacets = getSearchService().getSearchFacets();
        ProductSearchCriteria searchCriteria = facetService.buildSearchCriteria(request, availableFacets);
        try {
            ProductSearchResult result = null;
            result = getSearchService().findProductsByCategoryAndQuery(category, q, searchCriteria);
            facetService.setActiveFacetResults(result.getFacets(), request);

            SearchResultsWrapper wrapper = (SearchResultsWrapper) context.getBean(SearchResultsWrapper.class.getName());
            wrapper.wrapDetails(result, request);
            return wrapper;
        } catch (ServiceException e) {
            throw BroadleafWebServicesException.build(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
    }

    /**
     * Queries the products. The parameter q, which represents the query, is required. It can be any 
     * string, but is typically a name or keyword, similar to a search engine search.
     * @param request
     * @param q
     * @param pageSize
     * @param page
     * @return
     */
    public SearchResultsWrapper findProductsByQuery(HttpServletRequest request,
            String q,
            Integer pageSize,
            Integer page) {
        try {
            if (StringUtils.isNotEmpty(q)) {
                q = StringUtils.trim(q);
                q = exploitProtectionService.cleanString(q);
            } else {
                throw BroadleafWebServicesException.build(Response.Status.BAD_REQUEST.getStatusCode())
                        .addMessage(BroadleafWebServicesException.SEARCH_QUERY_EMPTY);
            }
        } catch (ServiceException e) {
            throw BroadleafWebServicesException.build(Response.Status.BAD_REQUEST.getStatusCode())
                    .addMessage(BroadleafWebServicesException.SEARCH_QUERY_MALFORMED, q);
        }

        List<SearchFacetDTO> availableFacets = getSearchService().getSearchFacets();
        ProductSearchCriteria searchCriteria = facetService.buildSearchCriteria(request, availableFacets);
        try {
            ProductSearchResult result = null;
            result = getSearchService().findProductsByQuery(q, searchCriteria);
            facetService.setActiveFacetResults(result.getFacets(), request);

            SearchResultsWrapper wrapper = (SearchResultsWrapper) context.getBean(SearchResultsWrapper.class.getName());
            wrapper.wrapDetails(result, request);
            return wrapper;
        } catch (ServiceException e) {
            throw BroadleafWebServicesException.build(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                    .addMessage(BroadleafWebServicesException.SEARCH_ERROR);

        }
    }

    /**
     * Search for {@code Sku} instances for a given product
     *
     * @param id
     * @return the list of sku instances for the product
     */
    public List<SkuWrapper> findSkusByProductById(HttpServletRequest request, Long id) {
        Product product = catalogService.findProductById(id);
        if (product != null) {
            List<Sku> skus = product.getAllSkus();
            List<SkuWrapper> out = new ArrayList<SkuWrapper>();
            if (skus != null) {
                for (Sku sku : skus) {
                    SkuWrapper wrapper = (SkuWrapper)context.getBean(SkuWrapper.class.getName());
                    wrapper.wrapSummary(sku, request);
                    out.add(wrapper);
                }
                return out;
            }
        }
        throw BroadleafWebServicesException.build(Response.Status.NOT_FOUND.getStatusCode())
                .addMessage(BroadleafWebServicesException.PRODUCT_NOT_FOUND, id);
    }
    
    public SkuWrapper findDefaultSkuByProductId(HttpServletRequest request, Long id) {
        Product product = catalogService.findProductById(id);
        if (product != null && product.getDefaultSku() != null) {
            SkuWrapper wrapper = (SkuWrapper)context.getBean(SkuWrapper.class.getName());
            wrapper.wrapDetails(product.getDefaultSku(), request);
            return wrapper;
        }
        throw BroadleafWebServicesException.build(Response.Status.NOT_FOUND.getStatusCode())
                .addMessage(BroadleafWebServicesException.PRODUCT_NOT_FOUND, id);
    }

    public CategoriesWrapper findAllCategories(HttpServletRequest request,
            String name,
            int limit,
            int offset) {
        List<Category> categories;
        if (name != null) {
            categories = catalogService.findCategoriesByName(name, limit, offset);
        } else {
            categories = catalogService.findAllCategories(limit, offset);
        }
        CategoriesWrapper wrapper = (CategoriesWrapper)context.getBean(CategoriesWrapper.class.getName());
        wrapper.wrapDetails(categories, request);
        return wrapper;
    }

    public CategoriesWrapper findSubCategories(HttpServletRequest request,
            Long id,
            int limit,
            int offset,
            boolean active) {
        Category category = catalogService.findCategoryById(id);
        if (category != null) {
            List<Category> categories;
            CategoriesWrapper wrapper = (CategoriesWrapper)context.getBean(CategoriesWrapper.class.getName());
            if (active) {
                categories = catalogService.findActiveSubCategoriesByCategory(category, limit, offset);
            } else {
                categories = catalogService.findAllSubCategories(category, limit, offset);
            }
            wrapper.wrapDetails(categories, request);
            return wrapper;
        }
        throw BroadleafWebServicesException.build(Response.Status.NOT_FOUND.getStatusCode())
                .addMessage(BroadleafWebServicesException.CATEGORY_NOT_FOUND, id);

    }

    public CategoriesWrapper findActiveSubCategories(HttpServletRequest request,
            Long id,
            int limit,
            int offset) {
        return findSubCategories(request, id, limit, offset, true);
    }

    public CategoryWrapper findCategoryById(HttpServletRequest request,
            Long id,
            int productLimit,
            int productOffset,
            int subcategoryLimit,
            int subcategoryOffset) {
        Category cat = catalogService.findCategoryById(id);
        if (cat != null) {

            //Explicitly setting these request attributes because the CategoryWrapper.wrap() method needs them
            request.setAttribute("productLimit", productLimit);
            request.setAttribute("productOffset", productOffset);
            request.setAttribute("subcategoryLimit", subcategoryLimit);
            request.setAttribute("subcategoryOffset", subcategoryOffset);

            CategoryWrapper wrapper = (CategoryWrapper)context.getBean(CategoryWrapper.class.getName());
            wrapper.wrapDetails(cat, request);
            return wrapper;
        }
        throw BroadleafWebServicesException.build(Response.Status.NOT_FOUND.getStatusCode())
                .addMessage(BroadleafWebServicesException.CATEGORY_NOT_FOUND, id);
    }

    /**
     * Allows you to search for a category by ID or by name.
     * @param request
     * @param searchParameter
     * @param productLimit
     * @param productOffset
     * @param subcategoryLimit
     * @param subcategoryOffset
     * @return
     */
    public CategoryWrapper findCategoryByIdOrName(HttpServletRequest request,
            String searchParameter,
            int productLimit,
            int productOffset,
            int subcategoryLimit,
            int subcategoryOffset) {

        Category cat = null;

        if (searchParameter != null) {
            try {
                cat = catalogService.findCategoryById(Long.parseLong(searchParameter));
            } catch (NumberFormatException e) {
                List<Category> categories = catalogService.findCategoriesByName(searchParameter);
                if (categories != null && !categories.isEmpty()) {
                    cat = categories.get(0);
                }
            }
        }
        if (cat != null) {

            //Explicitly setting these request attributes because the CategoryWrapper.wrap() method needs them
            request.setAttribute("productLimit", productLimit);
            request.setAttribute("productOffset", productOffset);
            request.setAttribute("subcategoryLimit", subcategoryLimit);
            request.setAttribute("subcategoryOffset", subcategoryOffset);

            CategoryWrapper wrapper = (CategoryWrapper) context.getBean(CategoryWrapper.class.getName());
            wrapper.wrapDetails(cat, request);
            return wrapper;
        }
        throw BroadleafWebServicesException.build(Response.Status.NOT_FOUND.getStatusCode())
                .addMessage(BroadleafWebServicesException.CATEGORY_NOT_FOUND, searchParameter);
    }

    public List<CategoryAttributeWrapper> findCategoryAttributesForCategory(HttpServletRequest request,
            Long id) {
        Category category = catalogService.findCategoryById(id);
        if (category != null) {
            ArrayList<CategoryAttributeWrapper> out = new ArrayList<CategoryAttributeWrapper>();
            if (category.getCategoryAttributes() != null) {
                for (CategoryAttribute attribute : category.getCategoryAttributes()) {
                    CategoryAttributeWrapper wrapper = (CategoryAttributeWrapper)context.getBean(CategoryAttributeWrapper.class.getName());
                    wrapper.wrapSummary(attribute, request);
                    out.add(wrapper);
                }
            }
            return out;
        }
        throw BroadleafWebServicesException.build(Response.Status.NOT_FOUND.getStatusCode())
                .addMessage(BroadleafWebServicesException.CATEGORY_NOT_FOUND, id);
    }

    public List<RelatedProductWrapper> findUpSaleProductsByProduct(HttpServletRequest request,
            Long id,
            int limit,
            int offset) {
        Product product = catalogService.findProductById(id);
        if (product != null) {
            List<RelatedProductWrapper> out = new ArrayList<RelatedProductWrapper>();

            //TODO: Write a service method that accepts offset and limit
            List<RelatedProduct> relatedProds = product.getUpSaleProducts();
            if (relatedProds != null) {
                for (RelatedProduct prod : relatedProds) {
                    RelatedProductWrapper wrapper = (RelatedProductWrapper)context.getBean(RelatedProductWrapper.class.getName());
                    wrapper.wrapSummary(prod, request);
                    out.add(wrapper);
                }
            }
            return out;
        }
        throw BroadleafWebServicesException.build(Response.Status.NOT_FOUND.getStatusCode())
                .addMessage(BroadleafWebServicesException.PRODUCT_NOT_FOUND, id);
    }

    public List<RelatedProductWrapper> findCrossSaleProductsByProduct(HttpServletRequest request,
            Long id,
            int limit,
            int offset) {
        Product product = catalogService.findProductById(id);
        if (product != null) {
            List<RelatedProductWrapper> out = new ArrayList<RelatedProductWrapper>();

            //TODO: Write a service method that accepts offset and limit
            List<RelatedProduct> xSellProds = product.getCrossSaleProducts();
            if (xSellProds != null) {
                for (RelatedProduct prod : xSellProds) {
                    RelatedProductWrapper wrapper = (RelatedProductWrapper)context.getBean(RelatedProductWrapper.class.getName());
                    wrapper.wrapSummary(prod, request);
                    out.add(wrapper);
                }
            }
            return out;
        }
        throw BroadleafWebServicesException.build(Response.Status.NOT_FOUND.getStatusCode())
                .addMessage(BroadleafWebServicesException.PRODUCT_NOT_FOUND, id);
    }
    
    public List<ProductAttributeWrapper> findProductAttributesForProduct(HttpServletRequest request,
            Long id) {
        Product product = catalogService.findProductById(id);
        if (product != null) {
            ArrayList<ProductAttributeWrapper> out = new ArrayList<ProductAttributeWrapper>();
            if (product.getProductAttributes() != null) {
                for (Map.Entry<String, ProductAttribute> entry : product.getProductAttributes().entrySet()) {
                    ProductAttributeWrapper wrapper = (ProductAttributeWrapper)context.getBean(ProductAttributeWrapper.class.getName());
                    wrapper.wrapSummary(entry.getValue(), request);
                    out.add(wrapper);
                }
            }
            return out;
        }
        throw BroadleafWebServicesException.build(Response.Status.NOT_FOUND.getStatusCode())
                .addMessage(BroadleafWebServicesException.PRODUCT_NOT_FOUND, id);
    }

    public List<SkuAttributeWrapper> findSkuAttributesForSku(HttpServletRequest request,
            Long id) {
        Sku sku = catalogService.findSkuById(id);
        if (sku != null) {
            ArrayList<SkuAttributeWrapper> out = new ArrayList<SkuAttributeWrapper>();
            if (sku.getSkuAttributes() != null) {
                for (Map.Entry<String, SkuAttribute> entry : sku.getSkuAttributes().entrySet()) {
                    SkuAttributeWrapper wrapper = (SkuAttributeWrapper)context.getBean(SkuAttributeWrapper.class.getName());
                    wrapper.wrapSummary(entry.getValue(), request);
                    out.add(wrapper);
                }
            }
            return out;
        }
        throw BroadleafWebServicesException.build(Response.Status.NOT_FOUND.getStatusCode())
                .addMessage(BroadleafWebServicesException.SKU_NOT_FOUND, id);
    }

    public List<MediaWrapper> findMediaForSku(HttpServletRequest request,
            Long id) {
        Sku sku = catalogService.findSkuById(id);
        if (sku != null) {
            List<MediaWrapper> medias = new ArrayList<MediaWrapper>();
            if (sku.getSkuMedia() != null && ! sku.getSkuMedia().isEmpty()) {
                for (Media media : sku.getSkuMedia().values()) {
                    MediaWrapper wrapper = (MediaWrapper)context.getBean(MediaWrapper.class.getName());
                    wrapper.wrapSummary(media, request);
                    if (wrapper.isAllowOverrideUrl()){
                        wrapper.setUrl(staticAssetPathService.convertAssetPath(media.getUrl(), request.getContextPath(), request.isSecure()));
                    }
                    medias.add(wrapper);
                }
            }
            return medias;
        }
        throw BroadleafWebServicesException.build(Response.Status.NOT_FOUND.getStatusCode())
                .addMessage(BroadleafWebServicesException.SKU_NOT_FOUND, id);
    }

    public SkuWrapper findSkuById(HttpServletRequest request,
            Long id) {
        Sku sku = catalogService.findSkuById(id);
        if (sku != null) {
            SkuWrapper wrapper = (SkuWrapper)context.getBean(SkuWrapper.class.getName());
            wrapper.wrapDetails(sku, request);
            return wrapper;
        }
        throw BroadleafWebServicesException.build(Response.Status.NOT_FOUND.getStatusCode())
                .addMessage(BroadleafWebServicesException.SKU_NOT_FOUND, id);
    }
    
    public List<InventoryWrapper> findInventoryForSkus(HttpServletRequest request, List<Long> ids) {
        List<Sku> skus = catalogService.findSkusByIds(ids);
        if (CollectionUtils.isNotEmpty(skus)) {
            Map<Sku, Integer> quantities = inventoryService.retrieveQuantitiesAvailable(new HashSet<Sku>(skus));
            List<InventoryWrapper> out = new ArrayList<InventoryWrapper>();
            for (Map.Entry<Sku, Integer> entry : quantities.entrySet()) {
                InventoryWrapper wrapper = (InventoryWrapper)context.getBean(InventoryWrapper.class.getName());
                wrapper.wrapSummary(entry.getKey(), entry.getValue(), request);
                out.add(wrapper);
            }
            return out;
        }
        throw BroadleafWebServicesException.build(Response.Status.NOT_FOUND.getStatusCode())
                .addMessage(BroadleafWebServicesException.SKU_NOT_FOUND, skus.toArray());
    }

    public List<MediaWrapper> findMediaForProduct(HttpServletRequest request,
            Long id) {
        Product product = catalogService.findProductById(id);
        if (product != null) {
            ArrayList<MediaWrapper> out = new ArrayList<MediaWrapper>();
            Map<String, Media> media = product.getMedia();
            if (media != null) {
                for (Media med : media.values()) {
                    MediaWrapper wrapper = (MediaWrapper)context.getBean(MediaWrapper.class.getName());
                    wrapper.wrapSummary(med, request);
                    if (wrapper.isAllowOverrideUrl()){
                        wrapper.setUrl(staticAssetPathService.convertAssetPath(med.getUrl(), request.getContextPath(), request.isSecure()));
                    }
                    out.add(wrapper);
                }
            }
            return out;
        }
        throw BroadleafWebServicesException.build(Response.Status.NOT_FOUND.getStatusCode())
                .addMessage(BroadleafWebServicesException.PRODUCT_NOT_FOUND, id);
    }

    public List<MediaWrapper> findMediaForCategory(HttpServletRequest request,
            Long id) {
        Category category = catalogService.findCategoryById(id);
        if (category != null) {
            ArrayList<MediaWrapper> out = new ArrayList<MediaWrapper>();
            Map<String, Media> media = category.getCategoryMedia();
            for (Media med : media.values()) {
                MediaWrapper wrapper = (MediaWrapper)context.getBean(MediaWrapper.class.getName());
                wrapper.wrapSummary(med, request);
                out.add(wrapper);
            }
            return out;
        }
        throw BroadleafWebServicesException.build(Response.Status.NOT_FOUND.getStatusCode())
                .addMessage(BroadleafWebServicesException.CATEGORY_NOT_FOUND, id);
    }

    public CategoriesWrapper findParentCategoriesForProduct(HttpServletRequest request,
            Long id) {
        Product product = catalogService.findProductById(id);
        if (product != null) {
            CategoriesWrapper wrapper = (CategoriesWrapper)context.getBean(CategoriesWrapper.class.getName());
            List<Category> categories = new ArrayList<Category>();
            for (CategoryProductXref categoryXref : product.getAllParentCategoryXrefs()) {
                categories.add(categoryXref.getCategory());
            }
            wrapper.wrapDetails(categories, request);
            return wrapper;
        }
        throw BroadleafWebServicesException.build(Response.Status.NOT_FOUND.getStatusCode())
                .addMessage(BroadleafWebServicesException.PRODUCT_NOT_FOUND, id);
    }

    protected SearchService getSearchService() {
        return searchService;
    }
}

