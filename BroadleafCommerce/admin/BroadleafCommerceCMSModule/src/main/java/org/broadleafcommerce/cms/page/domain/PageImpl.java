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
package org.broadleafcommerce.cms.page.domain;

import javax.persistence.*;

import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.openadmin.audit.AdminAuditable;
import org.broadleafcommerce.openadmin.audit.AdminAuditableListener;
import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.domain.SandBoxImpl;
import org.broadleafcommerce.openadmin.server.domain.Site;
import org.broadleafcommerce.presentation.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;

/**
 * Created by bpolster.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PAGE")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
@EntityListeners(value = { AdminAuditableListener.class })
@AdminPresentationOverrides(
    {
        @AdminPresentationOverride(name="auditable.createdBy.name", value=@AdminPresentation(hidden = true)),
        @AdminPresentationOverride(name="auditable.updatedBy.name", value=@AdminPresentation(hidden = true)),
        @AdminPresentationOverride(name="auditable.dateCreated", value=@AdminPresentation(hidden = true)),
        @AdminPresentationOverride(name="auditable.dateUpdated", value=@AdminPresentation(hidden = true)),
        @AdminPresentationOverride(name="auditable.createdBy.login", value=@AdminPresentation(excluded = true)),
        @AdminPresentationOverride(name="auditable.createdBy.password", value=@AdminPresentation(excluded = true)),
        @AdminPresentationOverride(name="auditable.createdBy.email", value=@AdminPresentation(excluded = true)),
        @AdminPresentationOverride(name="auditable.createdBy.currentSandBox", value=@AdminPresentation(excluded = true)),
        @AdminPresentationOverride(name="auditable.updatedBy.login", value=@AdminPresentation(excluded = true)),
        @AdminPresentationOverride(name="auditable.updatedBy.password", value=@AdminPresentation(excluded = true)),
        @AdminPresentationOverride(name="auditable.updatedBy.email", value=@AdminPresentation(excluded = true)),
        @AdminPresentationOverride(name="auditable.updatedBy.currentSandBox", value=@AdminPresentation(excluded = true)),
        @AdminPresentationOverride(name="pageTemplate.templateDescription", value=@AdminPresentation(excluded = true)),
        @AdminPresentationOverride(name="pageTemplate.locale", value=@AdminPresentation(excluded = true))
    }
)
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE)
public class PageImpl implements Page {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "PageId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "PageId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "PageImpl", allocationSize = 10)
    @Column(name = "ID")
    protected Long id;
    
    @ManyToOne (targetEntity = PageTemplateImpl.class)
    @JoinColumn(name = "PAGE_TEMPLATE_ID")
    @AdminPresentation(friendlyName="Page Template", order=3, group="Basic", excluded=true)
    protected PageTemplate pageTemplate;

    @Column (name = "DESCRIPTION")
    @AdminPresentation(friendlyName="Description", order=2, group="Basic", prominent=true)
    protected String description;

    @Column (name = "FULL_URL")
    @AdminPresentation(friendlyName="Full Url", order=1, group="Basic", prominent=true)
    protected String fullUrl;

    @ManyToMany(targetEntity = PageFieldImpl.class, cascade = CascadeType.ALL)
    @JoinTable(name = "BLC_PAGE_FIELD_MAP", inverseJoinColumns = @JoinColumn(name = "PAGE_FIELD_ID", referencedColumnName = "PAGE_FIELD_ID"))
    @org.hibernate.annotations.MapKey(columns = {@Column(name = "MAP_KEY", nullable = false)})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCMSElements")
    @BatchSize(size = 20)
    protected Map<String,PageField> pageFields = new HashMap<String,PageField>();

    @ManyToOne (targetEntity = SandBoxImpl.class)
    @JoinColumn(name="SANDBOX_ID")
    @AdminPresentation(excluded = true)
    protected SandBox sandbox;

    @ManyToOne(targetEntity = SandBoxImpl.class)
    @JoinColumn(name = "ORIGINAL_SANDBOX_ID")
    @AdminPresentation(excluded = true)
	protected SandBox originalSandBox;

    @Column (name = "DELETED_FLAG")
    @AdminPresentation(friendlyName="Deleted", order=2, group="Description", hidden = true)
    protected Boolean deletedFlag = false;

    @Column (name = "ARCHIVED_FLAG")
    @AdminPresentation(friendlyName="Archived", order=5, group="Page", hidden = true)
    protected Boolean archivedFlag = false;

    @Column (name = "LOCKED_FLAG")
    @AdminPresentation(friendlyName="Is Locked", hidden = true)
    protected Boolean lockedFlag = false;

    @Column (name = "ORIGINAL_PAGE_ID")
    @AdminPresentation(friendlyName="Original Page ID", order=6, group="Page", hidden = true)
    protected Long originalPageId;

    /*@ManyToOne(targetEntity = SiteImpl.class)
    @JoinColumn(name="SITE_ID")*/
    @Transient
    @AdminPresentation(excluded = true)
    protected Site site;

    @Embedded
    protected AdminAuditable auditable = new AdminAuditable();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public PageTemplate getPageTemplate() {
        return pageTemplate;
    }

    @Override
    public void setPageTemplate(PageTemplate pageTemplate) {
        this.pageTemplate = pageTemplate;
    }

    @Override
    public Map<String, PageField> getPageFields() {
        return pageFields;
    }

    @Override
    public void setPageFields(Map<String, PageField> pageFields) {
        this.pageFields = pageFields;
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
    public Boolean getArchivedFlag() {
        return archivedFlag;
    }

    @Override
    public void setArchivedFlag(Boolean archivedFlag) {
        this.archivedFlag = archivedFlag;
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
    public Site getSite() {
        return site;
    }

    @Override
    public void setSite(Site site) {
        this.site = site;
    }

    @Override
    public Long getOriginalPageId() {
        return originalPageId;
    }

    @Override
    public void setOriginalPageId(Long originalPageId) {
        this.originalPageId = originalPageId;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
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

    public Boolean getLockedFlag() {
        return lockedFlag;
    }

    public void setLockedFlag(Boolean lockedFlag) {
        this.lockedFlag = lockedFlag;
    }

    @Override
    public Page cloneEntity() {
        PageImpl newPage = new PageImpl();
        newPage.site=site;

        newPage.archivedFlag = archivedFlag;
        newPage.deletedFlag = deletedFlag;
        newPage.pageTemplate = pageTemplate;
        newPage.description = description;
        newPage.sandbox = sandbox;
        newPage.originalPageId = originalPageId;
        newPage.originalSandBox = originalSandBox;
        newPage.fullUrl = fullUrl;

        for (PageField oldPageField: pageFields.values()) {
            PageField newPageField = oldPageField.cloneEntity();
            newPageField.setPage(newPage);
            newPage.getPageFields().put(newPageField.getFieldKey(), newPageField);
        }

        return newPage;
    }
}

