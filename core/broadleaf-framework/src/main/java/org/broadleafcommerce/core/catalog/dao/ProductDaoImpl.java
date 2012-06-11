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

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.domain.ProductSku;
import org.broadleafcommerce.core.catalog.service.type.ProductType;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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

    private String DATE_LOCK = "DATE_LOCK"; // for use in synchronization

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
    public List<Product> readProductsByName(@Nonnull String searchName, @Nonnull int limit, @Nonnull int offset) {
        TypedQuery<Product> query = em.createNamedQuery("BC_READ_PRODUCTS_BY_NAME", Product.class);
        query.setParameter("name", searchName + '%');
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        return query.getResultList();
    }

    @Override
    public List<Product> readActiveProductsByCategory(Long categoryId, Date currentDate) {
    	Date myDate;
        Long myCurrentDateResolution = currentDateResolution;
    	synchronized(DATE_LOCK) {
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
    public List<Product> readActiveProductsByCategory(Long categoryId, Date currentDate, int limit, int offset) {
        Date myDate;
        Long myCurrentDateResolution = currentDateResolution;
        synchronized(DATE_LOCK) {
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
        query.setFirstResult(offset);
        query.setMaxResults(limit);

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
    public List<Product> readProductsByCategory(Long categoryId, int limit, int offset) {
        TypedQuery<Product> query = em.createNamedQuery("BC_READ_PRODUCTS_BY_CATEGORY", Product.class);
        query.setParameter("categoryId", categoryId);
        query.setFirstResult(offset);
        query.setMaxResults(limit);

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
    	synchronized(DATE_LOCK) {
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
    	synchronized(DATE_LOCK) {
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

    @Override
    public List<ProductBundle> readAutomaticProductBundles() {
        Date myDate;
        Long myCurrentDateResolution = currentDateResolution;
       	synchronized(DATE_LOCK) {
   	    	if (currentDate.getTime() - this.currentDate.getTime() > myCurrentDateResolution) {
   	    		this.currentDate = new Date(currentDate.getTime());
   	    		myDate = currentDate;
   	    	} else {
   	    		myDate = this.currentDate;
   	    	}
       	}
        TypedQuery<ProductBundle> query = em.createNamedQuery("BC_READ_AUTOMATIC_PRODUCT_BUNDLES", ProductBundle.class);
        query.setParameter("currentDate", myDate);
        query.setParameter("autoBundle", Boolean.TRUE);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }
    
    @Override
    public Product findProductByURI(String uri) {
        Query query;
    
        query = em.createNamedQuery("BC_READ_PRODUCTS_BY_OUTGOING_URL");
        query.setParameter("url", uri);
    
        @SuppressWarnings("unchecked")
        List<Product> results = (List<Product>) query.getResultList();
        if (results != null && !results.isEmpty()) {
            return results.get(0);
        } else {
            return null;
        }
    }
    
    public Long getCurrentDateResolution() {
		return currentDateResolution;
	}

	public void setCurrentDateResolution(Long currentDateResolution) {
		this.currentDateResolution = currentDateResolution;
	}
    
}
