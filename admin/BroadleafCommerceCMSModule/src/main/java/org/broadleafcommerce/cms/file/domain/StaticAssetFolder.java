package org.broadleafcommerce.cms.file.domain;


import org.broadleafcommerce.openadmin.audit.AdminAuditable;
import org.broadleafcommerce.openadmin.server.domain.Site;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bpolster.
 */
public interface StaticAssetFolder extends Serializable {

    public Long getId();

    public void setId(Long id);

    public StaticAssetFolder getParentFolder();

    public void setParentFolder(StaticAssetFolder parentFolder);

    public Site getSite();

    public void setSite(Site site);

    public List<StaticAssetFolder> getSubFolders();

    public void setSubFolders(List<StaticAssetFolder> subFolders);

    public Boolean getDeletedFlag();

    public void setDeletedFlag(Boolean deletedFlag);

    public String getName();

    public void setName(String name);

    public Boolean getFolderFlag();

    public void setFolderFlag(Boolean folderFlag);

    public String getFullUrl();

    public AdminAuditable getAuditable();

    public void setAuditable(AdminAuditable auditable);

    public Boolean getLockedFlag();

    public void setLockedFlag(Boolean lockedFlag);
}
