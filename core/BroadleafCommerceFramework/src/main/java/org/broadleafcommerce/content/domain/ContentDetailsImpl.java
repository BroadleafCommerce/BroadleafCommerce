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
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
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
    @JoinColumn(name = "ID", unique=true, nullable=false, updatable=false)
    @Index(name = "CONTENT_DETAILS_INDEX", columnNames={"ID"})
    protected Integer id;
    @Column(name = "CONTENT_HASH")
    protected String contentHash;
    @Lob
    @Column(name = "XML_CONTENT")
    protected String xmlContent;

    public ContentDetailsImpl() {}

    public ContentDetailsImpl(ContentDetails cnt, Integer id) {
        this.contentHash = cnt.getContentHash();
        this.xmlContent = cnt.getXmlContent();
        this.id = id;
    }


    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the hash
     */
    public String getContentHash() {
        return contentHash;
    }

    /**
     * @param contentHash the hash to set
     */
    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
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
