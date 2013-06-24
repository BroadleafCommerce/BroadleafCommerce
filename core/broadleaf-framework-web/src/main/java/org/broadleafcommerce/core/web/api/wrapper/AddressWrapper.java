/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.service.AddressService;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a JAXB wrapper around Address.
 *
 * User: Elbert Bautista
 * Date: 4/10/12
 */
@XmlRootElement(name = "address")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class AddressWrapper extends BaseWrapper implements APIWrapper<Address>, APIUnwrapper<Address> {

    @XmlElement
    protected Long id;

    @XmlElement
    protected String firstName;

    @XmlElement
    protected String lastName;

    @XmlElement
    protected String addressLine1;

    @XmlElement
    protected String addressLine2;

    @XmlElement
    protected String city;

    @XmlElement
    protected StateWrapper state;

    @XmlElement
    protected CountryWrapper country;

    @XmlElement
    protected String postalCode;

    @XmlElement
    protected PhoneWrapper phonePrimary;

    @XmlElement
    protected PhoneWrapper phoneSecondary;

    @XmlElement
    protected PhoneWrapper phoneFax;

    @XmlElement
    protected String companyName;

    @XmlElement
    protected Boolean isBusiness;

    @XmlElement
    protected Boolean isDefault;


    @Override
    public void wrapDetails(Address model, HttpServletRequest request) {
        this.id = model.getId();
        this.firstName = model.getFirstName();
        this.lastName = model.getLastName();
        this.addressLine1 = model.getAddressLine1();
        this.addressLine2 = model.getAddressLine2();
        this.city = model.getCity();

        StateWrapper stateWrapper = (StateWrapper) context.getBean(StateWrapper.class.getName());
        stateWrapper.wrapDetails(model.getState(), request);
        this.state = stateWrapper;

        CountryWrapper countryWrapper = (CountryWrapper) context.getBean(CountryWrapper.class.getName());
        countryWrapper.wrapDetails(model.getCountry(), request);
        this.country = countryWrapper;

        this.postalCode = model.getPostalCode();

        PhoneWrapper primaryWrapper = (PhoneWrapper) context.getBean(PhoneWrapper.class.getName());
        primaryWrapper.wrapDetails(model.getPhonePrimary(), request);
        this.phonePrimary = primaryWrapper;

        PhoneWrapper secondaryWrapper = (PhoneWrapper) context.getBean(PhoneWrapper.class.getName());
        secondaryWrapper.wrapDetails(model.getPhoneSecondary(), request);
        this.phoneSecondary = secondaryWrapper;

        PhoneWrapper faxWrapper = (PhoneWrapper) context.getBean(PhoneWrapper.class.getName());
        faxWrapper.wrapDetails(model.getPhoneFax(), request);
        this.phoneFax = faxWrapper;

        this.companyName = model.getCompanyName();
        this.isBusiness = model.isBusiness();
        this.isDefault = model.isDefault();

    }

    @Override
    public void wrapSummary(Address model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

    @Override
    public Address unwrap(HttpServletRequest request, ApplicationContext appContext) {
        AddressService addressService = (AddressService) appContext.getBean("blAddressService");
        Address address = addressService.create();

        address.setId(this.id);
        address.setFirstName(this.firstName);
        address.setLastName(this.lastName);
        address.setAddressLine1(this.addressLine1);
        address.setAddressLine2(this.addressLine2);
        address.setCity(this.city);

        if (this.state != null) {
            address.setState(this.state.unwrap(request, appContext));
        }

        if (this.country != null) {
            address.setCountry(this.country.unwrap(request, appContext));
        }

        address.setPostalCode(this.postalCode);

        if (this.phonePrimary != null) {
            address.setPhonePrimary(this.phonePrimary.unwrap(request, appContext));
        }

        if (this.phoneSecondary != null) {
            address.setPhoneSecondary(this.phoneSecondary.unwrap(request, appContext));
        }

        if (this.phoneFax != null) {
            address.setPhoneFax(this.phoneFax.unwrap(request, appContext));
        }

        address.setCompanyName(this.companyName);
        address.setBusiness(this.isBusiness);
        address.setDefault(this.isDefault);

        return address;
    }
}
