package org.broadleafcommerce.checkout.service;

import java.util.Map;

import org.broadleafcommerce.checkout.exception.CheckoutException;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.secure.domain.Referenced;

public interface CheckoutService {

    public void performCheckout(Order order) throws CheckoutException;

    public void performCheckout(Order order, Map<PaymentInfo, Referenced> payments) throws CheckoutException;

}
