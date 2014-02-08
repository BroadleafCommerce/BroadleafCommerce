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
package org.broadleafcommerce.core.search.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.broadleafcommerce.core.search.domain.FieldImpl;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

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
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Field> criteria = builder.createQuery(Field.class);
        
        Root<FieldImpl> root = criteria.from(FieldImpl.class);
        
        criteria.select(root);
        criteria.where(
            builder.equal(root.get("entityType").as(String.class), FieldEntity.PRODUCT.getType())
        );

        TypedQuery<Field> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");

        return query.getResultList();
    }

    public Field save(Field field) {
        return em.merge(field);
    }
}
