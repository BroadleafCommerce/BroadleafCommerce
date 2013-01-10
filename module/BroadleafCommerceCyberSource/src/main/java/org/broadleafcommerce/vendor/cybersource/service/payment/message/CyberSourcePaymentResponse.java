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
package org.broadleafcommerce.vendor.cybersource.service.payment.message;

import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceResponse;
import org.broadleafcommerce.vendor.cybersource.service.payment.type.CyberSourceMethodType;
import org.broadleafcommerce.vendor.cybersource.service.payment.type.CyberSourceTransactionType;
import org.broadleafcommerce.vendor.service.message.PaymentResponse;

public class CyberSourcePaymentResponse extends CyberSourceResponse implements PaymentResponse {
        
    private static final long serialVersionUID = 1L;
    
    protected boolean isErrorDetected = false;
    protected String errorText;
    protected String merchantReferenceCode;
    protected String requestID;
    protected String decision;
    protected Integer reasonCode;
    protected String[] missingField;
    protected String[] invalidField;
    protected String requestToken;
    private CyberSourceMethodType methodType;
    private CyberSourceTransactionType transactionType;

    public CyberSourceTransactionType getTransactionType() {
        return transactionType;
    }
    
    public void setTransactionType(CyberSourceTransactionType transactionType) {
        this.transactionType = transactionType;
    }
    
    public CyberSourceMethodType getMethodType() {
        return methodType;
    }

    public void setMethodType(CyberSourceMethodType venueType) {
        this.methodType = venueType;
    }
    
    public String getErrorCode() {
        throw new RuntimeException("ErrorCode not supported");
    }

    public String getErrorText() {
        return errorText;
    }

    public boolean isErrorDetected() {
        return isErrorDetected;
    }

    public void setErrorCode(String errorCode) {
        throw new RuntimeException("ErrorCode not supported");
    }

    public void setErrorDetected(boolean isErrorDetected) {
        this.isErrorDetected = isErrorDetected;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }
    
    public String getMerchantReferenceCode() {
        return merchantReferenceCode;
    }

    public void setMerchantReferenceCode(String merchantReferenceCode) {
        this.merchantReferenceCode = merchantReferenceCode;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public Integer getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(Integer reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String[] getMissingField() {
        return missingField;
    }

    public void setMissingField(String[] missingField) {
        this.missingField = missingField;
    }

    public String[] getInvalidField() {
        return invalidField;
    }

    public void setInvalidField(String[] invalidField) {
        this.invalidField = invalidField;
    }

    public String getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }

}
