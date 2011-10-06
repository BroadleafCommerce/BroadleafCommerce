package org.broadleafcommerce.cms.file.domain;

import org.broadleafcommerce.openadmin.server.domain.SandBox;

import java.util.Map;

/**
 * Created by bpolster.
 */
public interface StaticAsset extends StaticAssetFolder {

    public String getFullUrl();

    public Long getFileSize();

    public void setFullUrl(String fullUrl);

    public void setFileSize(Long fileSize);

    public Map<String, StaticAssetDescription> getContentMessageValues();

    public void setContentMessageValues(Map<String, StaticAssetDescription> contentMessageValues);

    public Boolean getArchivedFlag();

    public void setArchivedFlag(Boolean archivedFlag);

    public Long getOriginalAssetId();

    public void setOriginalAssetId(Long originalPageId);

    public SandBox getSandbox();

    public void setSandbox(SandBox sandbox);

    public StaticAsset cloneEntity();

    public String getMimeType();

    public void setMimeType(String mimeType);

    public String getFileExtension();

    public void setFileExtension(String fileExtension);

    public SandBox getOriginalSandBox();

    public void setOriginalSandBox(SandBox originalSandBox);
}
