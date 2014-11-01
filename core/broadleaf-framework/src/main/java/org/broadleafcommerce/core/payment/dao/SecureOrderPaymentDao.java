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
package org.broadleafcommerce.core.payment.dao;

import org.broadleafcommerce.core.payment.domain.secure.BankAccountPayment;
import org.broadleafcommerce.core.payment.domain.secure.CreditCardPayment;
import org.broadleafcommerce.core.payment.domain.secure.GiftCardPayment;
import org.broadleafcommerce.core.payment.domain.secure.Referenced;

public interface SecureOrderPaymentDao {

    public BankAccountPayment findBankAccountPayment(String referenceNumber);

    public CreditCardPayment findCreditCardPayment(String referenceNumber);

    public GiftCardPayment findGiftCardPayment(String referenceNumber);

    public Referenced save(Referenced securePaymentInfo);

    public BankAccountPayment createBankAccountPayment();

    public GiftCardPayment createGiftCardPayment();

    public CreditCardPayment createCreditCardPayment();

    public void delete(Referenced securePayment);

}
