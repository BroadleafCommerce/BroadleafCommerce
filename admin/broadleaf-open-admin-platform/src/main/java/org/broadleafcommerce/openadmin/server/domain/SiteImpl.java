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

package org.broadleafcommerce.openadmin.server.domain;

import org.broadleafcommerce.openadmin.client.dto.VisibilityEnum;
import org.broadleafcommerce.presentation.AdminPresentation;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.persistence.Entity;

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
    @AdminPresentation(friendlyName="Site Name", order=1, group="Site")
    protected String name;

    @Column (name = "SITE_IDENTIFIER_TYPE")
    @AdminPresentation(friendlyName="Site Identifier Type", order=2, group="Site")
    protected String siteIdentifierType;

    @Column (name = "SITE_IDENTIFIER_VALUE")
    @AdminPresentation(friendlyName="Site Identifier Value", order=3, group="Site")
    protected String siteIdentifierValue;

    @ManyToOne(targetEntity = SandBoxImpl.class)
    @JoinColumn(name = "PRODUCTION_SANDBOX_ID")
    @AdminPresentation(friendlyName="Production SandBox", order=4, group="Site", visibility = VisibilityEnum.HIDDEN_ALL)
    protected SandBox productionSandbox;

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
    public SandBox getProductionSandbox() {
        return productionSandbox;
    }

    @Override
    public void setProductionSandbox(SandBox productionSandbox) {
        this.productionSandbox = productionSandbox;
    }
}

