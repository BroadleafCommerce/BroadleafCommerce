package org.broadleafcommerce.cms.page.domain;


import org.broadleafcommerce.openadmin.server.domain.Site;

import java.util.List;

/**
 * Created by bpolster.
 */
public interface PageFolder {

    public Long getId();

    public void setId(Long id);

    public String getPath();

    public void setPath(String path);

    public PageFolder getParentFolder();

    public void setParentFolder(PageFolder parentFolder);

    public Site getSite();

    public void setSite(Site site);

    public List<PageFolder> getSubFolders();

    public void setSubFolders(List<PageFolder> subFolders);

    public Boolean getDeletedFlag();

    public void setDeletedFlag(Boolean deletedFlag);
}
