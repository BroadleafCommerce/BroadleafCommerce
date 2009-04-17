package org.broadleafcommerce.payment.order.workflow;

import java.util.Map;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.secure.domain.Referenced;

public class CombinedPaymentContextSeed {

    private Map<PaymentInfo, Referenced> infos;
    private PaymentActionType actionType;

    public CombinedPaymentContextSeed(Map<PaymentInfo, Referenced> infos, PaymentActionType actionType) {
        this.infos = infos;
        this.actionType = actionType;
    }

    public Map<PaymentInfo, Referenced> getInfos() {
        return infos;
    }

    public PaymentActionType getActionType() {
        return actionType;
    }

}
