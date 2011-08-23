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
package org.broadleafcommerce.cms.page.domain;

import org.broadleafcommerce.openadmin.audit.AuditableListener;
import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.domain.SandBoxImpl;
import org.broadleafcommerce.presentation.AdminPresentation;
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
@EntityListeners(value = { AuditableListener.class })
@Table(name = "BLC_PAGE")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
public class PageImpl extends PageFolderImpl implements Page {

    private static final long serialVersionUID = 1L;

    @ManyToOne (targetEntity = PageTemplateImpl.class)
    @JoinColumn(name = "PAGE_TEMPLATE_ID")
    protected PageTemplate pageTemplate;

    @Column (name = "META_KEYWORDS")
    @AdminPresentation(friendlyName="Meta Keywords", order=1, group="Page")
    protected String metaKeywords;

    @Column (name = "META_DESCRIPTION")
    @AdminPresentation(friendlyName="Meta Description", order=2, group="Page")
    protected String metaDescription;

    @OneToMany(mappedBy = "page", targetEntity = PageFieldImpl.class, cascade = {CascadeType.ALL})
    @MapKey(name = "fieldKey")
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCMSElements")
    protected Map<String,PageField> pageFields = new HashMap<String,PageField>();

    /*@ManyToOne (targetEntity = SandBoxImpl.class)
    @JoinTable(name = "BLC_SANDBOX_PAGE",joinColumns = @JoinColumn(name = "PAGE_ID"),inverseJoinColumns = @JoinColumn(name = "SANDBOX_ID"))
    @AdminPresentation(friendlyName="Page SandBox", order=3, group="Page", hidden = true)*/
    @Transient
    protected SandBox sandbox;

    @Column (name = "DELETED_FLAG")
    @AdminPresentation(friendlyName="Deleted", order=4, group="Page", hidden = true)
    protected Boolean deletedFlag = false;

    @Column (name = "ARCHIVED_FLAG")
    @AdminPresentation(friendlyName="Archived", order=5, group="Page", hidden = true)
    protected Boolean archivedFlag = false;

    @Column (name = "ORIGINAL_PAGE_ID")
    @AdminPresentation(friendlyName="Original Page ID", order=6, group="Page", hidden = true)
    protected Long originalPageId;

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
    public Long getOriginalPageId() {
        return originalPageId;
    }

    @Override
    public void setOriginalPageId(Long originalPageId) {
        this.originalPageId = originalPageId;
    }
}

