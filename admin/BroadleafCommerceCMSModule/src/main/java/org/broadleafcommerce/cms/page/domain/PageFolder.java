package org.broadleafcommerce.cms.page.domain;

import org.broadleafcommerce.cms.file.domain.StaticAsset;
import org.broadleafcommerce.cms.site.domain.Site;

import java.util.List;

/**
 * Created by bpolster.
 */
public interface PageFolder {
    public Long getId();

    public void setId(Long id);

    public String getFolderName();

    public void setFolderName(String folderName);

    public PageFolder getParentFolder();

    public void setParentFolder(PageFolder parentFolder);

    public Site getSite();

    public void setSite(Site site);

    public List<PageFolder> getSubFolders();

    public void setSubFolders(List<PageFolder> subFolders);

    public List<Page> getPages();

    public void setPages(List<Page> pages);

    public Boolean getDeletedFlag();

    public void setDeletedFlag(Boolean deletedFlag);
}
