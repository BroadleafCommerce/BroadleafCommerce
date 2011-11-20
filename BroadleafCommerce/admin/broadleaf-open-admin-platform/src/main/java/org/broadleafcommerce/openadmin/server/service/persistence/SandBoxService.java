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

    public void promoteAllSandBoxItems(SandBox sandBox, String comment);

    public void promoteSelectedItems(SandBox sandBox, String comment, List<SandBoxItem> sandBoxItems);

    public void schedulePromotionForSandBox(SandBox sandBox, Calendar calendar);

    public void schedulePromotionForSandBoxItems(List<SandBoxItem> sandBoxItems, Calendar calendar);

    public void revertAllSandBoxItems(SandBox originalSandBox, SandBox sandBox);

    public void revertSelectedSandBoxItems(SandBox sandBox, List<SandBoxItem> sandBoxItems);

    public void rejectAllSandBoxItems(SandBox originalSandBox, SandBox sandBox, String comment);

    public void rejectSelectedSandBoxItems(SandBox sandBox, String comment, List<SandBoxItem> sandBoxItems);

    //public PersistencePackage saveEntitySandBoxItems(PersistencePackage persistencePackage, ChangeType changeType, PersistenceManager persistenceManager, RecordHelper helper) throws SandBoxException;

    //public EntitySandBoxItem retrieveSandBoxItemByTemporaryId(Object temporaryId);

    public SandBox retrieveApprovalSandBox(SandBox sandBox);

}