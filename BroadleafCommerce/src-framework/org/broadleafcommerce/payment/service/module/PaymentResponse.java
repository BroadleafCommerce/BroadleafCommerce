package org.broadleafcommerce.payment.service.module;

import java.util.Map;

import org.broadleafcommerce.order.domain.PaymentInfo;

public interface PaymentResponse {

    public void addPaymentResponseItem(PaymentInfo paymentInfo, PaymentResponseItem paymentResponseItem);

    public PaymentResponseItem getPaymentResponseItem(PaymentInfo paymentInfo);

    public Map<PaymentInfo, PaymentResponseItem> getResponseItems();

}
