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

package org.broadleafcommerce.core.rest.api.v2.wrapper;

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
