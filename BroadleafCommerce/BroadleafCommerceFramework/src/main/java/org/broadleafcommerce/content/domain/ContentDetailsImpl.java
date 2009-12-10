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

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
@Table(name = "BLC_CONTENT_DETAILS")
public class ContentDetailsImpl implements ContentDetails {
    private static final Log LOG = LogFactory.getLog(CategoryImpl.class);
    /** The Constant serialVersionUID.  */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
	@OneToOne(targetEntity = ContentImpl.class, mappedBy="ID")
	@JoinColumn(name = "ID")
	@Index(name = "CONTENT_DETAILS_INDEX", columnNames={"ID"})
	protected Long id;
    
    @Column(name = "HASH")
    protected String hash;
    
	@Column(name = "XML_CONTENT")
	protected String xmlContent;
	
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
	 * @return the hash
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * @param hash the hash to set
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}

	/**
	 * @return the xmlContent
	 */
	public String getXmlContent() {
		return xmlContent;
	}

	/**
	 * @param xmlContent the xmlContent to set
	 */
	public void setXmlContent(String xmlContent) {
		this.xmlContent = xmlContent;
	}
	
}
