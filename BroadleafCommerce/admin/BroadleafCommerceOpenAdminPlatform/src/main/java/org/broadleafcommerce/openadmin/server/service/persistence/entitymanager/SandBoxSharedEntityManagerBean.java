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
