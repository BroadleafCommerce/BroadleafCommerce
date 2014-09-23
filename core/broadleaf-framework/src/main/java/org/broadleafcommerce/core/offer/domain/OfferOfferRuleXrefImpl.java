/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.core.offer.domain;

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
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Polymorphism;
import org.hibernate.annotations.PolymorphismType;

import javax.annotation.Nonnull;
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
@Polymorphism(type = PolymorphismType.EXPLICIT)
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
    @ManyToOne(targetEntity = OfferImpl.class, optional=false)
    @JoinColumn(name = "BLC_OFFER_OFFER_ID")
    @AdminPresentation(excluded = true)
    protected Offer offer;

    //for the collection join entity - don't pre-instantiate the reference (i.e. don't do myField = new MyFieldImpl())
    @ManyToOne(targetEntity = OfferRuleImpl.class, cascade = {CascadeType.ALL})
    @JoinColumn(name = "OFFER_RULE_ID")
    @ClonePolicy
    protected OfferRule offerRule;

    @Column(name = "MAP_KEY", nullable=false)
    @Index(name="SKUMEDIA_KEY_INDEX", columnNames={"MAP_KEY"})
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

    @Nonnull
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
}
