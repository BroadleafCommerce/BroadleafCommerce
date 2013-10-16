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

package org.broadleafcommerce.core.catalog.dao;

import org.broadleafcommerce.core.catalog.domain.CategoryProductXref;
import org.broadleafcommerce.core.catalog.domain.CategoryXref;
import org.broadleafcommerce.core.catalog.domain.CategoryXrefImpl;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * {@code CategoryXrefDao} provides persistence access to the relationship
 * between a category and its sub-categories. This includes an ordering field.
 *
 * @see CategoryXref
 * @author Jeff Fischer
 */
public interface CategoryXrefDao {

    /**
     * Retrieve all the category relationships for which the passed in
     * {@code Category} primary key is a parent
     *
     * @param categoryId the parent {@code Category} primary key
     * @return the list of child category relationships for the parent primary key
     */
    @Nonnull
    public List<CategoryXref> readXrefsByCategoryId(@Nonnull Long categoryId);

    /**
     * Retrieve all the category relationships for which the passed in
     * {@code Category} primary key is a sub category (or child)
     *
     * @param subCategoryId the sub-categories primary key
     * @return the list of category relationships for the sub-category primary key
     */
    @Nonnull
    public List<CategoryXref> readXrefsBySubCategoryId(@Nonnull Long subCategoryId);

    /**
     * Find a specific relationship between a parent categoy and sub-category (child)
     *
     * @param categoryId The primary key of the parent category
     * @param subCategoryId The primary key of the sub-category
     * @return The relationship between the parent and child categories
     */
    @Nonnull
    public CategoryXref readXrefByIds(@Nonnull Long categoryId, @Nonnull Long subCategoryId);

    /**
     * Persist the passed in category relationship to the datastore
     *
     * @param categoryXref the relationship between a parent and child category
     * @return the persisted relationship between a parent and child category
     */
    @Nonnull
    public CategoryXref save(@Nonnull CategoryXrefImpl categoryXref);

    /**
     * Remove the passed in category relationship from the datastore
     *
     * @param categoryXref the category relationship to remove
     */
    public void delete(@Nonnull CategoryXref categoryXref);

    /**
     * Persist the passed in category/product relationship to the datastore
     *
     * @param categoryProductXref the relationship between a category and product
     * @return the persisted relationship between a category and product
     */
    @Nonnull
    public CategoryProductXref save(CategoryProductXref categoryProductXref);
}
