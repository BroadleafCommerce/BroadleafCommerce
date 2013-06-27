/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.file.domain;

import org.broadleafcommerce.cms.field.type.StorageType;
import org.broadleafcommerce.cms.file.service.StaticAssetService;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.openadmin.audit.AdminAuditable;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by bpolster.
 */
public interface StaticAsset extends Serializable {

    /**
     * Returns the id of the static asset.
     * @return
     */
    public Long getId();

    /**
     * Sets the id of the static asset.    
     * @param id
     */
    public void setId(Long id);


    /**
     * The name of the static asset.
     * @return
     */
    public String getName();

    /**
     * Sets the name of the static asset.   Used primarily for 
     * @param name
     */
    public void setName(String name);

    /**
     * Returns the altText of this asset.
     * 
     * @return
     */
    public String getAltText();

    /**
     * Set the altText of the static asset.
     * @param title
     */
    public void setAltText(String altText);

    /**
     * Returns the title of this asset.
     * 
     * @return
     */
    public String getTitle();

    /**
     * Set the title of the static asset.
     * @param title
     */
    public void setTitle(String title);

    /**
     * URL used to retrieve this asset.
     * @return
     */
    public String getFullUrl();

    /**
     * Sets the URL for the asset
     * @param fullUrl
     */
    public void setFullUrl(String fullUrl);

    /**
     * Filesize of the asset.
     * @return
     */
    public Long getFileSize();

    /**
     * Sets the filesize of the asset
     * @param fileSize
     */
    public void setFileSize(Long fileSize);

    /**
     * @deprecated - Use {@link #getTitle()} or {@link #getAltText()}getAltText() instead.
     * @return
     */
    public Map<String, StaticAssetDescription> getContentMessageValues();

    /**
     * @deprecated - Use {@link #setTitle(String)} or {@link #setAltText(String)} instead.
     * @param contentMessageValues
     */
    public void setContentMessageValues(Map<String, StaticAssetDescription> contentMessageValues);

    /**
     * Returns the mimeType of the asset.
     * @return
     */
    public String getMimeType();

    /**
     * Sets the mimeType of the asset.
     * @return
     */
    public void setMimeType(String mimeType);

    /**
     * Returns the file extension of the asset.
     * @return
     */
    public String getFileExtension();

    /**
     * Sets the fileExtension of the asset.
     * @param fileExtension
     */
    public void setFileExtension(String fileExtension);

    /**
     * Returns how the underlying asset is stored.  Typically on the FileSystem or the Database.
     * 
     * If null, this method returns <code>StorageType.DATABASE</code> for backwards compatibility.
     * 
     * @see {@link StaticAssetService}
     * @return
     */
    public StorageType getStorageType();

    /**
     * Returns how the asset was stored in the backend (e.g. DATABASE or FILESYSTEM)
     * @param storageType
     */
    public void setStorageType(StorageType storageType);

    /**
     * @deprecated - not currently used
     * @return
     */
    public Site getSite();

    /**
     * @deprecated - not currently used
     * @param site
     */
    public void setSite(Site site);

    public SandBox getOriginalSandBox();

    public void setOriginalSandBox(SandBox originalSandBox);

    public AdminAuditable getAuditable();

    public void setAuditable(AdminAuditable auditable);

    public Boolean getLockedFlag();

    public void setLockedFlag(Boolean lockedFlag);

    public Boolean getDeletedFlag();

    public void setDeletedFlag(Boolean deletedFlag);

    public Boolean getArchivedFlag();

    public void setArchivedFlag(Boolean archivedFlag);

    public Long getOriginalAssetId();

    public void setOriginalAssetId(Long originalPageId);

    public SandBox getSandbox();

    public void setSandbox(SandBox sandbox);

    public StaticAsset cloneEntity();

}
