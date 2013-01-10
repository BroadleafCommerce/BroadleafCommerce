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

import java.util.Currency;

import org.broadleafcommerce.core.payment.domain.CreditCardPaymentInfo;
import org.broadleafcommerce.core.payment.domain.PaymentResponseItem;
import org.broadleafcommerce.core.payment.domain.PaymentResponseItemImpl;
import org.broadleafcommerce.core.payment.service.PaymentContext;
import org.broadleafcommerce.core.payment.service.exception.PaymentException;
import org.broadleafcommerce.core.payment.service.module.PaymentModule;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoType;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.service.module.CyberSourceModule;
import org.broadleafcommerce.vendor.cybersource.service.CyberSourceServiceManager;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceBillingRequest;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceItemRequest;
import org.broadleafcommerce.vendor.cybersource.service.payment.CyberSourcePaymentService;
import org.broadleafcommerce.vendor.cybersource.service.payment.message.CyberSourceCardRequest;
import org.broadleafcommerce.vendor.cybersource.service.payment.message.CyberSourceCardResponse;
import org.broadleafcommerce.vendor.cybersource.service.payment.type.CyberSourceTransactionType;

/**
 * 
 * @author jfischer
 *
 */
public class CyberSourceCreditCardModule extends CyberSourceModule implements PaymentModule {
    
    private CyberSourceServiceManager serviceManager;

    public PaymentResponseItem authorize(PaymentContext paymentContext) throws PaymentException {
        return authTypeTransaction(paymentContext, CyberSourceTransactionType.AUTHORIZE);
    }
    
    public PaymentResponseItem authorizeAndDebit(PaymentContext paymentContext) throws PaymentException {
        return authTypeTransaction(paymentContext, CyberSourceTransactionType.AUTHORIZEANDCAPTURE);
    }
    
    private PaymentResponseItem authTypeTransaction(PaymentContext paymentContext, CyberSourceTransactionType transactionType) throws PaymentException {
        CyberSourceCardRequest cardRequest = new CyberSourceCardRequest();
        setCardInfo(paymentContext, cardRequest);
        cardRequest.setTransactionType(transactionType);
        setCurrency(paymentContext, cardRequest);
        
        CyberSourceBillingRequest billingRequest = createBillingRequest(paymentContext.getPaymentInfo());
        cardRequest.setBillingRequest(billingRequest);
        
        CyberSourceItemRequest itemRequest = createItemRequest(paymentContext);
        cardRequest.getItemRequests().add(itemRequest);

        CyberSourceCardResponse response = callService(cardRequest);
        
        PaymentResponseItem responseItem = buildBasicResponse(response);
        responseItem.setAvsCode(response.getAuthResponse().getAvsCode());
        responseItem.setAuthorizationCode(response.getAuthResponse().getAuthorizationCode());
        responseItem.setAmountPaid(response.getAuthResponse().getAmount());
        responseItem.setProcessorResponseCode(response.getAuthResponse().getProcessorResponse());
        responseItem.setProcessorResponseText(response.getAuthResponse().getProcessorResponse());
        
        return responseItem;
    }

    public PaymentResponseItem balance(PaymentContext paymentContext) throws PaymentException {
        throw new PaymentException("balance not supported");
    }

    public PaymentResponseItem credit(PaymentContext paymentContext) throws PaymentException {
        CyberSourceCardRequest cardRequest = new CyberSourceCardRequest();
        cardRequest.setTransactionType(CyberSourceTransactionType.CREDIT);
        setCurrency(paymentContext, cardRequest);
        
        CyberSourceItemRequest itemRequest = createItemRequest(paymentContext);
        cardRequest.getItemRequests().add(itemRequest);
        
        cardRequest.setRequestID(paymentContext.getPaymentInfo().getAdditionalFields().get("requestId"));
        cardRequest.setRequestToken(paymentContext.getPaymentInfo().getAdditionalFields().get("requestToken"));
        
        CyberSourceCardResponse response = callService(cardRequest);
        
        PaymentResponseItem responseItem = buildBasicResponse(response);
        responseItem.setAmountPaid(response.getCreditResponse().getAmount());
        
        return responseItem;
    }

    public PaymentResponseItem debit(PaymentContext paymentContext) throws PaymentException {
        CyberSourceCardRequest cardRequest = new CyberSourceCardRequest();
        cardRequest.setTransactionType(CyberSourceTransactionType.CAPTURE);
        setCurrency(paymentContext, cardRequest);
        
        CyberSourceItemRequest itemRequest = createItemRequest(paymentContext);
        cardRequest.getItemRequests().add(itemRequest);
        
        cardRequest.setRequestID(paymentContext.getPaymentInfo().getAdditionalFields().get("requestId"));
        cardRequest.setRequestToken(paymentContext.getPaymentInfo().getAdditionalFields().get("requestToken"));
        
        CyberSourceCardResponse response = callService(cardRequest);
        
        PaymentResponseItem responseItem = buildBasicResponse(response);
        responseItem.setAmountPaid(response.getCaptureResponse().getAmount());
        
        return responseItem;
    }
    
