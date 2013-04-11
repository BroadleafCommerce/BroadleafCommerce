/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.payment.domain;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.payment.service.type.TransactionType;
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

    public String getReferenceNumber();

    public void setReferenceNumber(String referenceNumber);

    /**
     * @deprecated
     * @see #getTransactionAmount()
     */
    public Money getAmountPaid();

    /**
     * @deprecated  setTransactionAmount() instead.
     * @see #setTransactionAmount(org.broadleafcommerce.common.money.Money)
     */
    public void setAmountPaid(Money amount);

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

    public String getCvvCode();

    public void setCvvCode(String cvvCode);

    // TODO: Rename to getRemainingTransactionAmount
    public Money getRemainingBalance();

    public void setRemainingBalance(Money remainingBalance);

    public TransactionType getTransactionType();

    public void setTransactionType(TransactionType transactionType);

    public Map<String, String> getAdditionalFields();

    public void setAdditionalFields(Map<String, String> additionalFields);

    public Long getPaymentInfoId();

    public void setPaymentInfoId(Long paymentInfoId);

    public String getUserName();

    public void setUserName(String userName);

    public Customer getCustomer();

    public void setCustomer(Customer customer);

    public String getPaymentInfoReferenceNumber();

    public void setPaymentInfoReferenceNumber(String paymentInfoReferenceNumber);

    void setCurrency(BroadleafCurrency currency);

    BroadleafCurrency getCurrency();

}
