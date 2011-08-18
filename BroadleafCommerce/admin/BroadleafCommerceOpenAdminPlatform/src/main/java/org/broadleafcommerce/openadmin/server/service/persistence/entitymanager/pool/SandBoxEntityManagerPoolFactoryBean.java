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
