package org.broadleafcommerce.order.domain;

import java.util.Date;
import java.util.Map;

import org.broadleafcommerce.payment.service.type.BLCTransactionType;
import org.broadleafcommerce.util.money.Money;

public interface PaymentResponseItem {

    public String getAuthorizationCode();

    public void setAuthorizationCode(String authorizationCode);

    public String getMiddlewareResponseCode();

    public void setMiddlewareResponseCode(String middlewareResponseCode);

    public String getMiddlewareResponseText();

    public void setMiddlewareResponseText(String middlewareResponseText);

    public String getProcessorResponseCode();

    public void setProcessorResponseCode(String processorResponseCode);

    public String getProcessorResponseText();

    public void setProcessorResponseText(String processorResponseText);

    public String getReferenceNumber();

    public void setReferenceNumber(String referenceNumber);

    public Money getAmountPaid();

    public void setAmountPaid(Money amount);

    public Boolean getTransactionSuccess();

    public void setTransactionSuccess(Boolean transactionSuccess);

    public Date getTransactionTimestamp();

    public void setTransactionTimestamp(Date transactionTimestamp);

    public String getImplementorResponseCode();

    public void setImplementorResponseCode(String implementorResponseCode);

    public String getImplementorResponseText();

    public void setImplementorResponseText(String implementorResponseText);

    public String getTransactionId();

    public void setTransactionId(String transactionId);

    public String getAvsCode();

    public void setAvsCode(String avsCode);

    public String getCvvCode();

    public void setCvvCode(String cvvCode);

    public Money getRemainingBalance();

    public void setRemainingBalance(Money remainingBalance);

    public BLCTransactionType getTransactionType();

    public void setTransactionType(BLCTransactionType transactionType);

    public Map<String, String> getAdditionalFields();

    public void setAdditionalFields(Map<String, String> additionalFields);

    public PaymentInfo getPaymentInfo();

    public void setPaymentInfo(PaymentInfo paymentInfo);

    public String getUserName();

    public void setUserName(String userName);

}
