/*
 * #%L
 * BroadleafCommerce Common Libraries
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

package org.broadleafcommerce.common.payment.service;

import org.broadleafcommerce.common.vendor.service.exception.PaymentException;
import org.broadleafcommerce.common.vendor.service.monitor.ServiceStatusDetectable;
import org.broadleafcommerce.common.vendor.service.type.ServiceStatusType;

/**
 * <p>All payment gateway classes that intend to make an external call, either manually
 * from an HTTP Post or through an SDK which makes its own external call, should
 * extend this class. The implementations should override the abstract methods:
 * communicateWithVendor(), and getFailureReportingThreshold();</p>
 *
 * <p>The generic Type 'T' represents the payment request object that is going to be sent to the external gateway.
 * The generic Type 'R' represents the payment result object that will be returned</p>
 *
 * <p>This allows anyone using the framework to configure the ServiceMonitor AOP hooks
 * and detect any outages to provide (email/logging) feedback when necessary.</p>
 *
 * @see org.broadleafcommerce.common.vendor.service.monitor.ServiceMonitor
 * @see org.broadleafcommerce.common.vendor.service.monitor.StatusHandler
 * @see ServiceStatusDetectable
 *
 * @author Elbert Bautista (elbertbautista)
 */
public abstract class AbstractExternalPaymentGatewayCall<T,R> implements ServiceStatusDetectable<T> {

    protected Integer failureCount = 0;
    protected Boolean isUp = true;

    protected synchronized void clearStatus() {
        isUp = true;
        failureCount = 0;
    }

    protected synchronized void incrementFailure() {
        if (failureCount >= getFailureReportingThreshold()) {
            isUp = false;
        } else {
            failureCount++;
        }
    }

    @Override
    public synchronized ServiceStatusType getServiceStatus() {
        if (isUp) {
            return ServiceStatusType.UP;
        } else {
            return ServiceStatusType.DOWN;
        }
    }

    @Override
    public R process(T paymentRequest) throws PaymentException {
        R response;
        try {
            response = communicateWithVendor(paymentRequest);
        } catch (Exception e) {
            incrementFailure();
            throw new PaymentException(e);
        }
        clearStatus();

        return response;
    }

    public abstract R communicateWithVendor(T paymentRequest) throws Exception;

    public abstract Integer getFailureReportingThreshold();

}
