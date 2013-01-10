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

import org.hibernate.ejb.HibernateEntityManager;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.jpa.EntityManagerFactoryAccessor;
import org.springframework.orm.jpa.SharedEntityManagerCreator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.lang.reflect.Proxy;

public class SandBoxSharedEntityManagerBean extends EntityManagerFactoryAccessor implements FactoryBean<EntityManager>, InitializingBean {

    private HibernateEntityManager shared;
    protected EntityManagerFactory sandBoxEntityManagerFactory;
    protected HibernateCleaner cleaner;

    @SuppressWarnings("rawtypes")
    public final void afterPropertiesSet() {
        EntityManagerFactory emf = getEntityManagerFactory();
        if (emf == null) {
            throw new IllegalArgumentException("entityManagerFactory is required");
        }
        EntityManagerFactory sandBox = getSandBoxEntityManagerFactory();
        if (sandBox == null) {
            throw new IllegalArgumentException("sandBoxEntityManagerFactory is required");
        }
        Class[] ifcs = new Class[] {HibernateEntityManager.class};
        EntityManager standardEm = SharedEntityManagerCreator.createSharedEntityManager(emf, getJpaPropertyMap(), ifcs);
        EntityManager sandBoxEm = SharedEntityManagerCreator.createSharedEntityManager(sandBox, getJpaPropertyMap(), ifcs);
        BroadleafEntityManagerInvocationHandler handler = new BroadleafEntityManagerInvocationHandler((HibernateEntityManager) standardEm, (HibernateEntityManager) sandBoxEm, cleaner);
        HibernateEntityManager proxy = (HibernateEntityManager) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{HibernateEntityManager.class, DualEntityManager.class}, handler);
        this.shared = proxy;
    }

    public EntityManager getObject() {
        return this.shared;
    }

    public Class<? extends EntityManager> getObjectType() {
        return HibernateEntityManager.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public EntityManagerFactory getSandBoxEntityManagerFactory() {
        return sandBoxEntityManagerFactory;
    }

    public void setSandBoxEntityManagerFactory(
            EntityManagerFactory sandBoxEntityManagerFactory) {
        this.sandBoxEntityManagerFactory = sandBoxEntityManagerFactory;
    }

    public HibernateCleaner getCleaner() {
        return cleaner;
    }

    public void setCleaner(HibernateCleaner cleaner) {
        this.cleaner = cleaner;
    }
}
