/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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

import org.broadleafcommerce.profile.core.domain.CustomerAddress;
import org.broadleafcommerce.profile.core.service.CustomerAddressService;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a JAXB wrapper around CustomerAddress.
 *
 * @author Jeff Fischer
 */
@XmlRootElement(name = "customerAddress")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class CustomerAddressWrapper extends BaseWrapper implements APIWrapper<CustomerAddress>, APIUnwrapper<CustomerAddress> {

    @XmlElement
    protected Long id;

    @XmlElement
    protected String addressName;

    @XmlElement
    protected AddressWrapper address;

    @Override
    public void wrapDetails(CustomerAddress model, HttpServletRequest request) {
        this.id = model.getId();
        this.addressName = model.getAddressName();
        if (model.getAddress() != null) {
            this.address = (AddressWrapper) context.getBean(AddressWrapper.class.getName());
            this.address.wrapDetails(model.getAddress(), request);
        }
    }

    @Override
    public void wrapSummary(CustomerAddress model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

    @Override
    public CustomerAddress unwrap(HttpServletRequest request, ApplicationContext context) {
        CustomerAddressService customerAddressService = (CustomerAddressService) context.getBean("blCustomerAddressService");
        CustomerAddress customerAddress = customerAddressService.create();
        customerAddress.setId(this.id);
        if (this.address != null) {
            customerAddress.setAddress(address.unwrap(request, context));
        }
        customerAddress.setAddressName(this.addressName);
        return customerAddress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public AddressWrapper getAddress() {
        return address;
    }

    public void setAddress(AddressWrapper address) {
        this.address = address;
    }
}
