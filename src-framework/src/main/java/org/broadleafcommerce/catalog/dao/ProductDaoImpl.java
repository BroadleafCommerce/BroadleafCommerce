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
package org.broadleafcommerce.catalog.dao;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("blProductDao")
public class ProductDaoImpl implements ProductDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    protected String queryCacheableKey = "org.hibernate.cacheable";

    public Product save(Product product) {
        if (product.getId() == null) {
            em.persist(product);
        } else {
            product = em.merge(product);
        }
        return product;
    }

    @SuppressWarnings("unchecked")
    public Product readProductById(Long productId) {
        return (Product) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.catalog.domain.Product"), productId);
    }

    @SuppressWarnings("unchecked")
    public List<Product> readProductsByName(String searchName) {
        Query query = em.createNamedQuery("BC_READ_PRODUCTS_BY_NAME");
        query.setParameter("name", searchName + "%");
        query.setHint(getQueryCacheableKey(), true);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Product> readActiveProductsByCategory(Long categoryId, Date currentDate) {
        Query query = em.createNamedQuery("BC_READ_ACTIVE_PRODUCTS_BY_CATEGORY");
        query.setParameter("categoryId", categoryId);
        query.setParameter("currentDate", currentDate);
        query.setHint(getQueryCacheableKey(), true);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Product> readProductsBySku(Long skuId) {
        Query query = em.createNamedQuery("BC_READ_PRODUCTS_BY_SKU");
        query.setParameter("skuId", skuId);
        query.setHint(getQueryCacheableKey(), true);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Product> readActiveProductsBySku(Long skuId, Date currentDate) {
        Query query = em.createNamedQuery("BC_READ_ACTIVE_PRODUCTS_BY_SKU");
        query.setParameter("skuId", skuId);
        query.setParameter("currentDate", currentDate);
        query.setHint(getQueryCacheableKey(), true);
        return query.getResultList();
    }

    public String getQueryCacheableKey() {
        return queryCacheableKey;
    }

    public void setQueryCacheableKey(String queryCacheableKey) {
        this.queryCacheableKey = queryCacheableKey;
    }
}
