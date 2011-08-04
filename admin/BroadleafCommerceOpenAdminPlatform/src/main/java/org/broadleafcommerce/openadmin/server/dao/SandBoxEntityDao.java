package org.broadleafcommerce.openadmin.server.dao;

import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItem;

public interface SandBoxEntityDao {

	public abstract SandBox persist(SandBox entity);

	public abstract SandBox merge(SandBox entity);

	public abstract SandBox retrieve(Object primaryKey);

    public SandBox readSandBoxByName(String name);

    public SandBoxItem retrieveSandBoxItemByTemporaryId(Object temporaryId);

    public void deleteItem(SandBoxItem sandBoxItem);

}