package org.broadleafcommerce.cms.file.domain;

import java.util.Map;

/**
 * Created by bpolster.
 */
public interface StaticAsset {
    public Long getId();

    public String getFileName();

    public String getFullUrl();

    public Integer getFileSize();

    public StaticAssetFolder getParentFolder();

    public StaticAssetData getStaticAssetData();

    public void setId(Long id);

    public void setFileName(String fileName);

    public void setFullUrl(String fullUrl);

    public void setFileSize(Integer fileSize);

    public void setParentFolder(StaticAssetFolder parentFolder);

    public void setStaticAssetData(StaticAssetData data);

    public Map<String, StaticAssetDescription> getContentMessageValues();

    public void setContentMessageValues(Map<String, StaticAssetDescription> contentMessageValues);
}
