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

package org.broadleafcommerce.common.config.domain;

import org.broadleafcommerce.common.audit.Auditable;
import org.broadleafcommerce.common.audit.AuditableListener;
import org.broadleafcommerce.common.config.service.type.ModuleConfigurationType;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(name = "BLC_MODULE_CONFIGURATION")
@EntityListeners(value = { AuditableListener.class })
@Inheritance(strategy = InheritanceType.JOINED)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blStandardElements")
public class AbstractModuleConfiguration implements ModuleConfiguration {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "ModuleConfigurationId")
    @GenericGenerator(
        name = "ModuleConfigurationId",
        strategy = "org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
                @Parameter(name = "segment_value", value = "ModuleConfigurationImpl"),
                @Parameter(name = "entity_name", value = "org.broadleafcommerce.common.config.domain.AbstractModuleConfiguration")
        }
    )
    @Column(name = "MODULE_CONFIG_ID")
    protected Long id;

    @Column(name = "MODULE_NAME", nullable = false)
    @AdminPresentation(friendlyName = "AbstractModuleConfiguration_Module_Name", prominent = true)
    protected String moduleName;

    @Column(name = "IS_ACTIVE", nullable = false)
    @AdminPresentation(friendlyName = "AbstractModuleConfiguration_Is_Active")
    protected Boolean isActive = false;

    @Column(name = "IS_DEFAULT", nullable = false)
    @AdminPresentation(friendlyName = "AbstractModuleConfiguration_Is_Default")
    protected Boolean isDefault = false;

    @Column(name = "CONFIG_TYPE", nullable = false)
    @AdminPresentation(friendlyName = "AbstractModuleConfiguration_Config_Type", fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration = "org.broadleafcommerce.common.config.service.type.ModuleConfigurationType")
    protected String configType;

    @Embedded
    protected Auditable auditable = new Auditable();

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getModuleName() {
        return moduleName;
    }

    @Override
    public void setModuleName(String name) {
        this.moduleName = name;
    }

    @Override
    public Boolean getIsActive() {
        if (this.isActive == null) {
            return Boolean.FALSE;
        } else {
            return this.isActive;
        }
    }

    @Override
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public Boolean getIsDefault() {
        if (this.isDefault == null) {
            return Boolean.FALSE;
        } else {
            return this.isDefault;
        }
    }

    @Override
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public void setModuleConfigurationType(ModuleConfigurationType moduleConfigurationType) {
        this.configType = moduleConfigurationType.getType();
    }

    @Override
    public ModuleConfigurationType getModuleConfigurationType() {
        return ModuleConfigurationType.getInstance(this.configType);
    }

    @Override
    public void setAuditable(Auditable auditable) {
        this.auditable = auditable;
    }

    @Override
    public Auditable getAuditable() {
        return this.auditable;
    }

}
