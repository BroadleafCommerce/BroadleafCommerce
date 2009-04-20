package org.broadleafcommerce.payment.order.service;

import java.util.Map;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.order.exception.PaymentException;
import org.broadleafcommerce.payment.secure.domain.Referenced;

public interface PaymentService {

    public void executePayment(Order order, Map<PaymentInfo, Referenced> payments) throws PaymentException;
    public void executePayment(Order order) throws PaymentException;
}
