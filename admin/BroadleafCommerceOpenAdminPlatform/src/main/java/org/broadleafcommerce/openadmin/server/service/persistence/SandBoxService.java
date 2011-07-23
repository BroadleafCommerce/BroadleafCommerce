package org.broadleafcommerce.openadmin.server.service.persistence;

import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.dao.SandBoxEntityDao;
import org.broadleafcommerce.openadmin.server.domain.SandBox;

public interface SandBoxService {

	public abstract SandBox saveSandBox(PersistencePackage persistencePackage);

	public abstract SandBoxEntityDao getSandBoxDao();

	public abstract void setSandBoxDao(SandBoxEntityDao sandBoxDao);

}