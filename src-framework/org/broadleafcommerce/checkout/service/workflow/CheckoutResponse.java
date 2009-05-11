package org.broadleafcommerce.checkout.service.workflow;

import java.util.Map;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.payment.service.module.PaymentResponse;

public interface CheckoutResponse {

    public Map<PaymentInfo, Referenced> getInfos();

    public Order getOrder();

    public PaymentResponse getPaymentResponse();

}
