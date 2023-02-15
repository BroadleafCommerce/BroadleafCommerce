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
package org.broadleafcommerce.common.sandbox.domain;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.persistence.ArchiveStatus;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.ValidationConfiguration;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_SANDBOX")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blSandBoxElements")
@SQLDelete(sql="UPDATE BLC_SANDBOX SET ARCHIVED = 'Y' WHERE SANDBOX_ID = ?")
@DirectCopyTransform({
    @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.AUDITABLE_ONLY)
})
public class SandBoxImpl implements SandBox, AdminMainEntity {

    private static final Log LOG = LogFactory.getLog(SandBoxImpl.class);
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SandBoxId")
    @GenericGenerator(
        name="SandBoxId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="SandBoxImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.common.sandbox.domain.SandBoxImpl")
        }
    )
    @Column(name = "SANDBOX_ID")
    @AdminPresentation(visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;
    
    @Column(name = "SANDBOX_NAME")
    @Index(name="SANDBOX_NAME_INDEX", columnNames={"SANDBOX_NAME"})
    @AdminPresentation(friendlyName = "SandBoxImpl_Name", group = SandboxAdminPresentation.GroupName.Description, prominent = true, 
            gridOrder = 2000, order = 1000,
            validationConfigurations = { @ValidationConfiguration(validationImplementation = "blSandBoxNameValidator") })
    protected String name;
    
    @Column(name="AUTHOR")
    @AdminPresentation(friendlyName = "SandBoxImpl_Author", group = SandboxAdminPresentation.GroupName.Description,
        gridOrder = 3000, order = 3000, visibility = VisibilityEnum.FORM_HIDDEN)
    protected Long author;

    @Column(name = "SANDBOX_TYPE")
    @AdminPresentation(friendlyName = "SandBoxImpl_SandBox_Type", group = SandboxAdminPresentation.GroupName.Description,
        visibility = VisibilityEnum.HIDDEN_ALL, readOnly = true,
        fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
        broadleafEnumeration="org.broadleafcommerce.common.sandbox.domain.SandBoxType")
    //need to set a default value so that add sandbox works correctly in the admin
    protected String sandboxType = SandBoxType.APPROVAL.getType();

    @ManyToOne(targetEntity = SandBoxImpl.class)
    @JoinColumn(name = "PARENT_SANDBOX_ID")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blSandBoxElements")
    protected SandBox parentSandBox;

    @OneToMany(mappedBy = "parentSandBox", targetEntity = SandBoxImpl.class)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blSandBoxElements")
    protected List<SandBox> childSandBoxes;

    @Column(name = "COLOR")
    @AdminPresentation(friendlyName = "SandBoxImpl_Color", group = SandboxAdminPresentation.GroupName.Description, 
        prominent = true, gridOrder = 1000, fieldType = SupportedFieldType.COLOR, order = 2000)
    protected String color;

    @Column(name = "DESCRIPTION")
    @AdminPresentation(friendlyName = SandboxAdminPresentation.GroupName.Description, group = SandboxAdminPresentation.GroupName.Description,
        prominent = true, gridOrder = 4000, order = 4000)
    protected String description;

    /*
     * This field should not be used until logic for it is implemented.
     * 
     * @AdminPresentation(friendlyName = "SandBoxImpl_Go_Live_Date", group = SandboxAdminPresentation.GroupName.Description,
     *   prominent = true, gridOrder = 5000, order = 3000)
    */
    @Column(name = "GO_LIVE_DATE")
    protected Date goLiveDate;

    @Embedded
    protected ArchiveStatus archiveStatus = new ArchiveStatus();

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
    public SandBoxType getSandBoxType() {
        return SandBoxType.getInstance(sandboxType);
    }

    @Override
    public void setSandBoxType(final SandBoxType sandboxType) {
        if (sandboxType != null) {
            this.sandboxType = sandboxType.getType();
        }
    }

    @Override
    public Long getAuthor() {
        return author;
    }

    @Override
    public void setAuthor(Long author) {
        this.author = author;
    }

    @Override
    public SandBox getParentSandBox() {
        return parentSandBox;
    }

    @Override
    public void setParentSandBox(SandBox parentSandBox) {
        this.parentSandBox = parentSandBox;
    }

    @Override
    public String getColor() {
        if (StringUtils.isNotBlank(color)) {
            return color;
        }

        if (parentSandBox != null) {
            return parentSandBox.getColor();
        }

        return null;
    }

    @Override
    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public Date getGoLiveDate() {
        return goLiveDate;
    }

    @Override
    public void setGoLiveDate(Date goLiveDate) {
        this.goLiveDate = goLiveDate;
    }

    public List<SandBox> getChildSandBoxes() {
        return childSandBoxes;
    }

    public void setChildSandBoxes(List<SandBox> childSandBoxes) {
        this.childSandBoxes = childSandBoxes;
    }

    @Override
    public List<Long> getSandBoxIdsForUpwardHierarchy(boolean includeInherited) {
        return getSandBoxIdsForUpwardHierarchy(includeInherited, true);
    }

    @Override
    public List<Long> getSandBoxIdsForUpwardHierarchy(boolean includeInherited, boolean includeCurrent) {
        List<Long> ids = new ArrayList<Long>();
        if (includeCurrent) {
            ids.add(this.getId());
        }
        if (includeInherited) {
            SandBox current = this;
            while (current.getParentSandBox() != null) {
                current = current.getParentSandBox();
                ids.add(current.getId());
            }
            Collections.reverse(ids);
        }
        return ids;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(1, 31)
            .append(author)
            .append(id)
            .append(name)
            .append(color)
            .append(goLiveDate)
            .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && getClass().isAssignableFrom(obj.getClass())) {
            SandBoxImpl other = (SandBoxImpl) obj;
            return new EqualsBuilder()
                .append(author, other.author)
                .append(id, other.id)
                .append(name, other.name)
                .append(color, other.color)
                .append(goLiveDate, other.goLiveDate)
                .build();
        }
        return false;
    }

    @Override
    public String getMainEntityName() {
        return getName();
    }
    
    @Override
    public boolean getIsInDefaultHierarchy() {
        if (SandBoxType.DEFAULT.equals(getSandBoxType())) {
            return true;
        }

        if (getParentSandBox() != null) {
            return getParentSandBox().getIsInDefaultHierarchy();
        }
        
        return false;
    }

    @Override
    public void setArchived(Character archived) {
        if (archiveStatus == null) {
            archiveStatus = new ArchiveStatus();
        }
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
        return 'Y'!=getArchived();
    }
}
