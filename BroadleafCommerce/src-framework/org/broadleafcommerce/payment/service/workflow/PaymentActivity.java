package org.broadleafcommerce.payment.service.workflow;

import java.util.Iterator;
import java.util.Map;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.payment.service.PaymentContextImpl;
import org.broadleafcommerce.payment.service.PaymentService;
import org.broadleafcommerce.payment.service.exception.PaymentException;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class PaymentActivity extends BaseActivity {

    protected PaymentService paymentService;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.workflow.Activity#execute(org.broadleafcommerce.workflow.ProcessContext)
     */
    @Override
    public ProcessContext execute(ProcessContext context) throws Exception {
        CombinedPaymentContextSeed seed = ((WorkflowPaymentContext) context).getSeedData();
        Map<PaymentInfo, Referenced> infos = seed.getInfos();
        PaymentContextImpl paymentContext = new PaymentContextImpl(seed.getOrderTotal(), seed.getOrderTotal());
        Iterator<PaymentInfo> itr = infos.keySet().iterator();
        while(itr.hasNext()) {
            PaymentInfo info = itr.next();
            /*
             * TODO add database logging to a log table before and after each of the actions.
             * Detailed logging is a PCI requirement.
             */
            if (paymentService.isValidCandidate(info.getType())) {
                paymentContext.setPaymentData(info, infos.get(info));
                switch(seed.getActionType()) {
                case AUTHORIZE:
                    paymentService.authorize(paymentContext);
                    break;
                case AUTHORIZEANDDEBIT:
                    paymentService.authorizeAndDebit(paymentContext);
                    break;
                case BALANCE:
                    paymentService.balance(paymentContext);
                    break;
                case CREDIT:
                    paymentService.credit(paymentContext);
                    break;
                case DEBIT:
                    paymentService.debit(paymentContext);
                    break;
                case VOID:
                    paymentService.voidPayment(paymentContext);
                    break;
                default:
                    throw new PaymentException("Module ("+paymentService.getClass().getName()+") does not support payment type of: " + seed.getActionType().toString());
                }
            }
        }

        return context;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

}
