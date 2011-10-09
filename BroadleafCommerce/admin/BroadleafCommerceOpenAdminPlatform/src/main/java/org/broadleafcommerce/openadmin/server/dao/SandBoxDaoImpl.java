package org.broadleafcommerce.openadmin.server.dao;

import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.domain.SandBoxImpl;
import org.broadleafcommerce.openadmin.server.domain.SandBoxType;
import org.broadleafcommerce.openadmin.server.domain.Site;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository("blSandBoxDao")
public class SandBoxDaoImpl implements SandBoxDao {

	@PersistenceContext(unitName = "blPU")
	protected EntityManager sandBoxEntityManager;
	

	@Override
	public SandBox retrieve(Long id) {
		return sandBoxEntityManager.find(SandBoxImpl.class, id);
	}

    @Override
    public SandBox retrieveSandBoxByType(Site site, SandBoxType sandboxType) {
        Query query = sandBoxEntityManager.createNamedQuery("BC_READ_SANDBOX_BY_TYPE");
        //query.setParameter("site", site);
        query.setParameter("sandboxType", sandboxType.getType());
        SandBox response = null;
        try {
            response = (SandBox) query.getSingleResult();
        } catch (NoResultException e) {
            //do nothing - there is no sandbox
        }
        return response;
    }

    @Override
    public SandBox retrieveNamedSandBox(Site site, SandBoxType sandboxType, String sandboxName) {
        Query query = sandBoxEntityManager.createNamedQuery("BC_READ_SANDBOX_BY_TYPE_AND_NAME");
        //query.setParameter("site", site);
        query.setParameter("sandboxType", sandboxType.getType());
        query.setParameter("sandboxName", sandboxName);
        SandBox response = null;
        try {
            response = (SandBox) query.getSingleResult();
        } catch (NoResultException e) {
            //do nothing - there is no sandbox
        }
        return response;
    }

    @Override
    public SandBox persist(SandBox entity) {
		sandBoxEntityManager.persist(entity);
		sandBoxEntityManager.flush();
		return entity;
    }
}
