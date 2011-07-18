package org.broadleafcommerce.openadmin.server.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.springframework.stereotype.Repository;

@Repository("blSandBoxEntityDao")
public class SandBoxEntityDaoImpl implements SandBoxEntityDao {

	@PersistenceContext(unitName = "blSandboxPU")
	protected EntityManager sandBoxEntityManager;
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.dao.SandBoxEntityDao#persist(org.broadleafcommerce.openadmin.server.domain.SandBox)
	 */
	@Override
	public SandBox persist(SandBox entity) {
		sandBoxEntityManager.persist(entity);
		sandBoxEntityManager.flush();
		return entity;
	}
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.dao.SandBoxEntityDao#merge(org.broadleafcommerce.openadmin.server.domain.SandBox)
	 */
	@Override
	public SandBox merge(SandBox entity) {
		SandBox response = sandBoxEntityManager.merge(entity);
		sandBoxEntityManager.flush();
		return response;
	}
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.dao.SandBoxEntityDao#retrieve(java.lang.Class, java.lang.Object)
	 */
	@Override
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
