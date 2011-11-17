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
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * {@inheritDoc}
 *
 * @author Jeff Fischer
 */
@Repository("blCategoryDao")
public class CategoryDaoImpl implements CategoryDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public Category save(Category category) {
        return em.merge(category);
    }

    @Override
    public Category readCategoryById(Long categoryId) {
        return (Category) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.core.catalog.domain.Category"), categoryId);
    }

    @Override
    @Deprecated
    public Category readCategoryByName(String categoryName) {
        Query query = em.createNamedQuery("BC_READ_CATEGORY_BY_NAME");
        query.setParameter("categoryName", categoryName);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return (Category)query.getSingleResult();
    }

    @Override
    public List<Category> readCategoriesByName(String categoryName) {
        TypedQuery<Category> query = em.createNamedQuery("BC_READ_CATEGORY_BY_NAME", Category.class);
        query.setParameter("categoryName", categoryName);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }

    @Override
    public List<Category> readAllCategories() {
        TypedQuery<Category> query = em.createNamedQuery("BC_READ_ALL_CATEGORIES", Category.class);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }

    @Override
    public List<Product> readAllProducts() {
        TypedQuery<Product> query = em.createNamedQuery("BC_READ_ALL_PRODUCTS", Product.class);
        return query.getResultList();
    }

    @Override
    public List<Category> readAllSubCategories(Category category) {
        TypedQuery<Category> query = em.createNamedQuery("BC_READ_ALL_SUBCATEGORIES", Category.class);
        query.setParameter("defaultParentCategory", category);
        return query.getResultList();
    }

    @Override
    public void delete(Category category){
    	if (!em.contains(category)) {
    		category = readCategoryById(category.getId());
    	}
        em.remove(category);    	
    }

    @Override
    public Category create() {
        return (Category) entityConfiguration.createEntityInstance(Category.class.getName());
    }
    
}
