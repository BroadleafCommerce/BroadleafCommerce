package org.broadleafcommerce.openadmin.server.service.persistence;

import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.dao.SandBoxEntityDao;
import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItem;
import org.broadleafcommerce.openadmin.server.domain.Site;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.service.exception.SandBoxException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.broadleafcommerce.openadmin.server.service.type.ChangeType;

import java.util.List;

public interface SandBoxService {

    public SandBox retrieveSandboxById(Long id);

    public SandBox retrieveProductionSandBox(Site site);

    /**
     * Returns the sandbox currently associated with the passed in userId.
     * If one is not associated, it uses (or creates) a default user sandbox with the
     * name:   user:username.
     *
     * @param adminUser
     * @return
     */
    public SandBox retrieveUserSandBox(Site site, AdminUser adminUser);

    public SandBox retrieveApprovalSandBox(Site site);

    public void promoteSandBox(SandBox sandBox);

    public void promoteSelectedSandBoxItems(List<SandBoxItem> sandBoxItems);


    // TODO: Refactor this so that it isn't "saveSandbox".    This method is specific
    // to entity sandbox items.
    public PersistencePackage saveSandBox(PersistencePackage persistencePackage, ChangeType changeType, PersistenceManager persistenceManager, RecordHelper helper) throws SandBoxException;


    // TODO: Remove from interface?
    public abstract SandBoxEntityDao getSandBoxDao();

    // TODO: Remove from interface?
	public abstract void setSandBoxDao(SandBoxEntityDao sandBoxDao);


    // TODO: Remove from interface?
    public SandBoxIdGenerationService getSandBoxIdGenerationService();

    // TODO: Remove from interface?
    public void setSandBoxIdGenerationService(SandBoxIdGenerationService sandBoxIdGenerationService);

    public SandBoxItem retrieveSandBoxItemByTemporaryId(Object temporaryId);



}