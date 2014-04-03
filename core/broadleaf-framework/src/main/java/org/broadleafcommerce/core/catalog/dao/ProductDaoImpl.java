/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.catalog.dao;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.logging.SupportLogManager;
import org.broadleafcommerce.common.logging.SupportLogger;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.sandbox.SandBoxHelper;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.util.DateUtil;
import org.broadleafcommerce.common.util.DialectHelper;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXref;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXrefImpl;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.type.ProductType;
import org.broadleafcommerce.core.search.domain.ProductSearchCriteria;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author Jeff Fischer
 * @author Andre Azzolini (apazzolini)
 */
@Repository("blProductDao")
public class ProductDaoImpl implements ProductDao {

    private static final SupportLogger logger = SupportLogManager.getLogger("Enterprise", ProductDaoImpl.class);

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Resource(name="blSandBoxHelper")
    protected SandBoxHelper sandBoxHelper;

    @Resource(name = "blProductDaoExtensionManager")
    protected ProductDaoExtensionManager extensionManager;

    @Resource(name = "blDialectHelper")
    protected DialectHelper dialectHelper;

    protected Long currentDateResolution = 10000L;
    protected Date cachedDate = SystemTime.asDate();

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
        if (productIds.size() > 100) {
            logger.warn("Not recommended to use the readProductsByIds method for long lists of productIds, since " +
                    "Hibernate is required to transform the distinct results. The list of requested" +
                    "product ids was (" + productIds.size() +") in length.");
        }
        // Set up the criteria query that specifies we want to return Products
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
        Root<ProductImpl> product = criteria.from(ProductImpl.class);

        FetchParent fetchParent = product.fetch("defaultSku", JoinType.LEFT);
        if (!dialectHelper.isOracle() && !dialectHelper.isSqlServer()) {
            fetchParent.fetch("skuMedia", JoinType.LEFT);
        }
        criteria.select(product);

        // We only want results that match the product IDs
        criteria.where(product.get("id").as(Long.class).in(
                sandBoxHelper.mergeCloneIds(em, ProductImpl.class,
                        productIds.toArray(new Long[productIds.size()]))));
        if (!dialectHelper.isOracle() && !dialectHelper.isSqlServer()) {
            criteria.distinct(true);
        }

        TypedQuery<Product> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");

