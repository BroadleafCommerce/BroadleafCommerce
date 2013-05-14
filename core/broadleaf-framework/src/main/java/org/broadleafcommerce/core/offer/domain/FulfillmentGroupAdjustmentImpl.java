/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.offer.domain;

import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.currency.util.CurrencyCodeIdentifiable;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "BLC_FG_ADJUSTMENT")
@Inheritance(strategy=InheritanceType.JOINED)
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
@AdminPresentationMergeOverrides(
    {
        @AdminPresentationMergeOverride(name = "", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.READONLY,
                                            booleanOverrideValue = true))
    }
)
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "FulfillmentGroupAdjustmentImpl_baseFulfillmentGroupAdjustment")
public class FulfillmentGroupAdjustmentImpl implements FulfillmentGroupAdjustment, CurrencyCodeIdentifiable {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "FGAdjustmentId")
    @GenericGenerator(
        name="FGAdjustmentId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="table_name", value="SEQUENCE_GENERATOR"),
            @Parameter(name="segment_column_name", value="ID_NAME"),
            @Parameter(name="value_column_name", value="ID_VAL"),
            @Parameter(name="segment_value", value="FulfillmentGroupAdjustmentImpl"),
            @Parameter(name="increment_size", value="50"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.offer.domain.FulfillmentGroupAdjustmentImpl")
        }
    )
    @Column(name = "FG_ADJUSTMENT_ID")
    protected Long id;

    @ManyToOne(targetEntity = FulfillmentGroupImpl.class)
    @JoinColumn(name = "FULFILLMENT_GROUP_ID")
    @Index(name="FGADJUSTMENT_INDEX", columnNames={"FULFILLMENT_GROUP_ID"})
    @AdminPresentation(excluded = true)
    protected FulfillmentGroup fulfillmentGroup;

    @ManyToOne(targetEntity = OfferImpl.class, optional=false)
    @JoinColumn(name = "OFFER_ID")
    @Index(name="FGADJUSTMENT_OFFER_INDEX", columnNames={"OFFER_ID"})
    @AdminPresentation(friendlyName = "FulfillmentGroupAdjustmentImpl_Offer", order=1000,
            prominent = true, gridOrder = 1000)
    @AdminPresentationToOneLookup()
    protected Offer offer;

    @Column(name = "ADJUSTMENT_REASON", nullable=false)
    @AdminPresentation(friendlyName = "FulfillmentGroupAdjustmentImpl_FG_Adjustment_Reason", order=2000)
    protected String reason;

    @Column(name = "ADJUSTMENT_VALUE", nullable=false, precision=19, scale=5)
    @AdminPresentation(friendlyName = "FulfillmentGroupAdjustmentImpl_FG_Adjustment_Value", order=3000,
            fieldType = SupportedFieldType.MONEY, prominent = true,
            gridOrder = 2000)
    protected BigDecimal value = Money.ZERO.getAmount();

    @Override
    public void init(FulfillmentGroup fulfillmentGroup, Offer offer, String reason){
        this.fulfillmentGroup = fulfillmentGroup;
        this.offer = offer;

        if (reason == null) {
            this.reason = offer.getName();
        } else {
            this.reason = reason;
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public FulfillmentGroup getFulfillmentGroup() {
        return fulfillmentGroup;
    }

    @Override
    public void setFulfillmentGroup(FulfillmentGroup fulfillmentGroup) {
        this.fulfillmentGroup = fulfillmentGroup;
    }

    @Override
    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    @Override
    public String getReason() {
        return reason;
    }

    @Override
    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public Money getValue() {
        return value == null ? null : BroadleafCurrencyUtils.getMoney(value, getFulfillmentGroup().getOrder().getCurrency());
    }
    
    @Override
    public void setValue(Money value) {
        this.value = value.getAmount();
    }

    @Override
    public String getCurrencyCode() {
        return ((CurrencyCodeIdentifiable) fulfillmentGroup).getCurrencyCode();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fulfillmentGroup == null) ? 0 : fulfillmentGroup.hashCode());
        result = prime * result + ((offer == null) ? 0 : offer.hashCode());
        result = prime * result + ((reason == null) ? 0 : reason.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FulfillmentGroupAdjustmentImpl other = (FulfillmentGroupAdjustmentImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (fulfillmentGroup == null) {
            if (other.fulfillmentGroup != null) {
                return false;
            }
        } else if (!fulfillmentGroup.equals(other.fulfillmentGroup)) {
            return false;
        }
        if (offer == null) {
            if (other.offer != null) {
                return false;
            }
        } else if (!offer.equals(other.offer)) {
            return false;
        }
        if (reason == null) {
            if (other.reason != null) {
                return false;
            }
        } else if (!reason.equals(other.reason)) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

}
