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
package org.broadleafcommerce.vendor.cybersource.service;

import java.math.BigInteger;

import org.broadleafcommerce.util.money.Money;
import org.broadleafcommerce.vendor.cybersource.service.api.CCAuthReply;
import org.broadleafcommerce.vendor.cybersource.service.api.CCAuthService;
import org.broadleafcommerce.vendor.cybersource.service.api.Card;
import org.broadleafcommerce.vendor.cybersource.service.api.ReplyMessage;
import org.broadleafcommerce.vendor.cybersource.service.api.RequestMessage;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceAuthResponse;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceCardRequest;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceCardResponse;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourcePaymentRequest;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourcePaymentResponse;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceRequest;
import org.broadleafcommerce.vendor.cybersource.service.type.CyberSourceMethodType;
import org.broadleafcommerce.vendor.cybersource.service.type.CyberSourceServiceType;
import org.broadleafcommerce.vendor.cybersource.service.type.CyberSourceTransactionType;
import org.broadleafcommerce.vendor.service.exception.PaymentException;
import org.broadleafcommerce.vendor.service.exception.PaymentHostException;

public class CyberSourceCreditCardPaymentServiceImpl extends AbstractCyberSourcePaymentService implements CyberSourcePaymentService {

	public CyberSourcePaymentResponse process(CyberSourcePaymentRequest paymentRequest) throws PaymentException {
		//TODO add validation for the request
		CyberSourcePaymentResponse paymentResponse = new CyberSourcePaymentResponse();
		paymentResponse.setServiceType(paymentRequest.getServiceType());
		paymentResponse.setTransactionType(paymentRequest.getTransactionType());
		paymentResponse.setVenueType(paymentRequest.getMethodType());
		RequestMessage request = buildRequestMessage(paymentRequest);
		ReplyMessage reply;
		try {
			reply = sendRequest(request);
        } catch (Exception e) {
            incrementFailure();
            throw new PaymentException(e);
        }
        clearStatus();
        String[] invalidFields = reply.getInvalidField();
        String[] missingFields = reply.getMissingField();
        if ((invalidFields != null && invalidFields.length > 0) || (missingFields != null && missingFields.length > 0)) {
            PaymentHostException e = new PaymentHostException();
            paymentResponse.setErrorDetected(true);
            StringBuffer sb = new StringBuffer();
            sb.append("invalid fields :[ ");
            for (String invalidField : invalidFields) {
            	sb.append(invalidField);
            }
            sb.append(" ]\nmissing fields: [ ");
            for (String missingField : missingFields) {
            	sb.append(missingField);
            }
            sb.append(" ]");
            paymentResponse.setErrorText(sb.toString());
            e.setPaymentResponse(paymentResponse);
            throw e;
        }
        buildResponse(paymentResponse, reply);
        
        return paymentResponse;
	}
	
	protected void buildResponse(CyberSourcePaymentResponse paymentResponse, ReplyMessage reply) {
		if (CyberSourceTransactionType.AUTHORIZE.equals(paymentResponse.getTransactionType())) {
			CCAuthReply authReply = reply.getCcAuthReply();
			CyberSourceAuthResponse authResponse = new CyberSourceAuthResponse();
			authResponse.setAccountBalance(new Money(authReply.getAccountBalance()));
			authResponse.setAmount(new Money(authReply.getAmount()));
			authResponse.setApprovedAmount(new Money(authReply.getApprovedAmount()));
			authResponse.setApprovedTerms(authReply.getApprovedTerms());
			authResponse.setAuthenticationXID(authReply.getAuthenticationXID());
			authResponse.setAuthFactorCode(authReply.getAuthFactorCode());
			authResponse.setAuthorizationXID(authReply.getAuthorizationXID());
			authResponse.setAuthorizedDateTime(authReply.getAuthorizedDateTime());
			authResponse.setAuthRecord(authReply.getAuthRecord());
			authResponse.setAvsCode(authReply.getAvsCode());
			authResponse.setAvsCodeRaw(authReply.getAvsCodeRaw());
			authResponse.setBmlAccountNumber(authReply.getBmlAccountNumber());
			authResponse.setCardCategory(authReply.getCardCategory());
			authResponse.setCavvResponseCode(authReply.getCavvResponseCode());
			authResponse.setCavvResponseCodeRaw(authReply.getCavvResponseCodeRaw());
			authResponse.setCreditLine(authReply.getCreditLine());
			authResponse.setCvCode(authReply.getCvCode());
			authResponse.setCvCodeRaw(authReply.getCvCodeRaw());
			authResponse.setEnhancedDataEnabled(authReply.getEnhancedDataEnabled());
			authResponse.setForwardCode(authReply.getForwardCode());
			authResponse.setMerchantAdviceCode(authReply.getMerchantAdviceCode());
			authResponse.setMerchantAdviceCodeRaw(authReply.getMerchantAdviceCodeRaw());
			authResponse.setPaymentNetworkTransactionID(authReply.getPaymentNetworkTransactionID());
			authResponse.setPersonalIDCode(authReply.getPersonalIDCode());
			authResponse.setProcessorCardType(authReply.getProcessorCardType());
			authResponse.setProcessorResponse(authReply.getProcessorResponse());
			authResponse.setReasonCode(authReply.getReasonCode());
			authResponse.setReconciliationID(authReply.getReconciliationID());
			authResponse.setReferralResponseNumber(authReply.getReferralResponseNumber());
			authResponse.setSubResponseCode(authReply.getSubResponseCode());
			
			((CyberSourceCardResponse) paymentResponse).setAuthResponse(authResponse);
		}
	}
	
	protected RequestMessage buildRequestMessage(CyberSourcePaymentRequest paymentRequest) {
		RequestMessage request = super.buildRequestMessage(paymentRequest);
		setCardInformation(paymentRequest, request);
		if (CyberSourceTransactionType.AUTHORIZE.equals(paymentRequest.getTransactionType())) {
			request.setCcAuthService(new CCAuthService());
	        request.getCcAuthService().setRun("true");
		}
		
		return request;
	}

	protected void setCardInformation(CyberSourcePaymentRequest paymentRequest, RequestMessage request) {
		CyberSourceCardRequest cardRequest = (CyberSourceCardRequest) paymentRequest;
		Card card = new Card();
		card.setAccountNumber(cardRequest.getAccountNumber());
		card.setBin(cardRequest.getBin());
		card.setCardType(cardRequest.getCardType());
		card.setCvIndicator(cardRequest.getCvIndicator());
		card.setCvNumber(cardRequest.getCvNumber());
		if (cardRequest.getExpirationMonth() != null) {
			card.setExpirationMonth(new BigInteger(String.valueOf(cardRequest.getExpirationMonth())));
		}
		if (cardRequest.getExpirationYear() != null) {
			card.setExpirationYear(new BigInteger(String.valueOf(cardRequest.getExpirationYear())));
		}
		card.setFullName(cardRequest.getFullName());
		card.setIssueNumber(cardRequest.getIssueNumber());
		card.setPin(cardRequest.getPin());
		if (cardRequest.getStartMonth() != null) {
			card.setStartMonth(new BigInteger(String.valueOf(cardRequest.getStartMonth())));
		}
		if (cardRequest.getStartYear() != null) {
			card.setStartYear(new BigInteger(String.valueOf(cardRequest.getStartYear())));
		}
		
        request.setCard(card);
	}

	public boolean isValidService(CyberSourceRequest request) {
		return CyberSourceServiceType.PAYMENT.equals(request.getServiceType()) && CyberSourceMethodType.CREDITCARD.equals(request.getMethodType());
	}
	
}
