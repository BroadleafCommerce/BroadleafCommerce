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
package org.broadleafcommerce.cms.structure.domain;

import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.domain.SandBoxImpl;
import org.broadleafcommerce.presentation.AdminPresentation;
import org.broadleafcommerce.presentation.RequiredOverride;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bpolster.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_STRUCTURED_CONTENT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
public class StructuredContentImpl implements StructuredContent {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "StructuredContentId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "StructuredContentId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "StructuredContentImpl", allocationSize = 10)
    @Column(name = "ID")
    protected Long id;

    @AdminPresentation(friendlyName="Content Name", order=2, group="Description", prominent=true, requiredOverride = RequiredOverride.REQUIRED)
    @Column(name = "CONTENT_NAME")
    protected String contentName;

    @AdminPresentation(friendlyName="Language Code", order=3, group="Description", requiredOverride = RequiredOverride.REQUIRED)
    @Column(name = "LANGUAGE_CODE")
    protected String languageCode;

    @AdminPresentation(friendlyName="Priority", order=4, group="Description", requiredOverride = RequiredOverride.REQUIRED)
    @Column(name = "PRIORITY")
    protected Integer priority;


    @AdminPresentation(friendlyName="Display Rule", order=1, group="Display Rule")
    @Column(name = "DISPLAY_RULE")
    protected String displayRule;

    @AdminPresentation(friendlyName="Original Item Id", order=1, group="Internal", hidden = true)
    @Column(name = "ORIGINAL_ITEM_ID")
    protected Long originalItemId;

    @ManyToOne (targetEntity = SandBoxImpl.class)
    @JoinColumn(name="SANDBOX_ID")
    @AdminPresentation(friendlyName="Content SandBox", order=1, group="Stuctured Content", hidden = true)
    protected SandBox sandbox;

    @ManyToOne(targetEntity = StructuredContentTypeImpl.class)
    @JoinColumn(name="STRUCTURED_CONTENT_TYPE_ID")
    protected StructuredContentType structuredContentType;

    @OneToMany(mappedBy = "structuredContent", targetEntity = StructuredContentFieldImpl.class, cascade = {CascadeType.ALL})
    @MapKey(name = "fieldKey")
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCMSElements")
    protected Map<String,StructuredContentField> structuredContentFields = new HashMap<String,StructuredContentField>();

    @AdminPresentation(friendlyName="Deleted", order=2, group="Internal", hidden = true)
    @Column(name = "DELETED_FLAG")
    protected Boolean deletedFlag;

    @AdminPresentation(friendlyName="Archived", order=3, group="Internal", hidden = true)
    @Column(name = "ARCHIVED_FLAG")
    protected Boolean archivedFlag;

    @AdminPresentation(friendlyName="Active Start Date", order=5, group="Description")
    @Column(name = "ACTIVE_START_DATE")
    protected Date activeStartDate;

    @AdminPresentation(friendlyName="Active End Date", order=6, group="Description")
    @Column(name = "ACTIVE_END_DATE")
    protected Date activeEndDate;

    @AdminPresentation(friendlyName="Online", order=7, group="Description")
    @Column(name = "ONLINE_FLAG")
    protected Boolean onlineFlag;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getContentName() {
        return contentName;
    }

    @Override
    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    @Override
    public String getLanguageCode() {
        return languageCode;
    }

    @Override
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    @Override
    public SandBox getSandbox() {
        return sandbox;
    }

    @Override
    public void setSandbox(SandBox sandbox) {
        this.sandbox = sandbox;
    }

    @Override
    public StructuredContentType getStructuredContentType() {
        return structuredContentType;
    }

    @Override
    public void setStructuredContentType(StructuredContentType structuredContentType) {
        this.structuredContentType = structuredContentType;
    }

    @Override
    public Map<String, StructuredContentField> getStructuredContentFields() {
        return structuredContentFields;
    }

    @Override
    public void setStructuredContentFields(Map<String, StructuredContentField> structuredContentFields) {
        this.structuredContentFields = structuredContentFields;
    }

    @Override
    public Boolean getDeletedFlag() {
        return deletedFlag;
    }

    @Override
    public void setDeletedFlag(Boolean deletedFlag) {
        this.deletedFlag = deletedFlag;
    }

    @Override
    public Boolean getOnlineFlag() {
        return onlineFlag;
    }

    @Override
    public void setOnlineFlag(Boolean onlineFlag) {
        this.onlineFlag = onlineFlag;
    }

    @Override
    public Date getActiveStartDate() {
        return activeStartDate;
    }

    @Override
    public void setActiveStartDate(Date activeStartDate) {
        this.activeStartDate = activeStartDate;
    }

    @Override
    public Date getActiveEndDate() {
        return activeEndDate;
    }

    @Override
    public void setActiveEndDate(Date activeEndDate) {
        this.activeEndDate = activeEndDate;
    }

    @Override
    public Integer getPriority() {
        return priority;
    }

    @Override
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Override
    public String getDisplayRule(String displayRule) {
        return displayRule;
    }

    @Override
    public void setDisplayRule(String displayRule) {
        this.displayRule = displayRule;
    }

    @Override
    public Long getOriginalItemId() {
        return originalItemId;
    }

    @Override
    public void setOriginalItemId(Long originalItemId) {
        this.originalItemId = originalItemId;
    }

    @Override
    public Boolean getArchivedFlag() {
        return archivedFlag;
    }

    @Override
    public void setArchivedFlag(Boolean archivedFlag) {
        this.archivedFlag = archivedFlag;
    }

    @Override
    public StructuredContent cloneEntity() {
        StructuredContentImpl newContent = new StructuredContentImpl();
        newContent.displayRule = displayRule;
        newContent.activeEndDate = activeEndDate;
        newContent.activeStartDate = activeStartDate;
        newContent.archivedFlag = archivedFlag;
        newContent.contentName = contentName;
        newContent.deletedFlag = deletedFlag;
        newContent.languageCode = languageCode;
        newContent.onlineFlag = onlineFlag;
        newContent.originalItemId = originalItemId;
        newContent.priority = priority;
        newContent.structuredContentType = structuredContentType;

        Map fieldMap = newContent.getStructuredContentFields();
        for (StructuredContentField field : structuredContentFields.values()) {
            StructuredContentField newField = field.cloneEntity();
            fieldMap.put(field.getFieldKey(), field);
        }
        return newContent;
    }
}
