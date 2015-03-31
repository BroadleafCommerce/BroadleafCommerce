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
package org.broadleafcommerce.core.web.checkout.model;

import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.CustomerPayment;
import org.broadleafcommerce.profile.core.domain.PhoneImpl;

import java.io.Serializable;

/**
 * <p>A form to model adding the Billing Address to the Order</p>
 *
 * @author Elbert Bautista (elbertbautista)
 * @author Brian Polster (bpolster)
 */
public class BillingInfoForm implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Address address = new AddressImpl();
    protected boolean useShippingAddress;
    protected Long customerPaymentId;
    protected CustomerPayment customerPayment;
    protected boolean saveNewPayment = false;
    protected boolean useCustomerPayment = false;
    protected String paymentName;

    public BillingInfoForm() {
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

    public boolean isUseShippingAddress() {
        return useShippingAddress;
    }

    public void setUseShippingAddress(boolean useShippingAddress) {
        this.useShippingAddress = useShippingAddress;
    }

    public Long getCustomerPaymentId() {
        return customerPaymentId;
    }

    public void setCustomerPaymentId(Long customerPaymentId) {
        this.customerPaymentId = customerPaymentId;
    }

    public CustomerPayment getCustomerPayment() {
        return customerPayment;
    }

    public void setCustomerPayment(CustomerPayment customerPayment) {
        this.customerPayment = customerPayment;
    }

    public boolean getUseCustomerPayment() {
        return useCustomerPayment;
    }

    public void setUseCustomerPayment(boolean useCustomerPayment) {
        this.useCustomerPayment = useCustomerPayment;
    }

    public boolean isSaveNewPayment() {
        return saveNewPayment;
    }

    public void setSaveNewPayment(boolean saveNewPayment) {
        this.saveNewPayment = saveNewPayment;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }
}
