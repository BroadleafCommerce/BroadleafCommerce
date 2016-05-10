/*
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.cms.file.domain;

import org.broadleafcommerce.cms.field.type.StorageType;
import org.broadleafcommerce.cms.file.service.StaticAssetService;
import org.broadleafcommerce.common.audit.Auditable;
import org.broadleafcommerce.common.copy.MultiTenantCloneable;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by bpolster.
 */
public interface StaticAsset extends Serializable, MultiTenantCloneable<StaticAsset> {

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

}
