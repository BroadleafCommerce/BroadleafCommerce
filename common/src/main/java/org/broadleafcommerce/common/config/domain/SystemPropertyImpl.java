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
package org.broadleafcommerce.common.config.domain;

import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.config.service.type.SystemPropertyFieldType;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * Allows the storage and retrieval of System Properties in the database
 * <p/>
 * User: Kelly Tisdell
 * Date: 6/20/12
 */
@Entity
@Table(name="BLC_SYSTEM_PROPERTY")
@Inheritance(strategy = InheritanceType.JOINED)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX)
})
public class SystemPropertyImpl implements SystemProperty, AdminMainEntity, SystemPropertyAdminPresentation {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SystemPropertyId")
    @GenericGenerator(
        name="SystemPropertyId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="SystemPropertyImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.common.config.domain.SystemPropertyImpl")
        }
    )
    @Column(name = "BLC_SYSTEM_PROPERTY_ID")
    protected Long id;

    @Column(name = "PROPERTY_NAME", nullable = false)
    @AdminPresentation(friendlyName = "SystemPropertyImpl_name",
            group = GroupName.General, order = FieldOrder.ATTRIBUTE_NAME,
            prominent = true, gridOrder = 1000,
            requiredOverride = RequiredOverride.REQUIRED)
    protected String name;

    @Column(name = "OVERRIDE_GENERATED_PROPERTY_NAME")
    @AdminPresentation(friendlyName = "FieldImpl_overrideGeneratedPropertyName",
            group = GroupName.General)
    protected Boolean overrideGeneratedPropertyName = false;

    @Column(name= "PROPERTY_VALUE")
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

    public SystemPropertyFieldType getPropertyType() {
        if (propertyType != null) {
            SystemPropertyFieldType returnType = SystemPropertyFieldType.getInstance(propertyType);
            if (returnType != null) {
                return returnType;
            }
        }
        return SystemPropertyFieldType.STRING_TYPE;
    }

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
}
