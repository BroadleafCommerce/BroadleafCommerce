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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author Chad Harchar (charchar)
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_INDEX_FIELD")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG)
})
public class IndexFieldImpl implements IndexField, Serializable {

    private static final long serialVersionUID = 2915813511754425605L;

    @Id
    @GeneratedValue(generator = "IndexFieldId")
    @GenericGenerator(
            name="IndexFieldId",
            strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
            parameters = {
                    @Parameter(name="segment_value", value="IndexFieldImpl"),
                    @Parameter(name="entity_name", value="org.broadleafcommerce.core.search.domain.IndexFieldImpl")
            }
    )
    @Column(name = "INDEX_FIELD_ID")
    @AdminPresentation(friendlyName = "IndexFieldImpl_ID", group = "IndexFieldImpl_description",
            visibility= VisibilityEnum.HIDDEN_ALL)
    protected Long id;
    
    @Column(name = "SEARCHABLE")
    @AdminPresentation(friendlyName = "IndexFieldImpl_searchable", defaultValue = "true", prominent = true, tooltip = "IndexFieldImpl_searchable_tooltip")
    protected Boolean searchable;

    @ManyToOne(optional=false, targetEntity = FieldImpl.class)
    @JoinColumn(name = "FIELD_ID")
    @AdminPresentation(friendlyName = "IndexFieldImpl_field", order = 1000, group = "IndexFieldImpl_description",
            prominent = true, gridOrder = 1000)
    @AdminPresentationToOneLookup(lookupDisplayProperty = "friendlyName")
    protected Field field;

    @OneToMany(mappedBy = "indexField", targetEntity = IndexFieldTypeImpl.class, cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    @AdminPresentationCollection(friendlyName = "IndexFieldImpl_fieldTypes", order = 1000)
    protected List<IndexFieldType> fieldTypes = new ArrayList<IndexFieldType>();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Boolean getSearchable() {
        return searchable;
    }

    @Override
    public void setSearchable(Boolean searchable) {
        this.searchable = searchable;
    }
    
    @Override
    public Field getField() {
        return field;
    }

    @Override
    public void setField(Field field) {
        this.field = field;
    }

    @Override
    public List<IndexFieldType> getFieldTypes() {
        return fieldTypes;
    }

    @Override
    public void setFieldTypes(List<IndexFieldType> fieldTypes) {
        this.fieldTypes = fieldTypes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && getClass().isAssignableFrom(obj.getClass())) {
            IndexFieldImpl other = (IndexFieldImpl) obj;
            return new EqualsBuilder()
                    .append(id, other.id)
                    .append(field, other.field)
                    .build();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(1, 31)
                .append(id)
                .append(field)
                .toHashCode();
    }

    @Override
    public <G extends IndexField> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        IndexField cloned = createResponse.getClone();
        cloned.setSearchable(searchable);
        cloned.setField(field);
        for(IndexFieldType entry : fieldTypes){
            cloned.getFieldTypes().add(entry.createOrRetrieveCopyInstance(context).getClone());
        }

        return createResponse;
    }
}
