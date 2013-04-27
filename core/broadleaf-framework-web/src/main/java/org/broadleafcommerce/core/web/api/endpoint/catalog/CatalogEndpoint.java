/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.core.web.api.endpoint.catalog;

import org.broadleafcommerce.cms.file.service.StaticAssetService;
import org.broadleafcommerce.common.media.domain.Media;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryAttribute;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXref;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductAttribute;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.domain.RelatedProduct;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuAttribute;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.web.api.wrapper.CategoriesWrapper;
import org.broadleafcommerce.core.web.api.wrapper.CategoryAttributeWrapper;
import org.broadleafcommerce.core.web.api.wrapper.CategoryWrapper;
import org.broadleafcommerce.core.web.api.wrapper.MediaWrapper;
import org.broadleafcommerce.core.web.api.wrapper.ProductAttributeWrapper;
import org.broadleafcommerce.core.web.api.wrapper.ProductBundleWrapper;
import org.broadleafcommerce.core.web.api.wrapper.ProductWrapper;
import org.broadleafcommerce.core.web.api.wrapper.RelatedProductWrapper;
import org.broadleafcommerce.core.web.api.wrapper.SkuAttributeWrapper;
import org.broadleafcommerce.core.web.api.wrapper.SkuWrapper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This class exposes catalog services as RESTful APIs.  It is dependent on
 * a JAX-RS implementation such as Jersey.  This class has to be in a war, with
 * appropriate configuration to ensure that it is delegated requests from the
 * servlet.
 *
 * User: Kelly Tisdell
 */
