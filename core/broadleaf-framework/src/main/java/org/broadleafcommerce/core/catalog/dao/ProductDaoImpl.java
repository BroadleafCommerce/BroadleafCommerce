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

import org.apache.commons.lang.StringUtils;
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
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
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
 * @author Andre Azzolini (apazzolini)
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
        return em.find(ProductImpl.class, productId);
    }

    @Override
    public List<Product> readProductsByIds(List<Long> productIds) {
        if (productIds == null || productIds.size() == 0) {
            return null;
        }
        
        // Set up the criteria query that specifies we want to return Products
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
        Root<ProductImpl> product = criteria.from(ProductImpl.class);
        criteria.select(product);
        
        // We only want results that match the product IDs
        criteria.where(product.get("id").as(Long.class).in(productIds));
        
        return em.createQuery(criteria).getResultList();
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
    public List<Product> readFilteredActiveProductsByQuery(String query, Date currentDate, ProductSearchCriteria searchCriteria) {
        // Set up the criteria query that specifies we want to return Products
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
        
        // The root of our search is Product since we are searching
        Root<ProductImpl> product = criteria.from(ProductImpl.class);
        
        // We also want to filter on attributes from sku and productAttributes
        Join<Product, Sku> sku = product.join("defaultSku");
        
        // Product objects are what we want back
        criteria.select(product);
        
        // We only want results that match the search query
        List<Predicate> restrictions = new ArrayList<Predicate>();
        String lq = query.toLowerCase();
        restrictions.add(
            builder.or(
                builder.like(builder.lower(sku.get("name").as(String.class)), '%' + lq + '%'),
                builder.like(builder.lower(sku.get("longDescription").as(String.class)), '%' + lq + '%')
            )
        );
                
        attachProductSearchCriteria(searchCriteria, product, sku, restrictions);
        
        attachActiveRestriction(currentDate, product, sku, restrictions);
        
        attachOrderBy(searchCriteria, product, sku, criteria);
        
        // Execute the query with the restrictions
        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));
        return em.createQuery(criteria).getResultList();
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
        Join<Product, Sku> sku = product.join("defaultSku");
        
        // Product objects are what we want back
        criteria.select(product);
        
        // We only want results from the determine category
        List<Predicate> restrictions = new ArrayList<Predicate>();
        restrictions.add(builder.equal(category.get("id"), categoryId));
        
        attachProductSearchCriteria(searchCriteria, product, sku, restrictions);
        
        attachActiveRestriction(currentDate, product, sku, restrictions);
        
        attachOrderBy(searchCriteria, product, sku, criteria);
        
        // Execute the query with the restrictions
        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));
        return em.createQuery(criteria).getResultList();
    }

    protected void attachActiveRestriction(Date currentDate, Path<? extends Product> product, 
            Path<? extends Sku> sku, List<Predicate> restrictions) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        
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
    }
    
    protected void attachOrderBy(ProductSearchCriteria searchCriteria, 
            From<?, ? extends Product> product, Path<? extends Sku> sku, CriteriaQuery<?> criteria) {
        if (StringUtils.isNotBlank(searchCriteria.getSortQuery())) {
            CriteriaBuilder builder = em.getCriteriaBuilder();
        
            List<Order> sorts = new ArrayList<Order>();
            
            String sortQueries = searchCriteria.getSortQuery();
            for (String sortQuery : sortQueries.split(",")) {
                String[] sort = sortQuery.split(" ");
                if (sort.length == 2) {
                    String key = sort[0];
                    boolean asc = sort[1].toLowerCase().contains("asc");
                    
                    // Determine whether we should use the product path or the sku path
                    Path<?> pathToUse;
                    if (key.contains("defaultSku.")) {
                        pathToUse = sku;
                        key = key.substring("defaultSku.".length());
                    } else if (key.contains("product.")) {
                        pathToUse = product;
                        key = key.substring("product.".length());
                    } else {
                        // We don't know which path this facet is built on - resolves previous bug that attempted
                        // to attach search facet to any query parameter
                        continue;
                    }
                    
                    if (asc) {
                        sorts.add(builder.asc(pathToUse.get(key)));
                    } else {
                        sorts.add(builder.desc(pathToUse.get(key)));
                    }
                }
            }
            
            criteria.orderBy(sorts.toArray(new Order[sorts.size()]));
        }
    }

    protected void attachProductSearchCriteria(ProductSearchCriteria searchCriteria, 
            From<?, ? extends Product> product, From<?, ? extends Sku> sku, List<Predicate> restrictions) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        
        // Build out the filter criteria from the users request
        for (Entry<String, String[]> entry : searchCriteria.getFilterCriteria().entrySet()) {
            String key = entry.getKey();
            List<String> eqValues = new ArrayList<String>();
            List<String[]> rangeValues = new ArrayList<String[]>();
            
            // Determine which path is the appropriate one to use
            Path<?> pathToUse;
            if (key.contains("defaultSku.")) {
                pathToUse = sku;
                key = key.substring("defaultSku.".length());
            } else if (key.contains("productAttributes.")) {
                pathToUse = product.join("productAttributes");
                
                key = key.substring("productAttributes.".length());
                restrictions.add(builder.equal(pathToUse.get("name").as(String.class), key));
                
                key = "value";
            } else if (key.contains("product.")) {
                pathToUse = product;
                key = key.substring("product.".length());
            } else {
                // We don't know which path this facet is built on - resolves previous bug that attempted
                // to attach search facet to any query parameter
                continue;
            }
            
            // Values can be equality checks (ie manufacturer=Dave's) or range checks, which take the form
            // key=range[minRange:maxRange]. Figure out what type of check this is
            for (String value : entry.getValue()) {
                if (value.contains("range[")) {
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
            // ?manufacturer=Dave&manufacturer=Bob would match either Dave or Bob
            if (eqValues.size() > 0) {
                restrictions.add(pathToUse.get(key).in(eqValues));
            }
            
            // If we have any range restrictions, we need to build those too. Ranges are also "or"ed together,
            // such that specifying range[0:5] and range[10:null] for the same field would match items
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

    @Override
    public List<Product> readAllActiveProducts(int page, int pageSize, Date currentDate) {
        CriteriaQuery<Product> criteria = getCriteriaForActiveProducts(currentDate);
        int firstResult = page * pageSize;
        return em.createQuery(criteria).setFirstResult(firstResult).setMaxResults(pageSize).getResultList();
    }

    @Override
    public List<Product> readAllActiveProducts(Date currentDate) {
        CriteriaQuery<Product> criteria = getCriteriaForActiveProducts(currentDate);
        return em.createQuery(criteria).getResultList();
    }

    @Override
    public Long readCountAllActiveProducts(Date currentDate) {
        // Set up the criteria query that specifies we want to return a Long
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);

        // The root of our search is Product
        Root<ProductImpl> product = criteria.from(ProductImpl.class);

        // We need to filter on active date on the sku
        Join<Product, Sku> sku = product.join("defaultSku");

        // We want the count of products
        criteria.select(builder.count(product));

        // Ensure the product is currently active
        List<Predicate> restrictions = new ArrayList<Predicate>();
        attachActiveRestriction(currentDate, product, sku, restrictions);

        // Add the restrictions to the criteria query
        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));
        return em.createQuery(criteria).getSingleResult();
    }

    protected CriteriaQuery<Product> getCriteriaForActiveProducts(Date currentDate) {
        // Set up the criteria query that specifies we want to return Products
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
        
        // The root of our search is Product
        Root<ProductImpl> product = criteria.from(ProductImpl.class);
        
        // We need to filter on active date on the sku
        Join<Product, Sku> sku = product.join("defaultSku");
        product.fetch("defaultSku");
        
        // Product objects are what we want back
        criteria.select(product);
        
        // Ensure the product is currently active
        List<Predicate> restrictions = new ArrayList<Predicate>();
        attachActiveRestriction(currentDate, product, sku, restrictions);
        
        // Add the restrictions to the criteria query
        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));
        return criteria;
    }
}
