package org.broadleafcommerce.payment.service.workflow;

import java.util.Iterator;
import java.util.Map;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.payment.service.PaymentContextImpl;
import org.broadleafcommerce.payment.service.PaymentService;
import org.broadleafcommerce.payment.service.exception.PaymentException;
import org.broadleafcommerce.payment.service.module.PaymentResponseItem;
import org.broadleafcommerce.util.money.Money;
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
        Money orderTotal = seed.getOrderTotal();
        Money remainingTotal = seed.getOrderTotal();
        Iterator<PaymentInfo> itr = infos.keySet().iterator();
        while(itr.hasNext()) {
            PaymentInfo info = itr.next();
            /*
             * TODO add database logging to a log table before and after each of the actions.
             * Detailed logging is a PCI requirement.
             */
            if (paymentService.isValidCandidate(info.getType())) {
                PaymentContextImpl paymentContext = new PaymentContextImpl(orderTotal, remainingTotal, info, infos.get(info));
                PaymentResponseItem paymentResponseItem;
                switch(seed.getActionType()) {
                case AUTHORIZE:
                    paymentResponseItem = paymentService.authorize(paymentContext);
                    break;
                case AUTHORIZEANDDEBIT:
                    paymentResponseItem = paymentService.authorizeAndDebit(paymentContext);
                    break;
                case BALANCE:
                    paymentResponseItem = paymentService.balance(paymentContext);
                    break;
                case CREDIT:
                    paymentResponseItem = paymentService.credit(paymentContext);
                    break;
                case DEBIT:
                    paymentResponseItem = paymentService.debit(paymentContext);
                    break;
                case VOID:
                    paymentResponseItem = paymentService.voidPayment(paymentContext);
                    break;
                default:
                    throw new PaymentException("Module ("+paymentService.getClass().getName()+") does not support payment type of: " + seed.getActionType().toString());
                }
                if (paymentResponseItem != null) {
                    //validate payment response item
                    if (paymentResponseItem.getAmountPaid() == null || paymentResponseItem.getTransactionTimestamp() == null || paymentResponseItem.getTransactionSuccess() == null) {
                        throw new PaymentException("The PaymentResponseItem instance did not contain one or more of the following: amountPaid, transactionTimestamp or transactionSuccess");
                    }
                    seed.getPaymentResponse().addPaymentResponseItem(info, paymentResponseItem);
                    remainingTotal = remainingTotal.subtract(paymentResponseItem.getAmountPaid());
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
