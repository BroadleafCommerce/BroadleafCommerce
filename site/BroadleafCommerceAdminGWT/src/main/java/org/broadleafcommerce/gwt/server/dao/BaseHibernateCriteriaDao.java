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
package org.broadleafcommerce.gwt.server.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.ejb.HibernateEntityManager;

import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;

/**
 * Inspired by the com.anasoft.os.daofusion.AbstractHibernateEntityDao class by vojtech.szocs.
 * 
 * @author jfischer
 *
 * @param <T>
 */
public abstract class BaseHibernateCriteriaDao<T extends Serializable> implements BaseCriteriaDao<T> {
	
	private static final Log LOG = LogFactory.getLog(BaseHibernateCriteriaDao.class);
	
	protected Criteria getCriteria(PersistentEntityCriteria entityCriteria, Class<?> entityClass) {
		Criteria criteria = ((HibernateEntityManager) getEntityManager()).getSession().createCriteria(entityClass);
        entityCriteria.apply(criteria);
        
        return criteria;
    }
	
	@SuppressWarnings("unchecked")
	public List<T> query(PersistentEntityCriteria entityCriteria, Class<?> targetEntityClass) {
		return getCriteria(entityCriteria, targetEntityClass).list();
	}
	
	public List<T> query(PersistentEntityCriteria entityCriteria) {
		return query(entityCriteria, getEntityClass());
	}
	
	public int count(PersistentEntityCriteria entityCriteria, Class<?> targetEntityClass) {
        Criteria criteria = getCriteria(entityCriteria, targetEntityClass);
        return rowCount(criteria);
	}
	
	public int count(PersistentEntityCriteria entityCriteria) {
		return count(entityCriteria, getEntityClass());
	}
	
	protected int rowCount(Criteria criteria) {
        criteria.setProjection(Projections.rowCount());
        
        List<?> projectionResults = criteria.list();
        int rowCount = 0;
        
        Object firstResult = projectionResults.get(0);
        if (projectionResults.size() != 1 || !Integer.class.isAssignableFrom(firstResult.getClass())) {
            LOG.warn("rowCount projection for the given criteria did not result a single integer value, returning zero - did you add unnecessary paging constraints to the criteria?");
        } else {
            rowCount = Integer.class.cast(firstResult).intValue();
        }
        
        return rowCount;
    }
    
	public abstract EntityManager getEntityManager();
	
	public abstract Class<? extends Serializable> getEntityClass();

}
