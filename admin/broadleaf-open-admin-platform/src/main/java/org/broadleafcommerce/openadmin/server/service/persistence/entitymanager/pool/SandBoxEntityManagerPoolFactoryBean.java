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

import org.broadleafcommerce.openadmin.server.service.SandBoxContext;
import org.springframework.beans.factory.FactoryBean;

import javax.persistence.EntityManager;

public class SandBoxEntityManagerPoolFactoryBean implements FactoryBean<EntityManager> {

    protected KeyedEntityManagerPool sandboxPool;

    @Override
    public EntityManager getObject() throws Exception {
        String sandBoxName = SandBoxContext.getSandBoxContext().getSandBoxName();
        return (EntityManager) sandboxPool.borrowObject(sandBoxName);
    }

    public void returnObject(Object obj) throws Exception {
        String sandBoxName = SandBoxContext.getSandBoxContext().getSandBoxName();
        getSandboxPool().returnObject(sandBoxName, obj);
    }

    @Override
    public Class<?> getObjectType() {
        return EntityManager.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    public KeyedEntityManagerPool getSandboxPool() {
        return sandboxPool;
    }

    public void setSandboxPool(KeyedEntityManagerPool sandboxPool) {
        this.sandboxPool = sandboxPool;
    }
}
