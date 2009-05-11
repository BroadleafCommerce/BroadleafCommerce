package org.broadleafcommerce.payment.service;

import java.util.Map;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.payment.service.exception.PaymentException;
import org.broadleafcommerce.payment.service.module.PaymentResponse;
import org.broadleafcommerce.payment.service.workflow.CompositePaymentResponse;

public interface CompositePaymentService {

    public CompositePaymentResponse executePayment(Order order, Map<PaymentInfo, Referenced> payments, PaymentResponse response) throws PaymentException;

    public CompositePaymentResponse executePayment(Order order, Map<PaymentInfo, Referenced> payments) throws PaymentException;

    public CompositePaymentResponse executePayment(Order order) throws PaymentException;

}
