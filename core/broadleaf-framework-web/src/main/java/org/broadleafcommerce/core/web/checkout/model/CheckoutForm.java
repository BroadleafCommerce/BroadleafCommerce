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

import org.broadleafcommerce.common.i18n.domain.ISOCountryImpl;
import org.broadleafcommerce.common.payment.CreditCardType;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.CountryImpl;
import org.broadleafcommerce.profile.core.domain.PhoneImpl;
import org.broadleafcommerce.profile.core.domain.StateImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CheckoutForm implements Serializable {
    
    private static final long serialVersionUID = 8866879738364589339L;
    
    private String emailAddress;
    private Address shippingAddress = new AddressImpl();
    private Address billingAddress = new AddressImpl();
    private String creditCardNumber;
    private String creditCardCvvCode;
    private String creditCardExpMonth;
    private String creditCardExpYear;
    private String selectedCreditCardType;
    private boolean isSameAddress;

    public CheckoutForm() {
        shippingAddress = new AddressImpl();
        billingAddress = new AddressImpl();
        shippingAddress.setPhonePrimary(new PhoneImpl());
        billingAddress.setPhonePrimary(new PhoneImpl());
        shippingAddress.setPhoneSecondary(new PhoneImpl());
        billingAddress.setPhoneSecondary(new PhoneImpl());
        shippingAddress.setPhoneFax(new PhoneImpl());
        billingAddress.setPhoneFax(new PhoneImpl());
        shippingAddress.setIsoCountryAlpha2(new ISOCountryImpl());
        billingAddress.setIsoCountryAlpha2(new ISOCountryImpl());
        isSameAddress = true;

        /**
         * @deprecated - setCountry() and setState() on address have been deprecated in favor of ISO standardization.
         * Leaving here for legacy implementations.
         */
        shippingAddress.setCountry(new CountryImpl());
        billingAddress.setCountry(new CountryImpl());
        shippingAddress.setState(new StateImpl());
        billingAddress.setState(new StateImpl());
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getSelectedCreditCardType() {
        return selectedCreditCardType;
    }

    public void setSelectedCreditCardType(String selectedCreditCardType) {
        this.selectedCreditCardType = selectedCreditCardType;
    }

    public List<CreditCardType> getApprovedCreditCardTypes() {
        List<CreditCardType> approvedCCTypes = new ArrayList<CreditCardType>();
        approvedCCTypes.add(CreditCardType.VISA);
        approvedCCTypes.add(CreditCardType.MASTERCARD);
        approvedCCTypes.add(CreditCardType.AMEX);
        return approvedCCTypes;
    }

    public Address getShippingAddress() {
        return shippingAddress == null ? new AddressImpl() : shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Address getBillingAddress() {
        return billingAddress == null ? new AddressImpl() : billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
       this.billingAddress = billingAddress;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public String getCreditCardCvvCode() {
        return creditCardCvvCode;
    }

    public void setCreditCardCvvCode(String creditCardCvvCode) {
        this.creditCardCvvCode = creditCardCvvCode;
    }

    public String getCreditCardExpMonth() {
        return creditCardExpMonth;
    }

    public void setCreditCardExpMonth(String creditCardExpMonth) {
        this.creditCardExpMonth = creditCardExpMonth;
    }

    public String getCreditCardExpYear() {
        return creditCardExpYear;
    }

    public void setCreditCardExpYear(String creditCardExpYear) {
        this.creditCardExpYear = creditCardExpYear;
    }

    public boolean getIsSameAddress() {
        return isSameAddress;
    }

    public void setIsSameAddress(boolean isSameAddress) {
        this.isSameAddress = isSameAddress;
    }

}
