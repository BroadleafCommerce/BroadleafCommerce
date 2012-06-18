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

package org.broadleafcommerce.core.catalog.dao;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.service.type.ProductType;

import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * {@code ProductDao} provides persistence access to {@code Product} instances
 *
 * @see Product
 * @author Jeff Fischer
 */
public interface ProductDao {

    /**
     * Retrieve a {@code Product} instance by its primary key
     *
     * @param productId the primary key of the product
     * @return the product instance at the specified primary key
     */
    @Nonnull
    public Product readProductById(@Nonnull Long productId);

    /**
     * Persist a {@code Product} instance to the datastore
     *
     * @param product the product instance
     * @return the updated state of the product instance after being persisted
     */
    @Nonnull
    public Product save(@Nonnull Product product);

    /**
     * Find all {@code Product} instances whose name starts with
     * or is equal to the passed in search parameter
     *
     * @param searchName the partial or whole name to match
     * @return the list of product instances that were search hits
     */
    @Nonnull
    public List<Product> readProductsByName(@Nonnull String searchName);

    /**
     * Find a subset of {@code Product} instances whose name starts with
     * or is equal to the passed in search parameter.  Res
     * @param searchName
     * @param limit the maximum number of results
     * @param offset the starting point in the record set
     * @return the list of product instances that fit the search criteria
     */
    @Nonnull
    public List<Product> readProductsByName(@Nonnull String searchName, @Nonnull int limit, @Nonnull int offset);

    /**
     * Find all products whose start and end dates are before and after the passed in
     * date and who are related to the given category
     *
     * @param categoryId the primary key of the category to whom the resulting product list should be related
     * @param currentDate the date for which the products should be checked against to determine their active state
     * @return the list of products qualified for the category and date
     */
    @Nonnull
    public List<Product> readActiveProductsByCategory(@Nonnull Long categoryId, @Nonnull Date currentDate);

    @Nonnull
    public List<Product> readActiveProductsByCategory(@Nonnull Long categoryId, @Nonnull Date currentDate, @Nonnull int limit, @Nonnull int offset);

    /**
     * Find all products related to the passed in category
     *
     * @param categoryId the primary key of the category to whom the resulting product list should be related
     * @return the list of products qualified for the category
     */
    @Nonnull
    public List<Product> readProductsByCategory(@Nonnull Long categoryId);

    /**
     * Find all products related to the passed in category
     *
     * @param categoryId the primary key of the category to whom the resulting product list should be related
     * @param limit the maximum number of results to return
     * @param offset the starting point in the record set
     * @return the list of products qualified for the category
     */
    @Nonnull
    public List<Product> readProductsByCategory(@Nonnull Long categoryId, @Nonnull int limit, @Nonnull int offset);

    /**
     * Remove the passed in product instance from the datastore
     *
     * @param product the product instance to remove
     */
    public void delete(@Nonnull Product product);

    /**
     * Create a new {@code Product} instance. The system will use the configuration in
     * {@code /BroadleafCommerce/core/BroadleafCommerceFramework/src/main/resources/bl-framework-applicationContext-entity.xml}
     * to determine which polymorphic version of {@code Product} to instantiate. To make Broadleaf instantiate your
     * extension of {@code Product} by default, include an entity configuration bean in your application context xml similar to:
     * <p>
     * {@code
     *     <bean id="blEntityConfiguration" class="org.broadleafcommerce.common.persistence.EntityConfiguration">
	 *	        <property name="entityContexts">
	 *		        <list>
	 *			        <value>classpath:myCompany-applicationContext-entity.xml</value>
	 *		        </list>
	 *	        </property>
	 *      </bean>
     * }
     * </p>
     * Declare the same key for your desired entity in your entity xml that is used in the Broadleaf entity xml, but change the value to the fully
     * qualified classname of your entity extension.
     *
     * @param productType the type of product you would like to create (presumably a Product or ProductSku instance). The getType method of {@code ProductType} provides the key for the entity configuration.
     * @return a {@code Product} instance based on the Broadleaf entity configuration.
     */
    public Product create(ProductType productType);

    /**
     * Returns all active ProductBundles whose automatic property is true.
     *
     * @return
     */
    public List<ProductBundle> readAutomaticProductBundles();

    /**
     * Look up a product that matches the given URI
     * 
     * @param uri - the relative URL to look up the Product by
     * @return List of products that match the passed in URI.
     * 
     */
	List<Product> findProductByURI(String key);
}
