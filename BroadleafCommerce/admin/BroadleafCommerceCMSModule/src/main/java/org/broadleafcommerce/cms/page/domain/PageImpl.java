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

import org.broadleafcommerce.openadmin.audit.AdminAuditableListener;
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
@Table(name = "BLC_PAGE")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
@EntityListeners(value = { AdminAuditableListener.class })
public class PageImpl extends PageFolderImpl implements Page {

    private static final long serialVersionUID = 1L;
    
    @ManyToOne (targetEntity = PageTemplateImpl.class)
    @JoinColumn(name = "PAGE_TEMPLATE_ID")
    @AdminPresentation(friendlyName="Page Template", order=1, group="Page", requiredOverride = RequiredOverride.REQUIRED)
    protected PageTemplate pageTemplate;

    @Column (name = "FULL_URL")
    @AdminPresentation(friendlyName="Full Url", order=1, group="Page", hidden=true)
    protected String fullUrl;

    @Column (name = "META_KEYWORDS")
    @AdminPresentation(friendlyName="Meta Keywords", order=2, group="Page", largeEntry = true)
    protected String metaKeywords;

    @Column (name = "META_DESCRIPTION")
    @AdminPresentation(friendlyName="Meta Description", order=3, group="Page", largeEntry = true)
    protected String metaDescription;

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

    @Column (name = "ARCHIVED_FLAG")
    @AdminPresentation(friendlyName="Archived", order=5, group="Page", hidden = true)
    protected Boolean archivedFlag = false;

    @Column (name = "ORIGINAL_PAGE_ID")
    @AdminPresentation(friendlyName="Original Page ID", order=6, group="Page", hidden = true)
    protected Long originalPageId;

    public PageImpl() {
        folderFlag = false;
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
    public String getMetaKeywords() {
        return metaKeywords;
    }

    @Override
    public void setMetaKeywords(String metaKeywords) {
        this.metaKeywords = metaKeywords;
    }

    @Override
    public String getMetaDescription() {
        return metaDescription;
    }

    @Override
    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
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

    public SandBox getOriginalSandBox() {
        return originalSandBox;
    }

    public void setOriginalSandBox(SandBox originalSandBox) {
        this.originalSandBox = originalSandBox;
    }

    @Override
    public Page cloneEntity() {
        PageImpl newPage = new PageImpl();
        newPage.name=name;
        newPage.parentFolder=parentFolder;
        newPage.site=site;
        newPage.folderFlag=folderFlag;

        newPage.archivedFlag = archivedFlag;
        newPage.deletedFlag = deletedFlag;
        newPage.pageTemplate = pageTemplate;
        newPage.metaDescription = metaDescription;
        newPage.metaKeywords = metaKeywords;
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

