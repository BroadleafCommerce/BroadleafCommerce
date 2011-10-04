package org.broadleafcommerce.cms.page.domain;


import org.broadleafcommerce.openadmin.audit.AdminAuditable;
import org.broadleafcommerce.openadmin.server.domain.Site;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bpolster.
 */
public interface PageFolder extends Serializable {

    public Long getId();

    public void setId(Long id);

    public PageFolder getParentFolder();

    public void setParentFolder(PageFolder parentFolder);

    public Site getSite();

    public void setSite(Site site);

    public List<PageFolder> getSubFolders();

    public void setSubFolders(List<PageFolder> subFolders);

    public Boolean getDeletedFlag();

    public void setDeletedFlag(Boolean deletedFlag);

    public String getName();

    public void setName(String name);

    public Boolean isFolder();

    public String getFullUrl();

    public AdminAuditable getAuditable();

    public void setAuditable(AdminAuditable auditable);

    public Boolean getLockedFlag();

    public void setLockedFlag(Boolean lockedFlag);
}
