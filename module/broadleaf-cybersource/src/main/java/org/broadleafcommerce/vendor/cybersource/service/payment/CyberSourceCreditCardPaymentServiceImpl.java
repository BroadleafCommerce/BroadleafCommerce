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

package org.broadleafcommerce.vendor.cybersource.service.payment;

import java.math.BigInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;
import org.broadleafcommerce.common.vendor.service.exception.PaymentHostException;
import org.broadleafcommerce.vendor.cybersource.service.api.BillTo;
import org.broadleafcommerce.vendor.cybersource.service.api.CCAuthReply;
import org.broadleafcommerce.vendor.cybersource.service.api.CCAuthReversalReply;
import org.broadleafcommerce.vendor.cybersource.service.api.CCAuthReversalService;
import org.broadleafcommerce.vendor.cybersource.service.api.CCAuthService;
import org.broadleafcommerce.vendor.cybersource.service.api.CCCaptureReply;
import org.broadleafcommerce.vendor.cybersource.service.api.CCCaptureService;
import org.broadleafcommerce.vendor.cybersource.service.api.CCCreditReply;
import org.broadleafcommerce.vendor.cybersource.service.api.CCCreditService;
import org.broadleafcommerce.vendor.cybersource.service.api.Card;
import org.broadleafcommerce.vendor.cybersource.service.api.Item;
import org.broadleafcommerce.vendor.cybersource.service.api.ReplyMessage;
import org.broadleafcommerce.vendor.cybersource.service.api.RequestMessage;
import org.broadleafcommerce.vendor.cybersource.service.api.VoidReply;
import org.broadleafcommerce.vendor.cybersource.service.api.VoidService;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceRequest;
import org.broadleafcommerce.vendor.cybersource.service.payment.message.CyberSourceAuthResponse;
import org.broadleafcommerce.vendor.cybersource.service.payment.message.CyberSourceAuthReverseResponse;
import org.broadleafcommerce.vendor.cybersource.service.payment.message.CyberSourceCaptureResponse;
import org.broadleafcommerce.vendor.cybersource.service.payment.message.CyberSourceCardRequest;
import org.broadleafcommerce.vendor.cybersource.service.payment.message.CyberSourceCardResponse;
import org.broadleafcommerce.vendor.cybersource.service.payment.message.CyberSourceCreditResponse;
import org.broadleafcommerce.vendor.cybersource.service.payment.message.CyberSourcePaymentRequest;
import org.broadleafcommerce.vendor.cybersource.service.payment.message.CyberSourcePaymentResponse;
import org.broadleafcommerce.vendor.cybersource.service.payment.message.CyberSourceVoidResponse;
import org.broadleafcommerce.vendor.cybersource.service.payment.type.CyberSourceMethodType;
import org.broadleafcommerce.vendor.cybersource.service.payment.type.CyberSourceTransactionType;
import org.broadleafcommerce.vendor.cybersource.service.type.CyberSourceServiceType;

/**
 * 
 * @author jfischer
 *
 */
public class CyberSourceCreditCardPaymentServiceImpl extends AbstractCyberSourcePaymentService implements CyberSourcePaymentService {
    
    private static final Log LOG = LogFactory.getLog(CyberSourceCreditCardPaymentServiceImpl.class);

    public CyberSourcePaymentResponse process(CyberSourcePaymentRequest paymentRequest) throws PaymentException {
        //TODO add validation for the request
        CyberSourceCardResponse cardResponse = new CyberSourceCardResponse();
        cardResponse.setServiceType(paymentRequest.getServiceType());
        cardResponse.setTransactionType(paymentRequest.getTransactionType());
        cardResponse.setMethodType(paymentRequest.getMethodType());
        RequestMessage request = buildRequestMessage(paymentRequest);
        ReplyMessage reply;
        try {
            reply = sendRequest(request);
        } catch (Exception e) {
            incrementFailure();
            throw new PaymentException(e);
        }
        clearStatus();
        buildResponse(cardResponse, reply);
        String[] invalidFields = reply.getInvalidField();
        String[] missingFields = reply.getMissingField();
        if ((invalidFields != null && invalidFields.length > 0) || (missingFields != null && missingFields.length > 0)) {
            PaymentHostException e = new PaymentHostException();
            cardResponse.setErrorDetected(true);
            StringBuffer sb = new StringBuffer();
            if (invalidFields != null && invalidFields.length > 0) {
                sb.append("invalid fields :[ ");
                for (String invalidField : invalidFields) {
                    sb.append(invalidField);
                }
                sb.append(" ]\n");
            }
            if (missingFields != null && missingFields.length > 0) {
                sb.append("missing fields: [ ");
                for (String missingField : missingFields) {
                    sb.append(missingField);
                }
                sb.append(" ]");
            }
            cardResponse.setErrorText(sb.toString());
            e.setPaymentResponse(cardResponse);
            throw e;
        }
        
        return cardResponse;
    }
    
