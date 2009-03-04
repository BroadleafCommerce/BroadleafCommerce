package org.broadleafcommerce;

import javax.persistence.spi.PersistenceUnitInfo;

import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;

public class MyPersistenceUnitManager extends DefaultPersistenceUnitManager {

	/* (non-Javadoc)
	 * @see org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager#obtainPersistenceUnitInfo(java.lang.String)
	 */
	@Override
	public PersistenceUnitInfo obtainPersistenceUnitInfo(
			String persistenceUnitName) {
		return this.getPersistenceUnitInfo(persistenceUnitName);
	}

}
