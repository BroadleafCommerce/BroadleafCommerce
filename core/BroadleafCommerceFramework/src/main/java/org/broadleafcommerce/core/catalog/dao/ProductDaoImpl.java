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

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductSku;
import org.broadleafcommerce.core.catalog.service.type.ProductType;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.persistence.EntityConfiguration;
import org.broadleafcommerce.profile.time.SystemTime;
import org.springframework.stereotype.Repository;

@Repository("blProductDao")
public class ProductDaoImpl implements ProductDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    protected String queryCacheableKey = "org.hibernate.cacheable";
    
    protected Long currentDateResolution = 10000L;
    
    protected Date currentDate = SystemTime.asDate();

    public Product save(Product product) {
        return em.merge(product);
    }

    public Product readProductById(Long productId) {
        return (Product) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.core.catalog.domain.Product"), productId);
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
    	Date myDate;
    	synchronized(this.currentDate) {
	    	if (currentDate.getTime() - this.currentDate.getTime() > currentDateResolution) {
	    		this.currentDate = currentDate;
	    		myDate = currentDate;
	    	} else {
	    		myDate = this.currentDate;
	    	}
    	}
        Query query = em.createNamedQuery("BC_READ_ACTIVE_PRODUCTS_BY_CATEGORY");
        query.setParameter("categoryId", categoryId);
        query.setParameter("currentDate", myDate);
        query.setHint(getQueryCacheableKey(), true);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Product> readProductsByCategory(Long categoryId) {
        Query query = em.createNamedQuery("BC_READ_PRODUCTS_BY_CATEGORY");
        query.setParameter("categoryId", categoryId);
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
    public List<ProductSku> readProductsBySkuOneToOne(Long skuId) {
        Query query = em.createNamedQuery("BC_READ_PRODUCTS_BY_SKU_ONE_TO_ONE");
        query.setParameter("skuId", skuId);
        query.setHint(getQueryCacheableKey(), true);
        return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<Product> readActiveProductsBySku(Long skuId, Date currentDate) {
    	Date myDate;
    	synchronized(this.currentDate) {
	    	if (currentDate.getTime() - this.currentDate.getTime() > currentDateResolution) {
	    		this.currentDate = currentDate;
	    		myDate = currentDate;
	    	} else {
	    		myDate = this.currentDate;
	    	}
    	}
        Query query = em.createNamedQuery("BC_READ_ACTIVE_PRODUCTS_BY_SKU");
        query.setParameter("skuId", skuId);
        query.setParameter("currentDate", myDate);
        query.setHint(getQueryCacheableKey(), true);
        return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<ProductSku> readActiveProductsBySkuOneToOne(Long skuId, Date currentDate) {
    	Date myDate;
    	synchronized(this.currentDate) {
	    	if (currentDate.getTime() - this.currentDate.getTime() > currentDateResolution) {
	    		this.currentDate = currentDate;
	    		myDate = currentDate;
	    	} else {
	    		myDate = this.currentDate;
	    	}
    	}
        Query query = em.createNamedQuery("BC_READ_ACTIVE_PRODUCTS_BY_SKU_ONE_TO_ONE");
        query.setParameter("skuId", skuId);
        query.setParameter("currentDate", myDate);
        query.setHint(getQueryCacheableKey(), true);
        return query.getResultList();
    }

    public void delete(Product product){
    	if (!em.contains(product)) {
    		product = readProductById(product.getId());
    	}
        em.remove(product);    	
    }
    
    public Product create(final ProductType productType) {
        Product item = (Product) entityConfiguration.createEntityInstance(productType.getType());
        return item;
    }
    
    public String getQueryCacheableKey() {
        return queryCacheableKey;
    }

    public void setQueryCacheableKey(String queryCacheableKey) {
        this.queryCacheableKey = queryCacheableKey;
    }

	public Long getCurrentDateResolution() {
		return currentDateResolution;
	}

	public void setCurrentDateResolution(Long currentDateResolution) {
		this.currentDateResolution = currentDateResolution;
	}
    
}
