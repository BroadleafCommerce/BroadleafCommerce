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

import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.core.payment.domain.secure.BankAccountPayment;
import org.broadleafcommerce.core.payment.domain.secure.CreditCardPayment;
import org.broadleafcommerce.core.payment.domain.secure.GiftCardPayment;
import org.broadleafcommerce.core.payment.domain.secure.Referenced;
import org.broadleafcommerce.core.payment.service.SecureOrderPaymentService;
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
 *  <code>CreditCardPayment</code>
 *  <code>BankAccountPayment</code>
 *  <code>GiftCardPayment</code>
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

        if (model instanceof CreditCardPayment) {
            CreditCardPayment referenced = (CreditCardPayment) model;
            this.type = CreditCardPayment.class.getName();

            this.pan = referenced.getPan();
            this.cvvCode = referenced.getCvvCode();
            this.expirationMonth = referenced.getExpirationMonth();
            this.expirationYear = referenced.getExpirationYear();
        }

        if (model instanceof BankAccountPayment) {
            BankAccountPayment referenced = (BankAccountPayment) model;
            this.type = BankAccountPayment.class.getName();

            this.accountNumber = referenced.getAccountNumber();
            this.routingNumber = referenced.getRoutingNumber();
        }

        if (model instanceof GiftCardPayment) {
            GiftCardPayment referenced = (GiftCardPayment) model;
            this.type = GiftCardPayment.class.getName();

            this.pan = referenced.getPan();
            this.pin = referenced.getPin();
        }

    }

    @Override
    public Referenced unwrap(HttpServletRequest request, ApplicationContext context) {
        SecureOrderPaymentService securePaymentInfoService = (SecureOrderPaymentService) context.getBean("blSecureOrderPaymentService");

        if (CreditCardPayment.class.getName().equals(this.type)) {
            CreditCardPayment paymentInfo = (CreditCardPayment) securePaymentInfoService.create(PaymentType.CREDIT_CARD);
            paymentInfo.setId(this.id);
            paymentInfo.setReferenceNumber(this.referenceNumber);
            paymentInfo.setPan(this.pan);
            paymentInfo.setCvvCode(this.cvvCode);
            paymentInfo.setExpirationMonth(this.expirationMonth);
            paymentInfo.setExpirationYear(this.expirationYear);

            return paymentInfo;
        }

        if (BankAccountPayment.class.getName().equals(this.type)) {
            BankAccountPayment paymentInfo = (BankAccountPayment) securePaymentInfoService.create(PaymentType.BANK_ACCOUNT);
            paymentInfo.setId(this.id);
            paymentInfo.setReferenceNumber(this.referenceNumber);
            paymentInfo.setAccountNumber(this.accountNumber);
            paymentInfo.setRoutingNumber(this.routingNumber);

            return paymentInfo;
        }

        if (GiftCardPayment.class.getName().equals(this.type)) {
            GiftCardPayment paymentInfo = (GiftCardPayment) securePaymentInfoService.create(PaymentType.GIFT_CARD);
            paymentInfo.setId(this.id);
            paymentInfo.setReferenceNumber(this.referenceNumber);
            paymentInfo.setPan(this.pan);
            paymentInfo.setPin(this.pin);

            return paymentInfo;
        }

        return null;
    }

    @Override
    public void wrapSummary(Referenced model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

    
    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    
    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    
    /**
     * @return the referenceNumber
     */
    public String getReferenceNumber() {
        return referenceNumber;
    }

    
    /**
     * @param referenceNumber the referenceNumber to set
     */
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    
    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    
    /**
     * @return the pan
     */
    public String getPan() {
        return pan;
    }

    
    /**
     * @param pan the pan to set
     */
    public void setPan(String pan) {
        this.pan = pan;
    }

    
    /**
     * @return the cvvCode
     */
    public String getCvvCode() {
        return cvvCode;
    }

    
    /**
     * @param cvvCode the cvvCode to set
     */
    public void setCvvCode(String cvvCode) {
        this.cvvCode = cvvCode;
    }

    
    /**
     * @return the expirationMonth
     */
    public Integer getExpirationMonth() {
        return expirationMonth;
    }

    
    /**
     * @param expirationMonth the expirationMonth to set
     */
    public void setExpirationMonth(Integer expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    
    /**
     * @return the expirationYear
     */
    public Integer getExpirationYear() {
        return expirationYear;
    }

    
    /**
     * @param expirationYear the expirationYear to set
     */
    public void setExpirationYear(Integer expirationYear) {
        this.expirationYear = expirationYear;
    }

    
    /**
     * @return the accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    
    /**
     * @param accountNumber the accountNumber to set
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    
    /**
     * @return the routingNumber
     */
    public String getRoutingNumber() {
        return routingNumber;
    }

    
    /**
     * @param routingNumber the routingNumber to set
     */
    public void setRoutingNumber(String routingNumber) {
        this.routingNumber = routingNumber;
    }

    
    /**
     * @return the pin
     */
    public String getPin() {
        return pin;
    }

    
    /**
     * @param pin the pin to set
     */
    public void setPin(String pin) {
        this.pin = pin;
    }
}
