package org.broadleafcommerce.cms.file.domain;

import org.broadleafcommerce.cms.site.domain.Site;

import java.util.List;

/**
 * Created by bpolster.
 */
public interface StaticAssetFolder {
    public Long getId();

    public void setId(Long id);

    public String getFolderName();

    public void setFolderName(String folderName);

    public StaticAssetFolder getParentFolder();

    public void setParentFolder(StaticAssetFolder parentFolder);

    public Site getSite();

    public void setSite(Site site);

    public List<StaticAssetFolder> getSubFolders();

    public void setSubFolders(List<StaticAssetFolder> subFolders);

    public List<StaticAsset> getStaticAssets();

    public void setStaticAssets(List<StaticAsset> staticAssets);

    public Boolean getDeletedFlag();

    public void setDeletedFlag(Boolean deletedFlag);
}
