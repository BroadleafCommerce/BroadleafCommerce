/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.service.module;

import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceBillingRequest;

/**
 * 
 * @author jfischer
 *
 */
public class CyberSourceModule {

    protected CyberSourceBillingRequest createBillingRequest(PaymentInfo info) {
        CyberSourceBillingRequest billingRequest = new CyberSourceBillingRequest();
        Address address = info.getAddress();
        billingRequest.setCity(address.getCity());
        billingRequest.setCountry(address.getCountry().getAbbreviation());
        billingRequest.setCounty(address.getCounty());
        billingRequest.setEmail(info.getOrder().getEmailAddress());
        billingRequest.setFirstName(address.getFirstName());
        billingRequest.setIpAddress(info.getCustomerIpAddress());
        billingRequest.setLastName(address.getLastName());
        billingRequest.setPhoneNumber(address.getPrimaryPhone());
        billingRequest.setPostalCode(address.getPostalCode());
        billingRequest.setState(address.getState().getAbbreviation());
        billingRequest.setStreet1(address.getAddressLine1());
        billingRequest.setStreet2(address.getAddressLine2());
        
        return billingRequest;
    }
    
}
