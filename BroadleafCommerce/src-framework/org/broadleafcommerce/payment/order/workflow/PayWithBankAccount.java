package org.broadleafcommerce.payment.order.workflow;

import java.util.Iterator;
import java.util.Map;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.order.module.BankAccountModule;
import org.broadleafcommerce.payment.secure.domain.BankAccountPaymentInfo;
import org.broadleafcommerce.payment.secure.domain.Referenced;
import org.broadleafcommerce.type.PaymentInfoType;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class PayWithBankAccount extends BaseActivity {

    private BankAccountModule bankAccountModule;

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
            if (info.getType().equals(PaymentInfoType.CREDIT_CARD)) {
                if (seed.getActionType() == PaymentActionType.AUTHORIZE) {
                    bankAccountModule.authorize(info, (BankAccountPaymentInfo) infos.get(info));
                } else if (seed.getActionType() == PaymentActionType.DEBIT) {
                    bankAccountModule.debit(info, (BankAccountPaymentInfo) infos.get(info));
                } else {
                    bankAccountModule.authorizeAndDebit(info, (BankAccountPaymentInfo) infos.get(info));
                }
            }
        }

        return context;
    }

    public BankAccountModule getBankAccountModule() {
        return bankAccountModule;
    }

    public void setBankAccountModule(BankAccountModule bankAccountModule) {
        this.bankAccountModule = bankAccountModule;
    }

}
