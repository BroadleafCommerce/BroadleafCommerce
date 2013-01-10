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

package org.broadleafcommerce.openadmin.server.service.persistence.entitymanager.pool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.broadleafcommerce.openadmin.server.service.persistence.datasource.SandBoxDataSource;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;

import javax.persistence.EntityManager;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 7/29/11
 * Time: 1:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultEntityManagerPool extends GenericObjectPool implements ApplicationContextAware {

    private static final Log LOG = LogFactory.getLog(DefaultEntityManagerPool.class);

    protected String mySharedEntityManagerBeanRef;
    private ApplicationContext applicationContext;

    protected GenericObjectPool sandboxPool;

    public DefaultEntityManagerPool() {
        sandboxPool = new GenericObjectPool(new PoolableSandBoxFactory());
    }

    public void invalidateObject(Object obj) throws Exception {
        sandboxPool.invalidateObject(obj);
    }

    public Object borrowObject() throws Exception {
        return sandboxPool.borrowObject();
    }

    public int getMaxActive() {
        return sandboxPool.getMaxActive();
    }

    public int getMaxIdle() {
        return sandboxPool.getMaxIdle();
    }

    public long getMaxWait() {
        return sandboxPool.getMaxWait();
    }

    public long getMinEvictableIdleTimeMillis() {
        return sandboxPool.getMinEvictableIdleTimeMillis();
    }

    public int getMinIdle() {
        return sandboxPool.getMinIdle();
    }

    public int getNumActive() {
        return sandboxPool.getNumActive();
    }

    public boolean getTestOnBorrow() {
        return sandboxPool.getTestOnBorrow();
    }

    public boolean getTestOnReturn() {
        return sandboxPool.getTestOnReturn();
    }

    public boolean getTestWhileIdle() {
        return sandboxPool.getTestWhileIdle();
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return sandboxPool.getTimeBetweenEvictionRunsMillis();
    }

    public byte getWhenExhaustedAction() {
        return sandboxPool.getWhenExhaustedAction();
    }

    public void returnObject(Object obj) throws Exception {
        sandboxPool.returnObject(obj);
    }

    public void setMaxActive(int maxActive) {
        sandboxPool.setMaxActive(maxActive);
    }

    public void setMaxIdle(int maxIdle) {
        sandboxPool.setMaxIdle(maxIdle);
    }

    public void setMaxWait(long maxWait) {
        sandboxPool.setMaxWait(maxWait);
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        sandboxPool.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
    }

    public void setMinIdle(int minIdle) {
        sandboxPool.setMinIdle(minIdle);
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        sandboxPool.setTestOnBorrow(testOnBorrow);
    }

    public void setTestOnReturn(boolean testOnReturn) {
        sandboxPool.setTestOnReturn(testOnReturn);
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        sandboxPool.setTestWhileIdle(testWhileIdle);
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        sandboxPool.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
    }

    public void setWhenExhaustedAction(byte whenExhaustedAction) {
        sandboxPool.setWhenExhaustedAction(whenExhaustedAction);
    }

    public String getMySharedEntityManagerBeanRef() {
        return mySharedEntityManagerBeanRef;
    }

    public void setMySharedEntityManagerBeanRef(String mySharedEntityManagerBeanRef) {
        this.mySharedEntityManagerBeanRef = mySharedEntityManagerBeanRef;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private class PoolableSandBoxFactory implements PoolableObjectFactory {

        @Override
        public Object makeObject() throws Exception {
            return applicationContext.getBean(mySharedEntityManagerBeanRef);
        }

        @Override
        public void destroyObject(Object obj) throws Exception {
            EntityManager em = (EntityManager) obj;
            try {
                ((SandBoxDataSource) ((EntityManagerFactoryInfo) em.getEntityManagerFactory()).getDataSource()).close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            em.close();
        }

        @Override
        public boolean validateObject(Object obj) {
            //TODO add a generic connection validation
            return true;
        }

        @Override
        public void activateObject(Object obj) throws Exception {
            //do nothing
        }

        @Override
        public void passivateObject(Object obj) throws Exception {
            //do nothing
        }

    }
    
}
