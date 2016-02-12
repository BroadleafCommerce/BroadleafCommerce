/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.common.payment.service;

import org.broadleafcommerce.common.payment.PaymentGatewayType;

public class AbstractPaymentGatewayConfiguration implements PaymentGatewayConfiguration {

    @Override
    public boolean isPerformAuthorizeAndCapture() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public void setPerformAuthorizeAndCapture(boolean performAuthorizeAndCapture) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public int getFailureReportingThreshold() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public void setFailureReportingThreshold(int failureReportingThreshold) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public boolean handlesAuthorize() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public boolean handlesCapture() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public boolean handlesAuthorizeAndCapture() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public boolean handlesReverseAuthorize() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public boolean handlesVoid() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public boolean handlesRefund() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public boolean handlesPartialCapture() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public boolean handlesMultipleShipment() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public boolean handlesRecurringPayment() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public boolean handlesSavedCustomerPayment() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public boolean handlesMultiplePayments() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public PaymentGatewayType getGatewayType() {
        throw new UnsupportedOperationException("Not Implemented");
    }
}
