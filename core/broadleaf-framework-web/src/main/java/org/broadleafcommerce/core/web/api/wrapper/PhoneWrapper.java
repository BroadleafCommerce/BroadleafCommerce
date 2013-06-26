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
 * User: Elbert Bautista
 * Date: 4/24/12
 */
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
}
