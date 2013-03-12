/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.server.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.ejb.HibernateEntityManager;

import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

/**
 * Inspired by the com.anasoft.os.daofusion.AbstractHibernateEntityDao class by vojtech.szocs.
 * 
 * @author jfischer
 *
 * @param <T>
 */
public abstract class BaseHibernateCriteriaDao<T extends Serializable> implements BaseCriteriaDao<T> {
    
    private static final Log LOG = LogFactory.getLog(BaseHibernateCriteriaDao.class);
    
    public Criteria getCriteria(PersistentEntityCriteria entityCriteria, Class<?> entityClass) {
        /*
         * TODO this method should return a proxied Criteria instance that will return a mixed list
         */
        //Criteria criteria = ((DualEntityManager) getStandardEntityManager()).getStandardManager().getSession().createCriteria(entityClass);
        Criteria criteria = createCriteria(entityClass);
        entityCriteria.apply(criteria);
        
        return criteria;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<T> query(PersistentEntityCriteria entityCriteria, Class<?> targetEntityClass) {
        return getCriteria(entityCriteria, targetEntityClass).list();
    }
    
    @Override
    public List<T> query(PersistentEntityCriteria entityCriteria) {
        return query(entityCriteria, getEntityClass());
    }
    
    @Override
    public int count(PersistentEntityCriteria entityCriteria, Class<?> targetEntityClass) {
        Criteria criteria = getCriteria(entityCriteria, targetEntityClass);
        return rowCount(criteria);
    }
    
    @Override
    public int count(PersistentEntityCriteria entityCriteria) {
        return count(entityCriteria, getEntityClass());
    }
    
    public int rowCount(Criteria criteria) {
        criteria.setProjection(Projections.rowCount());
        
        List<?> projectionResults = criteria.list();
        int rowCount = 0;
        
        Object firstResult = projectionResults.get(0);
        if (projectionResults.size() != 1 || !Long.class.isAssignableFrom(firstResult.getClass())) {
            LOG.warn("rowCount projection for the given criteria did not result a single integer value, returning zero - did you add unnecessary paging constraints to the criteria?");
        } else {
            rowCount = Long.class.cast(firstResult).intValue();
        }
        
        return rowCount;
    }

    public Criteria createCriteria(Class<?> entityClass) {
        return ((HibernateEntityManager) getStandardEntityManager()).getSession().createCriteria(entityClass);
    }
    
    @Override
    public abstract EntityManager getStandardEntityManager();
    
    @Override
    public abstract Class<? extends Serializable> getEntityClass();

}
