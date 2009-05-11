package org.broadleafcommerce.payment.service.workflow;

import java.util.Map;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.payment.service.module.PaymentResponse;
import org.broadleafcommerce.util.money.Money;

public class CombinedPaymentContextSeed {

    private Map<PaymentInfo, Referenced> infos;
    private PaymentActionType actionType;
    private Money orderTotal;
    private PaymentResponse paymentResponse;

    public CombinedPaymentContextSeed(Map<PaymentInfo, Referenced> infos, PaymentActionType actionType, Money orderTotal, PaymentResponse paymentResponse) {
        this.infos = infos;
        this.actionType = actionType;
        this.orderTotal = orderTotal;
        this.paymentResponse = paymentResponse;
    }

    public Map<PaymentInfo, Referenced> getInfos() {
        return infos;
    }

    public PaymentActionType getActionType() {
        return actionType;
    }

    public Money getOrderTotal() {
        return orderTotal;
    }

    public PaymentResponse getPaymentResponse() {
        return paymentResponse;
    }

}
