package org.broadleafcommerce.gwt.server.changeset.dao;

import java.util.HashMap;

import javax.persistence.spi.PersistenceUnitInfo;

import org.hibernate.ejb.Ejb3Configuration;

public class EJB3ConfigurationDaoImpl implements EJB3ConfigurationDao {

	private Ejb3Configuration configuration = null;
	
	protected PersistenceUnitInfo persistenceUnitInfo;
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.gwt.server.dao.EJB3ConfigurationDao#getConfiguration()
	 */
	public Ejb3Configuration getConfiguration() {
		synchronized(this) {
			if (configuration == null) {
				Ejb3Configuration temp = new Ejb3Configuration();
				configuration = temp.configure(persistenceUnitInfo, new HashMap());
			}
		}
		return configuration;
	}

	public PersistenceUnitInfo getPersistenceUnitInfo() {
		return persistenceUnitInfo;
	}

	public void setPersistenceUnitInfo(PersistenceUnitInfo persistenceUnitInfo) {
		this.persistenceUnitInfo = persistenceUnitInfo;
	}
	
}
