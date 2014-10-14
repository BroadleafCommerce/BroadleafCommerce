/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
import org.broadleafcommerce.common.rule.QuantityBasedRule;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Polymorphism;
import org.hibernate.annotations.PolymorphismType;
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

}