    public PaymentResponseItem reverseAuthorize(PaymentContext paymentContext) throws PaymentException {
        CyberSourceCardRequest cardRequest = new CyberSourceCardRequest();
        cardRequest.setTransactionType(CyberSourceTransactionType.REVERSEAUTHORIZE);
        setCurrency(paymentContext, cardRequest);
        
        CyberSourceItemRequest itemRequest = createItemRequest(paymentContext);
        cardRequest.getItemRequests().add(itemRequest);
        
        cardRequest.setRequestID(paymentContext.getPaymentInfo().getAdditionalFields().get("requestId"));
        cardRequest.setRequestToken(paymentContext.getPaymentInfo().getAdditionalFields().get("requestToken"));
        
        CyberSourceCardResponse response = callService(cardRequest);
        
        PaymentResponseItem responseItem = buildBasicResponse(response);
        responseItem.setAmountPaid(response.getAuthReverseResponse().getAmount());
        responseItem.setAuthorizationCode(response.getAuthReverseResponse().getAuthorizationCode());
        responseItem.setProcessorResponseCode(response.getAuthReverseResponse().getProcessorResponse());
        responseItem.setProcessorResponseText(response.getAuthReverseResponse().getProcessorResponse());
        
        return responseItem;
    }

    public PaymentResponseItem voidPayment(PaymentContext paymentContext) throws PaymentException {
        CyberSourceCardRequest cardRequest = new CyberSourceCardRequest();
        cardRequest.setTransactionType(CyberSourceTransactionType.VOIDTRANSACTION);
        setCurrency(paymentContext, cardRequest);
        
        CyberSourceItemRequest itemRequest = createItemRequest(paymentContext);
        cardRequest.getItemRequests().add(itemRequest);
        
        cardRequest.setRequestID(paymentContext.getPaymentInfo().getAdditionalFields().get("requestId"));
        cardRequest.setRequestToken(paymentContext.getPaymentInfo().getAdditionalFields().get("requestToken"));
        
        CyberSourceCardResponse response = callService(cardRequest);
        
        PaymentResponseItem responseItem = buildBasicResponse(response);
        responseItem.setAmountPaid(response.getVoidResponse().getAmount());
        
        return responseItem;
    }

    public Boolean isValidCandidate(PaymentInfoType paymentType) {
        return PaymentInfoType.CREDIT_CARD.equals(paymentType);
    }
    
    private CyberSourceCardResponse callService(CyberSourceCardRequest cardRequest) throws PaymentException {
        CyberSourcePaymentService service = (CyberSourcePaymentService) serviceManager.getValidService(cardRequest);
        CyberSourceCardResponse response;
        try {
            response = (CyberSourceCardResponse) service.process(cardRequest);
        } catch (org.broadleafcommerce.common.vendor.service.exception.PaymentException e) {
            throw new PaymentException(e);
        }
        
        return response;
    }
    
    private PaymentResponseItem buildBasicResponse(CyberSourceCardResponse response) {
        PaymentResponseItem responseItem = new PaymentResponseItemImpl();
        responseItem.setTransactionTimestamp(SystemTime.asDate());
        responseItem.setMiddlewareResponseCode(response.getReasonCode().toString());
        responseItem.setMiddlewareResponseText(response.getDecision());
        responseItem.setReferenceNumber(response.getMerchantReferenceCode());
        responseItem.setTransactionId(response.getRequestToken());
        responseItem.setTransactionSuccess(response.getReasonCode().intValue() == 100);
        responseItem.getAdditionalFields().put("requestId", response.getRequestID());
        responseItem.getAdditionalFields().put("requestToken", response.getRequestToken());
        
        return responseItem;
    }
    
    private void setCardInfo(PaymentContext paymentContext, CyberSourceCardRequest cardRequest) {
        CreditCardPaymentInfo ccInfo = (CreditCardPaymentInfo) paymentContext.getReferencedPaymentInfo();
        cardRequest.setAccountNumber(ccInfo.getPan());
        cardRequest.setExpirationMonth(ccInfo.getExpirationMonth());
        cardRequest.setExpirationYear(ccInfo.getExpirationYear());
        cardRequest.setCvNumber(ccInfo.getCvvCode());
    }
    
    private void setCurrency(PaymentContext paymentContext, CyberSourceCardRequest cardRequest) {
        Currency currency = paymentContext.getPaymentInfo().getAmount().getCurrency();
        if (currency == null) {
            currency = Money.defaultCurrency();
        }
        cardRequest.setCurrency(currency.getCurrencyCode());
    }
    
    private CyberSourceItemRequest createItemRequest(PaymentContext paymentContext) {
        CyberSourceItemRequest itemRequest = new CyberSourceItemRequest();
        itemRequest.setDescription("Order Charge");
        itemRequest.setQuantity(1L);
        itemRequest.setShortDescription("Order Charge");
        itemRequest.setUnitPrice(paymentContext.getPaymentInfo().getAmount());
        
        return itemRequest;
    }

    public CyberSourceServiceManager getServiceManager() {
        return serviceManager;
    }

    public void setServiceManager(CyberSourceServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

}
