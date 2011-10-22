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
import org.broadleafcommerce.persistence.EntityConfiguration;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository("blCategoryDao")
public class CategoryDaoImpl implements CategoryDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    protected String queryCacheableKey = "org.hibernate.cacheable";

    @NotNull
    public Category save(@NotNull Category category) {
        return em.merge(category);
    }

    @NotNull
    public Category readCategoryById(@NotNull Long categoryId) {
        return (Category) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.core.catalog.domain.Category"), categoryId);
    }

    @NotNull
    public Category readCategoryByName(@NotNull String categoryName) {
        Query query = em.createNamedQuery("BC_READ_CATEGORY_BY_NAME");
        query.setParameter("categoryName", categoryName);
        query.setHint(getQueryCacheableKey(), true);
        return (Category)query.getSingleResult();
    }

    @NotNull
    public List<Category> readAllCategories() {
        Query query = em.createNamedQuery("BC_READ_ALL_CATEGORIES");
        query.setHint(getQueryCacheableKey(), true);
        return query.getResultList();
    }

    @NotNull
    public List<Product> readAllProducts() {
        Query query = em.createNamedQuery("BC_READ_ALL_PRODUCTS");
        return query.getResultList();
    }

    @NotNull
    public List<Category> readAllSubCategories(@NotNull Category category) {
        Query query = em.createNamedQuery("BC_READ_ALL_SUBCATEGORIES");
        query.setParameter("defaultParentCategory", category);
        return query.getResultList();
    }

    @NotNull
    public String getQueryCacheableKey() {
        return queryCacheableKey;
    }

    @NotNull
    public void setQueryCacheableKey(@NotNull String queryCacheableKey) {
        this.queryCacheableKey = queryCacheableKey;
    }
    
    public void delete(@NotNull Category category){
    	if (!em.contains(category)) {
    		category = readCategoryById(category.getId());
    	}
        em.remove(category);    	
    }

    @NotNull
    public Category create() {
        final Category category =  ((Category) entityConfiguration.createEntityInstance(Category.class.getName()));
        return category;
    }
    
}
