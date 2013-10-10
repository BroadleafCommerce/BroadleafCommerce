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

package org.broadleafcommerce.common.sitemap.domain;

import org.broadleafcommerce.common.config.domain.AbstractModuleConfiguration;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapChangeFreqType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 
 * @author Joshua Skorton (jskorton)
 */
@Entity
@Table(name = "BLC_SITE_MAP_URL_ENTRY")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blConfigurationModuleElements")
@AdminPresentationClass(friendlyName = "SiteMapURLEntry")
public class SiteMapURLEntryImpl extends AbstractModuleConfiguration implements SiteMapURLEntry {

    private static final long serialVersionUID = 1L;
    
    @Column(name = "LOCATION", nullable = false)
    @AdminPresentation(friendlyName = "SiteMapURLEntryImpl_Location")
    protected String location;
    
    @Column(name = "LAST_MODIFIED", nullable = false)
    @AdminPresentation(friendlyName = "SiteMapURLEntryImpl_Last_Modified", fieldType = SupportedFieldType.DATE)
    protected Date lastModified = new Date();
    
    @Column(name = "CHANGE_FREQ_TYPE", nullable = false)
    @AdminPresentation(friendlyName = "SiteMapURLEntry_Site_Map_Change_Freq_Type", fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration = "org.broadleafcommerce.common.sitemap.service.type.SiteMapChangeFreqType")
    protected String siteMapChangeFreqType;

    @Column(name = "SITE_MAP_PRIORITY", precision = 2, scale = 1, nullable = false)
    @AdminPresentation(friendlyName = "SiteMapURLEntry_Site_Map_Priority")
    protected BigDecimal siteMapPriority = new BigDecimal("0.5");
    
    @ManyToOne(targetEntity = SiteMapGeneratorConfigurationImpl.class, optional = false)
    @JoinColumn(name = "MODULE_CONFIG_ID")
    protected SiteMapGeneratorConfiguration siteMapGeneratorConfiguration;
    
    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public Date getLastMod() {
        return lastModified;
    }

    @Override
    public void setLastMod(Date lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public SiteMapChangeFreqType getSiteMapChangeFreqType() {
        return SiteMapChangeFreqType.getInstance(this.siteMapChangeFreqType);
    }

    @Override
    public void setSiteMapChangeFreqType(SiteMapChangeFreqType siteMapChangeFreqType) {
        this.siteMapChangeFreqType = siteMapChangeFreqType.getType();
    }

    @Override
    public BigDecimal getSiteMapPriority() {
        return siteMapPriority;
    }

    @Override
    public void setSiteMapPriority(BigDecimal siteMapPriority) {
        this.siteMapPriority = siteMapPriority;
    }

    @Override
    public SiteMapGeneratorConfiguration getSiteMapGeneratorConfiguration() {
        return siteMapGeneratorConfiguration;
    }

    @Override
    public void setSiteMapGenerator(SiteMapGeneratorConfiguration siteMapGeneratorConfiguration) {
        this.siteMapGeneratorConfiguration = siteMapGeneratorConfiguration;
    }

}
