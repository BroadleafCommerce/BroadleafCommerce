/*
 * Copyright 2008-2009 the original author or authors.
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
package org.broadleafcommerce.pricing.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_SHIPPING_RATE")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class ShippingRateImpl implements ShippingRate, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "FEE_TYPE")
    private String feeType;

    @Column(name = "FEE_SUB_TYPE")
    private String feeSubType;

    @Column(name = "FEE_BAND")
    private Integer feeBand;

    @Column(name = "BAND_UNIT_QTY")
    private BigDecimal bandUnitQuantity;

    @Column(name = "BAND_RESULT_QTY")
    private BigDecimal bandResultQuantity;

    @Column(name = "BAND_RESULT_PCT")
    private Integer bandResultPercent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public String getFeeSubType() {
        return feeSubType;
    }

    public void setFeeSubType(String feeSubType) {
        this.feeSubType = feeSubType;
    }

    public Integer getFeeBand() {
        return feeBand;
    }

    public void setFeeBand(Integer feeBand) {
        this.feeBand = feeBand;
    }

    public BigDecimal getBandUnitQuantity() {
        return bandUnitQuantity;
    }

    public void setBandUnitQuantity(BigDecimal bandUnitQuantity) {
        this.bandUnitQuantity = bandUnitQuantity;
    }

    public BigDecimal getBandResultQuantity() {
        return bandResultQuantity;
    }

    public void setBandResultQuantity(BigDecimal bandResultQuantity) {
        this.bandResultQuantity = bandResultQuantity;
    }

    public Integer getBandResultPercent() {
        return bandResultPercent;
    }

    public void setBandResultPercent(Integer bandResultPercent) {
        this.bandResultPercent = bandResultPercent;
    }

    @Override
    public String toString() {
        return getFeeSubType() + " " + getBandResultQuantity() + " " + getBandResultPercent();
    }

}
