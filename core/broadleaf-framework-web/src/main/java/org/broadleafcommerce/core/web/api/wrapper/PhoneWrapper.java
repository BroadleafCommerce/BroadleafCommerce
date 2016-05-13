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

import org.broadleafcommerce.profile.core.domain.Phone;
import org.broadleafcommerce.profile.core.service.PhoneService;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a JAXB wrapper around Phone.
 *
 * @deprecated - use {@link com.broadleafcommerce.core.rest.api.v2.wrapper.PhoneWrapper}
 * 
 * User: Elbert Bautista
 * Date: 4/24/12
 */
@Deprecated
@XmlRootElement(name = "phone")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class PhoneWrapper extends BaseWrapper implements APIWrapper<Phone>, APIUnwrapper<Phone> {

    @XmlElement
    protected Long id;

    @XmlElement
    protected String phoneNumber;

    @XmlElement
    protected Boolean isActive;

    @XmlElement
    protected Boolean isDefault;

    @Override
    public void wrapDetails(Phone model, HttpServletRequest request) {
        this.id = model.getId();
        this.phoneNumber = model.getPhoneNumber();
        this.isActive = model.isActive();
        this.isDefault = model.isDefault();
    }

    @Override
    public void wrapSummary(Phone model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

    @Override
    public Phone unwrap(HttpServletRequest request, ApplicationContext appContext) {
        PhoneService phoneService = (PhoneService) appContext.getBean("blPhoneService");
        Phone phone = phoneService.create();
        phone.setId(this.id);

        if (this.isActive != null) {
            phone.setActive(this.isActive);
        }

        if (this.isDefault != null) {
            phone.setDefault(this.isDefault);
        }

        phone.setPhoneNumber(this.phoneNumber);

        return phone;
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
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    
    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    
    /**
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    
    /**
     * @param isActive the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    
    /**
     * @return the isDefault
     */
    public Boolean getIsDefault() {
        return isDefault;
    }

    
    /**
     * @param isDefault the isDefault to set
     */
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
}
