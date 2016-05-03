/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.payment.domain;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.profile.core.domain.Customer;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public interface PaymentResponseItem extends Serializable {

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

    /**
     * The amount that the system processed. For example, when submitting an order, this would be the order.getTotal.
     * If refunding $10, this would be 10.
     *
     * @return
     */
    public Money getTransactionAmount();

    /**
     * Sets the transaction amount.
     *
     * @param amount
     */
    public void setTransactionAmount(Money amount);

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

    // TODO: Rename to getRemainingTransactionAmount
    public Money getRemainingBalance();

    public void setRemainingBalance(Money remainingBalance);

    public Map<String, String> getAdditionalFields();

    public void setAdditionalFields(Map<String, String> additionalFields);

    public String getUserName();

    public void setUserName(String userName);

    public Customer getCustomer();

    public void setCustomer(Customer customer);

    public void setPaymentTransaction(PaymentTransaction paymentTransaction);

    public PaymentTransaction getPaymentTransaction();

}
