/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.common.config.domain;

import org.broadleafcommerce.common.audit.Auditable;
import org.broadleafcommerce.common.audit.AuditableListener;
import org.broadleafcommerce.common.config.service.type.ModuleConfigurationType;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.persistence.ArchiveStatus;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.util.DateUtil;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * Modules that need to be configured via the database should extend this.  Classes that
 * extend this MUST call setModuleConfigurationType(ModuleConfigurationType type) in their
 * constructor.
 *
 * @author Kelly Tisdell
 */

@Entity
@Table(name = "BLC_MODULE_CONFIGURATION")
@EntityListeners(value = {AuditableListener.class})
@Inheritance(strategy = InheritanceType.JOINED)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blConfigurationModuleElements")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTI_PHASE_ADD)
})
public abstract class AbstractModuleConfiguration implements ModuleConfiguration, Status, AbstractModuleConfigurationAdminPresentation {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "ModuleConfigurationId")
    @GenericGenerator(
            name = "ModuleConfigurationId",
            strategy = "org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
            parameters = {
                    @Parameter(name = "segment_value", value = "ModuleConfigurationImpl"),
                    @Parameter(name = "entity_name", value = "org.broadleafcommerce.common.config.domain" +
                            ".AbstractModuleConfiguration")
            }
    )
    @Column(name = "MODULE_CONFIG_ID")
    @AdminPresentation(visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @Column(name = "MODULE_NAME", nullable = false)
    @AdminPresentation(friendlyName = "AbstractModuleConfiguration_Module_Name", order = 2000, gridOrder = 2,
            prominent = true, requiredOverride = RequiredOverride.REQUIRED, group = GroupName.General)
    protected String moduleName;

    @Column(name = "ACTIVE_START_DATE", nullable = true)
    @AdminPresentation(friendlyName = "AbstractModuleConfiguration_Active_Start_Date", order = 3000, gridOrder = 3,
            prominent = true, fieldType = SupportedFieldType.DATE, group = GroupName.ActiveDates)
    protected Date activeStartDate;

    @Column(name = "ACTIVE_END_DATE", nullable = true)
    @AdminPresentation(friendlyName = "AbstractModuleConfiguration_Active_End_Date", order = 4000, gridOrder = 4,
            prominent = true, fieldType = SupportedFieldType.DATE, group = GroupName.ActiveDates)
    protected Date activeEndDate;

    @Column(name = "IS_DEFAULT", nullable = false)
    @AdminPresentation(friendlyName = "AbstractModuleConfiguration_Is_Default", order = 5000, gridOrder = 5,
            prominent = true, requiredOverride = RequiredOverride.REQUIRED, group = GroupName.Options)
    protected Boolean isDefault = false;

    /*
     * This should be set via the constructor of the child class with a call to setModuleConfigurationType
     * (ModuleConfigurationType).
     * It will not be set via the admin. The reason is that the type is know by the subclass.  The reason for this
     * field is to allow us to search for various types.
     * But this field must be set via the constructor on the subclass.
     */
    @Column(name = "CONFIG_TYPE", nullable = false)
    @AdminPresentation(friendlyName = "AbstractModuleConfiguration_Config_Type", order = 1000, gridOrder = 1,
            prominent = true, fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration = "org.broadleafcommerce.common.config.service.type.ModuleConfigurationType",
            requiredOverride = RequiredOverride.NOT_REQUIRED, readOnly = true, visibility = VisibilityEnum.FORM_HIDDEN)
    protected String configType;

    @Column(name = "MODULE_PRIORITY", nullable = false)
    @AdminPresentation(friendlyName = "AbstractModuleConfiguration_Priority",
            order = 6000, gridOrder = 6, prominent = true, requiredOverride = RequiredOverride.REQUIRED,
            tooltip = "AbstractModuleConfiguration_Priority_Tooltip", group = GroupName.Options)
    protected Integer priority = 100;

    @Embedded
    protected Auditable auditable = new Auditable();

    @Embedded
    protected ArchiveStatus archiveStatus = new ArchiveStatus();

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
    public Boolean getIsDefault() {
        if (this.isDefault == null) {
            this.isDefault = Boolean.FALSE;
        }
        return this.isDefault;
    }
    
    @Override
    public boolean isDefault() {
    	if (getIsDefault() != null) {
    		return getIsDefault();
    	}
    	return false;
    }

    @Override
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * Subclasses of this must set the ModuleConfigType in their constructor.
     */
    protected void setModuleConfigurationType(ModuleConfigurationType moduleConfigurationType) {
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

    @Override
    public void setArchived(Character archived) {
        archiveStatus.setArchived(archived);
    }

    @Override
    public Character getArchived() {
        ArchiveStatus temp;
        if (archiveStatus == null) {
            temp = new ArchiveStatus();
        } else {
            temp = archiveStatus;
        }
        return temp.getArchived();
    }

    @Override
    public boolean isActive() {
        return DateUtil.isActive(activeStartDate, activeEndDate, true) && 'Y' != getArchived();
    }

    @Override
    public void setActiveStartDate(Date startDate) {
        this.activeStartDate = startDate;
    }

    @Override
    public Date getActiveStartDate() {
        return this.activeStartDate;
    }

    @Override
    public void setActiveEndDate(Date endDate) {
        this.activeEndDate = endDate;
    }

    @Override
    public Date getActiveEndDate() {
        return this.activeEndDate;
    }

    @Override
    public Integer getPriority() {
        return priority;
    }

    @Override
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
