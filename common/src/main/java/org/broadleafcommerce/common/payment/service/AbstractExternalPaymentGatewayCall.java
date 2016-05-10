/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
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
public abstract class AbstractExternalPaymentGatewayCall<T, R> implements ServiceStatusDetectable<T>, FailureCountExposable {

    protected Integer failureCount = 0;
    protected Boolean isUp = true;

    public synchronized void clearStatus() {
        isUp = true;
        failureCount = 0;
    }

    public synchronized void incrementFailure() {
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
