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
package org.broadleafcommerce.payment.service;

import javax.annotation.Resource;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.order.domain.PaymentResponseItem;
import org.broadleafcommerce.order.service.PaymentInfoService;
import org.broadleafcommerce.payment.service.exception.PaymentException;
import org.broadleafcommerce.payment.service.module.PaymentModule;
import org.broadleafcommerce.payment.service.type.BLCTransactionType;

public class PaymentServiceImpl implements PaymentService {

    protected PaymentModule paymentModule;

    @Resource
    protected PaymentInfoService paymentInfoService;

    public PaymentModule getPaymentModule() {
        return paymentModule;
    }

    public void setPaymentModule(PaymentModule paymentModule) {
        this.paymentModule = paymentModule;
    }

    public PaymentResponseItem authorize(PaymentContext paymentContext) throws PaymentException  {
        PaymentInfo info = paymentContext.getPaymentInfo();
        PaymentResponseItem response = paymentModule.authorize(paymentContext);
        response.setTransactionType(BLCTransactionType.AUTHORIZE);
        response.setPaymentInfo(info);
        response.setUserName(paymentContext.getUserName());
        response.setCustomer(info.getOrder().getCustomer());
        response.setPaymentInfoReferenceNumber(info.getReferenceNumber());
        info.getPaymentResponseItems().add(response);

        paymentInfoService.save(info);

        return response;
    }

    public PaymentResponseItem authorizeAndDebit(PaymentContext paymentContext) throws PaymentException {
        PaymentInfo info = paymentContext.getPaymentInfo();
        PaymentResponseItem response = paymentModule.authorizeAndDebit(paymentContext);
        response.setTransactionType(BLCTransactionType.AUTHORIZEANDDEBIT);
        response.setPaymentInfo(info);
        response.setUserName(paymentContext.getUserName());
        response.setCustomer(info.getOrder().getCustomer());
        response.setPaymentInfoReferenceNumber(info.getReferenceNumber());
        info.getPaymentResponseItems().add(response);

        paymentInfoService.save(info);

        return response;
    }

    public PaymentResponseItem balance(PaymentContext paymentContext) throws PaymentException {
        PaymentInfo info = paymentContext.getPaymentInfo();
        PaymentResponseItem response = paymentModule.balance(paymentContext);
        response.setTransactionType(BLCTransactionType.BALANCE);
        response.setPaymentInfo(info);
        response.setUserName(paymentContext.getUserName());
        response.setCustomer(info.getOrder().getCustomer());
        response.setPaymentInfoReferenceNumber(info.getReferenceNumber());
        info.getPaymentResponseItems().add(response);

        paymentInfoService.save(info);

        return response;
    }

    public PaymentResponseItem credit(PaymentContext paymentContext) throws PaymentException {
        PaymentInfo info = paymentContext.getPaymentInfo();
        PaymentResponseItem response = paymentModule.credit(paymentContext);
        response.setTransactionType(BLCTransactionType.CREDIT);
        response.setPaymentInfo(info);
        response.setUserName(paymentContext.getUserName());
        response.setCustomer(info.getOrder().getCustomer());
        response.setPaymentInfoReferenceNumber(info.getReferenceNumber());
        info.getPaymentResponseItems().add(response);

        paymentInfoService.save(info);

        return response;
    }

    public PaymentResponseItem debit(PaymentContext paymentContext) throws PaymentException {
        PaymentInfo info = paymentContext.getPaymentInfo();
        PaymentResponseItem response = paymentModule.debit(paymentContext);
        response.setTransactionType(BLCTransactionType.DEBIT);
        response.setPaymentInfo(info);
        response.setUserName(paymentContext.getUserName());
        response.setCustomer(info.getOrder().getCustomer());
        response.setPaymentInfoReferenceNumber(info.getReferenceNumber());
        info.getPaymentResponseItems().add(response);

        paymentInfoService.save(info);

        return response;
    }

    public PaymentResponseItem voidPayment(PaymentContext paymentContext) throws PaymentException {
        PaymentInfo info = paymentContext.getPaymentInfo();
        PaymentResponseItem response = paymentModule.voidPayment(paymentContext);
        response.setTransactionType(BLCTransactionType.VOIDPAYMENT);
        response.setPaymentInfo(info);
        response.setUserName(paymentContext.getUserName());
        response.setCustomer(info.getOrder().getCustomer());
        response.setPaymentInfoReferenceNumber(info.getReferenceNumber());
        info.getPaymentResponseItems().add(response);

        paymentInfoService.save(info);

        return response;
    }

    @Override
    public Boolean isValidCandidate(String paymentType) {
        return paymentModule.isValidCandidate(paymentType);
    }

}
