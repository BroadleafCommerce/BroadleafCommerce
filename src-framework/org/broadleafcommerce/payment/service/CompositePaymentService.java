package org.broadleafcommerce.payment.service;

import java.util.Map;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.payment.service.exception.PaymentException;

public interface CompositePaymentService {

    public void executePayment(Order order, Map<PaymentInfo, Referenced> payments) throws PaymentException;
    public void executePayment(Order order) throws PaymentException;
}
