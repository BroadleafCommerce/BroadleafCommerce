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
import org.broadleafcommerce.common.locale.domain.LocaleImpl;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationMap;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverrides;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxImpl;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.openadmin.audit.AdminAuditable;
import org.broadleafcommerce.openadmin.audit.AdminAuditableListener;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Index;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

/**
 * Created by bpolster.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(value = { AdminAuditableListener.class })
@Table(name = "BLC_STATIC_ASSET")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
@AdminPresentationOverrides(
        {
            @AdminPresentationOverride(name="auditable.createdBy.id", value=@AdminPresentation(readOnly = true, visibility = VisibilityEnum.HIDDEN_ALL)),
            @AdminPresentationOverride(name="auditable.updatedBy.id", value=@AdminPresentation(readOnly = true, visibility = VisibilityEnum.HIDDEN_ALL)),
            @AdminPresentationOverride(name="auditable.createdBy.name", value=@AdminPresentation(readOnly = true, visibility = VisibilityEnum.HIDDEN_ALL)),
            @AdminPresentationOverride(name="auditable.updatedBy.name", value=@AdminPresentation(readOnly = true, visibility = VisibilityEnum.HIDDEN_ALL)),
            @AdminPresentationOverride(name="auditable.dateCreated", value=@AdminPresentation(readOnly = true, visibility = VisibilityEnum.HIDDEN_ALL)),
            @AdminPresentationOverride(name="auditable.dateUpdated", value=@AdminPresentation(readOnly = true, visibility = VisibilityEnum.HIDDEN_ALL)),
            @AdminPresentationOverride(name="sandbox", value=@AdminPresentation(excluded = true))
        }
)
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE)
public class StaticAssetImpl implements StaticAsset {

    private static final long serialVersionUID = 6990685254640110350L;

    @Id
    @GeneratedValue(generator = "StaticAssetId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "StaticAssetId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "StaticAssetFolderImpl", allocationSize = 10)
    @Column(name = "STATIC_ASSET_ID")
    protected Long id;

    @Embedded
    @AdminPresentation(excluded = true)
    protected AdminAuditable auditable = new AdminAuditable();

    @Column(name = "NAME", nullable = false)
    @AdminPresentation(friendlyName = "StaticAssetImpl_Item_Name",
            order = Presentation.FieldOrder.NAME,
            requiredOverride = RequiredOverride.NOT_REQUIRED,
            prominent = true)
    protected String name;

    /*@ManyToOne(targetEntity = SiteImpl.class)
    @JoinColumn(name="SITE_ID")*/
    @Transient
    @AdminPresentation(excluded = true)
    protected Site site;

    @Column(name ="FULL_URL", nullable = false)
    @AdminPresentation(friendlyName = "StaticAssetImpl_Full_URL",
            order = Presentation.FieldOrder.URL,
            requiredOverride = RequiredOverride.REQUIRED,
            prominent = true)
    @Index(name="ASST_FULL_URL_INDX", columnNames={"FULL_URL"})
    protected String fullUrl;

    @Column(name = "TITLE", nullable = true)
    @AdminPresentation(friendlyName = "StaticAssetImpl_Title",
            order = Presentation.FieldOrder.TITLE,
            translatable = true)
    protected String title;

    @Column(name = "ALT_TEXT", nullable = true)
    @AdminPresentation(friendlyName = "StaticAssetImpl_Alt_Text",
            order = Presentation.FieldOrder.ALT_TEXT,
            translatable = true)
    protected String altText;

    @Column(name = "MIME_TYPE")
    @AdminPresentation(friendlyName = "StaticAssetImpl_Mime_Type",
            order = Presentation.FieldOrder.MIME_TYPE,
            tab = Presentation.Tab.Name.File_Details, tabOrder = Presentation.Tab.Order.File_Details,
            readOnly = true)
    protected String mimeType;

    @Column(name = "FILE_SIZE")
    @AdminPresentation(friendlyName = "StaticAssetImpl_File_Size_Bytes",
            order = Presentation.FieldOrder.FILE_SIZE,
            tab = Presentation.Tab.Name.File_Details, tabOrder = Presentation.Tab.Order.File_Details,
            readOnly = true)
    protected Long fileSize;

    @Column(name = "FILE_EXTENSION")
    @AdminPresentation(friendlyName = "StaticAssetImpl_File_Extension",
            order = Presentation.FieldOrder.FILE_EXTENSION,
            tab = Presentation.Tab.Name.File_Details, tabOrder = Presentation.Tab.Order.File_Details,
            readOnly = true)
    protected String fileExtension;

    @ManyToMany(targetEntity = StaticAssetDescriptionImpl.class, cascade = CascadeType.ALL)
    @JoinTable(name = "BLC_ASSET_DESC_MAP", joinColumns = @JoinColumn(name = "STATIC_ASSET_ID"),
            inverseJoinColumns = @JoinColumn(name = "STATIC_ASSET_DESC_ID"))
    @MapKeyColumn(name = "MAP_KEY")
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCMSElements")
    @BatchSize(size = 20)
    @AdminPresentationMap(
        excluded = true,
            tab = Presentation.Tab.Name.Advanced, tabOrder = Presentation.Tab.Order.Advanced,
        friendlyName = "assetDescriptionTitle",
        keyPropertyFriendlyName = "SkuImpl_Sku_Media_Key",
        deleteEntityUponRemove = true,
        mapKeyOptionEntityClass = LocaleImpl.class,
        mapKeyOptionEntityDisplayField = "friendlyName",
        mapKeyOptionEntityValueField = "localeCode"
)
    protected Map<String,StaticAssetDescription> contentMessageValues = new HashMap<String,StaticAssetDescription>();

    @ManyToOne (targetEntity = SandBoxImpl.class)
    @JoinColumn(name = "SANDBOX_ID")
    @AdminPresentation(excluded = true)
    protected SandBox sandbox;

    @ManyToOne(targetEntity = SandBoxImpl.class)
    @JoinColumn(name = "ORIG_SANDBOX_ID")
    @AdminPresentation(excluded = true)
    protected SandBox originalSandBox;

    @Column (name = "ARCHIVED_FLAG")
    @AdminPresentation(friendlyName = "StaticAssetImpl_Archived_Flag", visibility = VisibilityEnum.HIDDEN_ALL)
    @Index(name="ASST_ARCHVD_FLG_INDX", columnNames={"ARCHIVED_FLAG"})
    protected Boolean archivedFlag = false;

    @Column (name = "DELETED_FLAG")
    @AdminPresentation(friendlyName = "StaticAssetImpl_Deleted_Flag", visibility = VisibilityEnum.HIDDEN_ALL)
    @Index(name="ASST_DLTD_FLG_INDX", columnNames={"DELETED_FLAG"})
    protected Boolean deletedFlag = false;

    @Column (name = "LOCKED_FLAG")
    @AdminPresentation(friendlyName = "StaticAssetImpl_Is_Locked", visibility = VisibilityEnum.HIDDEN_ALL)
    @Index(name="ASST_LCKD_FLG_INDX", columnNames={"LOCKED_FLAG"})
    protected Boolean lockedFlag = false;

    @Column (name = "ORIG_ASSET_ID")
    @AdminPresentation(friendlyName = "StaticAssetImpl_Original_Asset_ID", visibility = VisibilityEnum.HIDDEN_ALL)
    @Index(name="ORIG_ASSET_ID_INDX", columnNames={"ORIG_ASSET_ID"})
    protected Long originalAssetId;

    @Column(name = "STORAGE_TYPE")
    @AdminPresentation(excluded = true)
    protected String storageType;

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
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
        if (archivedFlag == null) {
            return Boolean.FALSE;
        } else {
            return archivedFlag;
        }
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

    public AdminAuditable getAuditable() {
        return auditable;
    }

    public void setAuditable(AdminAuditable auditable) {
        this.auditable = auditable;
    }

    public Boolean getDeletedFlag() {
        return deletedFlag;
    }

    public void setDeletedFlag(Boolean deletedFlag) {
        this.deletedFlag = deletedFlag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getLockedFlag() {
        return lockedFlag;
    }

    public void setLockedFlag(Boolean lockedFlag) {
        this.lockedFlag = lockedFlag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    @Override
    public StaticAsset cloneEntity() {
        StaticAssetImpl asset = new StaticAssetImpl();
        asset.name = name;
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

    @Override
    public StorageType getStorageType() {
        StorageType st = StorageType.getInstance(storageType);
        if (st == null) {
            return StorageType.DATABASE;
        } else {
            return st;
        }
    }

    @Override
    public void setStorageType(StorageType storageType) {
        this.storageType = storageType.getType();
    }

    public static class Presentation {

        public static class Tab {

            public static class Name {

                public static final String File_Details = "AssetImpl_FileDetails_Tab";
                public static final String Advanced = "AssetImpl_Advanced_Tab";
            }

            public static class Order {

                public static final int File_Details = 2000;
                public static final int Advanced = 3000;
            }
        }

        public static class FieldOrder {

            // General Fields
            public static final int NAME = 1000;
            public static final int URL = 2000;
            public static final int TITLE = 3000;
            public static final int ALT_TEXT = 4000;

            public static final int MIME_TYPE = 5000;
            public static final int FILE_EXTENSION = 6000;
            public static final int FILE_SIZE = 7000;
            
            // Used by subclasses to know where the last field is.
            public static final int LAST = 7000;

        }
    }
}
