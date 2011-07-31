package org.broadleafcommerce.openadmin.server.service.persistence;

import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.dao.SandBoxEntityDao;
import org.broadleafcommerce.openadmin.server.service.type.ChangeType;

public interface SandBoxService {

	public abstract PersistencePackage saveSandBox(PersistencePackage persistencePackage, ChangeType changeType);

	public abstract SandBoxEntityDao getSandBoxDao();

	public abstract void setSandBoxDao(SandBoxEntityDao sandBoxDao);

}