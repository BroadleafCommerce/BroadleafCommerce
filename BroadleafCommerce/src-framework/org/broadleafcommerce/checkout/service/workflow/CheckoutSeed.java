package org.broadleafcommerce.checkout.service.workflow;

import java.util.Map;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.payment.service.module.PaymentResponse;
import org.broadleafcommerce.payment.service.module.PaymentResponseImpl;

public class CheckoutSeed implements CheckoutResponse {

    private Map<PaymentInfo, Referenced> infos;
    private Order order;
    private PaymentResponse paymentResponse = new PaymentResponseImpl();

    public CheckoutSeed(Order order, Map<PaymentInfo, Referenced> infos) {
        this.order = order;
        this.infos = infos;
    }

    public Map<PaymentInfo, Referenced> getInfos() {
        return infos;
    }

    public Order getOrder() {
        return order;
    }

    public PaymentResponse getPaymentResponse() {
        return paymentResponse;
    }
}
