package org.broadleafcommerce.payment.service.workflow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.order.service.OrderService;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.payment.service.SecurePaymentInfoService;
import org.broadleafcommerce.workflow.ProcessContext;
import org.broadleafcommerce.workflow.ProcessContextFactory;
import org.broadleafcommerce.workflow.WorkflowException;

public class PaymentProcessContextFactory implements ProcessContextFactory {

    @Resource
    private SecurePaymentInfoService securePaymentInfoService;

    @Resource
    private OrderService orderService;

    private PaymentActionType paymentActionType;

    @Override
    public ProcessContext createContext(Object seedData) throws WorkflowException {
        if(!(seedData instanceof PaymentSeed)){
            throw new WorkflowException("Seed data instance is incorrect. " +
                    "Required class is "+PaymentSeed.class.getName()+" " +
                    "but found class: "+seedData.getClass().getName());
        }
        PaymentSeed paymentSeed = (PaymentSeed) seedData;
        Map<PaymentInfo, Referenced> secureMap = paymentSeed.getInfos();
        if (secureMap == null) {
            secureMap = new HashMap<PaymentInfo, Referenced>();
            List<PaymentInfo> paymentInfoList = orderService.readPaymentInfosForOrder(paymentSeed.getOrder());
            if (paymentInfoList == null || paymentInfoList.size() == 0) {
                throw new WorkflowException("No payment info instances associated with the order -- id: " + paymentSeed.getOrder().getId());
            }
            Iterator<PaymentInfo> infos = paymentInfoList.iterator();
            while(infos.hasNext()) {
                PaymentInfo info = infos.next();
                secureMap.put(info, securePaymentInfoService.findSecurePaymentInfo(info.getReferenceNumber(), info.getType()));
            }
        }
        CombinedPaymentContextSeed combinedSeed = new CombinedPaymentContextSeed(secureMap, paymentActionType);
        PaymentContext response = new PaymentContext();
        response.setSeedData(combinedSeed);

        return response;
    }

    public PaymentActionType getPaymentActionType() {
        return paymentActionType;
    }

    public void setPaymentActionType(PaymentActionType paymentActionType) {
        this.paymentActionType = paymentActionType;
    }

}
