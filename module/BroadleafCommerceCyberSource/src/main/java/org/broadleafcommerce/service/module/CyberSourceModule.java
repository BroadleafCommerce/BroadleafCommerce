package org.broadleafcommerce.service.module;

import org.broadleafcommerce.payment.domain.PaymentInfo;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceBillingRequest;

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
