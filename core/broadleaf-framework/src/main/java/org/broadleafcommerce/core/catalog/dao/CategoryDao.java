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
package org.broadleafcommerce.core.catalog.dao;


import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * {@code CategoryDao} provides persistence access to {@code Category} instances.
 *
 * @see Category
 * @see Product
 * @author Jeff Fischer
 */
public interface CategoryDao {

    /**
     * Retrieve a {@code Category} instance by its primary key
     *
     * @param categoryId the primary key of the {@code Category}
     * @return the {@code Category}  at the specified primary key
     */
    @Nonnull
    public Category readCategoryById(@Nonnull Long categoryId);

    /**
     * Retrieves a List of Category IDs
     *
     * @param categoryIds
     * @return
     */
    public List<Category> readCategoriesByIds(List<Long> categoryIds);

    /**
     * Retrieve a {@link Category} instance by the external id
     * @param externalId
     * @return
     */
    public Category readCategoryByExternalId(@Nonnull String externalId);

    /**
     * Retrieve a {@code Category} instance by its name.
     *
     * Broadleaf allows more than one category to have the same name. Calling
     * this method could produce an exception in such situations. Use
     * {@link #readCategoriesByName(String)} instead.
     *
     * @param categoryName the name of the category
     * @return the Category having the specified name
     */
    @Nonnull
    @Deprecated
    public Category readCategoryByName(@Nonnull String categoryName);

    /**
     * Retrieve a list of {@code Category} instances by name.
     *
     * @param categoryName the name to search by
     * @return the Category instances having the specified name
     */
    @Nonnull
    public List<Category> readCategoriesByName(@Nonnull String categoryName);

    @Nonnull
    public List<Category> readCategoriesByName(@Nonnull String categoryName, int limit, int offset);

    @Nonnull
    public List<Category> readCategoriesByNames(List<String> names);

    /**
     * Persist a {@code Category} instance to the datastore
     *
     * @param category the {@code Category} instance
     * @return the updated state of the passed in {@code Category} after being persisted
     */
    @Nonnull
    public Category save(@Nonnull Category category);

    /**
     * Retrieve all categories in the datastore
     *
     * @return a list of all the {@code Category} instances in the datastore
     */
    @Nonnull
    public List<Category> readAllCategories();

    /**
     * Retrieve a subset of all categories
     *
     * @param limit the maximum number of results, defaults to 20
     * @param offset the starting point in the record set, defaults to 0
     * @return
     */
    @Nonnull
    public List<Category> readAllCategories(@Nonnull int limit, @Nonnull int offset);

    /**
     * Retrieve all products in the datastore
     *
     * @return a list of all {@code Category} instances in the datastore, regardless of their category association
     */
    @Nonnull
    public List<Product> readAllProducts();

    @Nonnull
    public List<Product> readAllProducts(@Nonnull int limit, @Nonnull int offset);

    /**
     * Retrieve a list of all child categories of the passed in {@code Category} instance
     *
     * @param category the parent category
     * @return a list of all child categories
     */
    @Nonnull
    public List<Category> readAllSubCategories(@Nonnull Category category);

    /**
     * Retrieve a list of all child categories of the passed in {@code Category} instance
     *
     * @param category the parent category
     * @param limit the maximum number of results to return
     * @param offset the starting point in the record set
     * @return a list of all child categories
     */
    @Nonnull
    public List<Category> readAllSubCategories(@Nonnull Category category, @Nonnull int limit, @Nonnull int offset);

    /**
     * Removed the passed in {@code Category} instance from the datastore
     *
     * @param category the {@code Category} instance to remove
     */
    public void delete(@Nonnull Category category);

    /**
     * Create a new {@code Category} instance. The system will use the configuration in
     * {@code /BroadleafCommerce/core/BroadleafCommerceFramework/src/main/resources/bl-framework-applicationContext-entity.xml}
     * to determine which polymorphic version of {@code Category} to instantiate. To make Broadleaf instantiate your
     * extension of {@code Category} by default, include an entity configuration bean in your application context xml similar to:
     * <p>
     * {@code
     *     <bean id="blEntityConfiguration" class="org.broadleafcommerce.common.persistence.EntityConfiguration">
     *          <property name="entityContexts">
     *              <list>
     *                  <value>classpath:myCompany-applicationContext-entity.xml</value>
     *              </list>
     *          </property>
     *      </bean>
     * }
     * </p>
     * Declare the same key for your desired entity in your entity xml that is used in the Broadleaf entity xml, but change the value to the fully
     * qualified classname of your entity extension.
     *
     * @return a {@code Category} instance based on the Broadleaf entity configuration.
     */
    @Nonnull
    public Category create();

    /**
     * Retrieve a list of all active child categories of the passed in {@code Category} instance.
     * This method bases its search on a current time value. To make the retrieval of values more
     * efficient, the current time is cached for a configurable amount of time. See
     * {@link #getCurrentDateResolution()}
     *
     * @param category the parent category
     * @return a list of all active child categories
     */
    @Nonnull
    public List<Category> readActiveSubCategoriesByCategory(Category category);

    /**
     * Retrieve a list of all active child categories of the passed in {@code Category} instance.
     * This method bases its search on a current time value. To make the retrieval of values more
     * efficient, the current time is cached for a configurable amount of time. See
     * {@link #getCurrentDateResolution()}
     *
     * @param category the parent category
     * @param limit the maximum number of results to return
     * @param offset the starting point in the record set
     * @return a list of all active child categories
     */
    @Nonnull
    public List<Category> readActiveSubCategoriesByCategory(@Nonnull Category category, @Nonnull int limit, @Nonnull int offset);

    public Category findCategoryByURI(String uri);

    /**
     * Returns the number of milliseconds that the current date/time will be cached for queries before refreshing.
     * This aids in query caching, otherwise every query that utilized current date would be different and caching
     * would be ineffective.
     *
     * @return the milliseconds to cache the current date/time
     */
    public Long getCurrentDateResolution();

    /**
     * Sets the number of milliseconds that the current date/time will be cached for queries before refreshing.
     * This aids in query caching, otherwise every query that utilized current date would be different and caching
     * would be ineffective.
     *
     * @param currentDateResolution the milliseconds to cache the current date/time
     */
    public void setCurrentDateResolution(Long currentDateResolution);

}
