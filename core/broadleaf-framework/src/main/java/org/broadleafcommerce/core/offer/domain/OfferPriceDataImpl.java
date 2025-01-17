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
package org.broadleafcommerce.core.offer.domain;

import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.persistence.ArchiveStatus;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;
import org.broadleafcommerce.common.presentation.ConfigurationItem;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.ValidationConfiguration;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.util.DateUtil;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferPriceDataIdentifierType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "BLC_OFFER_PRICE_DATA")
@Inheritance(strategy=InheritanceType.JOINED)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blOrderElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.FALSE, friendlyName = "OfferPriceDataImpl_baseOfferPriceData")
@SQLDelete(sql="UPDATE BLC_OFFER_PRICE_DATA SET ARCHIVED = 'Y' WHERE OFFER_PRICE_DATA_ID = ?")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps = true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG)
})
public class OfferPriceDataImpl implements OfferPriceData {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "OfferPriceDataId")
    @GenericGenerator(
        name="OfferPriceDataId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="OfferPriceDataImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.offer.domain.OfferPriceDataImpl")
        }
    )
    @Column(name = "OFFER_PRICE_DATA_ID")
    @AdminPresentation(friendlyName = "OfferPriceDataImpl_Offer_Price_Data_Id", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @ManyToOne(targetEntity = OfferImpl.class, optional=false, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "OFFER_ID")
    @Index(name="OFFER_PRICE_DATA_OFFER_INDEX", columnNames={"OFFER_ID"})
    @AdminPresentation(friendlyName = "OfferPriceDataImpl_Offer", order = 1000)
    @AdminPresentationToOneLookup()
    protected Offer offer;

    @Column(name = "START_DATE")
    @AdminPresentation(friendlyName = "OfferPriceDataImpl_Active_Start_Date", order = 2000,
            group = "OfferImpl_Activity_Range",
            defaultValue = "today")
    protected Date activeStartDate;

    @Column(name = "END_DATE")
    @AdminPresentation(friendlyName = "OfferPriceDataImpl_Active_End_Date", order = 3000,
            group = "OfferImpl_Activity_Range",
            validationConfigurations = {
                    @ValidationConfiguration(
                            validationImplementation = "blAfterStartDateValidator",
                            configurationItems = {
                                    @ConfigurationItem(itemName = "otherField", itemValue = "activeStartDate")
                            })
            })
    protected Date activeEndDate;

    @Column(name = "IDENTIFIER_TYPE")
    @AdminPresentation(
            friendlyName = "OfferPriceDataImpl_Identifier_Type",
            fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration = "org.broadleafcommerce.core.offer.service.type.OfferPriceDataIdentifierType",
            requiredOverride = RequiredOverride.REQUIRED,
            order = 4000,
            prominent = true, gridOrder = 1000)
    protected String identifierType;

    @Column(name = "IDENTIFIER_VALUE")
    @AdminPresentation(
            friendlyName = "OfferPriceDataImpl_Identifier_Value",
            requiredOverride = RequiredOverride.REQUIRED,
            order = 5000,
            prominent = true, gridOrder = 2000)
    protected String identifierValue;

    @Column(name = "DISCOUNT_TYPE")
    @AdminPresentation(
            friendlyName = "OfferPriceDataImpl_Discount_Type",
            fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration = "org.broadleafcommerce.core.offer.service.type.OfferDiscountType",
            requiredOverride = RequiredOverride.REQUIRED,
            order = 6000,
            prominent = true, gridOrder = 3000)
    protected String discountType;

    @Column(name = "AMOUNT", nullable=false, precision=19, scale=5)
    @AdminPresentation(friendlyName = "OfferPriceDataImpl_Amount",
            requiredOverride = RequiredOverride.REQUIRED,
            order = 7000,
            defaultValue = "0.00",
            prominent = true, gridOrder = 4000)
    protected BigDecimal amount;

    @Column(name = "QUANTITY", nullable=false)
    @AdminPresentation(friendlyName = "OfferPriceDataImpl_Quantity",
            requiredOverride = RequiredOverride.REQUIRED,
            order = 8000,
            defaultValue = "1",
            prominent = true, gridOrder = 5000)
    protected Integer quantity;

    @Embedded
    protected ArchiveStatus archiveStatus = new ArchiveStatus();

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
    public Date getActiveStartDate() {
        return activeStartDate;
    }

    @Override
    public void setActiveStartDate(Date activeStartDate) {
        this.activeStartDate = activeStartDate;
    }

    @Override
    public Date getActiveEndDate() {
        return activeEndDate;
    }

    @Override
    public void setActiveEndDate(Date activeEndDate) {
        this.activeEndDate = activeEndDate;
    }

    @Override
    public OfferPriceDataIdentifierType getIdentifierType() {
        if (identifierType == null) {
            return null;
        }
        return OfferPriceDataIdentifierType.getInstance(identifierType);
    }

    @Override
    public void setIdentifierType(OfferPriceDataIdentifierType identifierType) {
        this.identifierType = identifierType.getType();
    }

    @Override
    public String getIdentifierValue() {
        return identifierValue;
    }

    @Override
    public void setIdentifierValue(String identifierValue) {
        this.identifierValue = identifierValue;
    }

    @Override
    public OfferDiscountType getDiscountType() {
        if (discountType == null) {
            return null;
        }
        return OfferDiscountType.getInstance(discountType);
    }

    @Override
    public void setDiscountType(OfferDiscountType discountType) {
        this.discountType = discountType.getType();
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public Character getArchived() {
        ArchiveStatus temp;
        if (archiveStatus == null) {
            temp = new ArchiveStatus();
        } else {
            temp = archiveStatus;
        }
        return temp.getArchived();
    }

    @Override
    public void setArchived(Character archived) {
        if (archiveStatus == null) {
            archiveStatus = new ArchiveStatus();
        }
        archiveStatus.setArchived(archived);
    }

    @Override
    public boolean isActive() {
        boolean datesActive;
        // If the start date for this offer code has not been set, just delegate to the offer to determine if the code is
        // active rather than requiring the user to set offer code dates as well
        if (activeStartDate == null) {
            datesActive = DateUtil.isActive(getOffer().getStartDate(), getOffer().getEndDate(), true);
        } else {
            datesActive = DateUtil.isActive(activeStartDate, activeEndDate, true);
        }
        return datesActive && 'Y' != getArchived();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OfferPriceDataImpl)) return false;

        OfferPriceDataImpl that = (OfferPriceDataImpl) o;

        if (!getId().equals(that.getId())) return false;
        if (!getOffer().equals(that.getOffer())) return false;
        if (!getActiveStartDate().equals(that.getActiveStartDate())) return false;
        if (getActiveEndDate() != null ? !getActiveEndDate().equals(that.getActiveEndDate()) : that.getActiveEndDate() != null)
            return false;
        if (!getIdentifierType().equals(that.getIdentifierType())) return false;
        if (!getIdentifierValue().equals(that.getIdentifierValue())) return false;
        if (!getDiscountType().equals(that.getDiscountType())) return false;
        if (!getAmount().equals(that.getAmount())) return false;
        if (!getQuantity().equals(that.getQuantity())) return false;
        return archiveStatus != null ? archiveStatus.equals(that.archiveStatus) : that.archiveStatus == null;
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getOffer().hashCode();
        result = 31 * result + getActiveStartDate().hashCode();
        result = 31 * result + (getActiveEndDate() != null ? getActiveEndDate().hashCode() : 0);
        result = 31 * result + getIdentifierType().hashCode();
        result = 31 * result + getIdentifierValue().hashCode();
        result = 31 * result + getDiscountType().hashCode();
        result = 31 * result + getAmount().hashCode();
        result = 31 * result + getQuantity().hashCode();
        result = 31 * result + (archiveStatus != null ? archiveStatus.hashCode() : 0);
        return result;
    }

    @Override
    public <G extends OfferPriceData> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        OfferPriceData cloned = createResponse.getClone();
        if (offer != null) {
            cloned.setOffer(offer.createOrRetrieveCopyInstance(context).getClone());
        }
        cloned.setActiveStartDate(activeStartDate);
        cloned.setActiveEndDate(activeEndDate);
        cloned.setArchived(getArchived());
        cloned.setDiscountType(getDiscountType());
        cloned.setAmount(amount);
        cloned.setIdentifierType(getIdentifierType());
        cloned.setIdentifierValue(identifierValue);
        cloned.setQuantity(quantity);
        return createResponse;
    }
}