        return query.getResultList();
    }

    @Override
    public List<Product> readProductsByName(String searchName) {
        TypedQuery<Product> query = em.createNamedQuery("BC_READ_PRODUCTS_BY_NAME", Product.class);
        query.setParameter("name", searchName + '%');
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");

        return query.getResultList();
    }

    @Override
    public List<Product> readProductsByName(@Nonnull String searchName, @Nonnull int limit, @Nonnull int offset) {
        TypedQuery<Product> query = em.createNamedQuery("BC_READ_PRODUCTS_BY_NAME", Product.class);
        query.setParameter("name", searchName + '%');
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");

        return query.getResultList();
    }

    @Override
    public List<Product> readActiveProductsByCategory(Long categoryId) {
        Date currentDate = DateUtil.getCurrentDateAfterFactoringInDateResolution(cachedDate, currentDateResolution);
        return readActiveProductsByCategoryInternal(categoryId, currentDate);
    }

    @Override
    @Deprecated
    public List<Product> readActiveProductsByCategory(Long categoryId, Date currentDate) {
        return readActiveProductsByCategoryInternal(categoryId, currentDate);
    }

    protected List<Product> readActiveProductsByCategoryInternal(Long categoryId, Date currentDate) {
        TypedQuery<Product> query = em.createNamedQuery("BC_READ_ACTIVE_PRODUCTS_BY_CATEGORY", Product.class);
        query.setParameter("categoryId", sandBoxHelper.mergeCloneIds(em, CategoryImpl.class, categoryId));
        query.setParameter("currentDate", currentDate);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");

        return query.getResultList();
    }
    
    @Override
    public List<Product> readFilteredActiveProductsByQuery(String query, ProductSearchCriteria searchCriteria) {
        Date currentDate = DateUtil.getCurrentDateAfterFactoringInDateResolution(cachedDate, currentDateResolution);
        return readFilteredActiveProductsByQueryInternal(query, currentDate, searchCriteria);
    }

    @Override
    @Deprecated
    public List<Product> readFilteredActiveProductsByQuery(String query, Date currentDate, ProductSearchCriteria searchCriteria) {
        return readFilteredActiveProductsByQueryInternal(query, currentDate, searchCriteria);
    }

    protected List<Product> readFilteredActiveProductsByQueryInternal(String query, Date currentDate, ProductSearchCriteria searchCriteria) {
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
        
        TypedQuery<Product> typedQuery = em.createQuery(criteria);
        //don't cache - not really practical for open ended search
        
        return typedQuery.getResultList();
    }
    
    @Override
    public List<Product> readFilteredActiveProductsByCategory(Long categoryId, ProductSearchCriteria searchCriteria) {
        Date currentDate = DateUtil.getCurrentDateAfterFactoringInDateResolution(cachedDate, currentDateResolution);
        return readFilteredActiveProductsByCategoryInternal(categoryId, currentDate, searchCriteria);
    }

    @Override
    @Deprecated
    public List<Product> readFilteredActiveProductsByCategory(Long categoryId, Date currentDate, 
            ProductSearchCriteria searchCriteria) {
        return readFilteredActiveProductsByCategoryInternal(categoryId, currentDate, searchCriteria);
    }

    protected List<Product> readFilteredActiveProductsByCategoryInternal(Long categoryId, Date currentDate,
            ProductSearchCriteria searchCriteria) {
        // Set up the criteria query that specifies we want to return Products
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
        
        // The root of our search is Category since we are browsing
        Root<CategoryProductXrefImpl> productXref = criteria.from(CategoryProductXrefImpl.class);
        
        // We want to filter on attributes from product and sku
        Join<CategoryProductXref, Product> product = productXref.join("product");
        Join<Product, Sku> sku = product.join("defaultSku");
        Join<CategoryProductXref, Category> category = productXref.join("category");

        // Product objects are what we want back
        criteria.select(product);
        
        // We only want results from the determine category
        List<Predicate> restrictions = new ArrayList<Predicate>();
        restrictions.add(category.get("id").in(sandBoxHelper.mergeCloneIds(em, CategoryImpl.class, categoryId)));
        
        attachProductSearchCriteria(searchCriteria, product, sku, restrictions);
        
        attachActiveRestriction(currentDate, product, sku, restrictions);
        
        attachOrderBy(searchCriteria, product, sku, criteria);
        
        // Execute the query with the restrictions
        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));
        
        TypedQuery<Product> typedQuery = em.createQuery(criteria);
        //don't cache - not really practical for open ended search
        //typedQuery.setHint(SandBoxHelper.QueryHints.FILTER_INCLUDE, ".*CategoryProductXrefImpl");

        return typedQuery.getResultList();
    }

    protected void attachActiveRestriction(Date currentDate, Path<? extends Product> product, 
            Path<? extends Sku> sku, List<Predicate> restrictions) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        
        // Add the product archived status flag restriction
        restrictions.add(builder.or(
                            builder.isNull(product.get("archiveStatus").get("archived")),
                            builder.equal(product.get("archiveStatus").get("archived"), 'N')));
        
        // Add the active start/end date restrictions
        restrictions.add(builder.lessThan(sku.get("activeStartDate").as(Date.class), currentDate));
        restrictions.add(builder.or(
                            builder.isNull(sku.get("activeEndDate")),
                builder.greaterThan(sku.get("activeEndDate").as(Date.class), currentDate)));
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
    public List<Product> readActiveProductsByCategory(Long categoryId, int limit, int offset) {
        Date currentDate = DateUtil.getCurrentDateAfterFactoringInDateResolution(cachedDate, currentDateResolution);
        return readActiveProductsByCategoryInternal(categoryId, currentDate, limit, offset);
    }
    
    @Override
    @Deprecated
    public List<Product> readActiveProductsByCategory(Long categoryId, Date currentDate, int limit, int offset) {
        return readActiveProductsByCategoryInternal(categoryId, currentDate, limit, offset);
    }

    public List<Product> readActiveProductsByCategoryInternal(Long categoryId, Date currentDate, int limit, int offset) {
        TypedQuery<Product> query = em.createNamedQuery("BC_READ_ACTIVE_PRODUCTS_BY_CATEGORY", Product.class);
        query.setParameter("categoryId", sandBoxHelper.mergeCloneIds(em, CategoryImpl.class, categoryId));
        query.setParameter("currentDate", currentDate);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");

        return query.getResultList();
    }

    @Override
    public List<Product> readProductsByCategory(Long categoryId) {
        TypedQuery<Product> query = em.createNamedQuery("BC_READ_PRODUCTS_BY_CATEGORY", Product.class);
        query.setParameter("categoryId", sandBoxHelper.mergeCloneIds(em, CategoryImpl.class, categoryId));
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");

        return query.getResultList();
    }

    @Override
    public List<Product> readProductsByCategory(Long categoryId, int limit, int offset) {
        TypedQuery<Product> query = em.createNamedQuery("BC_READ_PRODUCTS_BY_CATEGORY", Product.class);
        query.setParameter("categoryId", sandBoxHelper.mergeCloneIds(em, CategoryImpl.class, categoryId));
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");

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
        Date currentDate = DateUtil.getCurrentDateAfterFactoringInDateResolution(cachedDate, currentDateResolution);
        TypedQuery<ProductBundle> query = em.createNamedQuery("BC_READ_AUTOMATIC_PRODUCT_BUNDLES", ProductBundle.class);
        query.setParameter("currentDate", currentDate);
        query.setParameter("autoBundle", Boolean.TRUE);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");
        
        return query.getResultList();
    }       

    @Override
    public Long getCurrentDateResolution() {
        return currentDateResolution;
    }

    @Override
    public void setCurrentDateResolution(Long currentDateResolution) {
        this.currentDateResolution = currentDateResolution;
    }

    @Override
    public List<Product> findProductByURI(String uri) {
        if (extensionManager != null) {
            ExtensionResultHolder holder = new ExtensionResultHolder();
            ExtensionResultStatusType result = extensionManager.getProxy().findProductByURI(uri, holder);
            if (ExtensionResultStatusType.HANDLED.equals(result)) {
                return (List<Product>) holder.getResult();
            }
        }
        String urlKey = uri.substring(uri.lastIndexOf('/'));        
        Query query;
    
        query = em.createNamedQuery("BC_READ_PRODUCTS_BY_OUTGOING_URL");
        query.setParameter("url", uri);
        query.setParameter("urlKey", urlKey);
        query.setParameter("currentDate", DateUtil.getCurrentDateAfterFactoringInDateResolution(cachedDate, currentDateResolution));
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");
    
        @SuppressWarnings("unchecked")
        List<Product> results = query.getResultList();
        return results;
    }
    
    @Override
    public List<Product> readAllActiveProducts(int page, int pageSize) {
        Date currentDate = DateUtil.getCurrentDateAfterFactoringInDateResolution(cachedDate, currentDateResolution);
        return readAllActiveProductsInternal(page, pageSize, currentDate);
    }
    
    @Override
    @Deprecated
    public List<Product> readAllActiveProducts(int page, int pageSize, Date currentDate) {    
        return readAllActiveProductsInternal(page, pageSize, currentDate);
    }

    protected List<Product> readAllActiveProductsInternal(int page, int pageSize, Date currentDate) {
        CriteriaQuery<Product> criteria = getCriteriaForActiveProducts(currentDate);
        int firstResult = page * pageSize;
        TypedQuery<Product> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");

        return query.setFirstResult(firstResult).setMaxResults(pageSize).getResultList();
    }
    
    @Override
    public List<Product> readAllActiveProducts() {
        Date currentDate = DateUtil.getCurrentDateAfterFactoringInDateResolution(cachedDate, currentDateResolution);
        return readAllActiveProductsInternal(currentDate);
    }
    
    @Override
    @Deprecated
    public List<Product> readAllActiveProducts(Date currentDate) {
        return readAllActiveProductsInternal(currentDate);
    }

    protected List<Product> readAllActiveProductsInternal(Date currentDate) {
        CriteriaQuery<Product> criteria = getCriteriaForActiveProducts(currentDate);
        TypedQuery<Product> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");

        return query.getResultList();
    }
    
    @Override
    public Long readCountAllActiveProducts() {
        Date currentDate = DateUtil.getCurrentDateAfterFactoringInDateResolution(cachedDate, currentDateResolution);
        return readCountAllActiveProductsInternal(currentDate);
    }
    
    @Override
    @Deprecated
    public Long readCountAllActiveProducts(Date currentDate) {
        return readCountAllActiveProductsInternal(currentDate);
    }

    protected Long readCountAllActiveProductsInternal(Date currentDate) {
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

        TypedQuery<Long> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");

        return query.getSingleResult();
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
