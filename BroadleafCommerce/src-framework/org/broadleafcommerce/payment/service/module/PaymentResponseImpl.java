package org.broadleafcommerce.payment.service.module;

import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.order.domain.PaymentResponseItem;


public class PaymentResponseImpl implements PaymentResponse {

    protected Map<PaymentInfo, PaymentResponseItem> responses = new HashMap<PaymentInfo, PaymentResponseItem>();

    public void addPaymentResponseItem(PaymentInfo paymentInfo, PaymentResponseItem paymentResponseItem) {
        responses.put(paymentInfo, paymentResponseItem);
    }

    public PaymentResponseItem getPaymentResponseItem(PaymentInfo paymentInfo) {
        return responses.get(paymentInfo);
    }

    public Map<PaymentInfo, PaymentResponseItem> getResponseItems() {
        return responses;
    }
}
