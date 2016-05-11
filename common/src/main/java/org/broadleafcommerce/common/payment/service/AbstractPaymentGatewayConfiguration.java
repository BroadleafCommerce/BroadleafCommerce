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
