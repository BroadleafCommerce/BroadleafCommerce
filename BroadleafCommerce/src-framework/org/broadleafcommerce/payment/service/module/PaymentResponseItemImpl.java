package org.broadleafcommerce.payment.service.module;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.payment.service.type.BLCTransactionType;
import org.broadleafcommerce.util.money.Money;

public class PaymentResponseItemImpl implements PaymentResponseItem {

    protected Money amountPaid;
    protected String authorizationCode;
    protected String middlewareResponseCode;
    protected String middlewareResponseText;
    protected String processorResponseCode;
    protected String processorResponseText;
    protected String implementorResponseCode;
    protected String implementorResponseText;
    protected String referenceNumber;
    protected Boolean transactionSuccess;
    protected Date transactionTimestamp;
    protected String transactionId;
    protected String avsCode;
    protected String cvvCode;
    protected Money remainingBalance;
    protected BLCTransactionType transactionType;
    protected Map<String, Object> additionalFields = new HashMap<String, Object>();

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
        return amountPaid;
    }

    public void setAmountPaid(Money amountPaid) {
        this.amountPaid = amountPaid;
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
        return remainingBalance;
    }

    public void setRemainingBalance(Money remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    public BLCTransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(BLCTransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Map<String, Object> getAdditionalFields() {
        return additionalFields;
    }

    public void setAdditionalFields(Map<String, Object> additionalFields) {
        this.additionalFields = additionalFields;
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
        sb.append("remaining balance: " + this.getRemainingBalance());

        return sb.toString();
    }

}
