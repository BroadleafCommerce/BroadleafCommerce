/*
 * #%L
 * BroadleafCommerce Profile Web
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

package org.broadleafcommerce.common.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelperImpl;
import org.broadleafcommerce.common.util.dao.TypedQueryBuilder;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;


@Repository("blGenericEntityDao")
public class GenericEntityDaoImpl implements GenericEntityDao, ApplicationContextAware {

    private static ApplicationContext applicationContext;
    private static GenericEntityDaoImpl dao;

    public static GenericEntityDaoImpl getGenericEntityDao() {
        if (applicationContext == null) {
            return null;
        }
        if (dao == null) {
            dao = (GenericEntityDaoImpl) applicationContext.getBean("blGenericEntityDao");
        }
        return dao;
    }

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;
    
    protected DynamicDaoHelperImpl daoHelper = new DynamicDaoHelperImpl();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    @Override
    public <T> T readGenericEntity(Class<T> clazz, Object id) {
        Map<String, Object> md = daoHelper.getIdMetadata(clazz, (HibernateEntityManager) em);
        AbstractSingleColumnStandardBasicType type = (AbstractSingleColumnStandardBasicType) md.get("type");
        
        if (type instanceof LongType) {
            id = Long.parseLong(String.valueOf(id));
        } else if (type instanceof IntegerType) {
            id = Integer.parseInt(String.valueOf(id));
        }

        return em.find(clazz, id);
    }

    @Override
    public <T> Long readCountGenericEntity(Class<T> clazz) {
        TypedQuery<Long> q = new TypedQueryBuilder<T>(clazz, "root").toCountQuery(em);
        return q.getSingleResult();
    }

    @Override
    public <T> List<T> readAllGenericEntity(Class<T> clazz, int limit, int offset) {
        TypedQuery<T> q = new TypedQueryBuilder<T>(clazz, "root").toQuery(em);
        q.setMaxResults(limit);
        q.setFirstResult(offset);
        return q.getResultList();
    }

    @Override
    public <T> List<T> readAllGenericEntity(Class<T> clazz) {
        TypedQuery<T> q = new TypedQueryBuilder<T>(clazz, "root").toQuery(em);
        return q.getResultList();
    }

    @Override
    public List<Long> readAllGenericEntityId(Class<?> clazz) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root root = criteria.from(clazz);
        criteria.select(root.get(getIdField(clazz).getName()).as(Long.class));
        criteria.orderBy(builder.asc(root.get(getIdField(clazz).getName())));

        return em.createQuery(criteria).getResultList();
    }

    @Override
    public Class<?> getImplClass(String className) {
        Class<?> clazz = null;
        try {
            clazz = entityConfiguration.lookupEntityClass(className);
        } catch (NoSuchBeanDefinitionException e) {
            //do nothing
        }
        if (clazz == null) {
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return clazz;
    }
    
    @Override
    public Class<?> getCeilingImplClass(String className) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Class<?>[] entitiesFromCeiling = daoHelper.getAllPolymorphicEntitiesFromCeiling(clazz, em.unwrap(Session.class).getSessionFactory(), true, true);
        clazz = entitiesFromCeiling[entitiesFromCeiling.length - 1];
        return clazz;
    }

    @Override
    public Serializable getIdentifier(Object entity) {
        return daoHelper.getIdentifier(entity, em);
    }

    protected Field getIdField(Class<?> clazz) {
        return daoHelper.getIdField(clazz, em);
    }
    
    @Override
    public <T> T save(T object) {
        return em.merge(object);
    }

    @Override
    public void persist(Object object) {
        em.persist(object);
    }

    @Override
    public void remove(Object object) {
        em.remove(object);
    }

    @Override
    public void flush() {
        em.flush();
    }

    @Override
    public void clearAutoFlushMode() {
        em.unwrap(Session.class).setFlushMode(FlushMode.MANUAL);
    }

    @Override
    public void enableAutoFlushMode() {
        em.unwrap(Session.class).setFlushMode(FlushMode.AUTO);
    }

    @Override
    public void clear() {
        em.clear();
    }

    @Override
    public boolean sessionContains(Object object) {
        return em.contains(object);
    }

    @Override
    public boolean idAssigned(Object object) {
        return getIdentifier(object) != null;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}
