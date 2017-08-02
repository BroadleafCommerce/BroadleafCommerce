/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.checkout.model;

import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.PhoneImpl;

import java.io.Serializable;

/**
 * @author Chris Kittrell (ckittrell)
 * @author Jacob Mitash
 */
public class PaymentInfoForm implements Serializable {

    protected Address address = new AddressImpl();
    protected boolean shouldUseShippingAddress = false;
    protected Long customerPaymentId;
    protected boolean shouldSaveNewPayment = true;
    protected boolean shouldUseCustomerPayment = false;
    protected String emailAddress;

    protected String paymentName;
    protected boolean isDefault = false;
    protected String paymentToken;

    public PaymentInfoForm() {
        address.setPhonePrimary(new PhoneImpl());
        address.setPhoneSecondary(new PhoneImpl());
        address.setPhoneFax(new PhoneImpl());
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public boolean getShouldUseShippingAddress() {
        return shouldUseShippingAddress;
    }

    public void setShouldUseShippingAddress(boolean shouldUseShippingAddress) {
        this.shouldUseShippingAddress = shouldUseShippingAddress;
    }

    public Long getCustomerPaymentId() {
        return customerPaymentId;
    }

    public void setCustomerPaymentId(Long customerPaymentId) {
        this.customerPaymentId = customerPaymentId;
    }

    public boolean getShouldSaveNewPayment() {
        return shouldSaveNewPayment;
    }

    public void setShouldSaveNewPayment(boolean shouldSaveNewPayment) {
        this.shouldSaveNewPayment = shouldSaveNewPayment;
    }

    public boolean getShouldUseCustomerPayment() {
        return shouldUseCustomerPayment;
    }

    public void setShouldUseCustomerPayment(boolean shouldUseCustomerPayment) {
        this.shouldUseCustomerPayment = shouldUseCustomerPayment;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getPaymentToken() {
        return paymentToken;
    }

    public void setPaymentToken(String paymentToken) {
        this.paymentToken = paymentToken;
    }
}
