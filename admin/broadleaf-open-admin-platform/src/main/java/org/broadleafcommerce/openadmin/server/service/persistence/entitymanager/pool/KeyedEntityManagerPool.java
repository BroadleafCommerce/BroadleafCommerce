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
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 7/29/11
 * Time: 1:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class KeyedEntityManagerPool {

    private static final Log LOG = LogFactory.getLog(KeyedEntityManagerPool.class);

    protected DefaultEntityManagerPool defaultPool;
    protected GenericKeyedObjectPool sandboxPool;

    public KeyedEntityManagerPool(DefaultEntityManagerPool defaultPool) {
        this.defaultPool = defaultPool;
        sandboxPool = new GenericKeyedObjectPool(new PoolableSandBoxFactory());
    }

    public Object borrowObject(Object key) throws Exception {
        return sandboxPool.borrowObject(key);
    }

    public int getMaxActive() {
        return sandboxPool.getMaxActive();
    }

    public int getMaxIdle() {
        return sandboxPool.getMaxIdle();
    }

    public int getMaxTotal() {
        return sandboxPool.getMaxTotal();
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

    public long getTimeBetweenEvictionRunsMillis() {
        return sandboxPool.getTimeBetweenEvictionRunsMillis();
    }

    public byte getWhenExhaustedAction() {
        return sandboxPool.getWhenExhaustedAction();
    }

    public void returnObject(Object key, Object obj) throws Exception {
        sandboxPool.returnObject(key, obj);
    }

    public void setMaxActive(int maxActive) {
        sandboxPool.setMaxActive(maxActive);
    }

    public void setMaxIdle(int maxIdle) {
        sandboxPool.setMaxIdle(maxIdle);
    }

    public void setMaxTotal(int maxTotal) {
        sandboxPool.setMaxTotal(maxTotal);
    }

    public void setMaxWait(long maxWait) {
        sandboxPool.setMaxWait(maxWait);
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        sandboxPool.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
    }

    public void setMinIdle(int poolSize) {
        sandboxPool.setMinIdle(poolSize);
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        sandboxPool.setTestOnBorrow(testOnBorrow);
    }

    public void setTestOnReturn(boolean testOnReturn) {
        sandboxPool.setTestOnReturn(testOnReturn);
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

    public void setTestWhileIdle(boolean testWhileIdle) {
        sandboxPool.setTestWhileIdle(testWhileIdle);
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        sandboxPool.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
    }

    public void setWhenExhaustedAction(byte whenExhaustedAction) {
        sandboxPool.setWhenExhaustedAction(whenExhaustedAction);
    }

    public DefaultEntityManagerPool getDefaultPool() {
        return defaultPool;
    }

    public void setDefaultPool(DefaultEntityManagerPool defaultPool) {
        this.defaultPool = defaultPool;
    }

    private class PoolableSandBoxFactory implements KeyedPoolableObjectFactory {

        @Override
        public Object makeObject(Object key) throws Exception {
            return defaultPool.borrowObject();
        }

        @Override
        public void destroyObject(Object key, Object obj) throws Exception {
            defaultPool.invalidateObject(obj);
        }

        @Override
        public boolean validateObject(Object key, Object obj) {
            //TODO add a generic connection validation
            return true;
        }

        @Override
        public void activateObject(Object key, Object obj) throws Exception {
            //do nothing
        }

        @Override
        public void passivateObject(Object key, Object obj) throws Exception {
            //do nothing
        }

    }
}
