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

import org.broadleafcommerce.common.BroadleafEnumerationType;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a JAXB wrapper around HibuProduct.

 */
@XmlRootElement(name = "BroadleafEnumerationTypeWrapper")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class BroadleafEnumerationTypeWrapper extends BaseWrapper implements APIWrapper<BroadleafEnumerationType> {


    @XmlElement
    protected String friendlyName;

    @XmlElement
    protected String type;

    @Override
    public void wrapDetails(BroadleafEnumerationType model, HttpServletRequest request) {
        if (model == null) return;
        this.friendlyName = model.getFriendlyType();
        this.type = model.getType();
    }

    @Override
    public void wrapSummary(BroadleafEnumerationType model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

    
    /**
     * @return the friendlyName
     */
    public String getFriendlyName() {
        return friendlyName;
    }

    
    /**
     * @param friendlyName the friendlyName to set
     */
    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    
    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
}
