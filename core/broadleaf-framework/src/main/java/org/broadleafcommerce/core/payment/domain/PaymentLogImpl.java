/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.payment.domain;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrencyImpl;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.payment.PaymentLogEventType;
import org.broadleafcommerce.common.payment.PaymentTransactionType;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerImpl;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PAYMENT_LOG")
public class PaymentLogImpl implements PaymentLog {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "PaymentLogId")
    @GenericGenerator(
        name="PaymentLogId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="PaymentLogImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.payment.domain.PaymentLogImpl")
        }
    )
    @Column(name = "PAYMENT_LOG_ID")
    protected Long id;

    @Column(name = "USER_NAME", nullable=false)
    @Index(name="PAYMENTLOG_USER_INDEX", columnNames={"USER_NAME"})
    @AdminPresentation(friendlyName = "PaymentLogImpl_User_Name", order = 1, group = "PaymentLogImpl_Payment_Log", readOnly = true)
    protected String userName;

    @Column(name = "TRANSACTION_TIMESTAMP", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    @AdminPresentation(friendlyName = "PaymentLogImpl_Transaction_Time", order = 3, group = "PaymentLogImpl_Payment_Log", readOnly = true)
    protected Date transactionTimestamp;

    @Column(name = "ORDER_PAYMENT_ID")
    @Index(name="PAYMENTLOG_ORDERPAYMENT_INDEX", columnNames={"ORDER_PAYMENT_ID"})
    @AdminPresentation(excluded = true, readOnly = true)
    protected Long orderPaymentId;

    @ManyToOne(targetEntity = CustomerImpl.class)
    @JoinColumn(name = "CUSTOMER_ID")
    @Index(name="PAYMENTLOG_CUSTOMER_INDEX", columnNames={"CUSTOMER_ID"})
    protected Customer customer;

    @Column(name = "ORDER_PAYMENT_REFERENCE_NUMBER")
    @Index(name="PAYMENTLOG_REFERENCE_INDEX", columnNames={"ORDER_PAYMENT_REFERENCE_NUMBER"})
    @AdminPresentation(friendlyName = "PaymentLogImpl_Payment_Ref_Number", order = 4, group = "PaymentLogImpl_Payment_Log", readOnly = true)
    protected String orderPaymentReferenceNumber;

    @Column(name = "TRANSACTION_TYPE", nullable=false)
    @Index(name="PAYMENTLOG_TRANTYPE_INDEX", columnNames={"TRANSACTION_TYPE"})
    @AdminPresentation(friendlyName = "PaymentLogImpl_Transaction_Type", order = 5, group = "PaymentLogImpl_Payment_Log", readOnly = true)
    protected String transactionType;

    @Column(name = "TRANSACTION_SUCCESS")
    @AdminPresentation(friendlyName = "PaymentLogImpl_Transaction_Successfule", order = 6, group = "PaymentLogImpl_Payment_Log", readOnly = true)
    protected Boolean transactionSuccess = false;

    @Column(name = "EXCEPTION_MESSAGE")
    @AdminPresentation(friendlyName = "PaymentLogImpl_Exception_Message", order = 7, group = "PaymentLogImpl_Payment_Log", readOnly = true)
    protected String exceptionMessage;

    @Column(name = "LOG_TYPE", nullable=false)
    @Index(name="PAYMENTLOG_LOGTYPE_INDEX", columnNames={"LOG_TYPE"})
    @AdminPresentation(friendlyName = "PaymentLogImpl_Type", order = 8, group = "PaymentLogImpl_Payment_Log", readOnly = true)
    protected String logType;

    @Column(name = "AMOUNT_PAID", precision=19, scale=5)
    @AdminPresentation(friendlyName = "PaymentLogImpl_Amount", order = 2, group = "PaymentLogImpl_Payment_Log", readOnly = true)
    protected BigDecimal amountPaid;

    @ManyToOne(targetEntity = BroadleafCurrencyImpl.class)
    @JoinColumn(name = "CURRENCY_CODE")
    @AdminPresentation(friendlyName = "PaymentLogImpl_currency", order = 2, group = "PaymentLogImpl_Payment_Log", readOnly = true)
    protected BroadleafCurrency currency;
    
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public Date getTransactionTimestamp() {
        return transactionTimestamp;
    }

    @Override
    public void setTransactionTimestamp(Date transactionTimestamp) {
        this.transactionTimestamp = transactionTimestamp;
    }

    @Override
    public Long getPaymentInfoId() {
        return orderPaymentId;
    }

    @Override
    public void setPaymentInfoId(Long paymentInfoId) {
        this.orderPaymentId = paymentInfoId;
    }

    @Override
    public Customer getCustomer() {
        return customer;
    }

    @Override
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String getPaymentInfoReferenceNumber() {
        return orderPaymentReferenceNumber;
    }

    @Override
    public void setPaymentInfoReferenceNumber(String paymentInfoReferenceNumber) {
        this.orderPaymentReferenceNumber = paymentInfoReferenceNumber;
    }

    @Override
    public PaymentTransactionType getTransactionType() {
        return PaymentTransactionType.getInstance(transactionType);
    }

    @Override
    public void setTransactionType(PaymentTransactionType transactionType) {
        this.transactionType = transactionType.getType();
    }

    @Override
    public PaymentLogEventType getLogType() {
        return PaymentLogEventType.getInstance(logType);
    }

    @Override
    public void setLogType(PaymentLogEventType logType) {
        this.logType = logType.getType();
    }

    @Override
    public Boolean getTransactionSuccess() {
        if (transactionSuccess == null) {
            return Boolean.FALSE;
        } else {
            return transactionSuccess;
        }
    }

    @Override
    public void setTransactionSuccess(Boolean transactionSuccess) {
        this.transactionSuccess = transactionSuccess;
    }

    @Override
    public String getExceptionMessage() {
        return exceptionMessage;
    }

    @Override
    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    @Override
    public Money getAmountPaid() {
        return BroadleafCurrencyUtils.getMoney(amountPaid, currency);
    }

    @Override
    public void setAmountPaid(Money amountPaid) {
        this.amountPaid = Money.toAmount(amountPaid);
    }

    @Override
    public BroadleafCurrency getCurrency() {
        return currency;
    }

    @Override
    public void setCurrency(BroadleafCurrency currency) {
        this.currency = currency;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((customer == null) ? 0 : customer.hashCode());
        result = prime * result + ((orderPaymentId == null) ? 0 : orderPaymentId.hashCode());
        result = prime * result + ((orderPaymentReferenceNumber == null) ? 0 : orderPaymentReferenceNumber.hashCode());
        result = prime * result + ((transactionTimestamp == null) ? 0 : transactionTimestamp.hashCode());
        result = prime * result + ((userName == null) ? 0 : userName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PaymentLogImpl other = (PaymentLogImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (customer == null) {
            if (other.customer != null) {
                return false;
            }
        } else if (!customer.equals(other.customer)) {
            return false;
        }
        if (orderPaymentId == null) {
            if (other.orderPaymentId != null) {
                return false;
            }
        } else if (!orderPaymentId.equals(other.orderPaymentId)) {
            return false;
        }
        if (orderPaymentReferenceNumber == null) {
            if (other.orderPaymentReferenceNumber != null) {
                return false;
            }
        } else if (!orderPaymentReferenceNumber.equals(other.orderPaymentReferenceNumber)) {
            return false;
        }
        if (transactionTimestamp == null) {
            if (other.transactionTimestamp != null) {
                return false;
            }
        } else if (!transactionTimestamp.equals(other.transactionTimestamp)) {
            return false;
        }
        if (userName == null) {
            if (other.userName != null) {
                return false;
            }
        } else if (!userName.equals(other.userName)) {
            return false;
        }
        return true;
    }
}
