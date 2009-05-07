package org.broadleafcommerce.payment.service.workflow;

import java.util.Map;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.util.money.Money;

public class CombinedPaymentContextSeed {

    private Map<PaymentInfo, Referenced> infos;
    private PaymentActionType actionType;
    private Money orderTotal;

    public CombinedPaymentContextSeed(Map<PaymentInfo, Referenced> infos, PaymentActionType actionType, Money orderTotal) {
        this.infos = infos;
        this.actionType = actionType;
        this.orderTotal = orderTotal;
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

}
