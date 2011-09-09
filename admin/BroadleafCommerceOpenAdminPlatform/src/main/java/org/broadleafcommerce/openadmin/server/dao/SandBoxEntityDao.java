package org.broadleafcommerce.openadmin.server.dao;

import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItem;
import org.broadleafcommerce.openadmin.server.domain.SandBoxType;
import org.broadleafcommerce.openadmin.server.domain.Site;

public interface SandBoxEntityDao {

	public abstract SandBox persist(SandBox entity);

	public abstract SandBox merge(SandBox entity);

	public abstract SandBox retrieve(Long id);

    public SandBoxItem retrieveSandBoxItemByTemporaryId(Object temporaryId);

    public void deleteItem(SandBoxItem sandBoxItem);

    public SandBox retrieveSandBoxByType(Site site, SandBoxType sandboxType);

    public SandBox retrieveNamedSandBox(Site site, SandBoxType sandboxType, String sandboxName);
}