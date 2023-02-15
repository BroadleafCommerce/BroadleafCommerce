/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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

import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.clone.ClonePolicy;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.rule.SimpleRule;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.annotation.Nonnull;
import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_OFFER_RULE_MAP")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blOffers")
@AdminPresentationClass(excludeFromPolymorphism = false, populateToOneFields = PopulateToOneFieldsEnum.TRUE)
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG)
})
public class OfferOfferRuleXrefImpl implements OfferOfferRuleXref, SimpleRule {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    public OfferOfferRuleXrefImpl(Offer offer, OfferRule offerRule, String key) {
        this.offer = offer;
        this.offerRule = offerRule;
        this.key = key;
    }

    public OfferOfferRuleXrefImpl() {
        //support default constructor for Hibernate
    }

    @Id
    @GeneratedValue(generator= "OfferOfferRuleId")
    @GenericGenerator(
        name="OfferOfferRuleId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="OfferOfferRuleXrefImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.offer.domain.OfferOfferRuleXrefImpl")
        }
    )
    @Column(name = "OFFER_OFFER_RULE_ID")
    protected Long id;

    //for the collection join entity - don't pre-instantiate the reference (i.e. don't do myField = new MyFieldImpl())
    @ManyToOne(targetEntity = OfferImpl.class, optional=false, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "BLC_OFFER_OFFER_ID")
    @AdminPresentation(excluded = true)
    protected Offer offer;

    //for the collection join entity - don't pre-instantiate the reference (i.e. don't do myField = new MyFieldImpl())
    @ManyToOne(targetEntity = OfferRuleImpl.class, cascade = {CascadeType.ALL})
    @JoinColumn(name = "OFFER_RULE_ID")
    @ClonePolicy
    protected OfferRule offerRule;

    @Column(name = "MAP_KEY", nullable=false)
    @AdminPresentation(visibility = VisibilityEnum.HIDDEN_ALL)
    protected String key;

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
    public OfferRule getOfferRule() {
        return offerRule;
    }

    @Override
    public void setOfferRule(OfferRule offerRule) {
        this.offerRule = offerRule;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getMatchRule() {
        createEntityInstance();
        return offerRule.getMatchRule();
    }

    @Override
    public void setMatchRule(@Nonnull String matchRule) {
        createEntityInstance();
        offerRule.setMatchRule(matchRule);
    }

    protected void createEntityInstance() {
        if (offerRule == null) {
            offerRule = new OfferRuleImpl();
        }
    }

    @Override
    public <G extends OfferOfferRuleXref> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        OfferOfferRuleXref cloned = createResponse.getClone();
        cloned.setKey(key);
        if (offer != null) {
            cloned.setOffer(offer.createOrRetrieveCopyInstance(context).getClone());
        }
        if (offerRule != null) {
            cloned.setOfferRule(offerRule.createOrRetrieveCopyInstance(context).getClone());
        }
        return  createResponse;
    }
}
