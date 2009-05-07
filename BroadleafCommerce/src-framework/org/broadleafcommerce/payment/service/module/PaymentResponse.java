package org.broadleafcommerce.payment.service.module;

import org.broadleafcommerce.util.money.Money;

public interface PaymentResponse {

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

    public Money getRemainingBalance();

    public void setRemainingBalance(Money remainingBalance);

}
