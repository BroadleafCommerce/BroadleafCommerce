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

import org.hibernate.Criteria;

import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

/**
 * 
 * @author jfischer
 *
 * @param <T>
 */
public interface BaseCriteriaDao<T extends Serializable> {

    public List<T> query(PersistentEntityCriteria entityCriteria, Class<?> targetEntityClass);

    public int count(PersistentEntityCriteria entityCriteria, Class<?> targetEntityClass);

    public EntityManager getStandardEntityManager();
    
    public int count(PersistentEntityCriteria entityCriteria);
    
    /**
     * Convenience method for executing a row count for a given Hibernate criteria. This should not normally be used
     * except for very specific cases where circumstances dictate a transformation of the Hibernate criteria (like if
     * the <b>criteria</b> needs to have table aliases for joins). Normally the {{@link #count(PersistentEntityCriteria)} or
     * {@link #count(PersistentEntityCriteria, Class)} should be used instead.
     * 
     * @param criteria
     * @return
     */
    public int rowCount(Criteria criteria);

    public List<T> query(PersistentEntityCriteria entityCriteria);
    
    public Class<? extends Serializable> getEntityClass();

}