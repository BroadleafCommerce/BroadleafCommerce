package org.broadleafcommerce.openadmin.server.dao;

import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItem;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItemType;
import org.broadleafcommerce.openadmin.server.domain.SandBoxOperationType;

public interface SandBoxItemDao {

	public SandBoxItem retrieveById(Long id);

    public SandBoxItem retrieveBySandboxAndOriginalItemId(SandBox sandBox, SandBoxItemType type, Long originalItemId);

    public SandBoxItem retrieveBySandboxAndTemporaryItemId(SandBox sandBox, SandBoxItemType type, Long tempItemId);

    public SandBoxItem addSandBoxItem(SandBox sandBox, SandBoxOperationType operationType, SandBoxItemType itemType, String description, Long temporaryId, Long originalId);

    public SandBoxItem updateSandBoxItem(SandBoxItem sandBoxItem);

}