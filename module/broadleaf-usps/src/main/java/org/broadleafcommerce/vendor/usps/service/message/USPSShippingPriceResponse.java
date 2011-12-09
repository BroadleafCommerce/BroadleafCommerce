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

package org.broadleafcommerce.vendor.usps.service.message;

import java.util.Stack;

import org.broadleafcommerce.profile.vendor.service.message.ShippingPriceResponse;

public class USPSShippingPriceResponse implements ShippingPriceResponse {

    protected Stack<USPSContainerItemResponse> responses = new Stack<USPSContainerItemResponse>();
    protected boolean isErrorDetected = false;
    protected String errorCode;
    protected String errorText;

    public Stack<USPSContainerItemResponse> getResponses() {
        return responses;
    }

    public void setResponses(Stack<USPSContainerItemResponse> responses) {
        this.responses = responses;
    }

    public boolean isErrorDetected() {
        return isErrorDetected;
    }

    public void setErrorDetected(boolean isErrorDetected) {
        this.isErrorDetected = isErrorDetected;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

}
