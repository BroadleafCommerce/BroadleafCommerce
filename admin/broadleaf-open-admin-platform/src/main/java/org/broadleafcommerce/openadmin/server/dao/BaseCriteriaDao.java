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

package org.broadleafcommerce.openadmin.server.dao;

import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author jfischer
 *
 * @param <T>
 */
public interface BaseCriteriaDao<T extends Serializable> {

    public abstract List<T> query(PersistentEntityCriteria entityCriteria, Class<?> targetEntityClass);

    public abstract int count(PersistentEntityCriteria entityCriteria, Class<?> targetEntityClass);

    public abstract EntityManager getStandardEntityManager();
    
    public abstract int count(PersistentEntityCriteria entityCriteria);
    
    public abstract List<T> query(PersistentEntityCriteria entityCriteria);
    
    public abstract Class<? extends Serializable> getEntityClass();

}