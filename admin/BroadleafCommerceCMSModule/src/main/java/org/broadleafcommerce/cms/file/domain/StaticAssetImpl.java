/*
 * Copyright 2008-2011 the original author or authors.
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

import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.domain.PageField;
import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.domain.SandBoxImpl;
import org.broadleafcommerce.presentation.AdminPresentation;
import org.broadleafcommerce.presentation.RequiredOverride;
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
@Table(name = "BLC_STATIC_ASSET")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
public class StaticAssetImpl extends StaticAssetFolderImpl implements StaticAsset {

    @Column(name ="FULL_URL")
    @AdminPresentation(friendlyName="Full URL", order=2, group = "Asset Details", readOnly = true)
    protected String fullUrl;

    @Column(name = "FILE_SIZE")
    @AdminPresentation(friendlyName="File Size", order=3, group = "Asset Details", readOnly = true)
    protected Integer fileSize;

    @ManyToOne(targetEntity = StaticAssetFolderImpl.class)
    @JoinColumn(name = "PARENT_FOLDER_ID")
    protected StaticAssetFolder parentFolder;

    @ManyToMany(targetEntity = StaticAssetDescriptionImpl.class, cascade = CascadeType.ALL)
    @JoinTable(name = "BLC_ASSET_DESC_MAP", inverseJoinColumns = @JoinColumn(name = "STATIC_ASSET_DESC_ID", referencedColumnName = "STATIC_ASSET_DESC_ID"))
    @org.hibernate.annotations.MapKey(columns = {@Column(name = "MAP_KEY", nullable = false)})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCMSElements")
    @BatchSize(size = 20)
    protected Map<String,StaticAssetDescription> contentMessageValues = new HashMap<String,StaticAssetDescription>();

    @ManyToOne (targetEntity = SandBoxImpl.class)
    @JoinTable(name = "BLC_SANDBOX_PAGE",joinColumns = @JoinColumn(name = "PAGE_ID"),inverseJoinColumns = @JoinColumn(name = "SANDBOX_ID"))
    protected SandBox sandbox;

    @Column (name = "ARCHIVED_FLAG")
    @AdminPresentation(friendlyName="Archived Flag", hidden = true)
    protected Boolean archivedFlag = false;

    @Column (name = "ORIGINAL_ASSET_ID")
    @AdminPresentation(friendlyName="Original Asset ID", hidden = true)
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

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    public StaticAssetFolder getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(StaticAssetFolder parentFolder) {
        this.parentFolder = parentFolder;
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
        asset.sandbox = sandbox;
        asset.originalAssetId = originalAssetId;

        for (StaticAssetDescription oldAssetDescription : contentMessageValues.values()) {
            StaticAssetDescription newAssetDescription = oldAssetDescription.cloneEntity();
            newAssetDescription.setStaticAsset(asset);
            asset.getContentMessageValues().put(newAssetDescription.getFieldKey(), newAssetDescription);
        }

        return asset;
    }
}
