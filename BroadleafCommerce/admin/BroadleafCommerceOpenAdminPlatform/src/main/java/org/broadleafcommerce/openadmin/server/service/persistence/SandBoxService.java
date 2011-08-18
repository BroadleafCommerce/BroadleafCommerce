package org.broadleafcommerce.openadmin.server.service.persistence;

import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.dao.SandBoxEntityDao;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItem;
import org.broadleafcommerce.openadmin.server.service.exception.SandBoxException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.broadleafcommerce.openadmin.server.service.type.ChangeType;

public interface SandBoxService {

	public PersistencePackage saveSandBox(PersistencePackage persistencePackage, ChangeType changeType, PersistenceManager persistenceManager, RecordHelper helper) throws SandBoxException;

	public abstract SandBoxEntityDao getSandBoxDao();

	public abstract void setSandBoxDao(SandBoxEntityDao sandBoxDao);

    public SandBoxIdGenerationService getSandBoxIdGenerationService();

    public void setSandBoxIdGenerationService(SandBoxIdGenerationService sandBoxIdGenerationService);

    public SandBoxItem retrieveSandBoxItemByTemporaryId(Object temporaryId);

}