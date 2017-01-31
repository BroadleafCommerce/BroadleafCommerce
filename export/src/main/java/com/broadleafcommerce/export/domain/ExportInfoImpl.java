/*
 * #%L
 * BroadleafCommerce Export Module
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package com.broadleafcommerce.export.domain;

import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BLC_EXPORT_INFO")
@DirectCopyTransform({
    @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE)
})
public class ExportInfoImpl implements ExportInfo, AdminMainEntity {

    @Id
    @GeneratedValue(generator = "ExportInfoId")
    @GenericGenerator(
        name="ExportInfoId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="ExportInfoImpl"),
            @Parameter(name="entity_name", value="com.broadleafcommerce.export.domain.ExportInfoImpl")
        }
    )
    @Column(name = "EXPORT_INFO_ID")
    @AdminPresentation(visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;
    
    @Column(name = "EXPORT_INFO_NAME")
    @AdminPresentation(friendlyName = "ExportInfo_Name",
        order=1000, gridOrder=2000,
        prominent = true
    )
    protected String name;
    
    @Column(name = "ENTITY_TYPE")
    @AdminPresentation(friendlyName = "ExportInfo_Entity_Type",
        order=2000,
        prominent = false
    )
    protected String entityType;
    
    @Column(name = "FILE_DATE_CREATED")
    @AdminPresentation(friendlyName = "ExportInfo_Date_Created",
        order=3000, gridOrder=3000,
        prominent = true
    )
    protected Date dateCreated;
    
    @Column(name = "FILE_SIZE")
    @AdminPresentation(friendlyName = "ExportInfo_File_Size",
        order=4000, gridOrder=4000,
        prominent = true
    )
    protected Long size;
    
    @Column(name = "RESOURCE_PATH")
    @AdminPresentation(visibility = VisibilityEnum.HIDDEN_ALL)
    protected String resourcePath;
    
    @Column(name = "FRIENDLY_RESOURCE_PATH")
    @AdminPresentation(friendlyName = "ExportInfo_Friendly_Path",
        order=5000, gridOrder=1000,
        prominent = true
    )
    protected String friendlyResourcePath;
    
    @Column(name = "FILE_CREATED_BY")
    @AdminPresentation(friendlyName = "ExportInfo_Admin_User",
        order=6000,
        prominent = false
    )
    protected Long adminUserId;
    
    @Column(name = "SHAREABLE")
    @AdminPresentation(friendlyName = "ExportInfo_Shareable",
        order=7000, gridOrder=5000,
        prominent = true
    )
    protected boolean shared;
    
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
    public String getEntityType() {
        return entityType;
    }
    
    @Override
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
    
    @Override
    public Date getDateCreated() {
        return dateCreated;
    }
    
    @Override
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
    
    @Override
    public Long getSize() {
        return size;
    }
    
    @Override
    public void setSize(Long size) {
        this.size = size;
    }
    
    @Override
    public String getResourcePath() {
        return resourcePath;
    }
    
    @Override
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }
    
    @Override
    public String getFriendlyResourcePath() {
        return friendlyResourcePath;
    }
    
    @Override
    public void setFriendlyResourcePath(String friendlyResourcePath) {
        this.friendlyResourcePath = friendlyResourcePath;
    }

    @Override
    public Long getAdminUserId() {
        return adminUserId;
    }
    
    @Override
    public void setAdminUserId(Long adminUserId) {
        this.adminUserId = adminUserId;
    }
    
    @Override
    public boolean isShared() {
        return shared;
    }
    
    @Override
    public void setShared(boolean shared) {
        this.shared = shared;
    }

    @Override
    public String getMainEntityName() {
        return getName();
    }
}
