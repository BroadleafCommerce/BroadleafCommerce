/*
 * #%L
 * BroadleafCommerce Common Libraries
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

package org.broadleafcommerce.common.util.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

/**
 * Provides utility methods for interacting with dynamic entities
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface DynamicDaoHelper {

    public Map<String, Object> getIdMetadata(Class<?> entityClass, HibernateEntityManager entityManager);
    
    public List<String> getPropertyNames(Class<?> entityClass, HibernateEntityManager entityManager);
    
    public List<Type> getPropertyTypes(Class<?> entityClass, HibernateEntityManager entityManager);
    
    public SessionFactory getSessionFactory(HibernateEntityManager entityManager);

    public Class<?>[] getAllPolymorphicEntitiesFromCeiling(Class<?> ceilingClass, SessionFactory sessionFactory, boolean includeUnqualifiedPolymorphicEntities, boolean useCache);

    public Class<?>[] sortEntities(Class<?> ceilingClass, List<Class<?>> entities);

    public boolean isExcludeClassFromPolymorphism(Class<?> clazz);

    Serializable getIdentifier(Object entity, EntityManager em);

    Serializable getIdentifier(Object entity, Session session);

    Field getIdField(Class<?> clazz, EntityManager em);

    Field getIdField(Class<?> clazz, Session session);

}
