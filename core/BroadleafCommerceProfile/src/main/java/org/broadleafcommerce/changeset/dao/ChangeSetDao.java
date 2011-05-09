package org.broadleafcommerce.changeset.dao;

import java.io.Serializable;

import org.hibernate.Session;
import org.hibernate.envers.AuditReader;

public interface ChangeSetDao {

	public abstract Object[] getState(String entityName, Serializable entity);

	public abstract void saveChangeSet(String entityName, Serializable entity, Serializable id, Object[] currentState, Object[] previousState);

	public abstract AuditReader getAuditReader();
	
	public abstract AuditReader getAuditReader(Session session);
	
}