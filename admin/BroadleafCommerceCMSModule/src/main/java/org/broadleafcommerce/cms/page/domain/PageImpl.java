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

import org.broadleafcommerce.cms.site.domain.Site;
import org.broadleafcommerce.cms.site.domain.SiteImpl;
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
public class PageImpl implements Page {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "PageId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "PageId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "PageImpl", allocationSize = 10)
    @Column(name = "PAGE_ID")
    protected Long id;

    @ManyToOne(targetEntity = PageFolderImpl.class)
    @JoinColumn(name="PARENT_FOLDER_ID")
    protected PageFolder parentFolder;

    @ManyToOne(targetEntity = SiteImpl.class)
    @JoinColumn(name="SITE_ID")
    protected Site site;

    @Column (name = "FULL_URL")
    protected String fullUrl;

    @Column (name = "PAGE_FILE_NAME")
    protected String pageFileName;

    @ManyToOne (targetEntity = PageTemplateImpl.class)
    @JoinColumn(name = "PAGE_TEMPLATE_ID")
    protected PageTemplate pageTemplate;

    @Column (name = "META_KEYWORDS")
    protected String metaKeywords;

    @Column (name = "META_DESCRIPTION")
    protected String metaDescription;

    @OneToMany(mappedBy = "page", targetEntity = PageFieldImpl.class, cascade = {CascadeType.ALL})
    @MapKey(name = "fieldKey")
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCMSElements")
    protected Map<String,PageField> pageFields = new HashMap<String,PageField>();

    @Column (name = "DELETED_FLAG")
    protected Boolean deletedFlag = false;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public PageFolder getParentFolder() {
        return parentFolder;
    }

    @Override
    public void setParentFolder(PageFolder parentFolder) {
        this.parentFolder = parentFolder;
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
    public String getFullUrl() {
        return fullUrl;
    }

    @Override
    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    @Override
    public String getPageFileName() {
        return pageFileName;
    }

    @Override
    public void setPageFileName(String pageFileName) {
        this.pageFileName = pageFileName;
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
    public Boolean getDeletedFlag() {
        return deletedFlag;
    }

    @Override
    public void setDeletedFlag(Boolean deletedFlag) {
        this.deletedFlag = deletedFlag;
    }
}

