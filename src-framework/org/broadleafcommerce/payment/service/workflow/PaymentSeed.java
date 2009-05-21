package org.broadleafcommerce.payment.service.workflow;

import java.util.Map;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.payment.service.module.PaymentResponse;

public class PaymentSeed implements CompositePaymentResponse {

    private Map<PaymentInfo, Referenced> infos;
    private Order order;
    private PaymentResponse paymentResponse;

    public PaymentSeed(Order order, Map<PaymentInfo, Referenced> infos, PaymentResponse paymentResponse) {
        this.order = order;
        this.infos = infos;
        this.paymentResponse = paymentResponse;
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
