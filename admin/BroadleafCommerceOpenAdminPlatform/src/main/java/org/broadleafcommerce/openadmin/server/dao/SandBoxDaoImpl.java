package org.broadleafcommerce.openadmin.server.dao;

import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.domain.SandBoxImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository("blSandBoxDao")
public class SandBoxDaoImpl implements SandBoxDao {

	@PersistenceContext(unitName = "blPU")
	protected EntityManager sandBoxEntityManager;
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.dao.SandBoxEntityDao#retrieve(java.lang.Class, java.lang.Object)
	 */
	@Override
	public SandBox retrieve(Long id) {
		return sandBoxEntityManager.find(SandBoxImpl.class, id);
	}
}
