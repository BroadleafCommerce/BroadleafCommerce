/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.core.offer.domain.Offer;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a JAXB wrapper around OfferWrapper.
 * <p/>
 * User: Priyesh Patel
 */
@XmlRootElement(name = "offer")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class OfferWrapper extends BaseWrapper implements APIWrapper<Offer> {

    @XmlElement
    protected Long offerId;
    @XmlElement
    protected String startDate;
    @XmlElement
    protected String endDate;
    @XmlElement
    protected String marketingMessage;
    @XmlElement
    protected String description;
    @XmlElement
    protected String name;
    @XmlElement
    protected Boolean automatic;
    @XmlElement
    protected BroadleafEnumerationTypeWrapper offerType;

    @XmlElement
    protected BroadleafEnumerationTypeWrapper discountType;
    @XmlElement
    protected int maxUses;

    @Override
    public void wrapDetails(Offer model, HttpServletRequest request) {
        wrapSummary(model, request);
        this.startDate = model.getStartDate().toString();
        this.endDate = model.getStartDate().toString();
        this.description = model.getDescription();
        this.maxUses = model.getMaxUses();
    }

    @Override
    public void wrapSummary(Offer model, HttpServletRequest request) {
        this.automatic = model.isAutomaticallyAdded();
        this.offerType = (BroadleafEnumerationTypeWrapper) context.getBean(BroadleafEnumerationTypeWrapper.class.getName());
        this.offerType.wrapDetails(model.getType(), request);
        this.discountType = (BroadleafEnumerationTypeWrapper) context.getBean(BroadleafEnumerationTypeWrapper.class.getName());
        this.discountType.wrapDetails(model.getDiscountType(), request);
        this.offerId = model.getId();
        this.marketingMessage = model.getMarketingMessage();
        this.name = model.getName();
    }

    
    /**
     * @return the offerId
     */
    public Long getOfferId() {
        return offerId;
    }

    
    /**
     * @param offerId the offerId to set
     */
    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    
    /**
     * @return the startDate
     */
    public String getStartDate() {
        return startDate;
    }

    
    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    
    /**
     * @return the endDate
     */
    public String getEndDate() {
        return endDate;
    }

    
    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
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
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    
    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    
    /**
     * @return the automatic
     */
    public Boolean getAutomatic() {
        return automatic;
    }

    
    /**
     * @param automatic the automatic to set
     */
    public void setAutomatic(Boolean automatic) {
        this.automatic = automatic;
    }

    
    /**
     * @return the offerType
     */
    public BroadleafEnumerationTypeWrapper getOfferType() {
        return offerType;
    }

    
    /**
     * @param offerType the offerType to set
     */
    public void setOfferType(BroadleafEnumerationTypeWrapper offerType) {
        this.offerType = offerType;
    }

    
    /**
     * @return the discountType
     */
    public BroadleafEnumerationTypeWrapper getDiscountType() {
        return discountType;
    }

    
    /**
     * @param discountType the discountType to set
     */
    public void setDiscountType(BroadleafEnumerationTypeWrapper discountType) {
        this.discountType = discountType;
    }

    
    /**
     * @return the maxUses
     */
    public int getMaxUses() {
        return maxUses;
    }

    
    /**
     * @param maxUses the maxUses to set
     */
    public void setMaxUses(int maxUses) {
        this.maxUses = maxUses;
    }
}
