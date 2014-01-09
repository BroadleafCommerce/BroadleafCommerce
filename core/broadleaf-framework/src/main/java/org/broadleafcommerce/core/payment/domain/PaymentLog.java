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
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.payment.PaymentLogEventType;
import org.broadleafcommerce.common.payment.PaymentTransactionType;
import org.broadleafcommerce.profile.core.domain.Customer;

import java.io.Serializable;
import java.util.Date;

public interface PaymentLog extends Serializable {

    public Long getId();

    public void setId(Long id);

    public String getUserName();

    public void setUserName(String userName);

    public Date getTransactionTimestamp();

    public void setTransactionTimestamp(Date transactionTimestamp);

    public Long getPaymentInfoId();

    public void setPaymentInfoId(Long paymentInfoId);

    public Customer getCustomer();

    public void setCustomer(Customer customer);

    public String getPaymentInfoReferenceNumber();

    public void setPaymentInfoReferenceNumber(String paymentInfoReferenceNumber);

    public PaymentTransactionType getTransactionType();

    public void setTransactionType(PaymentTransactionType transactionType);

    public Boolean getTransactionSuccess();

    public void setTransactionSuccess(Boolean transactionSuccess);

    public String getExceptionMessage();

    public void setExceptionMessage(String exceptionMessage);

    public PaymentLogEventType getLogType();

    public void setLogType(PaymentLogEventType logType);

    public Money getAmountPaid();

    public void setAmountPaid(Money amountPaid);

    void setCurrency(BroadleafCurrency currency);

    BroadleafCurrency getCurrency();

}
