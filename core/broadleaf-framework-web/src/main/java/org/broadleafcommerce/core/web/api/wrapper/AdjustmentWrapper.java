/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License” located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License” located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.Adjustment;
import org.broadleafcommerce.core.offer.domain.Offer;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a JAXB wrapper around OrderAdjustmentWrapper.
 * <p/>
 * Author: ppatel, bpolster
 */
@XmlRootElement(name = "adjustment")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class AdjustmentWrapper extends BaseWrapper implements APIWrapper<Adjustment> {

    @XmlElement
    protected Long id;

    @XmlElement
    protected Long offerid;
    
    @XmlElement
    protected String reason;    

    @XmlElement
    protected String marketingMessage;

    @XmlElement
    protected Money adjustmentValue;

    @XmlElement
    protected String discountType;

    @XmlElement
    protected BigDecimal discountAmount;
    

    public void wrapDetails(Adjustment model, HttpServletRequest request) {
        if (model == null) {
            return;
        }
        this.id = model.getId();
        this.reason = model.getReason();

        Offer offer = model.getOffer();
        if (offer != null) {
            if (model.getReason() == null) {
                this.reason = "OFFER";
            }
            this.offerid = offer.getId();
            this.marketingMessage = offer.getMarketingMessage();
            this.discountType = offer.getDiscountType().getType();
            this.discountAmount = offer.getValue();
        }

        this.adjustmentValue = model.getValue();
    }

    @Override
    public void wrapSummary(Adjustment model, HttpServletRequest request) {
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
     * @return the offerid
     */
    public Long getOfferid() {
        return offerid;
    }

    
    /**
     * @param offerid the offerid to set
     */
    public void setOfferid(Long offerid) {
        this.offerid = offerid;
    }

    
    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    
    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    
    /**
     * @return the marketingMessage
     */
    public String getMarketingMessage() {
        return marketingMessage;
    }

    
    /**
     * @param marketingMessage the marketingMessage to set
     */
    public void setMarketingMessage(String marketingMessage) {
        this.marketingMessage = marketingMessage;
    }

    
    /**
     * @return the adjustmentValue
     */
    public Money getAdjustmentValue() {
        return adjustmentValue;
    }

    
    /**
     * @param adjustmentValue the adjustmentValue to set
     */
    public void setAdjustmentValue(Money adjustmentValue) {
        this.adjustmentValue = adjustmentValue;
    }

    
    /**
     * @return the discountType
     */
    public String getDiscountType() {
        return discountType;
    }

    
    /**
     * @param discountType the discountType to set
     */
    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    
    /**
     * @return the discountAmount
     */
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    
    /**
     * @param discountAmount the discountAmount to set
     */
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
}
