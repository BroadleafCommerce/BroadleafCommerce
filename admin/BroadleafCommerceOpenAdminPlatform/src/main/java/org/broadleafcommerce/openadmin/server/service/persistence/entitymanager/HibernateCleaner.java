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

package org.broadleafcommerce.openadmin.server.service.persistence.entitymanager;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.SessionFactory;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.engine.IdentifierValue;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tuple.IdentifierProperty;
import org.hibernate.type.Type;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 8/3/11
 * Time: 1:41 PM
 * To change this template use File | Settings | File Templates.
 */
@Service("blHibernateCleaner")
public class HibernateCleaner {

    protected Map typePool = Collections.synchronizedMap(new LRUMap(1000));

    public Object convertBean(Object originalBean, Method method, HibernateEntityManager em, PlatformTransactionManager txManager) throws Throwable {
        Class<?> starterClass = originalBean.getClass();
        Object targetBean = starterClass.newInstance();
        Field[] fields = getFields(starterClass);
        performConvert(originalBean, targetBean, fields, method, em, txManager);

        return targetBean;
    }

    protected void performConvert(Object originalBean, Object targetBean, Field[] fields, Method method, HibernateEntityManager em, PlatformTransactionManager txManager) throws Throwable {
        SessionFactory sessionFactory = em.getSession().getSessionFactory();
        ClassMetadata metadata = sessionFactory.getClassMetadata(originalBean.getClass());
        String idProperty = metadata.getIdentifierPropertyName();
        if (!typePool.containsKey(originalBean.getClass().getName())) {
            List<String> propertyNames = new ArrayList<String>();
            for (String propertyName : metadata.getPropertyNames()) {
                propertyNames.add(propertyName);
            }
            propertyNames.add(idProperty);
            List<Type> propertyTypes = new ArrayList<Type>();
            Type idType = metadata.getIdentifierType();
            for (Type propertyType : metadata.getPropertyTypes()) {
                propertyTypes.add(propertyType);
            }
            propertyTypes.add(idType);
            Map<String, Type> types = new HashMap<String, Type>();
            int j=0;
            for (String propertyName : propertyNames) {
                types.put(propertyName, propertyTypes.get(j));
                j++;
            }
            typePool.put(originalBean.getClass().getName(), types);
        }
        Map<String, Type> types = (Map<String, Type>) typePool.get(originalBean.getClass().getName());
        Field idField = null;
        for (Field field : fields) {
            if (types.containsKey(field.getName())) {
                field.setAccessible(true);
                Type fieldType = types.get(field.getName());
                if (fieldType.isCollectionType() || fieldType.isAnyType()) {
                    //field.set(targetBean, null);
                    //do nothing
                } else if(fieldType.isEntityType()) {
                    Object newOriginalBean = field.get(originalBean);
                    if (newOriginalBean == null) {
                        field.set(targetBean, null);
                    } else {
                        Object newTargetBean = newOriginalBean.getClass().newInstance();
                        field.set(targetBean, newTargetBean);
                        Field[] newFields = getFields(newOriginalBean.getClass());
                        performConvert(newOriginalBean, newTargetBean, newFields, method, em, txManager);
                    }
                } else {
                    field.set(targetBean, field.get(originalBean));
                }
                if (field.getName().equals(idProperty)) {
                    idField = field;
                }
            }
        }
        if (txManager != null) {
            Object temp = null;
            if (idField == null) {
                throw new Exception("Unable to find an identity field for the entity: " + originalBean.getClass().getName());
            }
            final Serializable primaryKey = (Serializable) idField.get(originalBean);
            if (primaryKey != null) {
                 temp = em.find(originalBean.getClass(), primaryKey);
            }

            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

            TransactionStatus status = txManager.getTransaction(def);
            try {
                if (primaryKey != null) {
                    if (temp != null && method.getName().equals("merge")) {
                        targetBean = em.merge(targetBean);
                    } else {
                        SessionImplementor session = (SessionImplementor) em.getDelegate();
                        EntityPersister persister = session.getEntityPersister(targetBean.getClass().getName(), targetBean);
                        IdentifierProperty ip = persister.getEntityMetamodel().getIdentifierProperty();
                        synchronized (ip) {
                            IdentifierValue backupUnsavedValue = setUnsavedValue(ip, IdentifierValue.ANY);
                            em.persist(targetBean);
                            setUnsavedValue(ip, backupUnsavedValue);
                        }
                    }
                } else {
                    targetBean = method.invoke(em, targetBean);
                }
            } catch (Throwable ex) {
              txManager.rollback(status);
              throw ex;
            }
            txManager.commit(status);
        }
    }

    public IdentifierValue setUnsavedValue(IdentifierProperty ip, IdentifierValue newUnsavedValue) throws Throwable {
      IdentifierValue backup = ip.getUnsavedValue();
      Field f = ip.getClass().getDeclaredField("unsavedValue");
      f.setAccessible(true);
      f.set(ip, newUnsavedValue);
      return backup;
    }

    protected Field[] getFields(Class<?> clazz) {
        Field[] fields = new Field[]{};
        Class<?> myClass = clazz;
        boolean eof = false;
        while (!eof) {
            Field[] temp = myClass.getDeclaredFields();
            fields = (Field[]) ArrayUtils.addAll(temp, fields);
            if (myClass.getSuperclass() != null) {
                myClass = myClass.getSuperclass();
            } else {
                eof = true;
            }
        }

        return fields;
    }
}
