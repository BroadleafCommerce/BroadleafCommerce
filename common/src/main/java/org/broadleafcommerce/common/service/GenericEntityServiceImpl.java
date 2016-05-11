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

package org.broadleafcommerce.common.service;

import org.broadleafcommerce.common.dao.GenericEntityDao;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;


@Service("blGenericEntityService")
public class GenericEntityServiceImpl implements GenericEntityService {
    
    @Resource(name = "blGenericEntityDao")
    protected GenericEntityDao genericEntityDao;
    
    @Override
    public Object readGenericEntity(String className, Object id) {
        Class<?> clazz = genericEntityDao.getImplClass(className);
        return genericEntityDao.readGenericEntity(clazz, id);
    }

    @Override
    public <T> T readGenericEntity(Class<T> clazz, Object id) {
        return genericEntityDao.readGenericEntity(clazz, id);
    }
    
    @Override
    public <T> T save(T object) {
        return genericEntityDao.save(object);
    }

    public void persist(Object object) {
        genericEntityDao.persist(object);
    }

    @Override
    public <T> Long readCountGenericEntity(Class<T> clazz) {
        return genericEntityDao.readCountGenericEntity(clazz);
    }

    @Override
    public <T> List<T> readAllGenericEntity(Class<T> clazz, int limit, int offset) {
        return genericEntityDao.readAllGenericEntity(clazz, limit, offset);
    }

    @Override
    public List<Long> readAllGenericEntityId(Class<?> clazz) {
        return genericEntityDao.readAllGenericEntityId(clazz);
    }

    @Override
    public Serializable getIdentifier(Object entity) {
        return genericEntityDao.getIdentifier(entity);
    }

    @Override
    public void flush() {
        genericEntityDao.flush();
    }

    @Override
    public void clearAutoFlushMode() {
        genericEntityDao.clearAutoFlushMode();
    }

    @Override
    public void enableAutoFlushMode() {
        genericEntityDao.enableAutoFlushMode();
    }

    @Override
    public void clear() {
        genericEntityDao.clear();
    }

    @Override
    public boolean sessionContains(Object object) {
        return genericEntityDao.sessionContains(object);
    }

    @Override
    public Class<?> getCeilingImplClass(String className) {
        return genericEntityDao.getCeilingImplClass(className);
    }

    @Override
    public boolean idAssigned(Object object) {
        return genericEntityDao.idAssigned(object);
    }

    @Override
    public EntityManager getEntityManager() {
        return genericEntityDao.getEntityManager();
    }

    @Override
    public void remove(Object object) {
        genericEntityDao.remove(object);
    }
}
