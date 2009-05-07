package org.broadleafcommerce.payment.service.module;

import org.broadleafcommerce.util.money.Money;

public class PaymentResponseImpl implements PaymentResponse {

    protected Money remainingBalance;
    protected String authorizationCode;
    protected String middlewareResponseCode;
    protected String middlewareResponseText;
    protected String processorResponseCode;
    protected String processorResponseText;
    protected String referenceNumber;

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

    public Money getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(Money remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

}
