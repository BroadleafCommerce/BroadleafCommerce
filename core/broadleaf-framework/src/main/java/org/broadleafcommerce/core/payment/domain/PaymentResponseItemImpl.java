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
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.core.payment.service.type.TransactionType;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerImpl;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PAYMENT_RESPONSE_ITEM")
public class PaymentResponseItemImpl implements PaymentResponseItem {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "PaymentResponseItemId")
    @GenericGenerator(
        name="PaymentResponseItemId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="PaymentResponseItemImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.payment.domain.PaymentResponseItemImpl")
        }
    )
    @Column(name = "PAYMENT_RESPONSE_ITEM_ID")
    protected Long id;

    @Column(name = "USER_NAME", nullable=false)
    @AdminPresentation(friendlyName = "PaymentResponseItemImpl_User_Name", order = 1, group = "PaymentResponseItemImpl_Payment_Response", readOnly = true)
    protected String userName;

    @Column(name = "AMOUNT_PAID", precision=19, scale=5)
    @AdminPresentation(friendlyName = "PaymentResponseItemImpl_Amount", order = 2, group = "PaymentResponseItemImpl_Payment_Response", readOnly = true, prominent = true, gridOrder = 200, fieldType = SupportedFieldType.MONEY)
    protected BigDecimal amountPaid;

    @Column(name = "TRANSACTION_AMOUNT", precision=19, scale=5)
    @AdminPresentation(friendlyName = "PaymentResponseItemImpl_Transaction_Amount", order = 2, group = "PaymentResponseItemImpl_Payment_Response", readOnly = true)
    protected BigDecimal transactionAmount;

    @Column(name = "AUTHORIZATION_CODE")
    @AdminPresentation(friendlyName = "PaymentResponseItemImpl_Authorization_Code", order = 3, group = "PaymentResponseItemImpl_Payment_Response", readOnly = true)
    protected String authorizationCode;

    @Column(name = "MIDDLEWARE_RESPONSE_CODE")
    @AdminPresentation(friendlyName = "PaymentResponseItemImpl_Middleware_Response_Code", order = 4, group = "PaymentResponseItemImpl_Payment_Response", readOnly = true)
    protected String middlewareResponseCode;

    @Column(name = "MIDDLEWARE_RESPONSE_TEXT")
    @AdminPresentation(friendlyName = "PaymentResponseItemImpl_Middleware_Response_Text", order = 5, group = "PaymentResponseItemImpl_Payment_Response", readOnly = true)
    protected String middlewareResponseText;

    @Column(name = "PROCESSOR_RESPONSE_CODE")
    @AdminPresentation(friendlyName = "PaymentResponseItemImpl_Processor_Response_Code", order = 6, group = "PaymentResponseItemImpl_Payment_Response", readOnly = true)
    protected String processorResponseCode;

    @Column(name = "PROCESSOR_RESPONSE_TEXT")
    @AdminPresentation(friendlyName = "PaymentResponseItemImpl_Processor_Response_Text", order = 7, group = "PaymentResponseItemImpl_Payment_Response", readOnly = true)
    protected String processorResponseText;

    @Column(name = "IMPLEMENTOR_RESPONSE_CODE")
    @AdminPresentation(friendlyName = "PaymentResponseItemImpl_Implementer_Response_Code", order = 8, group = "PaymentResponseItemImpl_Payment_Response", readOnly = true)
    protected String implementorResponseCode;

    @Column(name = "IMPLEMENTOR_RESPONSE_TEXT")
    @AdminPresentation(friendlyName = "PaymentResponseItemImpl_Implementer_Response_Text", order = 9, group = "PaymentResponseItemImpl_Payment_Response", readOnly = true)
    protected String implementorResponseText;

    @Column(name = "REFERENCE_NUMBER")
    @Index(name="PAYRESPONSE_REFERENCE_INDEX", columnNames={"REFERENCE_NUMBER"})
    @AdminPresentation(friendlyName = "PaymentResponseItemImpl_Response_Ref_Number", order = 10, group = "PaymentResponseItemImpl_Payment_Response", readOnly = true)
    protected String referenceNumber;

    @Column(name = "TRANSACTION_SUCCESS")
    @AdminPresentation(friendlyName = "PaymentResponseItemImpl_Transaction_Successful", order = 11, group = "PaymentResponseItemImpl_Payment_Response", readOnly = true, prominent = true, gridOrder = 300)
    protected Boolean transactionSuccess = false;

    @Column(name = "TRANSACTION_TIMESTAMP", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    @AdminPresentation(friendlyName = "PaymentResponseItemImpl_Transaction_Time", order = 12, group = "PaymentResponseItemImpl_Payment_Response", readOnly = true, prominent = true, gridOrder = 100)
    protected Date transactionTimestamp;

    @Column(name = "TRANSACTION_ID")
    @AdminPresentation(friendlyName = "PaymentResponseItemImpl_Transaction_Id", order = 13, group = "PaymentResponseItemImpl_Payment_Response", readOnly = true)
    protected String transactionId;

    @Column(name = "AVS_CODE")
    @AdminPresentation(friendlyName = "PaymentResponseItemImpl_AVS_Code", order = 14, group = "PaymentResponseItemImpl_Payment_Response", readOnly = true)
    protected String avsCode;

    @Transient
    protected String cvvCode;

    @Column(name = "REMAINING_BALANCE", precision=19, scale=5)
    @AdminPresentation(friendlyName = "PaymentResponseItemImpl_Remaining_Balance", order = 15, group = "PaymentResponseItemImpl_Payment_Response", readOnly = true)
    protected BigDecimal remainingBalance;

    @Column(name = "TRANSACTION_TYPE", nullable=false)
    @Index(name="PAYRESPONSE_TRANTYPE_INDEX", columnNames={"TRANSACTION_TYPE"})
    @AdminPresentation(friendlyName = "PaymentResponseItemImpl_Transaction_Type", order = 16, group = "PaymentResponseItemImpl_Payment_Response", readOnly = true, prominent = true, gridOrder = 400)
    protected String transactionType;

    @ElementCollection
    @MapKeyColumn(name="FIELD_NAME")
    @Column(name="FIELD_VALUE")
    @CollectionTable(name="BLC_PAYMENT_ADDITIONAL_FIELDS", joinColumns=@JoinColumn(name="PAYMENT_RESPONSE_ITEM_ID"))
    @BatchSize(size = 50)
    protected Map<String, String> additionalFields = new HashMap<String, String>();

    @Column(name = "ORDER_PAYMENT_ID")
    @Index(name="PAYRESPONSE_ORDERPAYMENT_INDEX", columnNames={"ORDER_PAYMENT_ID"})
    @AdminPresentation(excluded = true, readOnly = true)
    protected Long paymentInfoId;

    @ManyToOne(targetEntity = CustomerImpl.class)
    @JoinColumn(name = "CUSTOMER_ID")
    @Index(name="PAYRESPONSE_CUSTOMER_INDEX", columnNames={"CUSTOMER_ID"})
    protected Customer customer;

    @Column(name = "PAYMENT_INFO_REFERENCE_NUMBER")
    @Index(name="PAYRESPONSE_REFERENCE_INDEX", columnNames={"PAYMENT_INFO_REFERENCE_NUMBER"})
    @AdminPresentation(friendlyName = "PaymentResponseItemImpl_Payment_Ref_Number", order = 17, group = "PaymentResponseItemImpl_Payment_Response", readOnly = true)
    protected String paymentInfoReferenceNumber;

    @ManyToOne(targetEntity = BroadleafCurrencyImpl.class)
    @JoinColumn(name = "CURRENCY_CODE")
    @AdminPresentation(friendlyName = "PaymentResponseItemImpl_currency", order = 2, group = "PaymentLogImpl_Payment_Log", readOnly = true)
    protected BroadleafCurrency currency;
    
    @ManyToOne(targetEntity = PaymentInfoImpl.class)
    @JoinColumn(name = "PAYMENT_INFO_REFERENCE_NUMBER", referencedColumnName = "REFERENCE_NUMBER", insertable = false, updatable = false)
    protected PaymentInfo paymentInfo;
    
    @Override
    public String getAuthorizationCode() {
        return authorizationCode;
    }

    @Override
    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    @Override
    public String getMiddlewareResponseCode() {
        return middlewareResponseCode;
    }

    @Override
    public void setMiddlewareResponseCode(String middlewareResponseCode) {
        this.middlewareResponseCode = middlewareResponseCode;
    }

    @Override
    public String getMiddlewareResponseText() {
        return middlewareResponseText;
    }

    @Override
    public void setMiddlewareResponseText(String middlewareResponseText) {
        this.middlewareResponseText = middlewareResponseText;
    }

    @Override
    public String getProcessorResponseCode() {
        return processorResponseCode;
    }

    @Override
    public void setProcessorResponseCode(String processorResponseCode) {
        this.processorResponseCode = processorResponseCode;
    }

    @Override
    public String getProcessorResponseText() {
        return processorResponseText;
    }

    @Override
    public void setProcessorResponseText(String processorResponseText) {
        this.processorResponseText = processorResponseText;
    }

    @Override
    public String getReferenceNumber() {
        return referenceNumber;
    }

    @Override
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    @Override
    @Deprecated
    public Money getAmountPaid() {
        return BroadleafCurrencyUtils.getMoney(amountPaid, getCurrency());
    }

    @Override
    @Deprecated
    public void setAmountPaid(Money amountPaid) {
        this.amountPaid = Money.toAmount(amountPaid);
    }

    @Override
    public Money getTransactionAmount() {
        return BroadleafCurrencyUtils.getMoney(transactionAmount, getCurrency());
    }

    @Override
    public void setTransactionAmount(Money transactionAmount) {
        this.transactionAmount = Money.toAmount(transactionAmount);
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
    public Date getTransactionTimestamp() {
        return transactionTimestamp;
    }

    @Override
    public void setTransactionTimestamp(Date transactionTimestamp) {
        this.transactionTimestamp = transactionTimestamp;
    }

    @Override
    public String getImplementorResponseCode() {
        return implementorResponseCode;
    }

    @Override
    public void setImplementorResponseCode(String implementorResponseCode) {
        this.implementorResponseCode = implementorResponseCode;
    }

    @Override
    public String getImplementorResponseText() {
        return implementorResponseText;
    }

    @Override
    public void setImplementorResponseText(String implementorResponseText) {
        this.implementorResponseText = implementorResponseText;
    }

    @Override
    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String getAvsCode() {
        return avsCode;
    }

    @Override
    public void setAvsCode(String avsCode) {
        this.avsCode = avsCode;
    }

    @Override
    public String getCvvCode() {
        return cvvCode;
    }

    @Override
    public void setCvvCode(String cvvCode) {
        this.cvvCode = cvvCode;
    }

    @Override
    public Money getRemainingBalance() {
        return remainingBalance == null ? null : BroadleafCurrencyUtils.getMoney(remainingBalance, getCurrency());
    }

    @Override
    public void setRemainingBalance(Money remainingBalance) {
        this.remainingBalance = remainingBalance==null?null:Money.toAmount(remainingBalance);
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.getInstance(transactionType);
    }

    @Override
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType.getType();
    }

    @Override
    public Map<String, String> getAdditionalFields() {
        return additionalFields;
    }

    @Override
    public void setAdditionalFields(Map<String, String> additionalFields) {
        this.additionalFields = additionalFields;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getPaymentInfoId() {
        return paymentInfoId;
    }

    @Override
    public void setPaymentInfoId(Long paymentInfoId) {
        this.paymentInfoId = paymentInfoId;
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
    public Customer getCustomer() {
        return customer;
    }

    @Override
    public void setCustomer(Customer customer) {
        this.customer = customer;
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
    public String getPaymentInfoReferenceNumber() {
        return paymentInfoReferenceNumber;
    }

    @Override
    public void setPaymentInfoReferenceNumber(String paymentInfoReferenceNumber) {
        this.paymentInfoReferenceNumber = paymentInfoReferenceNumber;
    }
    
    @Override
    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }
    
    @Override
    public void setPaymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(PaymentResponseItem.class.getName()).append("\n");
        sb.append("auth code: ").append(this.getAuthorizationCode()).append("\n");
        sb.append("implementor response code: ").append(this.getImplementorResponseCode()).append("\n");
        sb.append("implementor response text: ").append(this.getImplementorResponseText()).append("\n");
        sb.append("middleware response code: ").append(this.getMiddlewareResponseCode()).append("\n");
        sb.append("middleware response text: ").append(this.getMiddlewareResponseText()).append("\n");
        sb.append("processor response code: ").append(this.getProcessorResponseCode()).append("\n");
        sb.append("processor response text: ").append(this.getProcessorResponseText()).append("\n");
        sb.append("reference number: ").append(this.getReferenceNumber()).append("\n");
        sb.append("transaction id: ").append(this.getTransactionId()).append("\n");
        sb.append("avs code: ").append(this.getAvsCode()).append("\n");
        if (remainingBalance != null) {
            sb.append("remaining balance: ").append(this.getRemainingBalance());
        }

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
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        PaymentResponseItemImpl other = (PaymentResponseItemImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (transactionId == null) {
            if (other.transactionId != null) {
                return false;
            }
        } else if (!transactionId.equals(other.transactionId)) {
            return false;
        }
        return true;
    }

}
