package org.broadleafcommerce.openadmin.server.dao;

import javax.persistence.EntityManager;

import org.broadleafcommerce.openadmin.server.domain.SandBox;

public class SandBoxEntityDaoImpl {

	protected EntityManager sandBoxEntityManager;
	
	public SandBox persist(SandBox entity) {
		sandBoxEntityManager.persist(entity);
		sandBoxEntityManager.flush();
		return entity;
	}
	
	public SandBox merge(SandBox entity) {
		SandBox response = sandBoxEntityManager.merge(entity);
		sandBoxEntityManager.flush();
		return response;
	}
	
	public SandBox retrieve(Class<SandBox> entityClass, Object primaryKey) {
		return sandBoxEntityManager.find(entityClass, primaryKey);
	}
	
	public EntityManager getSandBoxEntityManager() {
		return sandBoxEntityManager;
	}

	public void setSandBoxEntityManager(EntityManager sandBoxEntityManager) {
		this.sandBoxEntityManager = sandBoxEntityManager;
	}
}
