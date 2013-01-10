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
import org.broadleafcommerce.core.catalog.domain.ProductSku;
import org.broadleafcommerce.core.catalog.service.type.ProductType;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

/**
 * @author Jeff Fischer
 */
@Repository("blProductDao")
public class ProductDaoImpl implements ProductDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    protected Long currentDateResolution = 10000L;
    private Date currentDate = SystemTime.asDate();

    @Override
    public Product save(Product product) {
        return em.merge(product);
    }

    @Override
    public Product readProductById(Long productId) {
        return (Product) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.core.catalog.domain.Product"), productId);
    }

    @Override
    public List<Product> readProductsByName(String searchName) {
        TypedQuery<Product> query = em.createNamedQuery("BC_READ_PRODUCTS_BY_NAME", Product.class);
        query.setParameter("name", searchName + '%');
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }

    @Override
    public List<Product> readActiveProductsByCategory(Long categoryId, Date currentDate) {
        Date myDate;
        Long myCurrentDateResolution = currentDateResolution;
        synchronized(this) {
            if (currentDate.getTime() - this.currentDate.getTime() > myCurrentDateResolution) {
                this.currentDate = new Date(currentDate.getTime());
                myDate = currentDate;
            } else {
                myDate = this.currentDate;
            }
        }
        TypedQuery<Product> query = em.createNamedQuery("BC_READ_ACTIVE_PRODUCTS_BY_CATEGORY", Product.class);
        query.setParameter("categoryId", categoryId);
        query.setParameter("currentDate", myDate);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");

        return query.getResultList();
    }

    @Override
    public List<Product> readProductsByCategory(Long categoryId) {
        TypedQuery<Product> query = em.createNamedQuery("BC_READ_PRODUCTS_BY_CATEGORY", Product.class);
        query.setParameter("categoryId", categoryId);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");

        return query.getResultList();
    }

    @Override
    public List<Product> readProductsBySku(Long skuId) {
        TypedQuery<Product> query = em.createNamedQuery("BC_READ_PRODUCTS_BY_SKU", Product.class);
        query.setParameter("skuId", skuId);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }
    
    @Override
    public List<ProductSku> readProductsBySkuOneToOne(Long skuId) {
        TypedQuery<ProductSku> query = em.createNamedQuery("BC_READ_PRODUCTS_BY_SKU_ONE_TO_ONE", ProductSku.class);
        query.setParameter("skuId", skuId);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }
    
    @Override
    public List<Product> readActiveProductsBySku(Long skuId, Date currentDate) {
        Date myDate;
        Long myCurrentDateResolution = currentDateResolution;
        synchronized(this) {
            if (currentDate.getTime() - this.currentDate.getTime() > myCurrentDateResolution) {
                this.currentDate = new Date(currentDate.getTime());
                myDate = currentDate;
            } else {
                myDate = this.currentDate;
            }
        }
        TypedQuery<Product> query = em.createNamedQuery("BC_READ_ACTIVE_PRODUCTS_BY_SKU", Product.class);
        query.setParameter("skuId", skuId);
        query.setParameter("currentDate", myDate);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }
    
    @Override
    public List<ProductSku> readActiveProductsBySkuOneToOne(Long skuId, Date currentDate) {
        Date myDate;
        Long myCurrentDateResolution = currentDateResolution;
        synchronized(this) {
            if (currentDate.getTime() - this.currentDate.getTime() > myCurrentDateResolution) {
                this.currentDate = new Date(currentDate.getTime());
                myDate = currentDate;
            } else {
                myDate = this.currentDate;
            }
        }
        TypedQuery<ProductSku> query = em.createNamedQuery("BC_READ_ACTIVE_PRODUCTS_BY_SKU_ONE_TO_ONE", ProductSku.class);
        query.setParameter("skuId", skuId);
        query.setParameter("currentDate", myDate);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }

    @Override
    public void delete(Product product){
        if (!em.contains(product)) {
            product = readProductById(product.getId());
        }
        em.remove(product);     
    }

    @Override
    public Product create(ProductType productType) {
        return (Product) entityConfiguration.createEntityInstance(productType.getType());
    }

    public Long getCurrentDateResolution() {
        return currentDateResolution;
    }

    public void setCurrentDateResolution(Long currentDateResolution) {
        this.currentDateResolution = currentDateResolution;
    }
    
}
