package org.broadleafcommerce.gwt.server.changeset.dao;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.event.AuditEventListener;
import org.hibernate.envers.reader.AuditReaderImpl;
import org.hibernate.event.EventSource;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Repository;

@Repository("blChangeSetDao")
public class ChangeSetDaoImpl implements ChangeSetDao {

	private AuditEventListener auditEventListener = null;
	
	@PersistenceContext(unitName = "blPU")
    protected EntityManager em;
	
	@Resource(name = "blEJB3ConfigurationDao")
    protected EJB3ConfigurationDao ejb3ConfigurationDao;
	
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.gwt.server.dao.ChangeSetDao#getState(java.lang.String, java.io.Serializable)
	 */
	public Object[] getState(String entityName, Serializable entity) {
		EntityPersister persister = getEntityPersister(entityName);
		return persister.getPropertyValues(entity, EntityMode.POJO);
	}
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.gwt.server.dao.ChangeSetDao#saveChangeSet(java.lang.String, java.io.Serializable, java.io.Serializable, java.lang.Object[], java.lang.Object[])
	 */
	public void saveChangeSet(String entityName, Serializable entity, Serializable id, Object[] currentState, Object[] previousState) {
		PostUpdateEvent postUpdateEvent = new PostUpdateEvent(entity, id, currentState, previousState, getEntityPersister(entityName), (EventSource) ((HibernateEntityManager) em).getSession());
		getAuditEventListener().onPostUpdate(postUpdateEvent);
		//TODO do another step to save any collections, if there were changes.
	}
	
	protected EntityPersister getEntityPersister(String entityName) {
		return ((SessionFactoryImplementor) ((HibernateEntityManager) em).getSession().getSessionFactory()).getEntityPersister(entityName);
	}
	
	protected AuditEventListener getAuditEventListener() {
		synchronized(this) {
			if (auditEventListener == null) {
				Ejb3Configuration temp = ejb3ConfigurationDao.getConfiguration();
				Configuration configuration = temp.getHibernateConfiguration();
				auditEventListener = new AuditEventListener();
				auditEventListener.initialize(configuration);
			}
		}
		return auditEventListener;
	}
	
	public AuditReader getAuditReader() {
		Session session = ((HibernateEntityManager) em).getSession();
		return getAuditReader(session);
    }
	
	public AuditReader getAuditReader(Session session) {
		SessionImplementor sessionImpl;
		if (!(session instanceof SessionImplementor)) {
			sessionImpl = (SessionImplementor) session.getSessionFactory().getCurrentSession();
		} else {
			sessionImpl = (SessionImplementor) session;
		}

        return new AuditReaderImpl(getAuditEventListener().getVerCfg(), session, sessionImpl);
	}
}
