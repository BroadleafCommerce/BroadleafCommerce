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
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.io.Serializable;

import javax.persistence.CascadeType;
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
@Table(name = "BLC_SEARCH_FIELD_TYPES")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG)
})
@AdminPresentationClass(friendlyName = "SearchFieldTypeImpl_friendly", populateToOneFields = PopulateToOneFieldsEnum.TRUE)
public class SearchFieldTypeImpl implements SearchFieldType, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SearchFieldTypeId")
    @GenericGenerator(
            name="SearchFieldTypeId",
            strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
            parameters = {
                    @Parameter(name="segment_value", value="SearchFieldTypeImpl"),
                    @Parameter(name="entity_name", value="org.broadleafcommerce.core.search.domain.SearchFieldTypeImpl")
            }
    )
    @Column(name = "SEARCH_FIELD_TYPE_ID")
    @AdminPresentation(friendlyName = "SearchFieldTypeImpl_ID", group = "SearchFieldTypeImpl_description",
            visibility= VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @ManyToOne(optional=false, targetEntity = SearchFieldImpl.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "SEARCH_FIELD_ID")
    @AdminPresentation(friendlyName = "SearchFieldTypeImpl_searchField", group = "SearchFieldTypeImpl_description",
            prominent = true, order=3, gridOrder=3, visibility=VisibilityEnum.FORM_HIDDEN)
    @AdminPresentationToOneLookup(lookupDisplayProperty = "field.friendlyName")
    protected SearchField searchField;

    @Column(name = "SEARCHABLE_FIELD_TYPE")
    @AdminPresentation(friendlyName = "SearchFieldTypeImpl_searchableFieldType", group = "SearchFieldTypeImpl_description", order = 4, prominent = true, gridOrder = 4,
            fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration = "org.broadleafcommerce.core.search.domain.solr.FieldType",
            requiredOverride = RequiredOverride.REQUIRED,
            defaultValue = "t")
    protected String searchableFieldType;

    @Override
    public FieldType getSearchableFieldType() {
        return FieldType.getInstance(searchableFieldType);
    }

    @Override
    public void setSearchableFieldType(FieldType searchableFieldType) {
        this.searchableFieldType = searchableFieldType.getType();
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
    public SearchField getSearchField() {
        return searchField;
    }

    @Override
    public void setSearchField(SearchField searchField) {
        this.searchField = searchField;
    }

    @Override
    public <G extends SearchFieldType> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        SearchFieldType searchFieldType = createResponse.getClone();
        if (searchField != null) {
            searchFieldType.setSearchField(searchField.createOrRetrieveCopyInstance(context).getClone());
        }

        if (searchableFieldType != null) {
            searchFieldType.setSearchableFieldType(this.getSearchableFieldType());
        }
        return createResponse;
    }
}
