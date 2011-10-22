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


import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;

import java.util.List;

/**
 * @author Jeff Fischer
 *
 * CategoryDao provides persistence access to Category instances.
 *
 * @see Category
 */
public interface CategoryDao {

    /**
     * Retrieve a Category instance by its primary key
     *
     * @param categoryId the primary key of the Category
     * @return the Category at the specified primary key
     */
    public Category readCategoryById(Long categoryId);

    /**
     * Retrieve a Category instance by its name
     *
     * @param categoryName the name of the category
     * @return the Category having the specified name
     */
    public Category readCategoryByName(String categoryName);

    public Category save(Category category);

    public List<Category> readAllCategories();

    public List<Product> readAllProducts();

    public List<Category> readAllSubCategories(Category category);
    
    public void delete(Category category); 
    
    public Category create();
}
