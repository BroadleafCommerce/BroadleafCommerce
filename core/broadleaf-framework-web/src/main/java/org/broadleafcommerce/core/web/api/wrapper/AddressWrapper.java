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
    protected String addressLine3;

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
        this.addressLine3 = model.getAddressLine3();
        this.city = model.getCity();
        this.postalCode = model.getPostalCode();
        this.companyName = model.getCompanyName();
        this.isBusiness = model.isBusiness();
        this.isDefault = model.isDefault();

        if (model.getState() != null) {
            StateWrapper stateWrapper = (StateWrapper) context.getBean(StateWrapper.class.getName());
            stateWrapper.wrapDetails(model.getState(), request);
            this.state = stateWrapper;
        }

        if (model.getCountry() != null) {
            CountryWrapper countryWrapper = (CountryWrapper) context.getBean(CountryWrapper.class.getName());
            countryWrapper.wrapDetails(model.getCountry(), request);
            this.country = countryWrapper;
        }

        if (model.getPhonePrimary() != null) {
            PhoneWrapper primaryWrapper = (PhoneWrapper) context.getBean(PhoneWrapper.class.getName());
            primaryWrapper.wrapDetails(model.getPhonePrimary(), request);
            this.phonePrimary = primaryWrapper;
        }

        if (model.getPhoneSecondary() != null) {
            PhoneWrapper secondaryWrapper = (PhoneWrapper) context.getBean(PhoneWrapper.class.getName());
            secondaryWrapper.wrapDetails(model.getPhoneSecondary(), request);
            this.phoneSecondary = secondaryWrapper;
        }

        if (model.getPhoneFax() != null) {
            PhoneWrapper faxWrapper = (PhoneWrapper) context.getBean(PhoneWrapper.class.getName());
            faxWrapper.wrapDetails(model.getPhoneFax(), request);
            this.phoneFax = faxWrapper;
        }
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
        address.setAddressLine3(this.addressLine3);
        address.setCity(this.city);
        address.setPostalCode(this.postalCode);
        address.setCompanyName(this.companyName);

        if (this.isBusiness != null) {
            address.setBusiness(this.isBusiness);
        }

        if (this.isDefault != null) {
            address.setDefault(this.isDefault);
        }

        if (this.state != null) {
            address.setState(this.state.unwrap(request, appContext));
        }

        if (this.country != null) {
            address.setCountry(this.country.unwrap(request, appContext));
        }

        if (this.phonePrimary != null) {
            address.setPhonePrimary(this.phonePrimary.unwrap(request, appContext));
        }

        if (this.phoneSecondary != null) {
            address.setPhoneSecondary(this.phoneSecondary.unwrap(request, appContext));
        }

        if (this.phoneFax != null) {
            address.setPhoneFax(this.phoneFax.unwrap(request, appContext));
        }

        return address;
    }
}
