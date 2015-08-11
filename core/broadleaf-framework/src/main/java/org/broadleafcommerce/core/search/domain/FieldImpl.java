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
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_FIELD")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE)
})
public class FieldImpl implements Field, Serializable, AdminMainEntity {
    
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

    @Deprecated
    @Column(name = "ABBREVIATION")
    @AdminPresentation(friendlyName = "FieldImpl_abbreviation", group = "FieldImpl_general", order = 3, excluded = true)
    protected String abbreviation;

    @Deprecated
    @Column(name = "SEARCHABLE")
    @AdminPresentation(friendlyName = "FieldImpl_searchable", group = "FieldImpl_general", order = 4, excluded = true)
    protected Boolean searchable = false;
    
    // This is a broadleaf enumeration
    @Deprecated
    @Column(name = "FACET_FIELD_TYPE")
    @AdminPresentation(friendlyName = "FieldImpl_facetFieldType", group = "FieldImpl_general", excluded = true)
    protected String facetFieldType;

    // This is a broadleaf enumeration
    @Deprecated
    @ElementCollection
    @CollectionTable(name="BLC_FIELD_SEARCH_TYPES", joinColumns=@JoinColumn(name="FIELD_ID"))
    @Column(name="SEARCHABLE_FIELD_TYPE")
    @Cascade(value={org.hibernate.annotations.CascadeType.MERGE, org.hibernate.annotations.CascadeType.PERSIST})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected List<String> searchableFieldTypes = new ArrayList<String>();
    
    @Column(name = "TRANSLATABLE")
    @AdminPresentation(friendlyName = "FieldImpl_translatable", group = "FieldImpl_general", excluded = true)
    protected Boolean translatable = false;

    @Column(name = "IS_CUSTOM")
    @AdminPresentation(friendlyName = "FieldImpl_isCustom", group = "FieldImpl_general",
            visibility = VisibilityEnum.VISIBLE_ALL)
    protected Boolean isCustom = false;

    @Column(name = "FIELD_TYPE")
    @AdminPresentation(friendlyName = "FieldImpl_Field_Type", order = 4, prominent = true, gridOrder = 4,
            fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration = "org.broadleafcommerce.core.search.service.type.SearchFieldType",
            requiredOverride = RequiredOverride.REQUIRED)
    protected String fieldType;
    
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

    @Deprecated
    @Override
    public String getAbbreviation() {
        return abbreviation;
    }

    @Deprecated
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

    @Deprecated
    @Override
    public Boolean getSearchable() {
        return searchable;
    }

    @Deprecated
    @Override
    public void setSearchable(Boolean searchable) {
        this.searchable = searchable;
    }

    @Deprecated
    @Override
    public FieldType getFacetFieldType() {
        return FieldType.getInstance(facetFieldType);
    }

    @Deprecated
    @Override
    public void setFacetFieldType(FieldType facetFieldType) {
        this.facetFieldType = facetFieldType == null ? null : facetFieldType.getType();
    }

    @Deprecated
    @Override
    public List<FieldType> getSearchableFieldTypes() {
        List<FieldType> fieldTypes = new ArrayList<FieldType>();
        for (String fieldType : searchableFieldTypes) {
            fieldTypes.add(FieldType.getInstance(fieldType));
        }
        return fieldTypes;
    }

    @Deprecated
    @Override
    public void setSearchableFieldTypes(List<FieldType> searchableFieldTypes) {
        List<String> fieldTypes = new ArrayList<String>();
        for (FieldType fieldType : searchableFieldTypes) {
            fieldTypes.add(fieldType.getType());
        }
        this.searchableFieldTypes = fieldTypes;
    }
    
    @Override
    public Boolean getTranslatable() {
        return translatable == null ? false : translatable;
    }

    @Override
    public void setTranslatable(Boolean translatable) {
        this.translatable = translatable;
    }

    @Override
    public Boolean getIsCustom() {
        return translatable == null ? false : translatable;
    }

    @Override
    public void setIsCustom(Boolean isCustom) {
        this.isCustom = isCustom;
    }

    @Override
    public String getFieldType() {
        return fieldType;
    }

    @Override
    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
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
        cloned.setFacetFieldType(getFacetFieldType());
        cloned.setFriendlyName(friendlyName);
        cloned.setPropertyName(propertyName);
        cloned.setSearchable(searchable);
        cloned.setTranslatable(translatable);
        for (String entry : searchableFieldTypes) {
            ((FieldImpl) cloned).searchableFieldTypes.add(entry);
        }
        cloned.setEntityType(getEntityType());
        return createResponse;
    }
}
