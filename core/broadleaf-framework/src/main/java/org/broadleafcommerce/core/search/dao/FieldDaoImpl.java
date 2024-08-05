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
import org.broadleafcommerce.core.search.domain.FieldImpl;
import org.hibernate.jpa.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Repository("blFieldDao")
public class FieldDaoImpl implements FieldDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;
    
    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;
    
    @Override
    public Field readFieldByAbbreviation(String abbreviation) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Field> criteria = builder.createQuery(Field.class);
        
        Root<FieldImpl> root = criteria.from(FieldImpl.class);
        
        criteria.select(root);
        criteria.where(
            builder.equal(root.get("abbreviation").as(String.class), abbreviation)
        );

        TypedQuery<Field> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");

        try {
            return query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            //must not be an abbreviation
            return null;
        }
    }
    
    @Override
    public List<Field> readAllProductFields() {
        return readFieldsByEntityType(FieldEntity.PRODUCT);

    }

    @Override
    public List<Field> readAllSkuFields() {
        return readFieldsByEntityType(FieldEntity.SKU);
    }

    @Override
    public List<Field> readFieldsByEntityType(FieldEntity entityType) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Field> criteria = builder.createQuery(Field.class);

        Root<FieldImpl> root = criteria.from(FieldImpl.class);

        criteria.select(root);
        criteria.where(
                root.get("entityType").as(String.class).in(entityType.getAllLookupTypes())
                );

        TypedQuery<Field> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");

        return query.getResultList();
    }

    @Override
    public Field save(Field field) {
        return em.merge(field);
    }
}
