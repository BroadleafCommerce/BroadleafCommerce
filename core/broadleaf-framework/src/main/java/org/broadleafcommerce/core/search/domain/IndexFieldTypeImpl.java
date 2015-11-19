/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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

import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.io.Serializable;

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
 * @author Nick Crum (ncrum)
 */

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_INDEX_FIELD_TYPE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG)
})
@AdminPresentationMergeOverrides({
        @AdminPresentationMergeOverride(name = "indexField.field.friendlyName", mergeEntries = {
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.EXCLUDED, booleanOverrideValue = false),
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.PROMINENT, booleanOverrideValue = true),
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.GRIDORDER, intOverrideValue = 3),
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.VISIBILITY, overrideValue = "FORM_HIDDEN"),
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.FRIENDLYNAME, overrideValue = "IndexFieldTypeImpl_indexField")
        }),
        @AdminPresentationMergeOverride(name = "indexField.searchable", mergeEntries = {
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.EXCLUDED, booleanOverrideValue = false),
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.PROMINENT, booleanOverrideValue = true),
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.GRIDORDER, intOverrideValue = 3),
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.VISIBILITY, overrideValue = "FORM_HIDDEN"),
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.FRIENDLYNAME, overrideValue = "IndexFieldTypeImpl_searchable")
        })
})
@AdminPresentationClass(friendlyName = "IndexFieldTypeImpl_friendly", populateToOneFields = PopulateToOneFieldsEnum.TRUE)
public class IndexFieldTypeImpl implements IndexFieldType, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "IndexFieldTypeId")
    @GenericGenerator(
            name="IndexFieldTypeId",
            strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
            parameters = {
                    @Parameter(name="segment_value", value="IndexFieldTypeImpl"),
                    @Parameter(name="entity_name", value="org.broadleafcommerce.core.search.domain.IndexFieldTypeImpl")
            }
    )
    @Column(name = "INDEX_FIELD_TYPE_ID")
    @AdminPresentation(friendlyName = "IndexFieldTypeImpl_ID", group = "IndexFieldTypeTypeImpl_description",
            visibility= VisibilityEnum.HIDDEN_ALL)
    protected Long id;
    
    @Column(name = "FIELD_TYPE")
    @AdminPresentation(friendlyName = "IndexFieldTypeImpl_fieldType", group = "IndexFieldTypeImpl_description", order = 4, prominent = true, gridOrder = 4,
            fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration = "org.broadleafcommerce.core.search.domain.solr.FieldType",
            requiredOverride = RequiredOverride.REQUIRED,
            defaultValue = "t")
    protected String fieldType;

    @ManyToOne(optional=false, targetEntity = IndexFieldImpl.class)
    @JoinColumn(name = "INDEX_FIELD_ID")
    @AdminPresentation(friendlyName = "IndexFieldTypeImpl_indexField", group = "IndexFieldTypeImpl_description",
            order=3, gridOrder = 3, visibility=VisibilityEnum.FORM_HIDDEN)
    @AdminPresentationToOneLookup(lookupDisplayProperty = "field.friendlyName")
    protected IndexField indexField;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
    
    @Override
    public FieldType getFieldType() {
        return FieldType.getInstance(fieldType);
    }

    @Override
    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType.getType();
    }

    @Override
    public IndexField getIndexField() {
        return indexField;
    }

    @Override
    public void setIndexField(IndexField indexField) {
        this.indexField = indexField;
    }

    @Override
    public <G extends IndexFieldType> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        IndexFieldType indexFieldType = createResponse.getClone();
        if (indexField != null) {
            indexFieldType.setIndexField(indexField.createOrRetrieveCopyInstance(context).getClone());
        }

        if (fieldType != null) {
            indexFieldType.setFieldType(this.getFieldType());
        }
        return createResponse;
    }
}
