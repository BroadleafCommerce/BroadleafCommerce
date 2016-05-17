/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.broadleafcommerce.core.search.domain.IndexField;
import org.broadleafcommerce.core.search.domain.IndexFieldImpl;
import org.broadleafcommerce.core.search.domain.IndexFieldType;
import org.broadleafcommerce.core.search.domain.IndexFieldTypeImpl;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.hibernate.ejb.QueryHints;
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
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<IndexField> criteria = builder.createQuery(IndexField.class);

        Root<IndexFieldImpl> search = criteria.from(IndexFieldImpl.class);

        criteria.select(search);
        criteria.where(
                builder.equal(search.join("field").get("id").as(Long.class), field.getId())
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
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");

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
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");

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
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");

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
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");

        return query.getResultList();
    }

}