@Component("blRestCatalogEndpoint")
@Scope("singleton")
@Path("/catalog/")
@Produces(value={MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes(value={MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class CatalogEndpoint implements ApplicationContextAware {

    @Resource(name="blCatalogService")
    private CatalogService catalogService;

    private ApplicationContext context;

    //We don't inject this here because of a few dependency issues. Instead, we look this up dynamically
    //using the ApplicationContext
    private StaticAssetService staticAssetService;

    /**
     * Search for {@code Product} by product id
     *
     * @param id the product id
     * @return the product instance with the given product id
     */
    @GET
    @Path("product/{id}")
    public ProductWrapper findProductById(@Context HttpServletRequest request, @PathParam("id") Long id) {
        Product product = catalogService.findProductById(id);
        if (product != null) {
            ProductWrapper wrapper;
            if (product instanceof ProductBundle) {
                wrapper = (ProductWrapper)context.getBean(ProductBundleWrapper.class.getName());
            } else {
                wrapper = (ProductWrapper)context.getBean(ProductWrapper.class.getName());
                
            }
            wrapper.wrap(product, request);
            return wrapper;
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    /**
     * Search for {@code Product} instances whose name starts with
     * or is equal to the passed in product name.
     *
     * @param name
     * @param limit the maximum number of results, defaults to 20
     * @param offset the starting point in the record set, defaults to 0
     * @return the list of product instances that fit the search criteria
     */
    @GET
    @Path("products")
    public List<ProductWrapper> findProductsByName(@Context HttpServletRequest request,
                                                   @QueryParam("name") String name,
                                                   @QueryParam("limit") @DefaultValue("20") int limit,
                                                   @QueryParam("offset") @DefaultValue("0") int offset) {
        List<Product> result;
        if (name == null) {
            result = catalogService.findAllProducts(limit, offset);
        } else {
            result = catalogService.findProductsByName(name, limit, offset);
        }

        List<ProductWrapper> out = new ArrayList<ProductWrapper>();
        if (result != null) {
            for (Product product : result) {
                ProductWrapper wrapper;
                if (product instanceof ProductBundle) {
                    wrapper = (ProductWrapper)context.getBean(ProductBundleWrapper.class.getName());
                } else {
                    wrapper = (ProductWrapper)context.getBean(ProductWrapper.class.getName());
                    
                }
                wrapper.wrap(product, request);
                out.add(wrapper);
            }
        }
        return out;
    }

    /**
     * Search for {@code Sku} instances for a given product
     *
     * @param id
     * @return the list of sku instances for the product
     */
    @GET
    @Path("product/{id}/skus")
    public List<SkuWrapper> findSkusByProductById(@Context HttpServletRequest request, @PathParam("id") Long id) {
        Product product = catalogService.findProductById(id);
        if (product != null) {
            List<Sku> skus = product.getAllSkus();
            List<SkuWrapper> out = new ArrayList<SkuWrapper>();
            if (skus != null) {
                for (Sku sku : skus) {
                    SkuWrapper wrapper = (SkuWrapper)context.getBean(SkuWrapper.class.getName());
                    wrapper.wrap(sku, request);
                    out.add(wrapper);
                }
                return out;
            }
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }
    
    @GET
    @Path("product/{id}/defaultSku")
    public SkuWrapper findDefaultSkuByProductId(@Context HttpServletRequest request, @PathParam("id") Long id) {
        Product product = catalogService.findProductById(id);
        if (product != null && product.getDefaultSku() != null) {
            SkuWrapper wrapper = (SkuWrapper)context.getBean(SkuWrapper.class.getName());
            wrapper.wrap(product.getDefaultSku(), request);
            return wrapper;
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @GET
    @Path("categories")
    public CategoriesWrapper findAllCategories(@Context HttpServletRequest request,
                                               @QueryParam("name") String name,
                                               @QueryParam("limit") @DefaultValue("20") int limit,
                                               @QueryParam("offset") @DefaultValue("0") int offset) {
        List<Category> categories;
        if (name != null) {
            categories = catalogService.findCategoriesByName(name, limit, offset);
        } else {
            categories = catalogService.findAllCategories(limit, offset);
        }
        CategoriesWrapper wrapper = (CategoriesWrapper)context.getBean(CategoriesWrapper.class.getName());
        wrapper.wrap(categories, request);
        return wrapper;
    }

    @GET
    @Path("category/{id}/categories")
    public CategoriesWrapper findSubCategories(@Context HttpServletRequest request,
                                               @PathParam("id") Long id,
                                               @QueryParam("limit") @DefaultValue("20") int limit,
                                               @QueryParam("offset") @DefaultValue("0") int offset,
                                               @QueryParam("active") @DefaultValue("true") boolean active) {
        Category category = catalogService.findCategoryById(id);
        if (category != null) {
            List<Category> categories;
            CategoriesWrapper wrapper = (CategoriesWrapper)context.getBean(CategoriesWrapper.class.getName());
            if (active) {
                categories = catalogService.findActiveSubCategoriesByCategory(category, limit, offset);
            } else {
                categories = catalogService.findAllSubCategories(category, limit, offset);
            }
            wrapper.wrap(categories, request);
            return wrapper;
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);

    }

    @GET
    @Path("category/{id}/activeSubcategories")
    public CategoriesWrapper findActiveSubCategories(@Context HttpServletRequest request,
                                                     @PathParam("id") Long id,
                                                     @QueryParam("limit") @DefaultValue("20") int limit,
                                                     @QueryParam("offset") @DefaultValue("0") int offset) {
        Category category = catalogService.findCategoryById(id);
        if (category != null) {
            List<Category> categories = catalogService.findActiveSubCategoriesByCategory(category, limit, offset);
            CategoriesWrapper wrapper = (CategoriesWrapper)context.getBean(CategoriesWrapper.class.getName());
            wrapper.wrap(categories, request);
            return wrapper;
        }

        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @GET
    @Path("category/{id}")
    public CategoryWrapper findCategoryById(@Context HttpServletRequest request,
                                            @PathParam("id") Long id,
                                            @QueryParam("productLimit") @DefaultValue("20") int productLimit,
                                            @QueryParam("productOffset") @DefaultValue("0") int productOffset,                                            @QueryParam("subcategoryLimit") @DefaultValue("20") int subcategoryLimit,
                                            @QueryParam("subcategoryOffset") @DefaultValue("0") int subcategoryOffset,
                                            @QueryParam("subcategoryDepth") @DefaultValue("1") int subcategoryDepth) {
        Category cat = catalogService.findCategoryById(id);
        if (cat != null) {

            //Explicitly setting these request attributes because the CategoryWrapper.wrap() method needs them
            request.setAttribute("productLimit", productLimit);
            request.setAttribute("productOffset", productOffset);
            request.setAttribute("subcategoryLimit", subcategoryLimit);
            request.setAttribute("subcategoryOffset", subcategoryOffset);
            request.setAttribute("subcategoryDepth", subcategoryDepth);

            CategoryWrapper wrapper = (CategoryWrapper)context.getBean(CategoryWrapper.class.getName());
            wrapper.wrap(cat, request);
            return wrapper;
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @GET
    @Path("category/{id}/product-attributes")
    public List<CategoryAttributeWrapper> findCategoryAttributesForCategory(@Context HttpServletRequest request,
                                                                         @PathParam("id") Long id) {
        Category category = catalogService.findCategoryById(id);
        if (category != null) {
            ArrayList<CategoryAttributeWrapper> out = new ArrayList<CategoryAttributeWrapper>();
            if (category.getCategoryAttributes() != null) {
                for (CategoryAttribute attribute : category.getCategoryAttributes()) {
                    CategoryAttributeWrapper wrapper = (CategoryAttributeWrapper)context.getBean(CategoryAttributeWrapper.class.getName());
                    wrapper.wrap(attribute, request);
                    out.add(wrapper);
                }
            }
            return out;
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }
    
    @GET
    @Path("category/{id}/products")
    public List<ProductWrapper> findProductsForCategory(@Context HttpServletRequest request,
                                                        @PathParam("id") Long id,
                                                        @QueryParam("limit") @DefaultValue("20") int limit,
                                                        @QueryParam("offset") @DefaultValue("0") int offset,
                                                        @QueryParam("activeOnly") @DefaultValue("true") boolean activeOnly) {
        Category category = catalogService.findCategoryById(id);
        if (category != null) {
            List<Product> products;
            ArrayList<ProductWrapper> out = new ArrayList<ProductWrapper>();
            if (activeOnly) {
                products = catalogService.findActiveProductsByCategory(category, new Date(), limit, offset);
            } else {
                products = catalogService.findProductsForCategory(category, limit, offset);
            }
            if (products != null) {
                for (Product product : products) {
                    ProductWrapper wrapper;
                    if (product instanceof ProductBundle) {
                        wrapper = (ProductWrapper)context.getBean(ProductBundleWrapper.class.getName());
                    } else {
                        wrapper = (ProductWrapper)context.getBean(ProductWrapper.class.getName());
                        
                    }
                    wrapper.wrap(product, request);
                    out.add(wrapper);
                }
            }
            return out;
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @GET
    @Path("product/{id}/related-products/upsale")
    public List<RelatedProductWrapper> findUpSaleProductsByProduct(@Context HttpServletRequest request,
                                                                   @PathParam("id") Long id,
                                                                   @QueryParam("limit") @DefaultValue("20") int limit,
                                                                   @QueryParam("offset") @DefaultValue("0") int offset) {
        Product product = catalogService.findProductById(id);
        if (product != null) {
            List<RelatedProductWrapper> out = new ArrayList<RelatedProductWrapper>();

            //TODO: Write a service method that accepts offset and limit
            List<RelatedProduct> relatedProds = product.getUpSaleProducts();
            if (relatedProds != null) {
                for (RelatedProduct prod : relatedProds) {
                    RelatedProductWrapper wrapper = (RelatedProductWrapper)context.getBean(RelatedProductWrapper.class.getName());
                    wrapper.wrap(prod,request);
                    out.add(wrapper);
                }
            }
            return out;
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @GET
    @Path("product/{id}/related-products/crosssale")
    public List<RelatedProductWrapper> findCrossSaleProductsByProduct(@Context HttpServletRequest request,
                                                                      @PathParam("id") Long id,
                                                                      @QueryParam("limit") @DefaultValue("20") int limit,
                                                                      @QueryParam("offset") @DefaultValue("0") int offset) {
        Product product = catalogService.findProductById(id);
        if (product != null) {
            List<RelatedProductWrapper> out = new ArrayList<RelatedProductWrapper>();

            //TODO: Write a service method that accepts offset and limit
            List<RelatedProduct> xSellProds = product.getCrossSaleProducts();
            if (xSellProds != null) {
                for (RelatedProduct prod : xSellProds) {
                    RelatedProductWrapper wrapper = (RelatedProductWrapper)context.getBean(RelatedProductWrapper.class.getName());
                    wrapper.wrap(prod, request);
                    out.add(wrapper);
                }
            }
            return out;
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }
    
    @GET
    @Path("product/{id}/product-attributes")
    public List<ProductAttributeWrapper> findProductAttributesForProduct(@Context HttpServletRequest request,
                                                                         @PathParam("id") Long id) {
        Product product = catalogService.findProductById(id);
        if (product != null) {
            ArrayList<ProductAttributeWrapper> out = new ArrayList<ProductAttributeWrapper>();
            if (product.getProductAttributes() != null) {
                for (Map.Entry<String, ProductAttribute> entry : product.getProductAttributes().entrySet()) {
                    ProductAttributeWrapper wrapper = (ProductAttributeWrapper)context.getBean(ProductAttributeWrapper.class.getName());
                    wrapper.wrap(entry.getValue(), request);
                    out.add(wrapper);
                }
            }
            return out;
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @GET
    @Path("sku/{id}/sku-attributes")
    public List<SkuAttributeWrapper> findSkuAttributesForSku(@Context HttpServletRequest request,
                                                             @PathParam("id") Long id) {
        Sku sku = catalogService.findSkuById(id);
        if (sku != null) {
            ArrayList<SkuAttributeWrapper> out = new ArrayList<SkuAttributeWrapper>();
            if (sku.getSkuAttributes() != null) {
                for (Map.Entry<String, SkuAttribute> entry : sku.getSkuAttributes().entrySet()) {
                    SkuAttributeWrapper wrapper = (SkuAttributeWrapper)context.getBean(SkuAttributeWrapper.class.getName());
                    wrapper.wrap(entry.getValue(), request);
                    out.add(wrapper);
                }
            }
            return out;
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @GET
    @Path("sku/{id}/media")
    public List<MediaWrapper> findMediaForSku(@Context HttpServletRequest request,
                                              @PathParam("id") Long id) {
        Sku sku = catalogService.findSkuById(id);
        if (sku != null) {
            List<MediaWrapper> medias = new ArrayList<MediaWrapper>();
            if (sku.getSkuMedia() != null && ! sku.getSkuMedia().isEmpty()) {
                for (Media media : sku.getSkuMedia().values()) {
                    MediaWrapper wrapper = (MediaWrapper)context.getBean(MediaWrapper.class.getName());
                    wrapper.wrap(media, request);
                    if (wrapper.isAllowOverrideUrl()){
                        wrapper.setUrl(getStaticAssetService().convertAssetPath(media.getUrl(), request.getContextPath(), request.isSecure()));
                    }
                    medias.add(wrapper);
                }
            }
            return medias;
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @GET
    @Path("sku/{id}")
    public SkuWrapper findSkuById(@Context HttpServletRequest request,
                                  @PathParam("id") Long id) {
        Sku sku = catalogService.findSkuById(id);
        if (sku != null) {
            SkuWrapper wrapper = (SkuWrapper)context.getBean(SkuWrapper.class.getName());
            wrapper.wrap(sku, request);
            return wrapper;
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @GET
    @Path("product/{id}/media")
    public List<MediaWrapper> findMediaForProduct(@Context HttpServletRequest request,
                                                  @PathParam("id") Long id) {
        Product product = catalogService.findProductById(id);
        if (product != null) {
            ArrayList<MediaWrapper> out = new ArrayList<MediaWrapper>();
            Map<String, Media> media = product.getMedia();
            if (media != null) {
                for (Media med : media.values()) {
                    MediaWrapper wrapper = (MediaWrapper)context.getBean(MediaWrapper.class.getName());
                    wrapper.wrap(med, request);
                    if (wrapper.isAllowOverrideUrl()){
                        wrapper.setUrl(getStaticAssetService().convertAssetPath(med.getUrl(), request.getContextPath(), request.isSecure()));
                    }
                    out.add(wrapper);
                }
            }
            return out;
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @GET
    @Path("category/{id}/media")
    public List<MediaWrapper> findMediaForCategory(@Context HttpServletRequest request,
                                                   @PathParam("id") Long id) {
        Category category = catalogService.findCategoryById(id);
        if (category != null) {
            ArrayList<MediaWrapper> out = new ArrayList<MediaWrapper>();
            Map<String, Media> media = category.getCategoryMedia();
            for (Media med : media.values()) {
                MediaWrapper wrapper = (MediaWrapper)context.getBean(MediaWrapper.class.getName());
                wrapper.wrap(med, request);
                out.add(wrapper);
            }
            return out;
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @GET
    @Path("product/{id}/categories")
    public CategoriesWrapper findParentCategoriesForProduct(@Context HttpServletRequest request,
                                                            @PathParam("id") Long id) {
        Product product = catalogService.findProductById(id);
        if (product != null) {
            CategoriesWrapper wrapper = (CategoriesWrapper)context.getBean(CategoriesWrapper.class.getName());
            List<Category> categories = new ArrayList<Category>();
            for (CategoryProductXref categoryXref : product.getAllParentCategoryXrefs()) {
                categories.add(categoryXref.getCategory());
            }
            wrapper.wrap(categories, request);
            return wrapper;
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    private StaticAssetService getStaticAssetService() {
        if (staticAssetService == null) {
            staticAssetService = (StaticAssetService)this.context.getBean("blStaticAssetService");
        }
        return staticAssetService;
    }
}

