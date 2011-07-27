package org.broadleafcommerce.openadmin.server.service.persistence.entitymanager;

import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.broadleafcommerce.openadmin.server.service.SandBoxContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.persistence.EntityManager;

public class SandBoxEntityManagerPoolFactoryBean implements ApplicationContextAware, FactoryBean<EntityManager> {

	protected String mySharedEntityManagerBeanRef;
	protected GenericKeyedObjectPool sandboxPool;
	private ApplicationContext applicationContext;
	
	public SandBoxEntityManagerPoolFactoryBean() {
		sandboxPool = new GenericKeyedObjectPool(new PoolableSandBoxFactory());
	}

	@Override
	public EntityManager getObject() throws Exception {
        String sandBoxName = SandBoxContext.getSandBoxContext().getSandBoxName();
		return (EntityManager) sandboxPool.borrowObject(sandBoxName);
	}

	@Override
	public Class<?> getObjectType() {
		return EntityManager.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	//GenericKeyedObjectPool methods
	
	public void returnObject(Object obj) throws Exception {
		sandboxPool.returnObject(SandBoxContext.getSandBoxContext().getSandBoxName(), obj);
	}
	
	public int getMaxActive() {
		return sandboxPool.getMaxActive();
	}

	public void setMaxActive(int maxActive) {
		sandboxPool.setMaxActive(maxActive);
	}

	public int getMaxTotal() {
		return sandboxPool.getMaxTotal();
	}

	public void setMaxTotal(int maxTotal) {
		sandboxPool.setMaxTotal(maxTotal);
	}

	public byte getWhenExhaustedAction() {
		return sandboxPool.getWhenExhaustedAction();
	}

	public void setWhenExhaustedAction(byte whenExhaustedAction) {
		sandboxPool.setWhenExhaustedAction(whenExhaustedAction);
	}

	public long getMaxWait() {
		return sandboxPool.getMaxWait();
	}

	public void setMaxWait(long maxWait) {
		sandboxPool.setMaxWait(maxWait);
	}

	public int getMaxIdle() {
		return sandboxPool.getMaxIdle();
	}

	public void setMaxIdle(int maxIdle) {
		sandboxPool.setMaxIdle(maxIdle);
	}

	public void setMinIdle(int poolSize) {
		sandboxPool.setMinIdle(poolSize);
	}

	public int getMinIdle() {
		return sandboxPool.getMinIdle();
	}

	public long getTimeBetweenEvictionRunsMillis() {
		return sandboxPool.getTimeBetweenEvictionRunsMillis();
	}

	public void setTimeBetweenEvictionRunsMillis(
			long timeBetweenEvictionRunsMillis) {
		sandboxPool
				.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
	}

	public long getMinEvictableIdleTimeMillis() {
		return sandboxPool.getMinEvictableIdleTimeMillis();
	}

	public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
		sandboxPool
				.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
	}

	public boolean getLifo() {
		return sandboxPool.getLifo();
	}

	public void setLifo(boolean lifo) {
		sandboxPool.setLifo(lifo);
	}

	public int getNumActive() {
		return sandboxPool.getNumActive();
	}

	public int getNumIdle() {
		return sandboxPool.getNumIdle();
	}

	public int getNumActive(Object key) {
		return sandboxPool.getNumActive(key);
	}

	public int getNumIdle(Object key) {
		return sandboxPool.getNumIdle(key);
	}
	
	private class PoolableSandBoxFactory implements KeyedPoolableObjectFactory {

		@Override
		public Object makeObject(Object key) throws Exception {
			return applicationContext.getBean(mySharedEntityManagerBeanRef);
		}

		@Override
		public void destroyObject(Object key, Object obj) throws Exception {
			EntityManager entityManager = (EntityManager) obj;
			entityManager.close();
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

	public String getMySharedEntityManagerBeanRef() {
		return mySharedEntityManagerBeanRef;
	}

	public void setMySharedEntityManagerBeanRef(String mySharedEntityManagerBeanRef) {
		this.mySharedEntityManagerBeanRef = mySharedEntityManagerBeanRef;
	}
	
}
