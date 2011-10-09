package org.broadleafcommerce.openadmin.server.dao;

import org.broadleafcommerce.openadmin.server.domain.*;

public interface SandBoxEntityDao {

	public abstract SandBox persist(SandBox entity);

	public abstract SandBox merge(SandBox entity);

	public abstract SandBox retrieve(Long id);

    public EntitySandBoxItem retrieveSandBoxItemByTemporaryId(Object temporaryId);

    public void deleteEntitySandBoxItem(EntitySandBoxItem sandBoxItem);

    public SandBox retrieveNamedSandBox(Site site, SandBoxType sandboxType, String sandboxName);
}