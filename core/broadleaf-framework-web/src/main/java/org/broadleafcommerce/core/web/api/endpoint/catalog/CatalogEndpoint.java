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
package org.broadleafcommerce.core.web.api.endpoint.catalog;

import org.broadleafcommerce.core.catalog.domain.*;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.media.domain.Media;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
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
public class CatalogEndpoint {

    @Resource(name="blCatalogService")
    private CatalogService catalogService;

    /**
     * Search for {@code Product} by product id
     *
     * @param id the product id
     * @return the product instance with the given product id
     */
    @GET
    @Path("product/{id}")
    public Product findProductById(@PathParam("id") Long id) {
        return catalogService.findProductById(id);
    }

    /**
     * Search for {@code Product} instances whose name starts with
     * or is equal to the passed in product name.
     *
     * @param name
     * @param limit the maximum number of results, defaults to 20
     * @param offset the starting point in the record set, defaults to 1
     * @return the list of product instances that fit the search criteria
     */
    @GET
    @Path("products")
    public List<Product> findProductsByName(@QueryParam("name") String name, @QueryParam("limit") @DefaultValue("20") int limit, @QueryParam("offset") @DefaultValue("0") int offset) {
        if (name == null) {
            return catalogService.findAllProducts(limit, offset);
        }
        return catalogService.findProductsByName(name, limit, offset);
    }

    /**
     * Search for {@code Sku} instances for a given product
     *
     * @param id
     * @return the list of sku instances for the product
     */
    @GET
    @Path("product/{id}/skus")
    public List<Sku> findSkusByProductById(@PathParam("id") Long id) {
        Product product = catalogService.findProductById(id);
        return product.getAllSkus();
    }

    @GET
    @Path("categories")
    public List<Category> findAllCategories(@QueryParam("name") String name, @QueryParam("limit") @DefaultValue("20") int limit, @QueryParam("offset") @DefaultValue("0") int offset) {
        if (name != null) {
            return catalogService.findCategoriesByName(name, limit, offset);
        }
        return catalogService.findAllCategories(limit, offset);
    }

    @GET
    @Path("category/{id}/categories")
    public List<Category> findSubCategories(@PathParam("id") Long id, @QueryParam("limit") @DefaultValue("20") int limit, @QueryParam("offset") @DefaultValue("0") int offset, @QueryParam("active") @DefaultValue("false") boolean active) {
        Category category = catalogService.findCategoryById(id);
        if (category != null) {
            if (active) {
                return catalogService.findActiveSubCategoriesByCategory(category, limit, offset);
            }
            return catalogService.findAllSubCategories(category, limit, offset);
        }

        return null;
    }

    @GET
    @Path("category/{id}/activeSubcategories")
    public List<Category> findActiveSubCategories(@PathParam("id") Long id, @QueryParam("limit") @DefaultValue("20") int limit, @QueryParam("offset") @DefaultValue("0") int offset) {
        Category category = catalogService.findCategoryById(id);
        if (category != null) {
            return catalogService.findActiveSubCategoriesByCategory(category, limit, offset);
        }

        return null;
    }

    @GET
    @Path("category/{id}")
    public Category findCategoryById(@PathParam("id") Long id) {
        return catalogService.findCategoryById(id);
    }

    @GET
    @Path("category/{id}/products")
    public List<Product> findProductsForCategory(@PathParam("id") Long id, @QueryParam("limit") @DefaultValue("20") int limit, @QueryParam("offset") @DefaultValue("0") int offset, @QueryParam("activeOnly") @DefaultValue("false") boolean activeOnly) {
        Category category = catalogService.findCategoryById(id);
        if (category != null) {
            if (activeOnly) {
                return catalogService.findActiveProductsByCategory(category, new Date(), limit, offset);
            }
            return catalogService.findProductsForCategory(category, limit, offset);
        }
        return null;
    }

    @GET
    @Path("product/{id}/related-products/upsale")
    public List<RelatedProduct> findUpSaleProductsByProduct(@PathParam("id") Long id, @QueryParam("limit") @DefaultValue("20") int limit, @QueryParam("offset") @DefaultValue("0") int offset) {
        Product product = catalogService.findProductById(id);
        if (product != null) {
            return product.getUpSaleProducts();        
        }
        return null;
    }

    @GET
    @Path("product/{id}/related-products/crosssale")
    public List<RelatedProduct> findCrossSaleProductsByProduct(@PathParam("id") Long id, @QueryParam("limit") @DefaultValue("20") int limit, @QueryParam("offset") @DefaultValue("0") int offset) {
        Product product = catalogService.findProductById(id);
        if (product != null) {
            return product.getCrossSaleProducts();
        }
        return null;
    }

    @GET
    @Path("product/{id}/product-attributes")
    public List<ProductAttribute> findProductAttributesForProduct(@PathParam("id") Long id) {
        Product product = catalogService.findProductById(id);
        if (product != null) {
            return product.getProductAttributes();
        }
        return null;
    }

    @GET
    @Path("sku/{id}/sku-attributes")
    public List<SkuAttribute> findSkuAttributesForSku(@PathParam("id") Long id) {
        Sku sku = catalogService.findSkuById(id);
        if (sku != null) {
            return sku.getSkuAttributes();
        }
        return null;
    }

    @GET
    @Path("sku/{id}/media")
    public Map<String, Media> findMediaForSku(@PathParam("id") Long id) {
        Sku sku = catalogService.findSkuById(id);
        if (sku != null) {
            return sku.getSkuMedia();
        }
        return null;
    }

    @GET
    @Path("sku/{id}")
    public Sku findSkuById(@PathParam("id") Long id) {
        return catalogService.findSkuById(id);
    }

    @GET
    @Path("product/{id}/media")
    public Map<String, Media> findMediaForProduct(@PathParam("id") Long id) {
        Product product = catalogService.findProductById(id);
        if (product != null) {
            return product.getProductMedia();
        }
        return null;
    }

    @GET
    @Path("category/{id}/media")
    public Map<String, Media> findMediaForCategory(@PathParam("id") Long id) {
        Category category = catalogService.findCategoryById(id);
        if (category != null) {
            return category.getCategoryMedia();
        }
        return null;
    }

    @GET
    @Path("product/{id}/categories")
    public List<Category> findParentCategoriesForProduct(@PathParam("id") Long id) {
        Product product = catalogService.findProductById(id);
        if (product != null) {
            return product.getAllParentCategories();
        }
        return null;
    }

}

