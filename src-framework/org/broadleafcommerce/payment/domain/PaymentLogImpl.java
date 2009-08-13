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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.broadleafcommerce.payment.service.type.PaymentLogEventType;
import org.broadleafcommerce.payment.service.type.TransactionType;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.domain.CustomerImpl;
import org.broadleafcommerce.util.money.Money;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PAYMENT_LOG")
public class PaymentLogImpl implements PaymentLog {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "PaymentLogId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "PaymentLogId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "PaymentLogImpl", allocationSize = 50)
    @Column(name = "PAYMENT_LOG_ID")
    protected Long id;

    @Column(name = "USER_NAME", nullable = false)
    protected String userName;

    @Column(name = "TRANSACTION_TIMESTAMP", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date transactionTimestamp;

    @Column(name = "ORDER_PAYMENT_ID")
    protected Long paymentInfoId;

    @ManyToOne(targetEntity = CustomerImpl.class)
    @JoinColumn(name = "CUSTOMER_ID")
    protected Customer customer;

    @Column(name = "PAYMENT_INFO_REFERENCE_NUMBER")
    protected String paymentInfoReferenceNumber;

    @Column(name = "TRANSACTION_TYPE", nullable = false)
    protected String transactionType;

    @Column(name = "TRANSACTION_SUCCESS")
    protected Boolean transactionSuccess;

    @Column(name = "EXCEPTION_MESSAGE")
    protected String exceptionMessage;

    @Column(name = "LOG_TYPE", nullable = false)
    protected String logType;

    @Column(name = "AMOUNT_PAID")
    protected BigDecimal amountPaid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getTransactionTimestamp() {
        return transactionTimestamp;
    }

    public void setTransactionTimestamp(Date transactionTimestamp) {
        this.transactionTimestamp = transactionTimestamp;
    }

    public Long getPaymentInfoId() {
        return paymentInfoId;
    }

    public void setPaymentInfoId(Long paymentInfoId) {
        this.paymentInfoId = paymentInfoId;
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

    public TransactionType getTransactionType() {
        return transactionType == null ? null : TransactionType.getInstance(transactionType);
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType.getType();
    }

    public PaymentLogEventType getLogType() {
        return logType == null ? null : PaymentLogEventType.getInstance(logType);
    }

    public void setLogType(PaymentLogEventType logType) {
        this.logType = logType.getType();
    }

    public Boolean getTransactionSuccess() {
        return transactionSuccess;
    }

    public void setTransactionSuccess(Boolean transactionSuccess) {
        this.transactionSuccess = transactionSuccess;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public Money getAmountPaid() {
        return amountPaid == null ? null : new Money(amountPaid);
    }

    public void setAmountPaid(Money amountPaid) {
        this.amountPaid = Money.toAmount(amountPaid);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((customer == null) ? 0 : customer.hashCode());
        result = prime * result + ((paymentInfoId == null) ? 0 : paymentInfoId.hashCode());
        result = prime * result + ((paymentInfoReferenceNumber == null) ? 0 : paymentInfoReferenceNumber.hashCode());
        result = prime * result + ((transactionTimestamp == null) ? 0 : transactionTimestamp.hashCode());
        result = prime * result + ((userName == null) ? 0 : userName.hashCode());
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
        PaymentLogImpl other = (PaymentLogImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (customer == null) {
            if (other.customer != null)
                return false;
        } else if (!customer.equals(other.customer))
            return false;
        if (paymentInfoId == null) {
            if (other.paymentInfoId != null)
                return false;
        } else if (!paymentInfoId.equals(other.paymentInfoId))
            return false;
        if (paymentInfoReferenceNumber == null) {
            if (other.paymentInfoReferenceNumber != null)
                return false;
        } else if (!paymentInfoReferenceNumber.equals(other.paymentInfoReferenceNumber))
            return false;
        if (transactionTimestamp == null) {
            if (other.transactionTimestamp != null)
                return false;
        } else if (!transactionTimestamp.equals(other.transactionTimestamp))
            return false;
        if (userName == null) {
            if (other.userName != null)
                return false;
        } else if (!userName.equals(other.userName))
            return false;
        return true;
    }
}
