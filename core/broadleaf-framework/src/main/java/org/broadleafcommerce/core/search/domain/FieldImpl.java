/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.search.domain;

import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.i18n.service.DynamicTranslationProvider;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_FIELD")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@DirectCopyTransform({
    @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE)
})
public class FieldImpl implements Field, AdminMainEntity {
    
    /**
     * 
     */
    private static final long serialVersionUID = 2915813511754425605L;

    @Id
    @GeneratedValue(generator = "FieldId")
    @GenericGenerator(
        name="FieldId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="FieldImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.search.domain.FieldImpl")
        }
    )
    @Column(name = "FIELD_ID")
    @AdminPresentation(friendlyName = "FieldImpl_ID", group = "FieldImpl_general",visibility=VisibilityEnum.HIDDEN_ALL)
    protected Long id;
    
    // This is a broadleaf enumeration
    @AdminPresentation(friendlyName = "FieldImpl_EntityType", group = "FieldImpl_general", order = 1, prominent = true,
            gridOrder = 1, fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration = "org.broadleafcommerce.core.search.domain.FieldEntity",
            requiredOverride = RequiredOverride.REQUIRED)
    @Column(name = "ENTITY_TYPE", nullable = false)
    @Index(name="ENTITY_TYPE_INDEX", columnNames={"ENTITY_TYPE"})
    protected String entityType;
    
    @Column(name = "FRIENDLY_NAME")
    @AdminPresentation(friendlyName = "FieldImpl_friendlyName", group = "FieldImpl_general", order = 2, gridOrder = 2,
            prominent = true, translatable = true)
    protected String friendlyName;

    @Column(name = "PROPERTY_NAME", nullable = false)
    @AdminPresentation(friendlyName = "FieldImpl_propertyName", group = "FieldImpl_general", order = 3, gridOrder = 3,
            prominent = true)
    protected String propertyName;

    @Column(name = "ABBREVIATION")
    @AdminPresentation(friendlyName = "FieldImpl_abbreviation", group = "FieldImpl_general", order = 3, excluded = true)
    protected String abbreviation;

    @Column(name = "TRANSLATABLE")
    @AdminPresentation(friendlyName = "FieldImpl_translatable", group = "FieldImpl_general", excluded = true)
    protected Boolean translatable = false;

    @Override
    public String getQualifiedFieldName() {
        return getEntityType().getFriendlyType() + "." + propertyName;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public FieldEntity getEntityType() {
        return FieldEntity.getInstance(entityType);
    }

    @Override
    public void setEntityType(FieldEntity entityType) {
        this.entityType = entityType.getType();
    }

    @Override
    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String getAbbreviation() {
        return abbreviation;
    }

    @Override
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @Override
    public String getFriendlyName() {
        return DynamicTranslationProvider.getValue(this, "friendlyName", friendlyName);
    }

    @Override
    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    @Override
    public Boolean getTranslatable() {
        return translatable == null ? false : translatable;
    }

    @Override
    public void setTranslatable(Boolean translatable) {
        this.translatable = translatable;
    }

    @Deprecated
    @Override
    public List<SearchConfig> getSearchConfigs() {
        throw new UnsupportedOperationException("The default Field implementation does not support search configs");
    }

    @Deprecated
    @Override
    public void setSearchConfigs(List<SearchConfig> searchConfigs) {
        throw new UnsupportedOperationException("The default Field implementation does not support search configs");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        Field other = (Field) obj;
        
        return getEntityType().getType().equals(other.getEntityType().getType()) && getPropertyName().equals(other.getPropertyName());
                
    }

    @Override
    public String getMainEntityName() {
        return getFriendlyName();
    }

    @Override
    public <G extends Field> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws
            CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        Field cloned = createResponse.getClone();
        cloned.setAbbreviation(abbreviation);
        cloned.setFriendlyName(friendlyName);
        cloned.setPropertyName(propertyName);
        cloned.setTranslatable(translatable);
        cloned.setEntityType(getEntityType());
        return createResponse;
    }
}
