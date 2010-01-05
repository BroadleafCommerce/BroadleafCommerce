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
 * @author btaylor
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CONTENT")
public class ContentImpl implements Content {

    private static final Log LOG = LogFactory.getLog(CategoryImpl.class);
    /** The Constant serialVersionUID.  */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "ContentId", strategy= GenerationType.TABLE)
    @TableGenerator(name = "ContentId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "ContentImpl", allocationSize = 50)
    @Column(name = "ID")
    protected Long id;
    
    @Column(name = "CONTENT_TYPE")
    protected String contentType;
    
    @Column(name = "SANDBOX")
    @Index(name = "CONTENT_INDEX", columnNames={ "SANDBOX", "FILE_PATH_NAME"})
    protected String sandbox;
    
    @Column(name = "FILE_PATH_NAME")
    protected String filePathName;
    
    @Column(name = "MAX_COUNT")
    protected Integer maxCount;
    
    @Column(name = "priority")
    protected Integer priority;
    
    @Column(name = "ACTIVE")
    protected Boolean active;
    
    @Column(name = "ACTIVE_START_DATE")
    protected Date activeStartDate;
    
    @Column(name = "ACTIVE_END_DATE")
    protected Date activeEndDate;
    
    @Column(name = "DISPLAY_RULE")
    protected String displayRule;
    
    @Column(name = "LANGUAGE")
    protected String language;
    
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.content.domain.Content#getFilePathName()
	 */
	public String getFilePathName() {
		return filePathName;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.content.domain.Content#getSandbox()
	 */
	public String getSandbox() {
		return sandbox;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.content.domain.Content#setFilePathName(java.lang.String)
	 */
	public void setFilePathName(String filePathName) {
		this.filePathName = filePathName;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.content.domain.Content#setSandbox(java.lang.String)
	 */
	public void setSandbox(String sandbox) {
		this.sandbox = sandbox;
	}

	/**
	 * @return the maxCount
	 */
	public Integer getMaxCount() {
		return maxCount;
	}

	/**
	 * @param maxCount the maxCount to set
	 */
	public void setMaxCount(Integer maxCount) {
		this.maxCount = maxCount;
	}

	/**
	 * @return the priority
	 */
	public Integer getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	/**
	 * @return the active
	 */
	public Boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}

	/**
	 * @return the activeStartDate
	 */
	public Date getActiveStartDate() {
		return activeStartDate;
	}

	/**
	 * @param activeStartDate the activeStartDate to set
	 */
	public void setActiveStartDate(Date activeStartDate) {
		this.activeStartDate = activeStartDate;
	}

	/**
	 * @return the activeEndDate
	 */
	public Date getActiveEndDate() {
		return activeEndDate;
	}

	/**
	 * @param activeEndDate the activeEndDate to set
	 */
	public void setActiveEndDate(Date activeEndDate) {
		this.activeEndDate = activeEndDate;
	}

	/**
	 * @return the displayRule
	 */
	public String getDisplayRule() {
		return displayRule;
	}

	/**
	 * @param displayRule the displayRule to set
	 */
	public void setDisplayRule(String displayRule) {
		this.displayRule = displayRule;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	
	

}
