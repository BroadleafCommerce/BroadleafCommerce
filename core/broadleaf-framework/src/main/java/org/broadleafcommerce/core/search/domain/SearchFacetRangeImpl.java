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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.ValidationConfiguration;
import org.broadleafcommerce.common.presentation.ConfigurationItem;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverrides;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import java.io.Serializable;
import java.math.BigDecimal;

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

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_SEARCH_FACET_RANGE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE)
@AdminPresentationOverrides({
        @AdminPresentationOverride(name = "priceList.friendlyName", value = @AdminPresentation(excluded = false, friendlyName = "PriceListImpl_Friendly_Name", order = 1, gridOrder = 1, group = "SearchFacetRangeImpl_Description", prominent = true, visibility = VisibilityEnum.FORM_HIDDEN))
})
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.AUDITABLE_ONLY),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.ARCHIVE_ONLY),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG)
})
public class SearchFacetRangeImpl implements SearchFacetRange, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SearchFacetRangeId")
    @GenericGenerator(
            name = "SearchFacetRangeId",
            strategy = "org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
            parameters = {
                    @Parameter(name = "segment_value", value = "SearchFacetRangeImpl"),
                    @Parameter(name = "entity_name", value = "org.broadleafcommerce.core.search.domain.SearchFacetRangeImpl")
            })
    @Column(name = "SEARCH_FACET_RANGE_ID")
    protected Long id;

    @ManyToOne(targetEntity = SearchFacetImpl.class, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "SEARCH_FACET_ID")
    @Index(name = "SEARCH_FACET_INDEX", columnNames = { "SEARCH_FACET_ID" })
    @AdminPresentation(excluded = true, visibility = VisibilityEnum.HIDDEN_ALL)
    protected SearchFacet searchFacet = new SearchFacetImpl();

    @Column(name = "MIN_VALUE", precision = 19, scale = 5, nullable = false)
    @AdminPresentation(friendlyName = "SearchFacetRangeImpl_minValue", order = 3, gridOrder = 10, group = "SearchFacetRangeImpl_Description", prominent = true)
    protected BigDecimal minValue;

    @Column(name = "MAX_VALUE", precision = 19, scale = 5)
    @AdminPresentation(friendlyName = "SearchFacetRangeImpl_maxValue", order = 4, gridOrder = 11, group = "SearchFacetRangeImpl_Description", prominent = true, validationConfigurations = {
            @ValidationConfiguration(
                    validationImplementation = "blMaxGreaterThanMinValidator",
                    configurationItems = {
                            @ConfigurationItem(itemName = "otherField", itemValue = "minValue")
                    })
    })
    protected BigDecimal maxValue;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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
    public BigDecimal getMinValue() {
        return minValue;
    }

    @Override
    public void setMinValue(BigDecimal minValue) {
        this.minValue = minValue;
    }

    @Override
    public BigDecimal getMaxValue() {
        return maxValue;
    }

    @Override
    public void setMaxValue(BigDecimal maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(1, 31)
                .append(searchFacet)
                .append(minValue)
                .append(maxValue)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && getClass().isAssignableFrom(obj.getClass())) {
            SearchFacetRangeImpl other = (SearchFacetRangeImpl) obj;
            return new EqualsBuilder()
                    .append(searchFacet, other.searchFacet)
                    .append(minValue, other.minValue)
                    .append(maxValue, other.maxValue)
                    .build();
        }
        return false;
    }

    @Override
    public <G extends SearchFacetRange> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        SearchFacetRange cloned = createResponse.getClone();
        cloned.setMaxValue(maxValue);
        cloned.setMinValue(minValue);
        if (searchFacet != null) {
            cloned.setSearchFacet(searchFacet.createOrRetrieveCopyInstance(context).getClone());
        }
        return createResponse;
    }
}
