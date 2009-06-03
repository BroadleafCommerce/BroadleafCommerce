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

import java.util.Date;

import javax.annotation.Resource;

import org.broadleafcommerce.payment.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.PaymentLog;
import org.broadleafcommerce.payment.domain.PaymentResponseItem;
import org.broadleafcommerce.payment.service.exception.PaymentException;
import org.broadleafcommerce.payment.service.exception.PaymentProcessorException;
import org.broadleafcommerce.payment.service.module.PaymentModule;
import org.broadleafcommerce.payment.service.type.BLCPaymentLogEventType;
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
        logPaymentStartEvent(paymentContext, BLCTransactionType.AUTHORIZE);
        PaymentResponseItem response = null;
        try {
            response = paymentModule.authorize(paymentContext);
        } catch (PaymentException e) {
            if (e instanceof PaymentProcessorException) {
                response = ((PaymentProcessorException) e).getPaymentResponseItem();
            }
            throw e;
        } finally {
            logResponseItem(paymentContext, response, BLCTransactionType.AUTHORIZE);
        }

        return response;
    }

    public PaymentResponseItem authorizeAndDebit(PaymentContext paymentContext) throws PaymentException {
        logPaymentStartEvent(paymentContext, BLCTransactionType.AUTHORIZEANDDEBIT);
        PaymentResponseItem response = null;
        try {
            response = paymentModule.authorizeAndDebit(paymentContext);
        } catch (PaymentException e) {
            if (e instanceof PaymentProcessorException) {
                response = ((PaymentProcessorException) e).getPaymentResponseItem();
            }
            throw e;
        } finally {
            logResponseItem(paymentContext, response, BLCTransactionType.AUTHORIZEANDDEBIT);
        }

        return response;
    }

    public PaymentResponseItem balance(PaymentContext paymentContext) throws PaymentException {
        logPaymentStartEvent(paymentContext, BLCTransactionType.BALANCE);
        PaymentResponseItem response = null;
        try {
            response = paymentModule.balance(paymentContext);
        } catch (PaymentException e) {
            if (e instanceof PaymentProcessorException) {
                response = ((PaymentProcessorException) e).getPaymentResponseItem();
            }
            throw e;
        } finally {
            logResponseItem(paymentContext, response, BLCTransactionType.BALANCE);
        }

        return response;
    }

    public PaymentResponseItem credit(PaymentContext paymentContext) throws PaymentException {
        logPaymentStartEvent(paymentContext, BLCTransactionType.CREDIT);
        PaymentResponseItem response = null;
        try {
            response = paymentModule.credit(paymentContext);
        } catch (PaymentException e) {
            if (e instanceof PaymentProcessorException) {
                response = ((PaymentProcessorException) e).getPaymentResponseItem();
            }
            throw e;
        } finally {
            logResponseItem(paymentContext, response, BLCTransactionType.CREDIT);
        }

        return response;
    }

    public PaymentResponseItem debit(PaymentContext paymentContext) throws PaymentException {
        logPaymentStartEvent(paymentContext, BLCTransactionType.DEBIT);
        PaymentResponseItem response = null;
        try {
            response = paymentModule.debit(paymentContext);
        } catch (PaymentException e) {
            if (e instanceof PaymentProcessorException) {
                response = ((PaymentProcessorException) e).getPaymentResponseItem();
            }
            throw e;
        } finally {
            logResponseItem(paymentContext, response, BLCTransactionType.DEBIT);
        }

        return response;
    }

    public PaymentResponseItem voidPayment(PaymentContext paymentContext) throws PaymentException {
        logPaymentStartEvent(paymentContext, BLCTransactionType.VOIDPAYMENT);
        PaymentResponseItem response = null;
        try {
            response = paymentModule.voidPayment(paymentContext);
        } catch (PaymentException e) {
            if (e instanceof PaymentProcessorException) {
                response = ((PaymentProcessorException) e).getPaymentResponseItem();
            }
            throw e;
        } finally {
            logResponseItem(paymentContext, response, BLCTransactionType.VOIDPAYMENT);
        }

        return response;
    }

    @Override
    public Boolean isValidCandidate(String paymentType) {
        return paymentModule.isValidCandidate(paymentType);
    }

    protected void logResponseItem(PaymentContext paymentContext, PaymentResponseItem response, BLCTransactionType transactionType) {
        if (response != null) {
            PaymentInfo info = paymentContext.getPaymentInfo();
            response.setTransactionType(transactionType);
            response.setPaymentInfo(info);
            response.setCustomer(info.getOrder().getCustomer());
            response.setPaymentInfoReferenceNumber(info.getReferenceNumber());
            response.setUserName(paymentContext.getUserName());
            info.getPaymentResponseItems().add(response);
            paymentInfoService.save(info);
        }
    }

    protected void logPaymentStartEvent(PaymentContext paymentContext, BLCTransactionType transactionType) {
        PaymentInfo info = paymentContext.getPaymentInfo();
        PaymentLog log = paymentInfoService.createLog();
        log.setLogType(BLCPaymentLogEventType.START);
        log.setTransactionTimestamp(new Date());
        log.setTransactionSuccess(Boolean.TRUE);
        log.setTransactionType(transactionType);
        log.setCustomer(info.getOrder().getCustomer());
        log.setPaymentInfoReferenceNumber(info.getReferenceNumber());
        log.setUserName(paymentContext.getUserName());
        log.setExceptionMessage(null);
        log.setAmountPaid(info.getAmount());
        log.setPaymentInfo(info);
        info.getPaymentLogs().add(log);

        paymentInfoService.save(info);
    }
}
