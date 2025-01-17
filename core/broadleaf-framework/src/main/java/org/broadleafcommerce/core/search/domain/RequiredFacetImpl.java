/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
import org.broadleafcommerce.common.persistence.IdOverrideTableGenerator;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.io.Serial;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * @author Jeff Fischer
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_SEARCH_FACET_XREF")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blSearchElements")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps = true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG, skipOverlaps = true)
})
public class RequiredFacetImpl implements RequiredFacet {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "RequiredFacetId")
    @GenericGenerator(
            name = "RequiredFacetId",
            type = IdOverrideTableGenerator.class,
            parameters = {
                    @Parameter(name = "segment_value", value = "RequiredFacetImpl"),
                    @Parameter(name = "entity_name",
                            value = "org.broadleafcommerce.core.search.domain.RequiredFacetImpl")
            }
    )
    @Column(name = "ID")
    protected Long id;

    @ManyToOne(targetEntity = SearchFacetImpl.class, optional = false, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "SEARCH_FACET_ID")
    protected SearchFacet searchFacet;

    @ManyToOne(targetEntity = SearchFacetImpl.class, optional = false)
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
    public <G extends RequiredFacet> CreateResponse<G> createOrRetrieveCopyInstance(
            MultiTenantCopyContext context
    ) throws CloneNotSupportedException {
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
        return createResponse;
    }

}
