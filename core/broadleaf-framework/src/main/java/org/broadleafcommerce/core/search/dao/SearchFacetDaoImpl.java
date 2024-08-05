/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.search.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.broadleafcommerce.core.search.domain.SearchFacet;
import org.broadleafcommerce.core.search.domain.SearchFacetImpl;
import org.hibernate.jpa.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

@Repository("blSearchFacetDao")
public class SearchFacetDaoImpl implements SearchFacetDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;
    
    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;
    
    @Override
    public List<SearchFacet> readAllSearchFacets(FieldEntity entityType) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<SearchFacet> criteria = builder.createQuery(SearchFacet.class);
        
        Root<SearchFacetImpl> facet = criteria.from(SearchFacetImpl.class);
        
        criteria.select(facet);
        criteria.where(
                builder.equal(facet.get("showOnSearch").as(Boolean.class), true),
                facet.join("fieldType").join("indexField").join("field").get("entityType").as(String.class).in(entityType.getAllLookupTypes())
        );

        TypedQuery<SearchFacet> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Search");
        
        return query.getResultList();
    }
    
    @Override
    public <T> List<T> readDistinctValuesForField(String fieldName, Class<T> fieldValueClass) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(fieldValueClass);
        
        Root<ProductImpl> product = criteria.from(ProductImpl.class);
        Path<Sku> sku = product.get("defaultSku");
        
        Path<?> pathToUse;
        if (fieldName.contains("defaultSku.")) {
            pathToUse = sku;
            fieldName = fieldName.substring("defaultSku.".length());
        } else if (fieldName.contains("productAttributes.")) {
            pathToUse = product.join("productAttributes");
            
            fieldName = fieldName.substring("productAttributes.".length());
            criteria.where(builder.equal(
                builder.lower(pathToUse.get("name").as(String.class)), fieldName.toLowerCase()));
            
            fieldName = "value";
        } else if (fieldName.contains("product.")) {
            pathToUse = product;
            fieldName = fieldName.substring("product.".length());
        } else {
            throw new IllegalArgumentException("Invalid facet fieldName specified: " + fieldName);
        }
        
        criteria.where(pathToUse.get(fieldName).as(fieldValueClass).isNotNull());
        criteria.distinct(true).select(pathToUse.get(fieldName).as(fieldValueClass));

        TypedQuery<T> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Search");
        
        return query.getResultList();
    }

    @Override
    public SearchFacet save(SearchFacet searchFacet) {
        return em.merge(searchFacet);
    }

    @Override
    public SearchFacet readSearchFacetForField(Field field) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<SearchFacet> criteria = builder.createQuery(SearchFacet.class);

        Root<SearchFacetImpl> facet = criteria.from(SearchFacetImpl.class);

        criteria.select(facet);
        criteria.where(
                builder.equal(facet.join("fieldType").join("indexField").join("field").get("id").as(Long.class), field.getId())
        );

        TypedQuery<SearchFacet> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Search");

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
