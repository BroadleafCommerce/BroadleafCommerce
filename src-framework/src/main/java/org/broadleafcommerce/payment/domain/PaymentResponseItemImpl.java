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
package org.broadleafcommerce.payment.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.broadleafcommerce.payment.service.type.TransactionType;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.domain.CustomerImpl;
import org.broadleafcommerce.util.money.Money;
import org.hibernate.annotations.BatchSize;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PAYMENT_RESPONSE_ITEM")
public class PaymentResponseItemImpl implements PaymentResponseItem {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "PaymentResponseItemId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "PaymentResponseItemId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "PaymentResponseItemImpl", allocationSize = 50)
    @Column(name = "PAYMENT_RESPONSE_ITEM_ID")
    protected Long id;

    @Column(name = "USER_NAME", nullable = false)
    protected String userName;

    @Column(name = "AMOUNT_PAID")
    protected BigDecimal amountPaid;

    @Column(name = "AUTHORIZATION_CODE")
    protected String authorizationCode;

    @Column(name = "MIDDLEWARE_RESPONSE_CODE")
    protected String middlewareResponseCode;

    @Column(name = "MIDDLEWARE_RESPONSE_TEXT")
    protected String middlewareResponseText;

    @Column(name = "PROCESSOR_RESPONSE_CODE")
    protected String processorResponseCode;

    @Column(name = "PROCESSOR_RESPONSE_TEXT")
    protected String processorResponseText;

    @Column(name = "IMPLEMENTOR_RESPONSE_CODE")
    protected String implementorResponseCode;

    @Column(name = "IMPLEMENTOR_RESPONSE_TEXT")
    protected String implementorResponseText;

    @Column(name = "REFERENCE_NUMBER")
    protected String referenceNumber;

    @Column(name = "TRANSACTION_SUCCESS")
    protected Boolean transactionSuccess;

    @Column(name = "TRANSACTION_TIMESTAMP", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date transactionTimestamp;

    @Column(name = "TRANSACTION_ID")
    protected String transactionId;

    @Column(name = "AVS_CODE")
    protected String avsCode;

    @Transient
    protected String cvvCode;

    @Column(name = "REMAINING_BALANCE")
    protected BigDecimal remainingBalance;

    @Column(name = "TRANSACTION_TYPE", nullable = false)
    protected String transactionType;

    @ElementCollection
    @MapKeyColumn(name="FIELD_NAME", length = 150)
    @Column(name="FIELD_VALUE")
    @CollectionTable(name="BLC_PAYMENT_ADDITIONAL_FIELDS", joinColumns=@JoinColumn(name="PAYMENT_RESPONSE_ITEM_ID"))
    @BatchSize(size = 50)
    protected Map<String, String> additionalFields = new HashMap<String, String>();

    @Column(name = "ORDER_PAYMENT_ID")
    protected Long paymentInfoId;

    @ManyToOne(targetEntity = CustomerImpl.class)
    @JoinColumn(name = "CUSTOMER_ID")
    protected Customer customer;

    @Column(name = "PAYMENT_INFO_REFERENCE_NUMBER")
    protected String paymentInfoReferenceNumber;

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public String getMiddlewareResponseCode() {
        return middlewareResponseCode;
    }

    public void setMiddlewareResponseCode(String middlewareResponseCode) {
        this.middlewareResponseCode = middlewareResponseCode;
    }

    public String getMiddlewareResponseText() {
        return middlewareResponseText;
    }

    public void setMiddlewareResponseText(String middlewareResponseText) {
        this.middlewareResponseText = middlewareResponseText;
    }

    public String getProcessorResponseCode() {
        return processorResponseCode;
    }

    public void setProcessorResponseCode(String processorResponseCode) {
        this.processorResponseCode = processorResponseCode;
    }

    public String getProcessorResponseText() {
        return processorResponseText;
    }

    public void setProcessorResponseText(String processorResponseText) {
        this.processorResponseText = processorResponseText;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public Money getAmountPaid() {
        return amountPaid == null ? null : new Money(amountPaid);
    }

    public void setAmountPaid(Money amountPaid) {
        this.amountPaid = Money.toAmount(amountPaid);
    }

    public Boolean getTransactionSuccess() {
        return transactionSuccess;
    }

    public void setTransactionSuccess(Boolean transactionSuccess) {
        this.transactionSuccess = transactionSuccess;
    }

    public Date getTransactionTimestamp() {
        return transactionTimestamp;
    }

    public void setTransactionTimestamp(Date transactionTimestamp) {
        this.transactionTimestamp = transactionTimestamp;
    }

    public String getImplementorResponseCode() {
        return implementorResponseCode;
    }

    public void setImplementorResponseCode(String implementorResponseCode) {
        this.implementorResponseCode = implementorResponseCode;
    }

    public String getImplementorResponseText() {
        return implementorResponseText;
    }

    public void setImplementorResponseText(String implementorResponseText) {
        this.implementorResponseText = implementorResponseText;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAvsCode() {
        return avsCode;
    }

    public void setAvsCode(String avsCode) {
        this.avsCode = avsCode;
    }

    public String getCvvCode() {
        return cvvCode;
    }

    public void setCvvCode(String cvvCode) {
        this.cvvCode = cvvCode;
    }

    public Money getRemainingBalance() {
        return remainingBalance == null ? null : new Money(remainingBalance);
    }

    public void setRemainingBalance(Money remainingBalance) {
        this.remainingBalance = Money.toAmount(remainingBalance);
    }

    public TransactionType getTransactionType() {
        return transactionType == null ? null : TransactionType.getInstance(transactionType);
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType.getType();
    }

    public Map<String, String> getAdditionalFields() {
        return additionalFields;
    }

    public void setAdditionalFields(Map<String, String> additionalFields) {
        this.additionalFields = additionalFields;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPaymentInfoId() {
        return paymentInfoId;
    }

    public void setPaymentInfoId(Long paymentInfoId) {
        this.paymentInfoId = paymentInfoId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getPaymentInfoReferenceNumber() {
        return paymentInfoReferenceNumber;
    }

    public void setPaymentInfoReferenceNumber(String paymentInfoReferenceNumber) {
        this.paymentInfoReferenceNumber = paymentInfoReferenceNumber;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(PaymentResponseItem.class.getName() + "\n");
        sb.append("auth code: " + this.getAuthorizationCode() + "\n");
        sb.append("implementor response code: " + this.getImplementorResponseCode() + "\n");
        sb.append("implementor response text: " + this.getImplementorResponseText() + "\n");
        sb.append("middleware response code: " + this.getMiddlewareResponseCode() + "\n");
        sb.append("middleware response text: " + this.getMiddlewareResponseText() + "\n");
        sb.append("processor response code: " + this.getProcessorResponseCode() + "\n");
        sb.append("processor response text: " + this.getProcessorResponseText() + "\n");
        sb.append("reference number: " + this.getReferenceNumber() + "\n");
        sb.append("transaction id: " + this.getTransactionId() + "\n");
        sb.append("avs code: " + this.getAvsCode() + "\n");
        if (remainingBalance != null)
            sb.append("remaining balance: " + this.getRemainingBalance());

        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((transactionId == null) ? 0 : transactionId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PaymentResponseItemImpl other = (PaymentResponseItemImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (transactionId == null) {
            if (other.transactionId != null)
                return false;
        } else if (!transactionId.equals(other.transactionId))
            return false;
        return true;
    }

}
