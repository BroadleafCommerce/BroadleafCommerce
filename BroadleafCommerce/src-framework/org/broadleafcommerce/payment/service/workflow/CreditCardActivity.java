package org.broadleafcommerce.payment.service.workflow;

import java.util.Iterator;
import java.util.Map;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.CreditCardPaymentInfo;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.payment.service.module.CreditCardModule;
import org.broadleafcommerce.payment.service.type.BLCPaymentInfoType;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class CreditCardActivity extends BaseActivity {

    private CreditCardModule creditCardModule;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.workflow.Activity#execute(org.broadleafcommerce.workflow.ProcessContext)
     */
    @Override
    public ProcessContext execute(ProcessContext context) throws Exception {
        CombinedPaymentContextSeed seed = ((PaymentContext) context).getSeedData();
        Map<PaymentInfo, Referenced> infos = seed.getInfos();
        Iterator<PaymentInfo> itr = infos.keySet().iterator();
        while(itr.hasNext()) {
            PaymentInfo info = itr.next();
            /*
             * TODO add database logging to a log table before and after each of the actions.
             * Detailed logging is a PCI requirement.
             */
            if (info.getType().equals(BLCPaymentInfoType.CREDIT_CARD)) {
                if (seed.getActionType() == PaymentActionType.AUTHORIZE) {
                    creditCardModule.authorize(info, (CreditCardPaymentInfo) infos.get(info));
                } else if (seed.getActionType() == PaymentActionType.DEBIT) {
                    creditCardModule.debit(info, (CreditCardPaymentInfo) infos.get(info));
                } else {
                    creditCardModule.authorizeAndDebit(info, (CreditCardPaymentInfo) infos.get(info));
                }
            }
        }

        return context;
    }

    public CreditCardModule getCreditCardModule() {
        return creditCardModule;
    }

    public void setCreditCardModule(CreditCardModule creditCardModule) {
        this.creditCardModule = creditCardModule;
    }

}
