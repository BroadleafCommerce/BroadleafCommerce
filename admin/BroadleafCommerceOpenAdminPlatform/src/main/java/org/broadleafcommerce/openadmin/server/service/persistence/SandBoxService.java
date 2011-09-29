package org.broadleafcommerce.openadmin.server.service.persistence;

import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItem;
import org.broadleafcommerce.openadmin.server.domain.Site;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;

import java.util.Calendar;
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

    public void promoteAllSandBoxItems(AdminUser user, SandBox sandBox, String comment);

    public void promoteSelectedItems(AdminUser user, SandBox sandBox, String comment, List<SandBoxItem> sandBoxItems);

    public void schedulePromotionForSandBox(AdminUser user, SandBox sandBox, Calendar calendar);

    public void schedulePromotionForSandBoxItems(AdminUser user, List<SandBoxItem> sandBoxItems, Calendar calendar);

    public void revertAllSandBoxItems(AdminUser user, SandBox sandBox);

    public void revertSelectedSandBoxItems(AdminUser user, SandBox sandBox, List<SandBoxItem> sandBoxItems);

    public void rejectAllSandBoxItems(AdminUser user, SandBox sandBox, String comment);

    public void rejectSelectedSandBoxItems(AdminUser user, SandBox sandBox, String comment, List<SandBoxItem> sandBoxItems);

    //public PersistencePackage saveEntitySandBoxItems(PersistencePackage persistencePackage, ChangeType changeType, PersistenceManager persistenceManager, RecordHelper helper) throws SandBoxException;

    //public EntitySandBoxItem retrieveSandBoxItemByTemporaryId(Object temporaryId);

    public SandBox retrieveApprovalSandBox(SandBox sandBox);

}