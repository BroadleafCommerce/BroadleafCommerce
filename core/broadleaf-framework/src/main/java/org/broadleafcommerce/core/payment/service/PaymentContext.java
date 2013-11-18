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
package org.broadleafcommerce.core.payment.service;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.Referenced;

public interface PaymentContext {

    /**
     * @deprecated
     * @see #getTransactionAmount()
     */
    public Money getOriginalPaymentAmount();

    /**
     * @deprecated
     * @see #getRemainingTransactionAmount()
     */
    public Money getRemainingPaymentAmount();

    /**
     * The amount that the system should attempt to process.   For example, when submitting an order, this would be the order.getTotal.
     * If refunding $10, this would be 10.
     *
     * @return
     */
    public Money getTransactionAmount();

    /**
     * Sets the transaction amount
     *
     * @param amount
     */
    public void setTransactionAmount(Money amount);

    /**
     * Returns the remaining transaction amount that needs to be processed.   When using multiple forms of payment, each payment module will
     * attempt to perform the operation if they are able to up to this amount.
     *
     * @return
     */
    public Money getRemainingTransactionAmount();

    /**
     * Sets the remaining transaction amount.
     *
     * @param amount
     */
    public void setRemainingTransactionAmount(Money amount);

    public PaymentInfo getPaymentInfo();

    public Referenced getReferencedPaymentInfo();

    public String getTransactionId();

    public void setTransactionId(String transactionId);

    public String getUserName() ;
    
}
