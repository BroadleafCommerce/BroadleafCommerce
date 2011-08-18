package org.broadleafcommerce.openadmin.server.dao;

import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItem;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

//@Repository("blSandBoxEntityDao")
public class SandBoxEntityDaoImpl implements SandBoxEntityDao {

	//@PersistenceContext(unitName = "blSandboxPU")
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
	public SandBox retrieve(Object primaryKey) {
		return sandBoxEntityManager.find(SandBox.class, primaryKey);
	}
	
	public EntityManager getSandBoxEntityManager() {
		return sandBoxEntityManager;
	}

	public void setSandBoxEntityManager(EntityManager sandBoxEntityManager) {
		this.sandBoxEntityManager = sandBoxEntityManager;
	}

    public SandBox readSandBoxByName(String name) {
        Query query = sandBoxEntityManager.createNamedQuery("BC_READ_SANDBOX_BY_NAME");
        query.setParameter("name", name);
        SandBox response = null;
        try {
            response = (SandBox) query.getSingleResult();
        } catch (NoResultException e) {
            //do nothing - there is no sandbox
        }
        return response;
    }

    @Override
	public SandBoxItem retrieveSandBoxItemByTemporaryId(Object temporaryId) {
		Query query = sandBoxEntityManager.createNamedQuery("BC_READ_SANDBOX_ITEM_BY_TEMPORARY_ID");
        query.setParameter("temporaryId", temporaryId);
        SandBoxItem response = null;
        try {
            response = (SandBoxItem) query.getSingleResult();
        } catch (NoResultException e) {
            //do nothing - there is no sandbox
        }
        return response;
	}

    public void deleteItem(SandBoxItem sandBoxItem) {
    	if (!sandBoxEntityManager.contains(sandBoxItem)) {
    		sandBoxItem = retrieveSandBoxItemByTemporaryId(sandBoxItem.getTemporaryId());
    	}
        sandBoxEntityManager.remove(sandBoxItem);
    }
}
