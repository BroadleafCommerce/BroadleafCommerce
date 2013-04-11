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

package org.broadleafcommerce.core.payment.service.module;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.payment.domain.PaymentResponseItem;
import org.broadleafcommerce.core.payment.service.PaymentContext;
import org.broadleafcommerce.core.payment.service.exception.PaymentException;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoType;

public class DefaultModule extends AbstractModule {

    @Override
    public PaymentResponseItem processAuthorize(PaymentContext paymentContext, Money amountToAuthorize, PaymentResponseItem responseItem) throws PaymentException {
        throw new PaymentException("authorize not implemented.");
    }

    @Override
    public PaymentResponseItem processReverseAuthorize(PaymentContext paymentContext, Money amountToReverseAuthorize, PaymentResponseItem responseItem) throws PaymentException {
        throw new PaymentException("reverse authorize not implemented.");
    }

    @Override
    public PaymentResponseItem processAuthorizeAndDebit(PaymentContext paymentContext, Money amountToReverseAuthorize, PaymentResponseItem responseItem) throws PaymentException {
        throw new PaymentException("authorizeAndDebit not implemented.");
    }

    @Override
    public PaymentResponseItem processDebit(PaymentContext paymentContext, Money amountToReverseAuthorize, PaymentResponseItem responseItem) throws PaymentException {
        throw new PaymentException("debit not implemented.");
    }

    @Override
    public PaymentResponseItem processCredit(PaymentContext paymentContext, Money amountToReverseAuthorize, PaymentResponseItem responseItem) throws PaymentException {
        throw new PaymentException("credit not implemented.");
    }

    @Override
    public PaymentResponseItem processVoidPayment(PaymentContext paymentContext, Money amountToReverseAuthorize, PaymentResponseItem responseItem) throws PaymentException {
        throw new PaymentException("voidPayment not implemented.");
    }

    @Override
    public PaymentResponseItem processBalance(PaymentContext paymentContext, PaymentResponseItem responseItem) throws PaymentException {
        throw new PaymentException("balance not implemented.");
    }

    @Override
    public PaymentResponseItem processPartialPayment(PaymentContext paymentContext, Money amountToDebit, PaymentResponseItem responseItem) throws PaymentException {
        throw new PaymentException("partial payment not implemented.");
    }

    @Override
    public Boolean isValidCandidate(PaymentInfoType paymentType) {
        return false;
    }

}
