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
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

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
 * @author Jeff Fischer
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_SEARCH_FACET_XREF")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG, skipOverlaps=true)
})
public class RequiredFacetImpl implements RequiredFacet {

    protected static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "RequiredFacetId")
    @GenericGenerator(
        name="RequiredFacetId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="RequiredFacetImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.search.domain.RequiredFacetImpl")
        }
    )
    @Column(name = "ID")
    protected Long id;

    @ManyToOne(targetEntity = SearchFacetImpl.class, optional = false, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "SEARCH_FACET_ID")
    protected SearchFacet searchFacet;

    @ManyToOne(targetEntity = SearchFacetImpl.class, optional=false)
    @JoinColumn(name = "REQUIRED_FACET_ID", referencedColumnName = "SEARCH_FACET_ID")
    protected SearchFacet requiredFacet;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public SearchFacet getRequiredFacet() {
        return requiredFacet;
    }

    @Override
    public void setRequiredFacet(SearchFacet requiredFacet) {
        this.requiredFacet = requiredFacet;
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
    public <G extends RequiredFacet> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        RequiredFacet cloned = createResponse.getClone();
        if (requiredFacet != null) {
            cloned.setRequiredFacet(requiredFacet);
        }
        if (searchFacet != null) {
            cloned.setSearchFacet(searchFacet);
        }
        return  createResponse;
    }
}
