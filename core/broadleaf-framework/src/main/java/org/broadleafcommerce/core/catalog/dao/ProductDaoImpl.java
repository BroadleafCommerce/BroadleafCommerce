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
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.type.ProductType;
import org.broadleafcommerce.core.search.domain.ProductSearchCriteria;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

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
        return (Product) em.find(ProductImpl.class, productId);
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
    
    protected Date getDateFactoringInDateResolution(Date currentDate) {
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
    	return myDate;
    }

    @Override
    public List<Product> readActiveProductsByCategory(Long categoryId, Date currentDate) {
    	Date myDate = getDateFactoringInDateResolution(currentDate);
        TypedQuery<Product> query = em.createNamedQuery("BC_READ_ACTIVE_PRODUCTS_BY_CATEGORY", Product.class);
        query.setParameter("categoryId", categoryId);
        query.setParameter("currentDate", myDate);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");

        return query.getResultList();
    }
    
	@Override
    public List<Product> readFilteredActiveProductsByCategory(Long categoryId, Date currentDate, 
    		ProductSearchCriteria searchCriteria) {
		// Set up the criteria query that specifies we want to return Products
    	CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
		
		// The root of our search is Category since we are browsing
		Root<CategoryImpl> category = criteria.from(CategoryImpl.class);
		
		// We want to filter on attributes from product and sku
		Join<Category, Product> product = category.join("allProducts");
		Path<Sku> sku = product.get("defaultSku");
		
		// Product objects are what we want back
		criteria.select(product);
		
		// We only want results from the determine category
		List<Predicate> restrictions = new ArrayList<Predicate>();
		restrictions.add(builder.equal(category.get("id"), categoryId));
		
		// Build out the filter criteria from the users request
		for (Entry<String, String[]> entry : searchCriteria.getFilterCriteria().entrySet()) {
			String key = entry.getKey();
			List<String> eqValues = new ArrayList<String>();
			List<String[]> rangeValues = new ArrayList<String[]>();
			
			// Determine whether we should use the product path or the sku path
			Path<?> pathToUse;
			if (key.contains("defaultSku.")) {
				pathToUse = sku;
				key = key.substring("defaultSku.".length());
			} else {
				pathToUse = product;
			}
			
			// Values can be equality checks (ie manufacturer=Dave's) or range checks, which take the form
			// key=blcRange[minRange:maxRange]. Figure out what type of check this is
			for (String value : entry.getValue()) {
				if (value.contains("blcRange[")) {
					String[] rangeValue = new String[] {
						value.substring(value.indexOf("[") + 1, value.indexOf(":")),
						value.substring(value.indexOf(":") + 1, value.indexOf("]"))
					};
					rangeValues.add(rangeValue);
				} else { 
					eqValues.add(value);
				}
			}
			
			// Add the equality range restriction with the "in" builder. That means that the query string
			// manufacturer=Dave and manufacturer=Bob would match either Dave or Bob
			if (eqValues.size() > 0) {
				restrictions.add(pathToUse.get(key).in(eqValues));
			}
			
			// If we have any range restrictions, we need to build those too. Ranges are also "or"ed together,
			// such that specifying blcRange[0:5] and blcRange[10:null] for the same field would match items
			// that were valued between 0 and 5 OR over 10 for that field
			List<Predicate> rangeRestrictions = new ArrayList<Predicate>();
			for (String[] range : rangeValues) {
				BigDecimal min = new BigDecimal(range[0]);
				BigDecimal max = null;
				if (range[1] != null && !range[1].equals("null")) {
					max = new BigDecimal(range[1]);
				}
				
				Predicate minRange = builder.greaterThan(pathToUse.get(key).as(BigDecimal.class), min);
				Predicate maxRange = null;
		    	if (max != null) {
		    		maxRange = builder.lessThan(pathToUse.get(key).as(BigDecimal.class), max);
		    		rangeRestrictions.add(builder.and(minRange, maxRange));
		    	} else {
		    		rangeRestrictions.add(minRange);
		    	}
			}
			
			if (rangeRestrictions.size() > 0) {
				restrictions.add(builder.or(rangeRestrictions.toArray(new Predicate[rangeRestrictions.size()])));
			}
		}
		
		// Add the product archived status flag restriction
		restrictions.add(builder.or(
							builder.isNull(product.get("archiveStatus").get("archived")),
							builder.equal(product.get("archiveStatus").get("archived"), 'N')));
		
		// Add the active start/end date restrictions
    	Date myDate = getDateFactoringInDateResolution(currentDate);
    	restrictions.add(builder.lessThan(sku.get("activeStartDate").as(Date.class), myDate));
    	restrictions.add(builder.or(
    						builder.isNull(sku.get("activeEndDate")),
    						builder.greaterThan(sku.get("activeEndDate").as(Date.class), myDate)));
		
    	// Execute the query with the restrictions
		criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));
    	return (List<Product>) em.createQuery(criteria).getResultList();
    }

    @Override
    public List<Product> readActiveProductsByCategory(Long categoryId, Date currentDate, int limit, int offset) {
        Date myDate = getDateFactoringInDateResolution(currentDate);
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
    public void delete(Product product){
//    	if (!em.contains(product)) {
//    		product = readProductById(product.getId());
//    	}
//        em.remove(product);
        ((Status) product).setArchived('Y');
        em.merge(product);
    }

    @Override
    public Product create(ProductType productType) {
        return (Product) entityConfiguration.createEntityInstance(productType.getType());
    }

    @Override
    public List<ProductBundle> readAutomaticProductBundles() {
        Date myDate = getDateFactoringInDateResolution(currentDate);
        TypedQuery<ProductBundle> query = em.createNamedQuery("BC_READ_AUTOMATIC_PRODUCT_BUNDLES", ProductBundle.class);
        query.setParameter("currentDate", myDate);
        query.setParameter("autoBundle", Boolean.TRUE);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }       
    
    public Long getCurrentDateResolution() {
		return currentDateResolution;
	}

	public void setCurrentDateResolution(Long currentDateResolution) {
		this.currentDateResolution = currentDateResolution;
	}

	@Override
	public List<Product> findProductByURI(String uri) {
		
		String urlKey = uri.substring(uri.lastIndexOf('/'));		
		Query query;
	
		query = em.createNamedQuery("BC_READ_PRODUCTS_BY_OUTGOING_URL");
		query.setParameter("url", uri);
		query.setParameter("urlKey", urlKey);
	
		@SuppressWarnings("unchecked")
		List<Product> results = (List<Product>) query.getResultList();
		return results;
	}
}
