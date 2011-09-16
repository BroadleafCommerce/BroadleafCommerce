package org.broadleafcommerce.openadmin.server.service.persistence;

import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.domain.EntitySandBoxItem;
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

    /**
     * Returns the sandbox currently associated with the passed in userId.
     * If one is not associated, it uses (or creates) a default user sandbox with the
     * name:   user:username.
     *
     * @param adminUser
     * @return
     */
    public SandBox retrieveUserSandBox(Site site, AdminUser adminUser);

    public void promoteAllSandBoxItems(AdminUser user, SandBox sandBox);

    public void promoteSelectedItems(AdminUser user, List<SandBoxItem> sandBoxItems);

    public void revertAllSandBoxItems(AdminUser user, SandBox sandBox);

    public void revertSelectedSandBoxItems(AdminUser user, List<SandBoxItem> sandBoxItems);

    public void rejectAllSandBoxItems(AdminUser user, SandBox sandBox);

    public void rejectSelectedSandboxItems(List<SandBoxItem> sandBoxItems);

    // Entity sandbox item code
    public PersistencePackage saveEntitySandBoxItems(PersistencePackage persistencePackage, ChangeType changeType, PersistenceManager persistenceManager, RecordHelper helper) throws SandBoxException;

    // Entity sandbox item code
    public EntitySandBoxItem retrieveSandBoxItemByTemporaryId(Object temporaryId);



}