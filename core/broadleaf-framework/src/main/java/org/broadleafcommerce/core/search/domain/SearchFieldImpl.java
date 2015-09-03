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
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "BLC_SEARCH_FIELD")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG)
})
public class SearchFieldImpl implements SearchField, Serializable {

    private static final long serialVersionUID = 2915813511754425605L;

    @Id
    @GeneratedValue(generator = "SearchFieldId")
    @GenericGenerator(
            name="SearchFieldId",
            strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
            parameters = {
                    @Parameter(name="segment_value", value="SearchFieldImpl"),
                    @Parameter(name="entity_name", value="org.broadleafcommerce.core.search.domain.SearchFieldImpl")
            }
    )
    @Column(name = "SEARCH_FIELD_ID")
    @AdminPresentation(friendlyName = "SearchFieldImpl_ID", group = "SearchFieldImpl_description",
            visibility= VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @ManyToOne(optional=false, targetEntity = FieldImpl.class)
    @JoinColumn(name = "FIELD_ID")
    @AdminPresentation(friendlyName = "SearchFieldImpl_field", order = 1000, group = "SearchFieldImpl_description",
            prominent = true, gridOrder = 1000)
    @AdminPresentationToOneLookup(lookupDisplayProperty = "friendlyName")
    protected Field field;

    // This is a broadleaf enumeration
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "searchField", targetEntity = SearchFieldTypeImpl.class, cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    @AdminPresentationCollection(friendlyName = "SearchFieldImpl_searchableFieldTypes", order = 1000)
    protected List<SearchFieldType> searchableFieldTypes = new ArrayList<SearchFieldType>();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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
    public List<SearchFieldType> getSearchableFieldTypes() {
        return searchableFieldTypes;
    }

    @Override
    public void setSearchableFieldTypes(List<SearchFieldType> searchableFieldTypes) {
        this.searchableFieldTypes = searchableFieldTypes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && getClass().isAssignableFrom(obj.getClass())) {
            SearchFieldImpl other = (SearchFieldImpl) obj;
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
    public <G extends SearchField> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        SearchField cloned = createResponse.getClone();
        cloned.setField(field.createOrRetrieveCopyInstance(context).getClone());
        for(SearchFieldType entry : searchableFieldTypes){
            cloned.getSearchableFieldTypes().add(entry.createOrRetrieveCopyInstance(context).getClone());
        }

        return createResponse;
    }
}
