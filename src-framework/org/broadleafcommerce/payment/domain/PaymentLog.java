/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.payment.domain;

import java.util.Date;

import org.broadleafcommerce.payment.service.type.BLCPaymentLogEventType;
import org.broadleafcommerce.payment.service.type.BLCTransactionType;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.util.money.Money;

public interface PaymentLog {

    public Long getId();

    public void setId(Long id);

    public String getUserName();

    public void setUserName(String userName);

    public Date getTransactionTimestamp();

    public void setTransactionTimestamp(Date transactionTimestamp);

    public PaymentInfo getPaymentInfo();

    public void setPaymentInfo(PaymentInfo paymentInfo);

    public Customer getCustomer();

    public void setCustomer(Customer customer);

    public String getPaymentInfoReferenceNumber();

    public void setPaymentInfoReferenceNumber(String paymentInfoReferenceNumber);

    public BLCTransactionType getTransactionType();

    public void setTransactionType(BLCTransactionType transactionType);

    public Boolean getTransactionSuccess();

    public void setTransactionSuccess(Boolean transactionSuccess);

    public String getExceptionMessage();

    public void setExceptionMessage(String exceptionMessage);

    public BLCPaymentLogEventType getLogType();

    public void setLogType(BLCPaymentLogEventType logType);

    public Money getAmountPaid();

    public void setAmountPaid(Money amountPaid);

}