package org.broadleafcommerce.openadmin.server.service.remote;

import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.SandBoxInfo;
import org.broadleafcommerce.openadmin.server.dao.SandBoxEntityDao;
import org.broadleafcommerce.openadmin.server.domain.SandBox;

public interface SandBoxService {

	public abstract SandBox saveSandBox(Entity entity,
			PersistencePerspective persistencePerspective,
			SandBoxInfo sandBoxInfo);

	public abstract SandBoxEntityDao getSandBoxDao();

	public abstract void setSandBoxDao(SandBoxEntityDao sandBoxDao);

}