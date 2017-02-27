/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.persistence;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.util.StringUtil;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelperImpl;
import org.hibernate.Session;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Component("blEntityConfiguration")
public class EntityConfiguration implements ApplicationContextAware {

    private static final Log LOG = LogFactory.getLog(EntityConfiguration.class);

    private ApplicationContext webApplicationContext;
    private final HashMap<String, Class<?>> entityMap = new HashMap<String, Class<?>>(50);
    private ApplicationContext applicationcontext;
    private Resource[] entityContexts;

    @javax.annotation.Resource(name="blMergedEntityContexts")
    protected Set<String> mergedEntityContexts;

    @Autowired
    protected List<EntityManager> entityManagers;

    protected DynamicDaoHelperImpl daoHelper = new DynamicDaoHelperImpl();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.webApplicationContext = applicationContext;
    }

    @PostConstruct
    public void configureMergedItems() {
        Set<Resource> temp = new LinkedHashSet<Resource>();
        if (mergedEntityContexts != null && !mergedEntityContexts.isEmpty()) {
            for (String location : mergedEntityContexts) {
                temp.add(webApplicationContext.getResource(location));
            }
        }
        if (entityContexts != null) {
            for (Resource resource : entityContexts) {
                temp.add(resource);
            }
        }
        entityContexts = temp.toArray(new Resource[temp.size()]);
        applicationcontext = new GenericXmlApplicationContext(entityContexts);
    }

    public String[] getEntityBeanNames() {
        return applicationcontext.getBeanDefinitionNames();
    }

    public Class<?> lookupEntityClass(String beanId) {
        Class<?> clazz = entityMap.get(beanId);

        if (clazz == null) {
            clazz = retrieveEntityClass(beanId);
            entityMap.put(beanId, clazz);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Returning class (" + clazz.getName() + ") configured with bean id (" + StringUtil.sanitize(beanId) + ')');
        }
        return clazz;
    }

    public <T> Class<T> lookupEntityClass(String beanId, Class<T> resultClass) {
        Class<T> clazz = (Class<T>) entityMap.get(beanId);

        if (clazz == null) {
            clazz = (Class<T>) retrieveEntityClass(beanId);
            entityMap.put(beanId, clazz);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Returning class (" + clazz.getName() + ") configured with bean id (" + beanId + ')');
        }
        return clazz;
    }

    protected Class<?> retrieveEntityClass(String beanId) {
        try {
            return applicationcontext.getBean(beanId).getClass();
        } catch (NoSuchBeanDefinitionException e) {
            return retrieveEntityClassFromEntityManagers(beanId);
        }
    }

    protected Class<?> retrieveEntityClassFromEntityManagers(String beanId) {
        Class<?> beanIdClass = getClassForName(beanId);

        for (EntityManager em : entityManagers) {
            Class<?>[] entitiesFromCeiling = daoHelper.getAllPolymorphicEntitiesFromCeiling(beanIdClass, em.unwrap(Session.class).getSessionFactory(), true, true);

            if (ArrayUtils.isNotEmpty(entitiesFromCeiling)) {
                return entitiesFromCeiling[entitiesFromCeiling.length - 1];
            }
        }

        throw new RuntimeException("Unable to retrieve the entity class for the given bean id: " + beanId);
    }

    protected Class<?> getClassForName(String beanId) {
        try {
            return Class.forName(beanId);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Object createEntityInstance(String beanId) {
        Object bean = applicationcontext.getBean(beanId);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Returning instance of class (" + bean.getClass().getName() + ") configured with bean id (" + beanId + ')');
        }
        return bean;
    }

    public <T> T createEntityInstance(String beanId, Class<T> resultClass) {
        T bean = (T) applicationcontext.getBean(beanId);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Returning instance of class (" + bean.getClass().getName() + ") configured with bean id (" + beanId + ')');
        }
        return bean;
    }

    public Resource[] getEntityContexts() {
        return entityContexts;
    }

    public void setEntityContexts(Resource[] entityContexts) {
        this.entityContexts = entityContexts;
    }
}
