/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

    String getAuthorizationCode();

    void setAuthorizationCode(String authorizationCode);

    String getMiddlewareResponseCode();

    void setMiddlewareResponseCode(String middlewareResponseCode);

    String getMiddlewareResponseText();

    void setMiddlewareResponseText(String middlewareResponseText);

    String getProcessorResponseCode();

    void setProcessorResponseCode(String processorResponseCode);

    String getProcessorResponseText();

    void setProcessorResponseText(String processorResponseText);

    /**
     * The amount that the system processed. For example, when submitting an order, this would be the order.getTotal.
     * If refunding $10, this would be 10.
     *
     * @return
     */
    Money getTransactionAmount();

    /**
     * Sets the transaction amount.
     *
     * @param amount
     */
    void setTransactionAmount(Money amount);

    Boolean getTransactionSuccess();

    void setTransactionSuccess(Boolean transactionSuccess);

    Date getTransactionTimestamp();

    void setTransactionTimestamp(Date transactionTimestamp);

    String getImplementorResponseCode();

    void setImplementorResponseCode(String implementorResponseCode);

    String getImplementorResponseText();

    void setImplementorResponseText(String implementorResponseText);

    String getTransactionId();

    void setTransactionId(String transactionId);

    String getAvsCode();

    void setAvsCode(String avsCode);

    // TODO: Rename to getRemainingTransactionAmount
    Money getRemainingBalance();

    void setRemainingBalance(Money remainingBalance);

    Map<String, String> getAdditionalFields();

    void setAdditionalFields(Map<String, String> additionalFields);

    String getUserName();

    void setUserName(String userName);

    Customer getCustomer();

    void setCustomer(Customer customer);

    PaymentTransaction getPaymentTransaction();

    void setPaymentTransaction(PaymentTransaction paymentTransaction);

}