    protected void buildResponse(CyberSourcePaymentResponse paymentResponse, ReplyMessage reply) {
        logReply(reply);
        paymentResponse.setDecision(reply.getDecision());
        paymentResponse.setInvalidField(reply.getInvalidField());
        paymentResponse.setMerchantReferenceCode(reply.getMerchantReferenceCode());
        paymentResponse.setMissingField(reply.getMissingField());
        if (reply.getReasonCode() != null) {
            paymentResponse.setReasonCode(reply.getReasonCode().intValue());
        }
        paymentResponse.setRequestID(reply.getRequestID());
        paymentResponse.setRequestToken(reply.getRequestToken());
        if (CyberSourceTransactionType.AUTHORIZE.equals(paymentResponse.getTransactionType()) || CyberSourceTransactionType.AUTHORIZEANDCAPTURE.equals(paymentResponse.getTransactionType())) {
            CCAuthReply authReply = reply.getCcAuthReply();
            CyberSourceAuthResponse authResponse = new CyberSourceAuthResponse();
            if (authReply.getAccountBalance() != null) {
                authResponse.setAccountBalance(new Money(authReply.getAccountBalance()));
            }
            if (authReply.getAmount() != null) {
                authResponse.setAmount(new Money(authReply.getAmount()));
            }
            if (authReply.getApprovedAmount() != null) {
                authResponse.setApprovedAmount(new Money(authReply.getApprovedAmount()));
            }
            authResponse.setApprovedTerms(authReply.getApprovedTerms());
            authResponse.setAuthenticationXID(authReply.getAuthenticationXID());
            authResponse.setAuthFactorCode(authReply.getAuthFactorCode());
            authResponse.setAuthorizationCode(authReply.getAuthorizationCode());
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
        if (CyberSourceTransactionType.AUTHORIZEANDCAPTURE.equals(paymentResponse.getTransactionType()) || CyberSourceTransactionType.CAPTURE.equals(paymentResponse.getTransactionType())) {
            CCCaptureReply captureReply = reply.getCcCaptureReply();
            CyberSourceCaptureResponse captureResponse = new CyberSourceCaptureResponse();
            if (captureReply.getAmount() != null) {
                captureResponse.setAmount(new Money(captureReply.getAmount()));
            }
            captureResponse.setReasonCode(captureReply.getReasonCode());
            captureResponse.setRequestDateTime(captureResponse.getRequestDateTime());
            captureResponse.setReconciliationID(captureReply.getReconciliationID());
            
            ((CyberSourceCardResponse) paymentResponse).setCaptureResponse(captureResponse);
        }
        if (CyberSourceTransactionType.CREDIT.equals(paymentResponse.getTransactionType())) {
            CCCreditReply creditReply = reply.getCcCreditReply();
            CyberSourceCreditResponse creditResponse = new CyberSourceCreditResponse();
            if (creditReply.getAmount() != null) {
                creditResponse.setAmount(new Money(creditReply.getAmount()));
            }
            creditResponse.setReasonCode(creditReply.getReasonCode());
            creditResponse.setReconciliationID(creditReply.getReconciliationID());
            creditResponse.setRequestDateTime(creditReply.getRequestDateTime());
            
            ((CyberSourceCardResponse) paymentResponse).setCreditResponse(creditResponse);
        }
        if (CyberSourceTransactionType.VOIDTRANSACTION.equals(paymentResponse.getTransactionType())) {
            VoidReply voidReply = reply.getVoidReply();
            CyberSourceVoidResponse voidResponse = new CyberSourceVoidResponse();
            if (voidReply.getAmount() != null) {
                voidResponse.setAmount(new Money(voidReply.getAmount()));
            }
            voidResponse.setReasonCode(voidReply.getReasonCode());
            voidResponse.setRequestDateTime(voidReply.getRequestDateTime());
            
            ((CyberSourceCardResponse) paymentResponse).setVoidResponse(voidResponse);
        }
        if (CyberSourceTransactionType.REVERSEAUTHORIZE.equals(paymentResponse.getTransactionType())) {
            CCAuthReversalReply authReverseReply = reply.getCcAuthReversalReply();
            CyberSourceAuthReverseResponse authReverseResponse = new CyberSourceAuthReverseResponse();
            if (authReverseReply.getAmount() != null) {
                authReverseResponse.setAmount(new Money(authReverseReply.getAmount()));
            }
            authReverseResponse.setReasonCode(authReverseReply.getReasonCode());
            authReverseResponse.setRequestDateTime(authReverseReply.getRequestDateTime());
            authReverseResponse.setAuthorizationCode(authReverseReply.getAuthorizationCode());
            authReverseResponse.setProcessorResponse(authReverseReply.getProcessorResponse());
            
            ((CyberSourceCardResponse) paymentResponse).setAuthReverseResponse(authReverseResponse);
        }
    }
    
    protected RequestMessage buildRequestMessage(CyberSourcePaymentRequest paymentRequest) {
        RequestMessage request = super.buildRequestMessage(paymentRequest);
        if (CyberSourceTransactionType.AUTHORIZE.equals(paymentRequest.getTransactionType()) || CyberSourceTransactionType.AUTHORIZEANDCAPTURE.equals(paymentRequest.getTransactionType())) {
            setCardInformation(paymentRequest, request);
            setBillingInformation(paymentRequest, request);
            setItemInformation(paymentRequest, request);
            request.setCcAuthService(new CCAuthService());
            request.getCcAuthService().setRun("true");
        }
        if (CyberSourceTransactionType.AUTHORIZEANDCAPTURE.equals(paymentRequest.getTransactionType())) {
            request.setCcCaptureService(new CCCaptureService());
            request.getCcCaptureService().setRun("true");
        }
        if (CyberSourceTransactionType.CAPTURE.equals(paymentRequest.getTransactionType())) {
            CyberSourceCardRequest cardRequest = (CyberSourceCardRequest) paymentRequest;
            setItemInformation(paymentRequest, request);
            request.setCcCaptureService(new CCCaptureService());
            request.getCcCaptureService().setRun("true");
            request.getCcCaptureService().setAuthRequestID(cardRequest.getRequestID());
            request.getCcCaptureService().setAuthRequestToken(cardRequest.getRequestToken());
        }
        if (CyberSourceTransactionType.CREDIT.equals(paymentRequest.getTransactionType())) {
            CyberSourceCardRequest cardRequest = (CyberSourceCardRequest) paymentRequest;
            setItemInformation(paymentRequest, request);
            request.setCcCreditService(new CCCreditService());
            request.getCcCreditService().setRun("true");
            request.getCcCreditService().setCaptureRequestID(cardRequest.getRequestID());
            request.getCcCreditService().setCaptureRequestToken(cardRequest.getRequestToken());
        }
        if (CyberSourceTransactionType.VOIDTRANSACTION.equals(paymentRequest.getTransactionType())) {
            CyberSourceCardRequest cardRequest = (CyberSourceCardRequest) paymentRequest;
            request.setVoidService(new VoidService());
            request.getVoidService().setRun("true");
            request.getVoidService().setVoidRequestID(cardRequest.getRequestID());
            request.getVoidService().setVoidRequestToken(cardRequest.getRequestToken());
        }
        if (CyberSourceTransactionType.REVERSEAUTHORIZE.equals(paymentRequest.getTransactionType())) {
            CyberSourceCardRequest cardRequest = (CyberSourceCardRequest) paymentRequest;
            setItemInformation(paymentRequest, request);
            request.setCcAuthReversalService(new CCAuthReversalService());
            request.getCcAuthReversalService().setRun("true");
            request.getCcAuthReversalService().setAuthRequestID(cardRequest.getRequestID());
            request.getCcAuthReversalService().setAuthRequestToken(cardRequest.getRequestToken());
        }
        
        return request;
    }
    
    protected void setBillingInformation(CyberSourcePaymentRequest paymentRequest, RequestMessage request) {
        BillTo billTo = new BillTo();
        billTo.setCity(paymentRequest.getBillingRequest().getCity());
        billTo.setCompany(paymentRequest.getBillingRequest().getCompany());
        billTo.setCompanyTaxID(paymentRequest.getBillingRequest().getCompanyTaxID());
        billTo.setCountry(paymentRequest.getBillingRequest().getCountry());
        billTo.setCounty(paymentRequest.getBillingRequest().getCounty());
        billTo.setDateOfBirth(paymentRequest.getBillingRequest().getDateOfBirth());
        billTo.setDriversLicenseNumber(paymentRequest.getBillingRequest().getDriversLicenseNumber());
        billTo.setDriversLicenseState(paymentRequest.getBillingRequest().getDriversLicenseState());
        billTo.setEmail(paymentRequest.getBillingRequest().getEmail());
        billTo.setFirstName(paymentRequest.getBillingRequest().getFirstName());
        billTo.setIpAddress(paymentRequest.getBillingRequest().getIpAddress());
        billTo.setIpNetworkAddress(paymentRequest.getBillingRequest().getIpNetworkAddress());
        billTo.setLastName(paymentRequest.getBillingRequest().getLastName());
        billTo.setMiddleName(paymentRequest.getBillingRequest().getMiddleName());
        billTo.setPhoneNumber(paymentRequest.getBillingRequest().getPhoneNumber());
        billTo.setPostalCode(paymentRequest.getBillingRequest().getPostalCode());
        billTo.setSsn(paymentRequest.getBillingRequest().getSsn());
        billTo.setState(paymentRequest.getBillingRequest().getState());
        billTo.setStreet1(paymentRequest.getBillingRequest().getStreet1());
        billTo.setStreet2(paymentRequest.getBillingRequest().getStreet2());
        billTo.setStreet3(paymentRequest.getBillingRequest().getStreet3());
        billTo.setStreet4(paymentRequest.getBillingRequest().getStreet4());
        billTo.setSuffix(paymentRequest.getBillingRequest().getSuffix());
        billTo.setTitle(paymentRequest.getBillingRequest().getTitle());

        request.setBillTo( billTo );
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
    
    protected void setItemInformation(CyberSourcePaymentRequest paymentRequest, RequestMessage request) {
        Item[] items = new Item[paymentRequest.getItemRequests().size()];
        for (int j=0;j<items.length;j++) {
            items[j] = new Item();
            items[j].setId(new BigInteger(String.valueOf(paymentRequest.getItemRequests().get(j).getId())));
            items[j].setUnitPrice(paymentRequest.getItemRequests().get(j).getUnitPrice().toString());
            items[j].setQuantity(new BigInteger(String.valueOf(paymentRequest.getItemRequests().get(j).getQuantity())));
        }
        request.setItem(items);
    }

    public boolean isValidService(CyberSourceRequest request) {
        return CyberSourceServiceType.PAYMENT.equals(request.getServiceType()) && CyberSourceMethodType.CREDITCARD.equals(((CyberSourcePaymentRequest) request).getMethodType());
    }
    
    protected void logReply(ReplyMessage reply) {
        if (LOG.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer();
            sb.append("Decision: ");
            sb.append(reply.getDecision());
            sb.append("\nMerchant Reference Code: ");
            sb.append(reply.getMerchantReferenceCode());
            sb.append("\nInvalid Fields[]: ");
            if (reply.getInvalidField() != null) {
                for (String invalidField: reply.getInvalidField()) {
                    sb.append(invalidField);
                    sb.append(";");
                }
            }
            sb.append("\nMissing Fields[]: ");
            if (reply.getMissingField() != null) {
                for (String missingField: reply.getMissingField()) {
                    sb.append(missingField);
                    sb.append(";");
                }
            }
            sb.append("\nReason Code: ");
            sb.append(reply.getReasonCode());
            sb.append("\nRequest ID: ");
            sb.append(reply.getRequestID());
            sb.append("\nRequest Token: ");
            sb.append(reply.getRequestToken());
            
            if (reply.getCcAuthReply() != null) {
                sb.append("\nAUTH REPLY");
                CCAuthReply authReply = reply.getCcAuthReply();
                sb.append("\nAccount Balance: ");
                sb.append(authReply.getAccountBalance());
                sb.append("\nAmount: ");
                sb.append(authReply.getAmount());
                sb.append("\nApproved Amount: ");
                sb.append(authReply.getApprovedAmount());
                sb.append("\nApproved Terms: ");
                sb.append(authReply.getApprovedTerms());
                sb.append("\nAuthentication XID: ");
                sb.append(authReply.getAuthenticationXID());
                sb.append("\nAuth Factor Code: ");
                sb.append(authReply.getAuthFactorCode());
                sb.append("\nAuthorization Code: ");
                sb.append(authReply.getAuthorizationCode());
                sb.append("\nAuthorization XID: ");
                sb.append(authReply.getAuthorizationXID());
                sb.append("\nAuthorized Date Time: ");
                sb.append(authReply.getAuthorizedDateTime());
                sb.append("\nAuth Record: ");
                sb.append(authReply.getAuthRecord());
                sb.append(("\nAvs Code: "));
                sb.append(authReply.getAvsCode());
                sb.append("\nAvs Code Raw: ");
                sb.append(authReply.getAvsCodeRaw());
                sb.append("\nBML Account Number: ");
                sb.append(authReply.getBmlAccountNumber());
                sb.append("\nCard Category: ");
                sb.append(authReply.getCardCategory());
                sb.append("\nCAVV Response Code: ");
                sb.append(authReply.getCavvResponseCode());
                sb.append("\nCAVV Response Code Raw: ");
                sb.append(authReply.getCavvResponseCodeRaw());
                sb.append("\nCredit Line: ");
                sb.append(authReply.getCreditLine());
                sb.append("\nCv Code: ");
                sb.append(authReply.getCvCode());
                sb.append("\nCv Code Raw: ");
                sb.append(authReply.getCvCodeRaw());
                sb.append("\nEnhanced Data Enabled: ");
                sb.append(authReply.getEnhancedDataEnabled());
                sb.append("\nForward Code: ");
                sb.append(authReply.getForwardCode());
                sb.append("\nMerchant Advice Code: ");
                sb.append(authReply.getMerchantAdviceCode());
                sb.append("\nMerchant Advice Code Raw: ");
                sb.append(authReply.getMerchantAdviceCodeRaw());
                sb.append("\nPayment Network Transaction ID: ");
                sb.append(authReply.getPaymentNetworkTransactionID());
                sb.append("\nPersonal ID Code: ");
                sb.append(authReply.getPersonalIDCode());
                sb.append("\nProcessor Card Type: ");
                sb.append(authReply.getProcessorCardType());
                sb.append("\nProcessor Response: ");
                sb.append(authReply.getProcessorResponse());
                sb.append("\nReason Code: ");
                sb.append(authReply.getReasonCode());
                sb.append("\nReconciliation ID: ");
                sb.append(authReply.getReconciliationID());
                sb.append("\nReferral Response Number: ");
                sb.append(authReply.getReferralResponseNumber());
                sb.append("\nSub Response Code: ");
                sb.append(authReply.getSubResponseCode());
            }
            if (reply.getCcCaptureReply() != null) {
                sb.append("\nCAPTURE REPLY");
                CCCaptureReply captureReply = reply.getCcCaptureReply();
                sb.append("\nAmount: ");
                sb.append(captureReply.getAmount());
                sb.append("\nReconciliation Id: ");
                sb.append(captureReply.getReconciliationID());
                sb.append("\nRequest Date Time: ");
                sb.append(captureReply.getRequestDateTime());
                sb.append("\nReason Code: ");
                sb.append(captureReply.getReasonCode());
            }
            if (reply.getCcCreditReply() != null) {
                sb.append("\nCREDIT REPLY");
                CCCreditReply creditReply = reply.getCcCreditReply();
                sb.append("\nAmount: ");
                sb.append(creditReply.getAmount());
                sb.append("\nReconciliation Id: ");
                sb.append(creditReply.getReconciliationID());
                sb.append("\nRequest Date Time: ");
                sb.append(creditReply.getRequestDateTime());
                sb.append("\nReason Code: ");
                sb.append(creditReply.getReasonCode());
            }
            if (reply.getVoidReply() != null) {
                sb.append("\nVOID REPLY");
                VoidReply voidReply = reply.getVoidReply();
                sb.append("\nAmount: ");
                sb.append(voidReply.getAmount());
                sb.append("\nRequest Date Time: ");
                sb.append(voidReply.getRequestDateTime());
                sb.append("\nReason Code: ");
                sb.append(voidReply.getReasonCode());
            }
            if (reply.getCcAuthReversalReply() != null) {
                sb.append("\nAUTH REVERSE REPLY");
                CCAuthReversalReply authReverseReply = reply.getCcAuthReversalReply();
                sb.append("\nAmount: ");
                sb.append(authReverseReply.getAmount());
                sb.append("\nAuthorization Code: ");
                sb.append(authReverseReply.getAuthorizationCode());
                sb.append("\nRequest Date Time: ");
                sb.append(authReverseReply.getRequestDateTime());
                sb.append("\nReason Code: ");
                sb.append(authReverseReply.getReasonCode());
                sb.append("\nProcessor Response: ");
                sb.append(authReverseReply.getProcessorResponse());
            }
            LOG.debug("CyberSource Response:\n" + sb.toString());
        }
    }
    
}
