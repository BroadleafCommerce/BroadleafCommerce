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

import org.broadleafcommerce.core.order.domain.FulfillmentOption;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "fulfillmentOption")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class FulfillmentOptionWrapper extends BaseWrapper implements APIWrapper<FulfillmentOption> {

    @XmlElement
    protected Long id;

    @XmlElement
    protected String name;

    @XmlElement
    protected String description;

    @XmlElement
    protected BroadleafEnumerationTypeWrapper fulfillmentType;

    @Override
    public void wrapDetails(FulfillmentOption model, HttpServletRequest request) {
        this.id = model.getId();
        if (model.getFulfillmentType() != null) {
            this.fulfillmentType = (BroadleafEnumerationTypeWrapper) context.getBean(BroadleafEnumerationTypeWrapper.class.getName());
            this.fulfillmentType.wrapDetails(model.getFulfillmentType(), request);
        }
        this.name = model.getName();
        this.description = model.getLongDescription();
    }

    @Override
    public void wrapSummary(FulfillmentOption model, HttpServletRequest request) {
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
     * @return the fulfillmentType
     */
    public BroadleafEnumerationTypeWrapper getFulfillmentType() {
        return fulfillmentType;
    }

    
    /**
     * @param fulfillmentType the fulfillmentType to set
     */
    public void setFulfillmentType(BroadleafEnumerationTypeWrapper fulfillmentType) {
        this.fulfillmentType = fulfillmentType;
    }

}
