package org.broadleafcommerce.openadmin.server.service.persistence;

import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.dao.SandBoxEntityDao;
import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItem;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
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

    /**
     * Returns the sandbox currently associated with the passed in userId.
     * If one is not associated, it uses (or creates) a default user sandbox with the
     * name:   user:username.
     *
     * @param adminUser
     * @return
     */
    public SandBox retrieveSandBoxByUserId(AdminUser adminUser);


    /**
     * Returns the sandbox with the passed in sandboxName.
     *
     * If no sandbox is found with this exact name, the service will attempt to locate a
     * user sandbox using the convention "user:"+sandboxName.
     *
     * @param sandboxName
     * @return
     */
    public SandBox retrieveSandBoxByName(String sandboxName);

}