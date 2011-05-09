package org.broadleafcommerce.changeset.dao;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.persistence.spi.PersistenceUnitInfo;

import org.hibernate.ejb.Ejb3Configuration;
import org.springframework.stereotype.Repository;

@Repository("blEJB3ConfigurationDao")
public class EJB3ConfigurationDaoImpl implements EJB3ConfigurationDao {

	private Ejb3Configuration configuration = null;
	
	@Resource (name = "persistenceUnitInfo")
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
}
