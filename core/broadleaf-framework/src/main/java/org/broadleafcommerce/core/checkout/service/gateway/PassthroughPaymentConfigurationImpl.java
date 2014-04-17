/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.core.checkout.service.gateway;

import org.broadleafcommerce.common.payment.PaymentGatewayType;
import org.broadleafcommerce.common.payment.service.PaymentGatewayConfiguration;
import org.springframework.stereotype.Service;

/**
 * A Default Configuration to handle Passthrough Payments, for example COD payments.
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blPassthroughPaymentConfiguration")
public class PassthroughPaymentConfigurationImpl implements PaymentGatewayConfiguration {

    protected int failureReportingThreshold = 0;

    protected boolean performAuthorizeAndCapture = true;

    @Override
    public boolean isPerformAuthorizeAndCapture() {
        return false;
    }

    @Override
    public void setPerformAuthorizeAndCapture(boolean performAuthorizeAndCapture) {
        this.performAuthorizeAndCapture = performAuthorizeAndCapture;
    }

    @Override
    public int getFailureReportingThreshold() {
        return failureReportingThreshold;
    }

    @Override
    public void setFailureReportingThreshold(int failureReportingThreshold) {
        this.failureReportingThreshold = failureReportingThreshold;
    }

    @Override
    public boolean handlesAuthorize() {
        return true;
    }

    @Override
    public boolean handlesCapture() {
        return false;
    }

    @Override
    public boolean handlesAuthorizeAndCapture() {
        return true;
    }

    @Override
    public boolean handlesReverseAuthorize() {
        return false;
    }

    @Override
    public boolean handlesVoid() {
        return false;
    }

    @Override
    public boolean handlesRefund() {
        return false;
    }

    @Override
    public boolean handlesPartialCapture() {
        return false;
    }

    @Override
    public boolean handlesMultipleShipment() {
        return false;
    }

    @Override
    public boolean handlesRecurringPayment() {
        return false;
    }

    @Override
    public boolean handlesSavedCustomerPayment() {
        return false;
    }

    @Override
    public boolean handlesMultiplePayments() {
        return false;
    }

    @Override
    public PaymentGatewayType getGatewayType() {
        return PaymentGatewayType.PASSTHROUGH;
    }

}
