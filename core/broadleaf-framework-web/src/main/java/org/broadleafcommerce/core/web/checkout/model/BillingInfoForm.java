/*
 * Copyright 2008-2009 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.web.checkout.model;

import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;

/**
 * A form to model adding a payment to the order.
 *
 * @author Elbert Bautista (elbertbautista)
 */
public class BillingInfoForm {

    private Address address = new AddressImpl();
    private String creditCardNumber;
    private String creditCardCvvCode;
    private String creditCardExpMonth;
    private String creditCardExpYear;
    private String selectedCreditCardType;
    private boolean isSameAddress;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
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

    public String getSelectedCreditCardType() {
        return selectedCreditCardType;
    }

    public void setSelectedCreditCardType(String selectedCreditCardType) {
        this.selectedCreditCardType = selectedCreditCardType;
    }

    public boolean isSameAddress() {
        return isSameAddress;
    }

    public void setSameAddress(boolean sameAddress) {
        isSameAddress = sameAddress;
    }

}
