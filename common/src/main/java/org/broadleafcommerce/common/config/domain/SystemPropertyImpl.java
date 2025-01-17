/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.config.service.type.SystemPropertyFieldType;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.persistence.IdOverrideTableGenerator;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.ValidationConfiguration;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.io.Serial;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

/**
 * Allows the storage and retrieval of System Properties in the database
 * <p/>
 * User: Kelly Tisdell Date: 6/20/12
 */
@Entity
@Table(name = "BLC_SYSTEM_PROPERTY", indexes = {
        @Index(name = "IDX_BLSYPR_PROPERTY_NAME", columnList = "PROPERTY_NAME")
})
@Inheritance(strategy = InheritanceType.JOINED)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blSystemProperties")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX)
})
public class SystemPropertyImpl implements SystemProperty, AdminMainEntity, SystemPropertyAdminPresentation {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SystemPropertyId")
    @GenericGenerator(
            name = "SystemPropertyId",
            type = IdOverrideTableGenerator.class,
            parameters = {
                    @Parameter(name = "segment_value", value = "SystemPropertyImpl"),
                    @Parameter(name = "entity_name",
                            value = "org.broadleafcommerce.common.config.domain.SystemPropertyImpl")
            }
    )
    @Column(name = "BLC_SYSTEM_PROPERTY_ID")
    protected Long id;

    @Column(name = "PROPERTY_NAME", nullable = false)
    @AdminPresentation(friendlyName = "SystemPropertyImpl_name",
            group = GroupName.General, order = FieldOrder.ATTRIBUTE_NAME,
            prominent = true, gridOrder = 1000,
            requiredOverride = RequiredOverride.REQUIRED,
            validationConfigurations = @ValidationConfiguration(
                    validationImplementation = "blSystemPropertyAttributeNameValidator"))
    protected String name;

    @Column(name = "OVERRIDE_GENERATED_PROP_NAME")
    @AdminPresentation(friendlyName = "FieldImpl_overrideGeneratedPropertyName",
            group = GroupName.General)
    protected Boolean overrideGeneratedPropertyName = false;

    @Column(name = "PROPERTY_VALUE")
    @AdminPresentation(friendlyName = "SystemPropertyImpl_value",
            group = GroupName.General, order = FieldOrder.VALUE,
            prominent = true, gridOrder = 3000,
            requiredOverride = RequiredOverride.REQUIRED)
    protected String value;

    @Column(name = "PROPERTY_TYPE")
    @AdminPresentation(friendlyName = "SystemPropertyImpl_propertyType",
            group = GroupName.General, order = FieldOrder.PROPERTY_TYPE,
            prominent = true, gridOrder = 2000,
            fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration = "org.broadleafcommerce.common.config.service.type.SystemPropertyFieldType",
            requiredOverride = RequiredOverride.REQUIRED)
    protected String propertyType;

    @Column(name = "FRIENDLY_NAME")
    @AdminPresentation(friendlyName = "SystemPropertyImpl_friendlyName",
            group = GroupName.General, order = FieldOrder.FRIENDLY_NAME)
    protected String friendlyName;

    @Column(name = "FRIENDLY_GROUP")
    @AdminPresentation(friendlyName = "SystemPropertyImpl_friendlyGroup",
            group = GroupName.Placement, order = FieldOrder.GROUP_NAME)
    protected String friendlyGroup;

    @Column(name = "FRIENDLY_TAB")
    @AdminPresentation(friendlyName = "SystemPropertyImpl_friendlyTab",
            group = GroupName.Placement, order = FieldOrder.TAB_NAME)
    protected String friendlyTab;

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
    public Boolean getOverrideGeneratedPropertyName() {
        return overrideGeneratedPropertyName;
    }

    @Override
    public void setOverrideGeneratedPropertyName(Boolean overrideGeneratedPropertyName) {
        this.overrideGeneratedPropertyName = overrideGeneratedPropertyName;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getFriendlyName() {
        return friendlyName;
    }

    @Override
    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    @Override
    public String getFriendlyGroup() {
        return friendlyGroup;
    }

    @Override
    public void setFriendlyGroup(String friendlyGroup) {
        this.friendlyGroup = friendlyGroup;
    }

    @Override
    public String getFriendlyTab() {
        return friendlyTab;
    }

    @Override
    public void setFriendlyTab(String friendlyTab) {
        this.friendlyTab = friendlyTab;
    }

    @Override
    public SystemPropertyFieldType getPropertyType() {
        if (propertyType != null) {
            SystemPropertyFieldType returnType = SystemPropertyFieldType.getInstance(propertyType);
            if (returnType != null) {
                return returnType;
            }
        }
        return SystemPropertyFieldType.STRING_TYPE;
    }

    @Override
    public void setPropertyType(SystemPropertyFieldType propertyType) {
        this.propertyType = propertyType.getType();
    }

    @Override
    public String getMainEntityName() {
        return getName();
    }

    @Override
    public <G extends SystemProperty> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        SystemProperty cloned = createResponse.getClone();
        cloned.setFriendlyGroup(friendlyGroup);
        cloned.setFriendlyName(friendlyName);
        cloned.setFriendlyTab(friendlyTab);
        cloned.setName(name);
        cloned.setValue(value);
        cloned.setPropertyType(getPropertyType());
        return createResponse;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }
        if (obj == null || !getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }

        SystemPropertyImpl rhs = (SystemPropertyImpl) obj;
        return new EqualsBuilder()
                .append(this.id, rhs.id)
                .append(this.name, rhs.name)
                .append(this.overrideGeneratedPropertyName, rhs.overrideGeneratedPropertyName)
                .append(this.value, rhs.value)
                .append(this.propertyType, rhs.propertyType)
                .append(this.friendlyName, rhs.friendlyName)
                .append(this.friendlyGroup, rhs.friendlyGroup)
                .append(this.friendlyTab, rhs.friendlyTab)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(name)
                .append(overrideGeneratedPropertyName)
                .append(value)
                .append(propertyType)
                .append(friendlyName)
                .append(friendlyGroup)
                .append(friendlyTab)
                .toHashCode();
    }

}
