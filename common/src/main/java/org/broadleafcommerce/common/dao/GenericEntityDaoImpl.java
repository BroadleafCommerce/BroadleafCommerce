/*
 * #%L
 * BroadleafCommerce Profile Web
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

package org.broadleafcommerce.common.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.util.StreamCapableTransactionalOperationAdapter;
import org.broadleafcommerce.common.util.StreamingTransactionCapableUtil;
import org.broadleafcommerce.common.util.TransactionUtils;
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
import org.springframework.transaction.support.TransactionSynchronizationAdapter;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
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

    @Resource(name = "blStreamingTransactionCapableUtil")
    protected StreamingTransactionCapableUtil transactionUtil;
    
    protected DynamicDaoHelperImpl daoHelper = new DynamicDaoHelperImpl();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    @Override
    public <T> T readGenericEntity(Class<T> clazz, Object id) {
        clazz = (Class<T>) DynamicDaoHelperImpl.getNonProxyImplementationClassIfNecessary(clazz);
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
        clazz = (Class<T>) DynamicDaoHelperImpl.getNonProxyImplementationClassIfNecessary(clazz);
        TypedQuery<Long> q = new TypedQueryBuilder<T>(clazz, "root").toCountQuery(em);
        return q.getSingleResult();
    }

    @Override
    public <T> List<T> readAllGenericEntity(Class<T> clazz, int limit, int offset) {
        clazz = (Class<T>) DynamicDaoHelperImpl.getNonProxyImplementationClassIfNecessary(clazz);
        TypedQuery<T> q = new TypedQueryBuilder<T>(clazz, "root").toQuery(em);
        q.setMaxResults(limit);
        q.setFirstResult(offset);
        return q.getResultList();
    }

    @Override
    public <T> List<T> readAllGenericEntity(Class<T> clazz) {
        clazz = (Class<T>) DynamicDaoHelperImpl.getNonProxyImplementationClassIfNecessary(clazz);
        TypedQuery<T> q = new TypedQueryBuilder<T>(clazz, "root").toQuery(em);
        return q.getResultList();
    }

    @Override
    public List<Long> readAllGenericEntityId(Class<?> clazz) {
        clazz = DynamicDaoHelperImpl.getNonProxyImplementationClassIfNecessary(clazz);
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
            clazz = DynamicDaoHelperImpl.getNonProxyImplementationClassIfNecessary(clazz);
        }
        return clazz;
    }
    
    @Override
    public Class<?> getCeilingImplClass(final String className) {
        final Class<?>[] clazz = new Class<?>[1];
        try {
            clazz[0] = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        //em.unwrap requires a transactional entity manager. We'll only take the hit to start a transaction here if one has not already been started.
        transactionUtil.runOptionalTransactionalOperation(new StreamCapableTransactionalOperationAdapter() {
            @Override
            public void execute() throws Throwable {
                Class<?>[] entitiesFromCeiling = daoHelper.getAllPolymorphicEntitiesFromCeiling(clazz[0], em.unwrap(Session.class).getSessionFactory(), true, true);
                if (entitiesFromCeiling == null || entitiesFromCeiling.length < 1) {
                    clazz[0] = DynamicDaoHelperImpl.getNonProxyImplementationClassIfNecessary(clazz[0]);
                    entitiesFromCeiling = daoHelper.getAllPolymorphicEntitiesFromCeiling(clazz[0], em.unwrap(Session.class).getSessionFactory(), true, true);
                }
                if (entitiesFromCeiling == null || entitiesFromCeiling.length < 1) {
                    throw new IllegalArgumentException(String.format("Unable to find ceiling implementation for the requested class name (%s)", className));
                }
                clazz[0] = entitiesFromCeiling[entitiesFromCeiling.length - 1];
            }
        }, RuntimeException.class, !TransactionUtils.isTransactionalEntityManager(em));
        return clazz[0];
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

    @Override
    public List<Long> readOtherEntitiesWithPropertyValue(Serializable instance, String propertyName, String value) {
        Class clazz = DynamicDaoHelperImpl.getNonProxyImplementationClassIfNecessary(instance.getClass());

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root root = criteria.from(clazz);
        Path idField = root.get(getIdField(clazz).getName());
        criteria.select(idField.as(Long.class));

        List<Predicate> restrictions = new ArrayList<Predicate>();
        restrictions.add(builder.equal(root.get(propertyName), value));
        restrictions.add(builder.notEqual(idField, getIdentifier(instance)));

        if (instance instanceof Status) {
            restrictions.add(builder.or(
                    builder.isNull(root.get("archiveStatus").get("archived")),
                    builder.equal(root.get("archiveStatus").get("archived"), 'N')));
        }

        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));

        return em.createQuery(criteria).getResultList();
    }
}
