/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.broadleafcommerce.common.sitemap.domain;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapChangeFreqType;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapGeneratorType;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapPriorityType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 
 * @author Joshua Skorton (jskorton)
 */
@Entity
@Table(name = "BLC_SITE_MAP_GEN_CFG")
@Inheritance(strategy = InheritanceType.JOINED)
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blConfigurationModuleElements")
@AdminPresentationClass(friendlyName = "SiteMapGeneratorConfigurationImpl")
public class SiteMapGeneratorConfigurationImpl implements SiteMapGeneratorConfiguration {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "GeneratorConfigurationId")
    @GenericGenerator(
            name = "GeneratorConfigurationId",
            strategy = "org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
            parameters = {
                    @Parameter(name = "segment_value", value = "SiteMapGeneratorConfigurationImpl"),
                    @Parameter(name = "entity_name", value = "org.broadleafcommerce.common.sitemap.domain.SiteMapGeneratorConfigurationImpl")
            })
    @Column(name = "GEN_CONFIG_ID")
    protected Long id;

    @Column(name = "DISABLED", nullable = false)
    @AdminPresentation(friendlyName = "SiteMapGeneratorConfigurationImpl_Disabled", gridOrder = 2, prominent = true)
    protected Boolean disabled = false;
    
    @Column(name = "CHANGE_FREQ", nullable = false)
    @AdminPresentation(friendlyName = "SiteMapGeneratorConfigurationImpl_Change_Freq", fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration = "org.broadleafcommerce.common.sitemap.service.type.SiteMapChangeFreqType", gridOrder = 3, prominent = true)
    protected String changeFreq;

    @Column(name = "PRIORITY", nullable = true)
    @AdminPresentation(friendlyName = "SiteMapGeneratorConfigurationImpl_Priority", fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration = "org.broadleafcommerce.common.sitemap.service.type.SiteMapPriorityType", gridOrder = 4, prominent = true)
    protected String priority;

    @Column(name = "GENERATOR_TYPE", nullable = false)
    @AdminPresentation(friendlyName = "SiteMapGeneratorConfigurationImpl_Generator_Type", fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration = "org.broadleafcommerce.common.sitemap.service.type.SiteMapGeneratorType", gridOrder = 1, prominent = true)
    protected String generatorType;
    
    @ManyToOne(targetEntity = SiteMapConfigurationImpl.class, optional = false)
    @JoinColumn(name = "MODULE_CONFIG_ID")
    protected SiteMapConfiguration siteMapConfiguration;
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public SiteMapChangeFreqType getSiteMapChangeFreq() {
        if (changeFreq != null) {
            return SiteMapChangeFreqType.getInstance(this.changeFreq);
        } else {
            return null;
        }
    }

    @Override
    public void setSiteMapChangeFreq(SiteMapChangeFreqType siteMapChangeFreq) {
        if (siteMapChangeFreq != null) {
            this.changeFreq = siteMapChangeFreq.getType();
        } else {
            this.changeFreq = null;
        }
    }

    @Override
    public SiteMapPriorityType getSiteMapPriority() {
        if (priority != null) {
            return SiteMapPriorityType.getInstance(this.priority);
        } else {
            return null;
        }
    }

    @Override
    public void setSiteMapPriority(SiteMapPriorityType siteMapPriority) {
        if (siteMapPriority != null) {
            this.priority = siteMapPriority.getType();
        } else {
            this.priority = null;
        }
    }

    @Override
    public SiteMapGeneratorType getSiteMapGeneratorType() {
        if (generatorType != null) {
            return SiteMapGeneratorType.getInstance(this.generatorType);
        } else {
            return null;
        }
    }

    @Override
    public void setSiteMapGeneratorType(SiteMapGeneratorType siteMapGeneratorType) {
        if (siteMapGeneratorType != null) {
            this.generatorType = siteMapGeneratorType.getType();
        } else {
            this.generatorType = null;
        }
    }

    @Override
    public SiteMapConfiguration getSiteMapConfiguration() {
        return siteMapConfiguration;
    }

    @Override
    public void setSiteMapConfiguration(SiteMapConfiguration siteMapConfiguration) {
        this.siteMapConfiguration = siteMapConfiguration;
    }
}
