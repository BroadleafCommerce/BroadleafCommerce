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
