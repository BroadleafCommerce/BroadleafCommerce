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
package org.broadleafcommerce.core.offer.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.clone.ClonePolicy;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.rule.QuantityBasedRule;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_QUAL_CRIT_OFFER_XREF")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blOffers")
@AdminPresentationClass(excludeFromPolymorphism = false, populateToOneFields = PopulateToOneFieldsEnum.TRUE)
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG)
})
public class OfferQualifyingCriteriaXrefImpl implements OfferQualifyingCriteriaXref, QuantityBasedRule {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    public OfferQualifyingCriteriaXrefImpl(Offer offer, OfferItemCriteria offerItemCriteria) {
        this.offer = offer;
        this.offerItemCriteria = offerItemCriteria;
    }

    public OfferQualifyingCriteriaXrefImpl() {
        //do nothing - default constructor for Hibernate contract
    }

    @Id
    @GeneratedValue(generator= "OfferQualCritId")
    @GenericGenerator(
            name="OfferQualCritId",
            strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
            parameters = {
                    @Parameter(name="segment_value", value="OfferQualifyingCriteriaXrefImpl"),
                    @Parameter(name="entity_name", value="org.broadleafcommerce.core.offer.domain.OfferQualifyingCriteriaXrefImpl")
            }
    )
    @Column(name = "OFFER_QUAL_CRIT_ID")
    protected Long id;

    //for the basic collection join entity - don't pre-instantiate the reference (i.e. don't do myField = new MyFieldImpl())
    @ManyToOne(targetEntity = OfferImpl.class, optional=false, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "OFFER_ID")
    @AdminPresentation(excluded = true)
    protected Offer offer;

    //for the basic collection join entity - don't pre-instantiate the reference (i.e. don't do myField = new MyFieldImpl())
    @ManyToOne(targetEntity = OfferItemCriteriaImpl.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "OFFER_ITEM_CRITERIA_ID")
    @ClonePolicy
    protected OfferItemCriteria offerItemCriteria;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Offer getOffer() {
        return offer;
    }

    @Override
    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    @Override
    public OfferItemCriteria getOfferItemCriteria() {
        return offerItemCriteria;
    }

    @Override
    public void setOfferItemCriteria(OfferItemCriteria offerItemCriteria) {
        this.offerItemCriteria = offerItemCriteria;
    }

    @Override
    public Integer getQuantity() {
        createEntityInstance();
        return offerItemCriteria.getQuantity();
    }

    @Override
    public void setQuantity(Integer quantity) {
        createEntityInstance();
        offerItemCriteria.setQuantity(quantity);
    }

    @Override
    public String getMatchRule() {
        createEntityInstance();
        return offerItemCriteria.getMatchRule();
    }

    @Override
    public void setMatchRule(String matchRule) {
        createEntityInstance();
        offerItemCriteria.setMatchRule(matchRule);
    }

    protected void createEntityInstance() {
        if (offerItemCriteria == null) {
            offerItemCriteria = new OfferItemCriteriaImpl();
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(offer)
                .append(offerItemCriteria)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && getClass().isAssignableFrom(o.getClass())) {
            OfferTargetCriteriaXrefImpl that = (OfferTargetCriteriaXrefImpl) o;
            return new EqualsBuilder()
                    .append(this.id, that.id)
                    .append(this.offer, that.offer)
                    .append(this.offerItemCriteria, that.offerItemCriteria)
                    .build();
        }

        return false;
    }

    @Override
    public <G extends OfferQualifyingCriteriaXref> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        OfferQualifyingCriteriaXref cloned = createResponse.getClone();
        if (offer != null) {
            cloned.setOffer(offer.createOrRetrieveCopyInstance(context).getClone());
        }
        if (offerItemCriteria != null) {
            cloned.setOfferItemCriteria(offerItemCriteria.createOrRetrieveCopyInstance(context).getClone());
        }
        return  createResponse;
    }
}
