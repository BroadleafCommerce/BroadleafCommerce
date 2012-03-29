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
package org.broadleafcommerce.core.web.api.catalog;

import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * This class exposes catalog services as RESTful APIs.  It is dependent on
 * a JAX-RS implementation such as Jersey.  This class has to be in a war, with
 * appropriate configuration to ensure that it is delegated requests from the
 * servlet.
 *
 * User: Kelly Tisdell
 */
@Path("/catalog/")
@Component("blRestCatalogEndpoint")
@Scope("singleton")
@Produces(value={MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes(value={MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class CatalogEndpoint {

    @Resource(name="blCatalogService")
    private CatalogService catalogService;

    @GET
    @Path("product/{id}")
    public Product findProductById(@PathParam("id") Long productId) {
        return catalogService.findProductById(productId);
    }

    @GET
    @Path("product/name/{name}")
    public List<Product> findProductByName(@PathParam("name") String name) {
        return catalogService.findProductsByName(name);
    }

    @GET
    @Path("product/{id}/skus")
    public List<Sku> findSkusByProductById(@PathParam("id") Long productId) {
        Product product = catalogService.findProductById(productId);
        return product.getAllSkus();
    }

    @GET
    @Path("category/name/{name}")
    public List<Category> findCategoryByName(@PathParam("name") String name) {
        return catalogService.findCategoriesByName(name);
    }

    @GET
    @Path("category/{id}/allSubcategories")
    public List<Category> findSubCategories(@PathParam("id") Long id) {
        Category category = catalogService.findCategoryById(id);
        if (category != null) {
            return catalogService.findAllSubCategories(category);
        }

        return null;
    }

    @GET
    @Path("category/{id}/activeSubcategories")
    public List<Category> findActiveSubCategories(@PathParam("id") Long id) {
        Category category = catalogService.findCategoryById(id);
        if (category != null) {
            return catalogService.findActiveSubCategoriesByCategory(category);
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
    public List<Product> findProductsForCategory(@PathParam("id") Long id) {
        Category category = catalogService.findCategoryById(id);
        if (category != null) {
            return catalogService.findProductsForCategory(category);
        }
        return null;
    }
}
