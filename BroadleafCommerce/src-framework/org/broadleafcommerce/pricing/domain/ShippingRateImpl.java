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

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_SHIPPING_RATE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ShippingRateImpl implements ShippingRate {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "ShippingRateId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "ShippingRateId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "ShippingRateImpl", allocationSize = 50)
    @Column(name = "ID")
    protected Long id;

    @Column(name = "FEE_TYPE", nullable=false)
    protected String feeType;

    @Column(name = "FEE_SUB_TYPE")
    protected String feeSubType;

    @Column(name = "FEE_BAND", nullable=false)
    protected Integer feeBand;

    @Column(name = "BAND_UNIT_QTY", nullable=false)
    protected BigDecimal bandUnitQuantity;

    @Column(name = "BAND_RESULT_QTY", nullable=false)
    protected BigDecimal bandResultQuantity;

    @Column(name = "BAND_RESULT_PCT", nullable=false)
    protected Integer bandResultPercent;

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bandResultPercent == null) ? 0 : bandResultPercent.hashCode());
        result = prime * result + ((bandResultQuantity == null) ? 0 : bandResultQuantity.hashCode());
        result = prime * result + ((bandUnitQuantity == null) ? 0 : bandUnitQuantity.hashCode());
        result = prime * result + ((feeBand == null) ? 0 : feeBand.hashCode());
        result = prime * result + ((feeSubType == null) ? 0 : feeSubType.hashCode());
        result = prime * result + ((feeType == null) ? 0 : feeType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ShippingRateImpl other = (ShippingRateImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (bandResultPercent == null) {
            if (other.bandResultPercent != null)
                return false;
        } else if (!bandResultPercent.equals(other.bandResultPercent))
            return false;
        if (bandResultQuantity == null) {
            if (other.bandResultQuantity != null)
                return false;
        } else if (!bandResultQuantity.equals(other.bandResultQuantity))
            return false;
        if (bandUnitQuantity == null) {
            if (other.bandUnitQuantity != null)
                return false;
        } else if (!bandUnitQuantity.equals(other.bandUnitQuantity))
            return false;
        if (feeBand == null) {
            if (other.feeBand != null)
                return false;
        } else if (!feeBand.equals(other.feeBand))
            return false;
        if (feeSubType == null) {
            if (other.feeSubType != null)
                return false;
        } else if (!feeSubType.equals(other.feeSubType))
            return false;
        if (feeType == null) {
            if (other.feeType != null)
                return false;
        } else if (!feeType.equals(other.feeType))
            return false;
        return true;
    }

}
