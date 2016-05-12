/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.TaxDetail;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @deprecated - use {@link com.broadleafcommerce.core.rest.api.v2.wrapper.TaxDetailWrapper}
 *
 */

@Deprecated
@XmlRootElement(name = "taxDetail")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class TaxDetailWrapper extends BaseWrapper implements APIWrapper<TaxDetail> {

    @XmlElement
    protected Long id;

    @XmlElement
    protected BroadleafEnumerationTypeWrapper taxType;

    @XmlElement
    protected Money amount;

    @XmlElement
    protected BigDecimal rate;

    @XmlElement
    protected String currency;

    @XmlElement
    protected String jurisdictionName;

    @XmlElement
    protected String taxName;

    @XmlElement
    protected String region;

    @XmlElement
    protected String country;

    @Override
    public void wrapDetails(TaxDetail model, HttpServletRequest request) {
        this.id = model.getId();
        if (model.getType() != null) {
            this.taxType = (BroadleafEnumerationTypeWrapper) context.getBean(BroadleafEnumerationTypeWrapper.class.getName());
            this.taxType.wrapDetails(model.getType(), request);
        }
        this.amount = model.getAmount();
        this.rate = model.getRate();
        if (model.getCurrency() != null) {
            this.currency = model.getCurrency().getCurrencyCode();
        }
        this.jurisdictionName = model.getJurisdictionName();
        this.taxName = model.getTaxName();
        this.region = model.getRegion();
        this.country = model.getCountry();
    }

    @Override
    public void wrapSummary(TaxDetail model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

    
    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    
    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    
    /**
     * @return the taxType
     */
    public BroadleafEnumerationTypeWrapper getTaxType() {
        return taxType;
    }

    
    /**
     * @param taxType the taxType to set
     */
    public void setTaxType(BroadleafEnumerationTypeWrapper taxType) {
        this.taxType = taxType;
    }

    
    /**
     * @return the amount
     */
    public Money getAmount() {
        return amount;
    }

    
    /**
     * @param amount the amount to set
     */
    public void setAmount(Money amount) {
        this.amount = amount;
    }

    
    /**
     * @return the rate
     */
    public BigDecimal getRate() {
        return rate;
    }

    
    /**
     * @param rate the rate to set
     */
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    
    /**
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    
    /**
     * @param currency the currency to set
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    
    /**
     * @return the jurisdictionName
     */
    public String getJurisdictionName() {
        return jurisdictionName;
    }

    
    /**
     * @param jurisdictionName the jurisdictionName to set
     */
    public void setJurisdictionName(String jurisdictionName) {
        this.jurisdictionName = jurisdictionName;
    }

    
    /**
     * @return the taxName
     */
    public String getTaxName() {
        return taxName;
    }

    
    /**
     * @param taxName the taxName to set
     */
    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }

    
    /**
     * @return the region
     */
    public String getRegion() {
        return region;
    }

    
    /**
     * @param region the region to set
     */
    public void setRegion(String region) {
        this.region = region;
    }

    
    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    
    /**
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

}
