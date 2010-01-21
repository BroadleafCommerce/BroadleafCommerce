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
package org.broadleafcommerce.content.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.broadleafcommerce.catalog.domain.CategoryImpl;

import org.hibernate.annotations.Index;

/**
* DOCUMENT ME!
*
* @author btaylor
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CONTENT")
public class ContentImpl implements Content {
    private static final Log LOG = LogFactory.getLog(CategoryImpl.class);

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    @Column(name = "ACTIVE")
    protected Boolean active;
    @Column(name = "ACTIVE_END_DATE")
    protected Date activeEndDate;
    @Column(name = "ACTIVE_START_DATE")
    protected Date activeStartDate;
    @Column(name = "APPROVED_DATE")
    protected Date approvedDate;
    @Column(name = "REJECTED_DATE")
    protected Date rejectedDate;
    @Column(name = "SUBMITTED_DATE")
    protected Date submittedDate;
    @Column(name = "MAX_COUNT")
    protected Integer maxCount;
    @Column(name = "priority")
    protected Integer priority;
    @Id
    @GeneratedValue(generator = "ContentId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "ContentId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "ContentImpl", allocationSize = 50)
    @Column(name = "ID")
    protected Long id;
    @Column(name = "APPROVED_BY")
    protected String approvedBy;
    @Column(name = "CONTENT_TYPE")
    protected String contentType;
    @Column(name = "DISPLAY_RULE")
    protected String displayRule;
    @Column(name = "FILE_PATH_NAME")
    protected String filePathName;
    @Column(name = "LANGUAGE")
    protected String language;
    @Column(name = "NOTE")
    protected String note;
    @Column(name = "REJECTED_BY")
    protected String rejectedBy;
    @Column(name = "SANDBOX")
    @Index(name = "CONTENT_INDEX", columnNames =  {
        "SANDBOX", "FILE_PATH_NAME"}
    )
    protected String sandbox;
    @Column(name = "SUBMITTED_BY")
    protected String submittedBy;
    @Column(name = "DEPLOYED")
    protected boolean deployed;

    public ContentImpl() {
    }

    public ContentImpl(Content cnt, String sandbox, boolean deployed) {
        super();
        this.activeEndDate = cnt.getActiveEndDate();
        this.activeStartDate = cnt.getActiveStartDate();
        this.approvedDate = cnt.getApprovedDate();
        this.rejectedDate = cnt.getRejectedDate();
        this.submittedDate = cnt.getSubmittedDate();
        this.maxCount = cnt.getMaxCount();
        this.priority = cnt.getPriority();
        this.approvedBy = cnt.getApprovedBy();
        this.contentType = cnt.getContentType();
        this.displayRule = cnt.getDisplayRule();
        this.filePathName = cnt.getFilePathName();
        this.note = cnt.getNote();
        this.rejectedBy = cnt.getRejectedBy();
        this.sandbox = sandbox;
        this.submittedBy = cnt.getSubmittedBy();
        this.deployed = deployed;
    }

    /**
    * DOCUMENT ME!
    *
    * @return the activeEndDate
    */
    public Date getActiveEndDate() {
        return activeEndDate;
    }

    /**
    * DOCUMENT ME!
    *
    * @return the activeStartDate
    */
    public Date getActiveStartDate() {
        return activeStartDate;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.content.domain.Content#getApprovedBy()
     */
    public String getApprovedBy() {
        return approvedBy;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.content.domain.Content#getApprovedDate()
     */
    public Date getApprovedDate() {
        return approvedDate;
    }

    /**
    * DOCUMENT ME!
    *
    * @return the contentType
    */
    public String getContentType() {
        return contentType;
    }

    /**
    * DOCUMENT ME!
    *
    * @return the displayRule
    */
    public String getDisplayRule() {
        return displayRule;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.content.domain.Content#getFilePathName()
     */
    public String getFilePathName() {
        return filePathName;
    }

    /**
    * DOCUMENT ME!
    *
    * @return the id
    */
    public Long getId() {
        return id;
    }

    /**
    * DOCUMENT ME!
    *
    * @return the language
    */
    public String getLanguage() {
        return language;
    }

    /**
    * DOCUMENT ME!
    *
    * @return the maxCount
    */
    public Integer getMaxCount() {
        return maxCount;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.content.domain.Content#getNote()
     */
    public String getNote() {
        return note;
    }

    /**
    * DOCUMENT ME!
    *
    * @return the priority
    */
    public Integer getPriority() {
        return priority;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.content.domain.Content#getRejectedBy()
     */
    public String getRejectedBy() {
        return rejectedBy;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.content.domain.Content#getRejectedDate()
     */
    public Date getRejectedDate() {
        return rejectedDate;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.content.domain.Content#getSandbox()
     */
    public String getSandbox() {
        return sandbox;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.content.domain.Content#getSubmittedBy()
     */
    public String getSubmittedBy() {
        return submittedBy;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.content.domain.Content#getSubmittedDate()
     */
    public Date getSubmittedDate() {
        return submittedDate;
    }

    /**
    * DOCUMENT ME!
    *
    * @return the active
    */
    public Boolean isActive() {
        return active;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.content.domain.Content#isDeployed()
     */
    public boolean isDeployed() {
        return deployed;
    }

    /**
    * DOCUMENT ME!
    *
    * @param active the active to set
    */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
    * DOCUMENT ME!
    *
    * @param activeEndDate the activeEndDate to set
    */
    public void setActiveEndDate(Date activeEndDate) {
        this.activeEndDate = activeEndDate;
    }

    /**
    * DOCUMENT ME!
    *
    * @param activeStartDate the activeStartDate to set
    */
    public void setActiveStartDate(Date activeStartDate) {
        this.activeStartDate = activeStartDate;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public void setApprovedDate(Date approvedDate) {
        this.approvedDate = approvedDate;
    }

    /**
    * DOCUMENT ME!
    *
    * @param contentType the contentType to set
    */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
    * DOCUMENT ME!
    *
    * @param displayRule the displayRule to set
    */
    public void setDisplayRule(String displayRule) {
        this.displayRule = displayRule;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.content.domain.Content#setFilePathName(java.lang.String)
     */
    public void setFilePathName(String filePathName) {
        this.filePathName = filePathName;
    }

    /**
    * DOCUMENT ME!
    *
    * @param id the id to set
    */
    public void setId(Long id) {
        this.id = id;
    }

    /**
    * DOCUMENT ME!
    *
    * @param language the language to set
    */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
    * DOCUMENT ME!
    *
    * @param maxCount the maxCount to set
    */
    public void setMaxCount(Integer maxCount) {
        this.maxCount = maxCount;
    }

    /**
    * DOCUMENT ME!
    *
    * @param priority the priority to set
    */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public void setRejectedBy(String rejectedBy) {
        this.rejectedBy = rejectedBy;
    }

    public void setRejectedDate(Date rejectedDate) {
        this.rejectedDate = rejectedDate;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.content.domain.Content#setSandbox(java.lang.String)
     */
    public void setSandbox(String sandbox) {
        this.sandbox = sandbox;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }
}
