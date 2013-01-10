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

/**
 * 
 * @author jfischer
 *
 */
public class CyberSourceCardResponse extends CyberSourcePaymentResponse {
    
    private static final long serialVersionUID = 1L;
    
    private CyberSourceAuthResponse authResponse;
    private CyberSourceCaptureResponse captureResponse;
    private CyberSourceCreditResponse creditResponse;
    private CyberSourceVoidResponse voidResponse;
    private CyberSourceAuthReverseResponse authReverseResponse;

    public CyberSourceAuthResponse getAuthResponse() {
        return authResponse;
    }

    public void setAuthResponse(CyberSourceAuthResponse authResponse) {
        this.authResponse = authResponse;
    }

    public CyberSourceCaptureResponse getCaptureResponse() {
        return captureResponse;
    }

    public void setCaptureResponse(CyberSourceCaptureResponse captureResponse) {
        this.captureResponse = captureResponse;
    }

    public CyberSourceCreditResponse getCreditResponse() {
        return creditResponse;
    }

    public void setCreditResponse(CyberSourceCreditResponse creditResponse) {
        this.creditResponse = creditResponse;
    }

    public CyberSourceVoidResponse getVoidResponse() {
        return voidResponse;
    }

    public void setVoidResponse(CyberSourceVoidResponse voidResponse) {
        this.voidResponse = voidResponse;
    }

    public CyberSourceAuthReverseResponse getAuthReverseResponse() {
        return authReverseResponse;
    }

    public void setAuthReverseResponse(CyberSourceAuthReverseResponse authReverseResponse) {
        this.authReverseResponse = authReverseResponse;
    }

}
