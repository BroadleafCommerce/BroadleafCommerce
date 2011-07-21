package org.broadleafcommerce.openadmin.server.service.persistence.entitymanager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.ejb.HibernateEntityManager;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.jpa.EntityManagerFactoryAccessor;
import org.springframework.orm.jpa.SharedEntityManagerCreator;

public class SandBoxSharedEntityManagerBean extends EntityManagerFactoryAccessor implements FactoryBean<EntityManager>, InitializingBean {

	private HibernateEntityManager shared;
	protected EntityManagerFactory sandBoxEntityManagerFactory;

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
		this.shared = new BroadleafEntityManager((HibernateEntityManager) standardEm, (HibernateEntityManager) sandBoxEm);
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

}
