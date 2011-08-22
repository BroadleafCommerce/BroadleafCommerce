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
package org.broadleafcommerce.cms.site.domain;

import org.broadleafcommerce.cms.message.domain.ContentMessage;
import org.broadleafcommerce.cms.message.domain.ContentMessageImpl;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.List;

/**
 * Created by bpolster.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_SITE")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
public class SiteImpl implements Site {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SiteId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "SiteId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "SiteImpl", allocationSize = 10)
    @Column(name = "SITE_ID")
    protected Long id;

    @Column (name = "NAME")
    protected String name;

    @Column (name = "SITE_IDENTIFIER_TYPE")
    protected String siteIdentifierType;

    @Column (name = "SITE_IDENTIFIER_VALUE")
    protected String siteIdentifierValue;

    @Column (name = "SANDBOX_NAME")
    protected String sandboxName;

    @OneToMany(targetEntity = ContentMessageImpl.class)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCMSElements")
    @BatchSize(size = 20)
    protected List<ContentMessage> contentMessages;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getSiteIdentifierType() {
        return siteIdentifierType;
    }

    @Override
    public void setSiteIdentifierType(String siteIdentifierType) {
        this.siteIdentifierType = siteIdentifierType;
    }

    @Override
    public String getSiteIdentifierValue() {
        return siteIdentifierValue;
    }

    @Override
    public void setSiteIdentifierValue(String siteIdentifierValue) {
        this.siteIdentifierValue = siteIdentifierValue;
    }

    @Override
    public String getSandboxName() {
        return sandboxName;
    }

    @Override
    public void setSandboxName(String sandboxName) {
        this.sandboxName = sandboxName;
    }

    @Override
    public List<ContentMessage> getContentMessages() {
        return contentMessages;
    }

    @Override
    public void setContentMessages(List<ContentMessage> contentMessages) {
        this.contentMessages = contentMessages;
    }
}

