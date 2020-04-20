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
import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.i18n.service.DynamicTranslationProvider;
import org.broadleafcommerce.common.locale.domain.LocaleImpl;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationMap;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverrides;
import org.broadleafcommerce.openadmin.audit.AdminAuditableListener;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
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
@AdminPresentationOverrides(
        {
            @AdminPresentationOverride(name="auditable.createdBy.id", value=@AdminPresentation(readOnly = true, visibility = VisibilityEnum.HIDDEN_ALL)),
            @AdminPresentationOverride(name="auditable.updatedBy.id", value=@AdminPresentation(readOnly = true, visibility = VisibilityEnum.HIDDEN_ALL)),
            @AdminPresentationOverride(name="auditable.createdBy.name", value=@AdminPresentation(readOnly = true, visibility = VisibilityEnum.HIDDEN_ALL)),
            @AdminPresentationOverride(name="auditable.updatedBy.name", value=@AdminPresentation(readOnly = true, visibility = VisibilityEnum.HIDDEN_ALL)),
            @AdminPresentationOverride(name="auditable.dateCreated", value=@AdminPresentation(readOnly = true, visibility = VisibilityEnum.HIDDEN_ALL, group = StaticAssetAdminPresentation.GroupName.File_Details)),
            @AdminPresentationOverride(name="auditable.dateUpdated", value=@AdminPresentation(readOnly = true, visibility = VisibilityEnum.HIDDEN_ALL, group = StaticAssetAdminPresentation.GroupName.File_Details)),
            @AdminPresentationOverride(name="sandbox", value=@AdminPresentation(excluded = true))
        }
)
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.AUDITABLE_ONLY)
})
public class StaticAssetImpl implements StaticAsset, AdminMainEntity, StaticAssetAdminPresentation {

    private static final long serialVersionUID = 6990685254640110350L;

    @Id
    @GeneratedValue(generator = "StaticAssetId")
    @GenericGenerator(
        name="StaticAssetId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="StaticAssetImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.cms.file.domain.StaticAssetImpl")
        }
    )
    @Column(name = "STATIC_ASSET_ID")
    protected Long id;

    @Column(name = "NAME", nullable = false)
    @AdminPresentation(friendlyName = "StaticAssetImpl_Item_Name",
            group = GroupName.General,
            requiredOverride = RequiredOverride.NOT_REQUIRED,
            order = FieldOrder.NAME,
            gridOrder = 1000,
            readOnly = true,
            prominent = true)
    protected String name;

    @Column(name ="FULL_URL", nullable = false)
    @AdminPresentation(friendlyName = "StaticAssetImpl_Full_URL",
            group = GroupName.Image,
            order = FieldOrder.URL,
            gridOrder = 2000,
            requiredOverride = RequiredOverride.REQUIRED,
            fieldType = SupportedFieldType.ASSET_URL,
            prominent = true)
    @Index(name="ASST_FULL_URL_INDX", columnNames={"FULL_URL"})
    protected String fullUrl;

    @Column(name = "TITLE", nullable = true)
    @AdminPresentation(friendlyName = "StaticAssetImpl_Title",
            group = GroupName.General,
            order = FieldOrder.TITLE,
            translatable = true)
    protected String title;

    @Column(name = "ALT_TEXT", nullable = true)
    @AdminPresentation(friendlyName = "StaticAssetImpl_Alt_Text",
            group = GroupName.General,
            order = FieldOrder.ALT_TEXT,
            translatable = true)
    protected String altText;

    @Column(name = "MIME_TYPE")
    @AdminPresentation(friendlyName = "StaticAssetImpl_Mime_Type",
            order = FieldOrder.MIME_TYPE,
            group = GroupName.File_Details,
            readOnly = true)
    protected String mimeType;

    @Column(name = "FILE_SIZE")
    @AdminPresentation(friendlyName = "StaticAssetImpl_File_Size_Bytes",
            order = FieldOrder.FILE_SIZE,
            group = GroupName.File_Details,
            readOnly = true)
    protected Long fileSize;

    @Column(name = "FILE_EXTENSION")
    @AdminPresentation(friendlyName = "StaticAssetImpl_File_Extension",
            order = FieldOrder.FILE_EXTENSION,
            group = GroupName.File_Details,
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
            tab = TabName.Advanced, tabOrder = TabOrder.Advanced,
            friendlyName = "assetDescriptionTitle",
            keyPropertyFriendlyName = "SkuImpl_Sku_Media_Key",
            deleteEntityUponRemove = true,
            mapKeyOptionEntityClass = LocaleImpl.class,
            mapKeyOptionEntityDisplayField = "friendlyName",
            mapKeyOptionEntityValueField = "localeCode")
    protected Map<String,StaticAssetDescription> contentMessageValues = new HashMap<String,StaticAssetDescription>();

    @Column(name = "STORAGE_TYPE")
    @AdminPresentation(excluded = true)
    protected String storageType;

    @Override
    public String getFullUrl() {
        return fullUrl;
    }

    @Override
    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    @Override
    public String getTitle() {
        return DynamicTranslationProvider.getValue(this, "title", this.title);
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getAltText() {
        return DynamicTranslationProvider.getValue(this, "altText", this.altText);
    }

    @Override
    public void setAltText(String altText) {
        this.altText = altText;
    }

    @Override
    public Long getFileSize() {
        return fileSize;
    }

    @Override
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public Map<String, StaticAssetDescription> getContentMessageValues() {
        return contentMessageValues;
    }

    @Override
    public void setContentMessageValues(Map<String, StaticAssetDescription> contentMessageValues) {
        this.contentMessageValues = contentMessageValues;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public String getFileExtension() {
        return fileExtension;
    }

    @Override
    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
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

    @Override
    public <G extends StaticAsset> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        StaticAsset cloned = createResponse.getClone();
        cloned.setName(name);
        cloned.setAltText(altText);
        cloned.setFileExtension(fileExtension);
        cloned.setFileSize(fileSize);
        cloned.setFullUrl(fullUrl);
        cloned.setMimeType(mimeType);
        cloned.setTitle(title);
        cloned.setStorageType(getStorageType());
        for(Map.Entry<String, StaticAssetDescription> entry : contentMessageValues.entrySet()){
            CreateResponse<StaticAssetDescription> clonedDescRsp = entry.getValue().createOrRetrieveCopyInstance(context);
            cloned.getContentMessageValues().put(entry.getKey(),clonedDescRsp.getClone());
        }

        return createResponse;
    }

    @Override
    public String getMainEntityName() {
        return getName();
    }
}
