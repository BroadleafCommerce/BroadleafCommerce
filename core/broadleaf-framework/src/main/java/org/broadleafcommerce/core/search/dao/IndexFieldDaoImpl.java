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
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.broadleafcommerce.core.search.domain.IndexField;
import org.broadleafcommerce.core.search.domain.IndexFieldImpl;
import org.broadleafcommerce.core.search.domain.IndexFieldType;
import org.broadleafcommerce.core.search.domain.IndexFieldTypeImpl;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
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
import javax.persistence.criteria.Root;

/**
 * @author Nick Crum (ncrum)
 */
@Repository("blIndexFieldDao")
public class IndexFieldDaoImpl implements IndexFieldDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public IndexField readIndexFieldForField(Field field) {
        return readIndexFieldByFieldId(field.getId());
    }

    @Override
    public IndexField readIndexFieldByFieldId(Long fieldId) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<IndexField> criteria = builder.createQuery(IndexField.class);

        Root<IndexFieldImpl> search = criteria.from(IndexFieldImpl.class);

        criteria.select(search);
        criteria.where(
                builder.equal(search.join("field").get("id").as(Long.class), fieldId)
        );

        TypedQuery<IndexField> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Search");

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    @Override
    public List<IndexField> readFieldsByEntityType(FieldEntity entityType) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<IndexField> criteria = builder.createQuery(IndexField.class);

        Root<IndexFieldImpl> root = criteria.from(IndexFieldImpl.class);
        
        criteria.select(root);
        criteria.where(root.get("field").get("entityType").as(String.class).in(entityType.getAllLookupTypes()));

        TypedQuery<IndexField> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Search");

        return query.getResultList();
    }

    @Override
    public List<IndexField> readSearchableFieldsByEntityType(FieldEntity entityType) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<IndexField> criteria = builder.createQuery(IndexField.class);

        Root<IndexFieldImpl> root = criteria.from(IndexFieldImpl.class);

        criteria.select(root);
        criteria.where(
                builder.equal(root.get("searchable").as(Boolean.class), Boolean.TRUE),
                root.get("field").get("entityType").as(String.class).in(entityType.getAllLookupTypes())
        );

        TypedQuery<IndexField> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Search");

        return query.getResultList();
    }

    @Override
    public List<IndexFieldType> getIndexFieldTypesByAbbreviation(String abbreviation) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<IndexFieldType> criteria = builder.createQuery(IndexFieldType.class);

        Root<IndexFieldTypeImpl> root = criteria.from(IndexFieldTypeImpl.class);

        criteria.select(root);
        criteria.where(
                builder.equal(root.get("indexField").get("field").get("abbreviation").as(String.class), abbreviation)
        );

        TypedQuery<IndexFieldType> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Search");

        return query.getResultList();
    }

    @Override
    public List<IndexFieldType> getIndexFieldTypes(FieldType facetFieldType) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<IndexFieldType> criteria = builder.createQuery(IndexFieldType.class);

        Root<IndexFieldTypeImpl> root = criteria.from(IndexFieldTypeImpl.class);

        criteria.select(root);
        criteria.where(
                builder.equal(root.get("fieldType").as(String.class), facetFieldType.getType())
        );

        TypedQuery<IndexFieldType> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Search");

        return query.getResultList();
    }

}
