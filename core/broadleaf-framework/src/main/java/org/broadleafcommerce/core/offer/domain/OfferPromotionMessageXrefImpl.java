/*
 * #%L
 * broadleaf-enterprise
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt).
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 * #L%
 */
package org.broadleafcommerce.core.offer.domain;

import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.ConfigurationItem;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.ValidationConfiguration;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.broadleafcommerce.core.promotionMessage.domain.PromotionMessage;
import org.broadleafcommerce.core.promotionMessage.domain.PromotionMessageAdminPresentation;
import org.broadleafcommerce.core.promotionMessage.domain.PromotionMessageImpl;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
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
 * @author Chris Kittrell (ckittrell)
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_OFFER_PROMO_MSG_XREF")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blOffers")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE)
@AdminPresentationMergeOverrides({
    @AdminPresentationMergeOverride(name = "promotionMessage.endDate", mergeEntries =
        @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.VALIDATIONCONFIGURATIONS, validationConfigurations = {
            @ValidationConfiguration(
                validationImplementation = "blAfterStartDateValidator",
                configurationItems = {
                        @ConfigurationItem(itemName = "otherField", itemValue = "promotionMessage.startDate")
                })
        }))
    })
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG)
})
public class OfferPromotionMessageXrefImpl implements OfferPromotionMessageXref {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "OfferPromotionMessageXrefId")
    @GenericGenerator(
            name = "OfferPromotionMessageXrefId",
            strategy = "org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
            parameters = {
                    @Parameter(name = "segment_value", value = "OfferPromotionMessageXrefImpl"),
                    @Parameter(name = "entity_name", value = "org.broadleafcommerce.core.offer.domain.OfferPromotionMessageXrefImpl")
            })
    @Column(name = "OFFER_PROMO_MSG_XREF_ID")
    protected Long id;

    @ManyToOne(targetEntity = OfferImpl.class, optional=false, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "OFFER_ID")
    @AdminPresentation(excluded = true, visibility = VisibilityEnum.HIDDEN_ALL)
    protected Offer offer;

    @ManyToOne(targetEntity = PromotionMessageImpl.class,  optional=false, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "PROMOTION_MESSAGE_ID")
    protected PromotionMessage promotionMessage;

    @Column(name = "MESSAGE_TYPE", nullable=false)
    @Index(name="MESSAGE_TYPE_INDEX", columnNames={"MESSAGE_TYPE"})
    @AdminPresentation(friendlyName = "OfferPromotionMessageXrefImpl_MessageType",
            group = PromotionMessageAdminPresentation.GroupName.Placement,
            order = PromotionMessageAdminPresentation.FieldOrder.MessagePlacement - 500,
            fieldType= SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration="org.broadleafcommerce.core.promotionMessage.domain.type.PromotionMessageType",
            defaultValue = "TARGETS_OR_QUALIFIERS",
            requiredOverride = RequiredOverride.REQUIRED,
            prominent = true, gridOrder = PromotionMessageAdminPresentation.FieldOrder.Message + 1000)
    protected String messageType;

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
    public PromotionMessage getPromotionMessage() {
        return promotionMessage;
    }

    @Override
    public void setPromotionMessage(PromotionMessage promotionMessage) {
        this.promotionMessage = promotionMessage;
    }

    @Override
    public String getMessageType() {
        return messageType;
    }

    @Override
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
