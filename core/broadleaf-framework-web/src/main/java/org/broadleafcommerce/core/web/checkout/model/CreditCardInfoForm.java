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

import java.io.Serializable;

/**
 * <p>Typically, clients will utilize 3rd party payment integrations as the final
 * checkout step. See documentation specific to the integration(s) you are using
 * (e.g. PayPal, Braintree, Cybersource). With some of these integrations, Credit Card
 * information is passed directly to the gateway, bypassing the Broadleaf Servers to ease
 * on PCI compliance. In that case, this form will NOT be used.</p>
 *
 * <p>This form can also be used for simple payment methods where only a paymentMethod and
 * amount is required.</p>
 *
 * @author Elbert Bautista (elbertbautista)
 */
public class CreditCardInfoForm implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String paymentMethod;
    protected String creditCardName;
    protected String creditCardNumber;
    protected String creditCardCvvCode;
    protected String creditCardExpMonth;
    protected String creditCardExpYear;
    protected String selectedCreditCardType;

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCreditCardName() {
        return creditCardName;
    }

    public void setCreditCardName(String creditCardName) {
        this.creditCardName = creditCardName;
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
}
