package org.broadleafcommerce.openadmin.server.dao;

import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.domain.SandBoxType;
import org.broadleafcommerce.openadmin.server.domain.Site;

public interface SandBoxDao {

	public SandBox retrieve(Long id);

    public SandBox retrieveSandBoxByType(Site site, SandBoxType sandboxType);

    public SandBox retrieveNamedSandBox(Site site, SandBoxType sandboxType, String sandboxName);

    public SandBox persist(SandBox entity);

}