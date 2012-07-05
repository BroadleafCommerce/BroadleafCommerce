/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.order.fulfillment.domain;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.core.order.service.type.FulfillmentBandResultAmountType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
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

/**
 * 
 * @author Phillip Verheyden
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_FULFILLMENT_BAND")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@AdminPresentationClass(friendlyName = "Banded Price Fulfillment Option")
public class FulfillmentPriceBandImpl implements FulfillmentPriceBand {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "FulfillmentBandId")
    @GenericGenerator(
        name="FulfillmentBandId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="FulfillmentPriceBandImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.order.fulfillment.domain.FulfillmentPriceBandImpl")
        }
    )
    @Column(name = "FULFILLMENT_BAND_ID")
    protected Long id;

    @Column(name="RETAIL_PRICE_MINIMUM_AMOUNT")
    protected BigDecimal retailPriceMinimumAmount;

    @Column(name="RESULT_AMOUNT")
    protected BigDecimal resultAmount;
    
    @Column(name="RESULT__AMOUNT_TYPE")
    @AdminPresentation(friendlyName="Result Type", fieldType=SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.core.order.service.type.FulfillmentBandResultAmountType")
    protected String resultAmountType = FulfillmentBandResultAmountType.RATE.getType();
    
    @ManyToOne(targetEntity=BandedPriceFulfillmentOptionImpl.class)
    @JoinColumn(name="FULFILLMENT_OPTION_ID")
    protected BandedPriceFulfillmentOption option;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public BigDecimal getRetailPriceMinimumAmount() {
        return retailPriceMinimumAmount;
    }

    @Override
    public void setRetailPriceMinimumAmount(BigDecimal retailPriceMinimumAmount) {
        this.retailPriceMinimumAmount = retailPriceMinimumAmount;
    }

    @Override
    public BigDecimal getResultAmount() {
        return resultAmount;
    }

    @Override
    public void setResultAmount(BigDecimal resultAmount) {
        this.resultAmount = resultAmount;
    }

    @Override
    public FulfillmentBandResultAmountType getResultAmountType() {
        return FulfillmentBandResultAmountType.getInstance(resultAmountType);
    }

    @Override
    public void setResultAmountType(FulfillmentBandResultAmountType resultAmountType) {
        this.resultAmountType = resultAmountType.getType();
    }

}