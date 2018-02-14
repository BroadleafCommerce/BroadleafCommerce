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

import org.broadleafcommerce.common.persistence.ArchiveStatus;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.broadleafcommerce.core.search.domain.SearchFacet;
import org.broadleafcommerce.core.search.domain.SearchFacetImpl;
import org.broadleafcommerce.core.search.domain.SearchFacetRange;
import org.broadleafcommerce.core.search.domain.SearchFacetRangeImpl;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.*;
import javax.persistence.criteria.*;

@Repository("blSearchFacetDao")
public class SearchFacetDaoImpl implements SearchFacetDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Resource(name="blCatalogService")
    protected CatalogService catalogService;

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
    public List<Tuple> readDistinctValuesForField(String fieldName, List<Long> productIds) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteria = builder.createQuery(Tuple.class);
        List<Predicate> restrictions = new ArrayList<>();

        Root<ProductImpl> product = criteria.from(ProductImpl.class);
        Path<Sku> sku = product.get("defaultSku");

        if (productIds != null && !productIds.isEmpty()) {
            restrictions.add(product.get("id").in(productIds));
        }

        Path<?> pathToUse;
        if (fieldName.contains("defaultSku.")) {
            pathToUse = sku;
            fieldName = fieldName.substring("defaultSku.".length());
        } else if (fieldName.contains("productAttributes")) {
            pathToUse = product.join("productAttributes");

            fieldName = fieldName.substring("productAttributes(".length(), (fieldName.length() - ").value".length()));

            restrictions.add(builder.equal(
                    builder.lower(pathToUse.get("name").as(String.class)), fieldName.toLowerCase()));

            fieldName = "value";
        } else if (fieldName.contains("productOptionValuesMap")) {
            Join<Object, Object> productOption = product.join("productOptions")
                    .join("productOption");
            pathToUse = productOption
                    .join("allowedValues");

            fieldName = fieldName.substring("productOptionValuesMap(".length(), (fieldName.length() - ")".length()));
            Predicate attributeName = builder.equal(builder.lower(productOption.get("attributeName").as(String.class)), fieldName.toLowerCase());
            restrictions.add(attributeName);

            fieldName = "attributeValue";
        } else if (fieldName.toLowerCase().contains("product.")) {
            pathToUse = product;
            fieldName = fieldName.substring("product.".length());
        } else {
            throw new IllegalArgumentException("Invalid facet fieldName specified: " + fieldName);
        }
        restrictions.add(pathToUse.get(fieldName).isNotNull());

        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));
        criteria.groupBy(pathToUse.get(fieldName));
        criteria.multiselect(pathToUse.get(fieldName), builder.count(pathToUse));

        TypedQuery<Tuple> query = em.createQuery(criteria);
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

    @Override
    public List<SearchFacetRange> readSearchFacetRangesForSearchFacet(SearchFacet searchFacet) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<SearchFacetRange> criteria = builder.createQuery(SearchFacetRange.class);

        Root<SearchFacetRangeImpl> ranges = criteria.from(SearchFacetRangeImpl.class);
        criteria.select(ranges);
        Predicate facetRestriction = builder.equal(ranges.get("searchFacet"), searchFacet);
        // ArchiveStatus could have been dynamically weaved onto SearchFacet, this query will fail
        // if it hadn't
        if (ArchiveStatus.class.isAssignableFrom(SearchFacetRangeImpl.class)) {
            criteria.where(
                    builder.and(
                        facetRestriction,
                        builder.or(builder.isNull(ranges.get("archiveStatus").get("archived").as(String.class)),
                                builder.notEqual(ranges.get("archiveStatus").get("archived").as(Character.class), 'Y'))
                    )
            );
        } else {
            criteria.where(facetRestriction);
        }

        TypedQuery<SearchFacetRange> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Search");

        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }
}
