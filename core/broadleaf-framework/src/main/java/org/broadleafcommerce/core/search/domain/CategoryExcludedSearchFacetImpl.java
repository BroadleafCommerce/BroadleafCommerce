/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.domain;

import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CAT_SEARCH_FACET_EXCL_XREF")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blCategories")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE)
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG)
})
public class CategoryExcludedSearchFacetImpl implements CategoryExcludedSearchFacet, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "CategoryExcludedSearchFacetId")
    @GenericGenerator(
        name="CategoryExcludedSearchFacetId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="CategoryExcludedSearchFacetImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.search.domain.CategoryExcludedSearchFacetImpl")
        }
    )
    @Column(name = "CAT_EXCL_SEARCH_FACET_ID")
    protected Long id;

    @ManyToOne(targetEntity = CategoryImpl.class, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "CATEGORY_ID")
    protected Category category;

    @ManyToOne(targetEntity = SearchFacetImpl.class)
    @JoinColumn(name = "SEARCH_FACET_ID")
    protected SearchFacet searchFacet;
    
    @Column(name = "SEQUENCE")
    @AdminPresentation(friendlyName = "CategorySearchFacetImpl_sequence")
    protected BigDecimal sequence;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Category getCategory() {
        return category;
    }

    @Override
    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public SearchFacet getSearchFacet() {
        return searchFacet;
    }

    @Override
    public void setSearchFacet(SearchFacet searchFacet) {
        this.searchFacet = searchFacet;
    }

    @Override
    public BigDecimal getSequence() {
        return sequence;
    }

    @Override
    public void setSequence(BigDecimal sequence) {
        this.sequence = sequence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!getClass().isAssignableFrom(o.getClass())) return false;

        CategoryExcludedSearchFacetImpl that = (CategoryExcludedSearchFacetImpl) o;

        if (sequence != null ? !sequence.equals(that.sequence) : that.sequence != null) return false;
        if (category != null ? !category.equals(that.category) : that.category != null) return false;
        if (searchFacet != null ? !searchFacet.equals(that.searchFacet) : that.searchFacet != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = category != null ? category.hashCode() : 0;
        result = 31 * result + (searchFacet != null ? searchFacet.hashCode() : 0);
        result = 31 * result + (sequence != null ? sequence.hashCode() : 0);
        return result;
    }

    @Override
    public <G extends CategoryExcludedSearchFacet> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        CategoryExcludedSearchFacet cloned = createResponse.getClone();
        if (searchFacet != null) {
            cloned.setSearchFacet(searchFacet.createOrRetrieveCopyInstance(context).getClone());
        }
        cloned.setSequence(sequence);
        if (category != null) {
            cloned.setCategory(category.createOrRetrieveCopyInstance(context).getClone());
        }
        return createResponse;
    }
}
