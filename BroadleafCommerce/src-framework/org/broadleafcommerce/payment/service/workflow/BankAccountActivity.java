package org.broadleafcommerce.payment.service.workflow;

import java.util.Iterator;
import java.util.Map;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.BankAccountPaymentInfo;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.payment.service.module.BankAccountModule;
import org.broadleafcommerce.payment.service.type.BLCPaymentInfoType;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class BankAccountActivity extends BaseActivity {

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
            if (info.getType().equals(BLCPaymentInfoType.BANK_ACCOUNT)) {
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
