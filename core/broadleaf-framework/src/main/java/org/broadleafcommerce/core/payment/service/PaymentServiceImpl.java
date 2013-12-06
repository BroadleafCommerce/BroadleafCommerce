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


public class PaymentServiceImpl implements PaymentService {
    
    /*
    protected PaymentModule paymentModule;

    @Resource(name = "blOrderPaymentService")
    protected OrderPaymentService paymentInfoService;

    @Override
    public PaymentResponseItem authorize(PaymentContext paymentContext) throws PaymentException {
        logPaymentStartEvent(paymentContext, PaymentTransactionType.AUTHORIZE);
        PaymentResponseItem response = null;
        PaymentException paymentException = null;
        try {
            response = paymentModule.authorize(paymentContext);
        } catch (PaymentException e) {
            if (e instanceof PaymentProcessorException) {
                response = ((PaymentProcessorException) e).getPaymentResponseItem();
            }
            paymentException = e;
            throw e;
        } finally {
            logResponseItem(paymentContext, response, PaymentTransactionType.AUTHORIZE);
            logPaymentFinishEvent(paymentContext, PaymentTransactionType.AUTHORIZE, paymentException);
        }

        return response;
    }

    @Override
    public PaymentResponseItem authorizeAndDebit(PaymentContext paymentContext) throws PaymentException {
        logPaymentStartEvent(paymentContext, PaymentTransactionType.AUTHORIZE_AND_CAPTURE);
        PaymentResponseItem response = null;
        PaymentException paymentException = null;
        try {
            response = paymentModule.authorizeAndDebit(paymentContext);
        } catch (PaymentException e) {
            if (e instanceof PaymentProcessorException) {
                response = ((PaymentProcessorException) e).getPaymentResponseItem();
            }
            paymentException = e;
            throw e;
        } finally {
            logResponseItem(paymentContext, response, PaymentTransactionType.AUTHORIZE_AND_CAPTURE);
            logPaymentFinishEvent(paymentContext, PaymentTransactionType.AUTHORIZE_AND_CAPTURE, paymentException);
        }

        return response;
    }

    @Override
    public PaymentResponseItem credit(PaymentContext paymentContext) throws PaymentException {
        logPaymentStartEvent(paymentContext, PaymentTransactionType.REFUND);
        PaymentResponseItem response = null;
        PaymentException paymentException = null;
        try {
            response = paymentModule.credit(paymentContext);
        } catch (PaymentException e) {
            if (e instanceof PaymentProcessorException) {
                response = ((PaymentProcessorException) e).getPaymentResponseItem();
            }
            paymentException = e;
            throw e;
        } finally {
            logResponseItem(paymentContext, response, PaymentTransactionType.REFUND);
            logPaymentFinishEvent(paymentContext, PaymentTransactionType.REFUND, paymentException);
        }

        return response;
    }

    @Override
    public PaymentResponseItem debit(PaymentContext paymentContext) throws PaymentException {
        logPaymentStartEvent(paymentContext, PaymentTransactionType.CAPTURE);
        PaymentResponseItem response = null;
        PaymentException paymentException = null;
        try {
            response = paymentModule.debit(paymentContext);
        } catch (PaymentException e) {
            if (e instanceof PaymentProcessorException) {
                response = ((PaymentProcessorException) e).getPaymentResponseItem();
            }
            paymentException = e;
            throw e;
        } finally {
            logResponseItem(paymentContext, response, PaymentTransactionType.CAPTURE);
            logPaymentFinishEvent(paymentContext, PaymentTransactionType.CAPTURE, paymentException);
        }

        return response;
    }

    @Override
    public PaymentResponseItem voidPayment(PaymentContext paymentContext) throws PaymentException {
        logPaymentStartEvent(paymentContext, PaymentTransactionType.VOID);
        PaymentResponseItem response = null;
        PaymentException paymentException = null;
        try {
            response = paymentModule.voidPayment(paymentContext);
        } catch (PaymentException e) {
            if (e instanceof PaymentProcessorException) {
                response = ((PaymentProcessorException) e).getPaymentResponseItem();
            }
            paymentException = e;
            throw e;
        } finally {
            logResponseItem(paymentContext, response, PaymentTransactionType.VOID);
            logPaymentFinishEvent(paymentContext, PaymentTransactionType.VOID, paymentException);
        }

        return response;
    }
    
    @Override
    public PaymentResponseItem reverseAuthorize(PaymentContext paymentContext) throws PaymentException {
        logPaymentStartEvent(paymentContext, PaymentTransactionType.REVERSE_AUTH);
        PaymentResponseItem response = null;
        PaymentException paymentException = null;
        try {
            response = paymentModule.reverseAuthorize(paymentContext);
        } catch (PaymentException e) {
            if (e instanceof PaymentProcessorException) {
                response = ((PaymentProcessorException) e).getPaymentResponseItem();
            }
            paymentException = e;
            throw e;
        } finally {
            logResponseItem(paymentContext, response, PaymentTransactionType.REVERSE_AUTH);
            logPaymentFinishEvent(paymentContext, PaymentTransactionType.REVERSE_AUTH, paymentException);
        }

        return response;
    }

    @Override
    public Boolean isValidCandidate(PaymentType paymentType) {
        return paymentModule.isValidCandidate(paymentType);
    }

    protected void logResponseItem(PaymentContext paymentContext, PaymentResponseItem response, PaymentTransactionType transactionType) {
        if (response != null) {
            response.setTransactionType(transactionType);
            response.setUserName(paymentContext.getUserName());
            OrderPayment info = paymentContext.getPaymentInfo();
            if (info != null) {
                response.setPaymentInfoId(info.getId());
                if (info.getOrder() != null && info.getOrder().getCustomer() != null) {
                    response.setCustomer(info.getOrder().getCustomer());
                }
                response.setPaymentInfoReferenceNumber(info.getReferenceNumber());
            }
            paymentInfoService.save(response);
        }
    }

    protected void logPaymentStartEvent(PaymentContext paymentContext, PaymentTransactionType transactionType) {
        PaymentLog log = paymentInfoService.createLog();
        log.setLogType(PaymentLogEventType.START);
        log.setTransactionTimestamp(SystemTime.asDate());
        log.setTransactionSuccess(Boolean.TRUE);
        log.setTransactionType(transactionType);
        log.setUserName(paymentContext.getUserName());
        log.setExceptionMessage(null);

        OrderPayment info = paymentContext.getPaymentInfo();
        if (info != null) {
            log.setCustomer(info.getOrder().getCustomer());
            log.setPaymentInfoReferenceNumber(info.getReferenceNumber());
            log.setAmountPaid(info.getAmount());
            log.setCurrency(info.getOrder().getCurrency());
            log.setPaymentInfoId(info.getId());
        }
        paymentInfoService.save(log);
    }

    protected void logPaymentFinishEvent(PaymentContext paymentContext, PaymentTransactionType transactionType, Exception e) {
        PaymentLog log = paymentInfoService.createLog();
        log.setLogType(PaymentLogEventType.FINISHED);
        log.setTransactionTimestamp(SystemTime.asDate());
        log.setTransactionSuccess(e == null ? Boolean.TRUE : Boolean.FALSE);
        log.setTransactionType(transactionType);
        log.setUserName(paymentContext.getUserName());
        String exceptionMessage;
        if (e != null) {
            exceptionMessage = e.getMessage();
            if (exceptionMessage != null) {
                if (exceptionMessage.length() >= 255) {
                    exceptionMessage = exceptionMessage.substring(0, 254);
                }
            } else {
                exceptionMessage = e.getClass().getName();
            }
        } else {
            exceptionMessage = null;
        }
        log.setExceptionMessage(exceptionMessage);

        OrderPayment info = paymentContext.getPaymentInfo();
        if (info != null) {
            log.setCustomer(info.getOrder().getCustomer());
            log.setPaymentInfoReferenceNumber(info.getReferenceNumber());
            log.setAmountPaid(info.getAmount());
            log.setCurrency(info.getOrder().getCurrency());
            log.setPaymentInfoId(info.getId());
        }
        paymentInfoService.save(log);
    }
    */
}
