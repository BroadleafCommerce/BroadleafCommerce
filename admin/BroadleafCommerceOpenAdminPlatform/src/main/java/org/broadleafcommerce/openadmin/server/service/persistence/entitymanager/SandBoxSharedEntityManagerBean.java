package org.broadleafcommerce.openadmin.server.service.persistence.entitymanager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.jpa.EntityManagerFactoryAccessor;
import org.springframework.orm.jpa.SharedEntityManagerCreator;

public class SandBoxSharedEntityManagerBean extends EntityManagerFactoryAccessor implements FactoryBean<EntityManager>, InitializingBean {

	private EntityManager shared;
	protected EntityManagerFactory sandBoxEntityManagerFactory;

	public final void afterPropertiesSet() {
		EntityManagerFactory emf = getEntityManagerFactory();
		if (emf == null) {
			throw new IllegalArgumentException("entityManagerFactory is required");
		}
		EntityManagerFactory sandBox = getSandBoxEntityManagerFactory();
		if (sandBox == null) {
			throw new IllegalArgumentException("sandBoxEntityManagerFactory is required");
		}
		Class[] ifcs = new Class[] {EntityManager.class};
		EntityManager standardEm = SharedEntityManagerCreator.createSharedEntityManager(emf, getJpaPropertyMap(), ifcs);
		EntityManager sandBoxEm = SharedEntityManagerCreator.createSharedEntityManager(sandBox, getJpaPropertyMap(), ifcs);
		this.shared = new BroadleafEntityManager(standardEm, sandBoxEm);
	}

	public EntityManager getObject() {
		return this.shared;
	}

	public Class<? extends EntityManager> getObjectType() {
		return EntityManager.class;
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

}
