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
package org.broadleafcommerce.core.payment.service.module;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.payment.domain.PaymentResponseItem;
import org.broadleafcommerce.core.payment.service.PaymentContext;
import org.broadleafcommerce.core.payment.service.exception.PaymentException;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoType;
import org.springframework.beans.factory.annotation.Required;

/**
 * This payment module can be utilized when you wish to accept an order's payment without acting on it.
 * For example, if the customer is going to pay by check and mail it to you, you will want to accept 
 * the order and not perform any actual payment transaction.
 *
 * In order to instantiate this module, you must inject a valid PaymentInfoType for which this module will be executed.
 * If you are associating this module with several payment types, you should declare this bean as <code>prototype</code>
 * For example:
 *
 * <code>
 *  <bean id="blCheckPaymentModule" class="org.broadleafcommerce.core.payment.service.module.AcceptAndPassthroughModule" scope="prototype">
 *      <property name="validPaymentInfoType" value="CHECK"/>
 *  </bean>
 *  <bean id="blWirePaymentModule" class="org.broadleafcommerce.core.payment.service.module.AcceptAndPassthroughModule" scope="prototype">
 *      <property name="validPaymentInfoType" value="WIRE"/>
 *  </bean>
 * </code>
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class AcceptAndPassthroughModule extends AbstractModule {

    private PaymentInfoType validPaymentInfoType = null;

    @Required
    public void setValidPaymentInfoType(String validType) {
        validPaymentInfoType = PaymentInfoType.getInstance(validType);
    }

    @Override
    public PaymentResponseItem processAuthorize(PaymentContext paymentContext, Money amountToAuthorize, PaymentResponseItem responseItem) throws PaymentException {
        throw new PaymentException("authorize not implemented.");
    }

    @Override
    public PaymentResponseItem processReverseAuthorize(PaymentContext paymentContext, Money amountToReverseAuthorize, PaymentResponseItem responseItem) throws PaymentException {
        throw new PaymentException("reverse authorize not implemented.");
    }

    @Override
    public PaymentResponseItem processAuthorizeAndDebit(PaymentContext paymentContext, Money amountToDebit, PaymentResponseItem responseItem) throws PaymentException {
        responseItem.setTransactionSuccess(true);
        responseItem.setTransactionAmount(amountToDebit);
        return responseItem;
    }

    @Override
    public PaymentResponseItem processDebit(PaymentContext paymentContext, Money amountToDebit, PaymentResponseItem responseItem) throws PaymentException {
        throw new PaymentException("debit not implemented.");
    }

    @Override
    public PaymentResponseItem processCredit(PaymentContext paymentContext, Money amountToCredit, PaymentResponseItem responseItem) throws PaymentException {
        throw new PaymentException("credit not implemented.");
    }

    @Override
    public PaymentResponseItem processVoidPayment(PaymentContext paymentContext, Money amountToVoid, PaymentResponseItem responseItem) throws PaymentException {
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
        return validPaymentInfoType.equals(paymentType);
    }
}
