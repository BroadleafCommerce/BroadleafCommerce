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
package org.broadleafcommerce.payment.service.module;

import org.broadleafcommerce.payment.domain.PaymentResponseItem;
import org.broadleafcommerce.payment.service.PaymentContext;
import org.broadleafcommerce.payment.service.exception.PaymentException;
import org.broadleafcommerce.payment.service.type.PaymentInfoType;

public class DefaultModule extends AbstractModule {

    public PaymentResponseItem authorize(PaymentContext paymentContext) throws PaymentException {
        throw new PaymentException("authorize not implemented.");
    }

    public PaymentResponseItem authorizeAndDebit(PaymentContext paymentContext) throws PaymentException {
        throw new PaymentException("authorizeAndDebit not implemented.");
    }

    public PaymentResponseItem debit(PaymentContext paymentContext) throws PaymentException {
        throw new PaymentException("debit not implemented.");
    }

    public PaymentResponseItem credit(PaymentContext paymentContext) throws PaymentException {
        throw new PaymentException("credit not implemented.");
    }

    public PaymentResponseItem voidPayment(PaymentContext paymentContext) throws PaymentException {
        throw new PaymentException("voidPayment not implemented.");
    }

    public PaymentResponseItem balance(PaymentContext paymentContext) throws PaymentException {
        throw new PaymentException("balance not implemented.");
    }

    public Boolean isValidCandidate(PaymentInfoType paymentType) {
        return false;
    }

}
