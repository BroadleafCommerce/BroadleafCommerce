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

import org.broadleafcommerce.openadmin.server.domain.Site;
import org.broadleafcommerce.openadmin.server.domain.SiteImpl;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    @Column(name = "STRUCTURED_CONTENT_ID")
    protected Long id;

    @Column(name = "CONTENT_NAME")
    protected String contentName;

    @Column(name = "DESCRIPTION")
    protected String description;

    @Column(name = "LANGUAGE_CODE")
    protected String languageCode;

    /*@ManyToOne(targetEntity = SiteImpl.class)
    @JoinColumn(name="SITE_ID")*/
    @Transient
    protected Site site;

    @ManyToOne(targetEntity = StructuredContentTypeImpl.class)
    @JoinColumn(name="STRUCTURED_CONTENT_TYPE_ID")
    protected StructuredContentType structuredContentType;

    @OneToMany(mappedBy = "structuredContent", targetEntity = StructuredContentFieldImpl.class, cascade = {CascadeType.ALL})
    @MapKey(name = "fieldKey")
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCMSElements")
    protected Map<String,StructuredContentField> structuredContentFields = new HashMap<String,StructuredContentField>();

    @OneToMany(targetEntity = ContentDisplayRuleImpl.class)
    @JoinTable(name = "BLC_STRUCTURED_CONTENT_RULES",joinColumns = @JoinColumn(name = "STRUCTURED_CONTENT_ID"),inverseJoinColumns = @JoinColumn(name = "CONTENT_DISPLAY_RULE_ID"))
    protected List<ContentDisplayRule> contentDisplayRules;

    @Column(name = "DELETED_FLAG")
    protected Boolean deletedFlag;

    @Column(name = "ONLINE_FLAG")
    protected Boolean onlineFlag;

    @Column(name = "ACTIVE_START_DATE")
    protected Date activeStartDate;

    @Column(name = "ACTIVE_END_DATE")
    protected Date activeEndDate;

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
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
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
    public Site getSite() {
        return site;
    }

    @Override
    public void setSite(Site site) {
        this.site = site;
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
    public List<ContentDisplayRule> getContentDisplayRules() {
        return contentDisplayRules;
    }

    @Override
    public void setContentDisplayRules(List<ContentDisplayRule> contentDisplayRules) {
        this.contentDisplayRules = contentDisplayRules;
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
}