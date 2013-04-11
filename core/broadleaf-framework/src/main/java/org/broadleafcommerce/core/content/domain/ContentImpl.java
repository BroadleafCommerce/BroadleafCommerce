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

package org.broadleafcommerce.core.content.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import java.util.Date;

/**
* Basic content item for the BLC CMS support
*
* @author dwtalk
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CONTENT")
public class ContentImpl implements Content {
    private static final Log LOG = LogFactory.getLog(CategoryImpl.class);

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    @Column(name = "ACTIVE_END_DATE")
    protected Date activeEndDate;
    @Column(name = "ACTIVE_START_DATE")
    protected Date activeStartDate;
    @Column(name = "APPROVED_BY")
    protected String approvedBy;
    @Column(name = "APPROVED_DATE")
    protected Date approvedDate;
    @Column(name = "BROWSER_TITLE")
    protected String browserTitle;
    @Column(name = "CONTENT_DATE")
    protected Date contentDate;
    @Column(name = "CONTENT_TYPE")
    protected String contentType;
    @Column(name = "DISPLAY_RULE")
    protected String displayRule;
    @Column(name = "DEPLOYED")
    protected Boolean deployed;
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    @Column(name = "DESCRIPTION")
    protected String description;
    @Id
    @GeneratedValue(generator = "ContentId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "ContentId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "ContentImpl", allocationSize = 50)
    @Column(name = "ID")
    protected Integer id;
    @Column(name = "KEYWORDS")
    protected String keywords;
    @Column(name = "LANGUAGE_CODE")
    protected String languageCode;
    @Column(name = "LAST_MODIFIED_DATE")
    protected Date lastModifiedDate;
    @Column(name = "LAST_MODIFIED_BY")
    protected String lastModifiedBy;
    @Column(name = "META_DESCRIPTION")
    protected String metaDescription;
    @Column(name = "NOTE")
    protected String note;
    @Column(name = "ONLINE_STATE")
    protected Boolean online;
    @Column(name = "PARENT_CONTENT_ID")
    protected Integer parentContentId;
    @Column(name = "PRIORITY")
    protected Integer priority;
    @Column(name = "REJECTED_BY")
    protected String rejectedBy;
    @Column(name = "REJECTED_DATE")
    protected Date rejectedDate;
    @Column(name = "RENDER_TEMPLATE")
    protected String renderTemplate;
    @Column(name = "SANDBOX")
    @Index(name = "CONTENT_INDEX", columnNames =  {
        "SANDBOX", "TITLE"}
    )
    protected String sandbox;
    @Column(name = "SUBMITTED_BY")
    protected String submittedBy;
    @Column(name = "SUBMITTED_DATE")
    protected Date submittedDate;
    @Column(name = "TITLE")
    protected String title;
    @Column(name = "URL_TITLE")
    protected String urlTitle;

    public ContentImpl() {
    }

    public ContentImpl(Content cnt, String sandbox, boolean deployed) {
        super();
        this.activeEndDate = cnt.getActiveEndDate();
        this.activeStartDate = cnt.getActiveStartDate();
        this.approvedBy = cnt.getApprovedBy();
        this.approvedDate = cnt.getApprovedDate();
        this.browserTitle = cnt.getBrowserTitle();
        this.contentDate = cnt.getContentDate();
        this.contentType = cnt.getContentType();
        this.displayRule = cnt.getDisplayRule();
        this.description = cnt.getDescription();
        this.deployed = deployed;
        this.keywords = cnt.getKeywords();
        this.languageCode = cnt.getLanguageCode();
        this.lastModifiedBy = cnt.getLastModifiedBy();
        this.lastModifiedDate = cnt.getLastModifiedDate();
        this.metaDescription = cnt.getMetaDescription();
        this.note = cnt.getNote();
        this.parentContentId = cnt.getParentContentId();
        this.priority = cnt.getPriority();
        this.rejectedBy = cnt.getRejectedBy();
        this.rejectedDate = cnt.getRejectedDate();
        this.renderTemplate = cnt.getRenderTemplate();
        this.sandbox = sandbox;
        this.submittedBy = cnt.getSubmittedBy();
        this.submittedDate = cnt.getSubmittedDate();
        this.title = cnt.getTitle();
        this.urlTitle = cnt.getUrlTitle();
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#getActiveEndDate()
     */
    public Date getActiveEndDate() {
        return activeEndDate;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#getActiveStartDate()
     */
    public Date getActiveStartDate() {
        return activeStartDate;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#getApprovedBy()
     */
    public String getApprovedBy() {
        return approvedBy;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#getApprovedDate()
     */
    public Date getApprovedDate() {
        return approvedDate;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#getBrowserTitle()
     */
    public String getBrowserTitle() {
        return browserTitle;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#getContentDate()
     */
    public Date getContentDate() {
        return contentDate;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#getContentType()
     */
    public String getContentType() {
        return contentType;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#getDisplayRule()
     */
    public String getDisplayRule() {
        return displayRule;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#isDeployed()
     */
    public Boolean getDeployed() {
        return deployed;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#getId()
     */
    public Integer getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#getKeywords()
     */
    public String getKeywords() {
        return keywords;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#getLanguage()
     */
    public String getLanguageCode() {
        return languageCode;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#getLastModifiedBy()
     */
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#getLastModifiedDate()
     */
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#getMetaDescription()
     */
    public String getMetaDescription() {
        return metaDescription;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#getNote()
     */
    public String getNote() {
        return note;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#isOnline()
     */
    public Boolean getOnline() {
        return online;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#getParentContentId()
     */
    public Integer getParentContentId() {
        return parentContentId;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#getPriority()
     */
    public Integer getPriority() {
        return priority;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#getRejectedBy()
     */
    public String getRejectedBy() {
        return rejectedBy;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#getRejectedDate()
     */
    public Date getRejectedDate() {
        return rejectedDate;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#getRenderTemplate()
     */
    public String getRenderTemplate() {
        return renderTemplate;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#getSandbox()
     */
    public String getSandbox() {
        return sandbox;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#getSubmittedBy()
     */
    public String getSubmittedBy() {
        return submittedBy;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#getSubmittedDate()
     */
    public Date getSubmittedDate() {
        return submittedDate;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#getTitle()
     */
    public String getTitle() {
        return title;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#getUrlTitle()
     */
    public String getUrlTitle() {
        return urlTitle;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setActiveEndDate(java.util.Date)
     */
    public void setActiveEndDate(Date activeEndDate) {
        this.activeEndDate = activeEndDate;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setActiveStartDate(java.util.Date)
     */
    public void setActiveStartDate(Date activeStartDate) {
        this.activeStartDate = activeStartDate;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setApprovedBy(java.lang.String)
     */
    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setApprovedDate(java.util.Date)
     */
    public void setApprovedDate(Date approvedDate) {
        this.approvedDate = approvedDate;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setContentType(java.lang.String)
     */
    public void setBrowserTitle(String browserTitle) {
        this.browserTitle = browserTitle;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setContentDate(java.util.Date)
     */
    public void setContentDate(Date contentDate) {
        this.contentDate = contentDate;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setContentType(java.lang.String)
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setDisplayRule(java.lang.String)
     */
    public void setDisplayRule(String displayRule) {
        this.displayRule = displayRule;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setDeployed(java.lang.Boolean)
     */
    public void setDeployed(Boolean deployed) {
        this.deployed = deployed;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setFilePathName(java.lang.String)
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setId(java.lang.Integer)
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setKeywords(java.lang.String)
     */
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setLanguageCode(java.lang.String)
     */
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setLastModifiedBy(java.lang.String)
     */
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setLastModifiedDate(java.util.Date)
     */
    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setMetaDescription(java.lang.String)
     */
    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setNote(java.lang.String)
     */
    public void setNote(String note) {
        this.note = note;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setOnline(java.lang.Boolean)
     */
    public void setOnline(Boolean online) {
        this.online = online;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setParentContentId(java.lang.Integer)
     */
    public void setParentContentId(Integer parentContentId) {
        this.parentContentId = parentContentId;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setPriority(java.lang.Integer)
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setRejectedBy(java.lang.String)
     */
    public void setRejectedBy(String rejectedBy) {
        this.rejectedBy = rejectedBy;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setRejectedDate(java.util.Date)
     */
    public void setRejectedDate(Date rejectedDate) {
        this.rejectedDate = rejectedDate;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setRenderTemplate(java.lang.String)
     */
    public void setRenderTemplate(String renderTemplate) {
        this.renderTemplate = renderTemplate;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setSandbox(java.lang.String)
     */
    public void setSandbox(String sandbox) {
        this.sandbox = sandbox;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setSubmittedBy(java.lang.String)
     */
    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setSubmittedDate(java.util.Date)
     */
    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setTitle(java.lang.String)
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.domain.Content#setUrlTitle(java.lang.String)
     */
    public void setUrlTitle(String urlTitle) {
        this.urlTitle = urlTitle;
    }
}
