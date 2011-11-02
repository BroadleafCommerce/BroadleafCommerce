/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.cms.file.domain;

import org.broadleafcommerce.openadmin.audit.AdminAuditableListener;
import org.broadleafcommerce.openadmin.client.dto.VisibilityEnum;
import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.domain.SandBoxImpl;
import org.broadleafcommerce.presentation.AdminPresentation;
import org.broadleafcommerce.presentation.AdminPresentationClass;
import org.broadleafcommerce.presentation.PopulateToOneFieldsEnum;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bpolster.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(value = { AdminAuditableListener.class })
@Table(name = "BLC_STATIC_ASSET")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE)
public class StaticAssetImpl extends StaticAssetFolderImpl implements StaticAsset {

    @Column(name ="FULL_URL")
    @AdminPresentation(friendlyName="Full URL", order=2, group = "Details", readOnly = true)
    protected String fullUrl;

    @Column(name = "FILE_SIZE")
    @AdminPresentation(friendlyName="File Size (Bytes)", order=3, group = "Details", readOnly = true)
    protected Long fileSize;

    @Column(name = "MIME_TYPE")
    @AdminPresentation(friendlyName="Mime Type", order=4, group = "Details", readOnly = true)
    protected String mimeType;

    @Column(name = "FILE_EXTENSION")
    @AdminPresentation(friendlyName="File Extension", order=5, group = "Details", readOnly = true)
    protected String fileExtension;

    @ManyToMany(targetEntity = StaticAssetDescriptionImpl.class, cascade = CascadeType.ALL)
    @JoinTable(name = "BLC_ASSET_DESC_MAP", inverseJoinColumns = @JoinColumn(name = "STATIC_ASSET_DESC_ID", referencedColumnName = "STATIC_ASSET_DESC_ID"))
    @org.hibernate.annotations.MapKey(columns = {@Column(name = "MAP_KEY", nullable = false)})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCMSElements")
    @BatchSize(size = 20)
    protected Map<String,StaticAssetDescription> contentMessageValues = new HashMap<String,StaticAssetDescription>();

    @ManyToOne (targetEntity = SandBoxImpl.class)
    @JoinColumn(name = "SANDBOX_ID")
    @AdminPresentation(excluded = true)
    protected SandBox sandbox;

    @ManyToOne(targetEntity = SandBoxImpl.class)
    @JoinColumn(name = "ORIGINAL_SANDBOX_ID")
    @AdminPresentation(excluded = true)
	protected SandBox originalSandBox;

    @Column (name = "ARCHIVED_FLAG")
    @AdminPresentation(friendlyName="Archived Flag", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Boolean archivedFlag = false;

    @Column (name = "ORIGINAL_ASSET_ID")
    @AdminPresentation(friendlyName="Original Asset ID", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long originalAssetId;

    public StaticAssetImpl() {
        folderFlag = false;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Map<String, StaticAssetDescription> getContentMessageValues() {
        return contentMessageValues;
    }

    public void setContentMessageValues(Map<String, StaticAssetDescription> contentMessageValues) {
        this.contentMessageValues = contentMessageValues;
    }

    public Boolean getArchivedFlag() {
        return archivedFlag;
    }

    public void setArchivedFlag(Boolean archivedFlag) {
        this.archivedFlag = archivedFlag;
    }

    public Long getOriginalAssetId() {
        return originalAssetId;
    }

    public void setOriginalAssetId(Long originalAssetId) {
        this.originalAssetId = originalAssetId;
    }

    public SandBox getSandbox() {
        return sandbox;
    }

    public void setSandbox(SandBox sandbox) {
        this.sandbox = sandbox;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public SandBox getOriginalSandBox() {
        return originalSandBox;
    }

    public void setOriginalSandBox(SandBox originalSandBox) {
        this.originalSandBox = originalSandBox;
    }

    @Override
    public StaticAsset cloneEntity() {
        StaticAssetImpl asset = new StaticAssetImpl();
        asset.name = name;
        asset.parentFolder = parentFolder;
        asset.site = site;
        asset.archivedFlag = archivedFlag;
        asset.deletedFlag = deletedFlag;
        asset.fullUrl = fullUrl;
        asset.fileSize = fileSize;
        asset.mimeType = mimeType;
        asset.sandbox = sandbox;
        asset.originalSandBox = originalSandBox;
        asset.originalAssetId = originalAssetId;
        asset.fileExtension = fileExtension;

        for (String key : contentMessageValues.keySet()) {
            StaticAssetDescription oldAssetDescription = contentMessageValues.get(key);
            StaticAssetDescription newAssetDescription = oldAssetDescription.cloneEntity();
            asset.getContentMessageValues().put(key, newAssetDescription);
        }

        return asset;
    }
}
