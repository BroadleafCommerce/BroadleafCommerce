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

import org.broadleafcommerce.core.payment.domain.BankAccountPaymentInfo;
import org.broadleafcommerce.core.payment.domain.CreditCardPaymentInfo;
import org.broadleafcommerce.core.payment.domain.EmptyReferenced;
import org.broadleafcommerce.core.payment.domain.GiftCardPaymentInfo;
import org.broadleafcommerce.core.payment.domain.Referenced;
import org.broadleafcommerce.core.payment.service.SecurePaymentInfoService;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoType;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 * This is a JAXB wrapper around Referenced.
 * This wrapper can either be an instance of:
 *  <code>CreditCardPaymentInfo</code>
 *  <code>BankAccountPaymentInfo</code>
 *  <code>GiftCardPaymentInfo</code>
 *  <code>EmptyReferenced</code>
 *
 * <p/>
 * User: Elbert Bautista
 * Date: 4/26/12
 */
@XmlRootElement(name = "referenced")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ReferencedWrapper extends BaseWrapper implements APIWrapper<Referenced>, APIUnwrapper<Referenced>{

    @XmlElement
    protected Long id;

    @XmlElement
    protected String referenceNumber;

    @XmlElement
    protected String type;

    @XmlElement
    protected String pan;

    @XmlElement
    protected String cvvCode;

    @XmlElement
    protected Integer expirationMonth;

    @XmlElement
    protected Integer expirationYear;

    @XmlElement
    protected String accountNumber;

    @XmlElement
    protected String routingNumber;

    @XmlElement
    protected String pin;

    @Override
    public void wrapDetails(Referenced model, HttpServletRequest request) {
        this.id = model.getId();
        this.referenceNumber = model.getReferenceNumber();

        if (model instanceof CreditCardPaymentInfo) {
            CreditCardPaymentInfo referenced = (CreditCardPaymentInfo) model;
            this.type = CreditCardPaymentInfo.class.getName();

            this.pan = referenced.getPan();
            this.cvvCode = referenced.getCvvCode();
            this.expirationMonth = referenced.getExpirationMonth();
            this.expirationYear = referenced.getExpirationYear();
        }

        if (model instanceof BankAccountPaymentInfo) {
            BankAccountPaymentInfo referenced = (BankAccountPaymentInfo) model;
            this.type = BankAccountPaymentInfo.class.getName();

            this.accountNumber = referenced.getAccountNumber();
            this.routingNumber = referenced.getRoutingNumber();
        }

        if (model instanceof GiftCardPaymentInfo) {
            GiftCardPaymentInfo referenced = (GiftCardPaymentInfo) model;
            this.type = GiftCardPaymentInfo.class.getName();

            this.pan = referenced.getPan();
            this.pin = referenced.getPin();
        }

        if (model instanceof EmptyReferenced) {
            this.type = EmptyReferenced.class.getName();
        }

    }

    @Override
    public Referenced unwrap(HttpServletRequest request, ApplicationContext context) {
        SecurePaymentInfoService securePaymentInfoService = (SecurePaymentInfoService) context.getBean("blSecurePaymentInfoService");

        if (CreditCardPaymentInfo.class.getName().equals(this.type)) {
            CreditCardPaymentInfo paymentInfo = (CreditCardPaymentInfo) securePaymentInfoService.create(PaymentInfoType.CREDIT_CARD);
            paymentInfo.setId(this.id);
            paymentInfo.setReferenceNumber(this.referenceNumber);
            paymentInfo.setPan(this.pan);
            paymentInfo.setCvvCode(this.cvvCode);
            paymentInfo.setExpirationMonth(this.expirationMonth);
            paymentInfo.setExpirationYear(this.expirationYear);

            return paymentInfo;
        }

        if (BankAccountPaymentInfo.class.getName().equals(this.type)) {
            BankAccountPaymentInfo paymentInfo = (BankAccountPaymentInfo) securePaymentInfoService.create(PaymentInfoType.BANK_ACCOUNT);
            paymentInfo.setId(this.id);
            paymentInfo.setReferenceNumber(this.referenceNumber);
            paymentInfo.setAccountNumber(this.accountNumber);
            paymentInfo.setRoutingNumber(this.routingNumber);

            return paymentInfo;
        }

        if (GiftCardPaymentInfo.class.getName().equals(this.type)) {
            GiftCardPaymentInfo paymentInfo = (GiftCardPaymentInfo) securePaymentInfoService.create(PaymentInfoType.GIFT_CARD);
            paymentInfo.setId(this.id);
            paymentInfo.setReferenceNumber(this.referenceNumber);
            paymentInfo.setPan(this.pan);
            paymentInfo.setPin(this.pin);

            return paymentInfo;
        }

        if (EmptyReferenced.class.getName().equals(this.type)) {
            EmptyReferenced emptyReferenced = new EmptyReferenced();
            emptyReferenced.setId(this.id);
            emptyReferenced.setReferenceNumber(this.referenceNumber);

            return emptyReferenced;
        }

        return null;
    }

    @Override
    public void wrapSummary(Referenced model, HttpServletRequest request) {
        wrapDetails(model, request);
    }
}
