package org.broadleafcommerce.payment.order.workflow;

import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.order.module.CreditCardModule;
import org.broadleafcommerce.payment.order.service.CreditCardService;
import org.broadleafcommerce.payment.order.workflow.PaymentContext.ActionType;
import org.broadleafcommerce.payment.order.workflow.PaymentContext.CombinedPaymentContextSeed;
import org.broadleafcommerce.payment.secure.domain.CreditCardPaymentInfo;
import org.broadleafcommerce.payment.secure.domain.Referenced;
import org.broadleafcommerce.type.PaymentInfoType;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class PayWithCreditCard extends BaseActivity {

    @Resource
    private CreditCardService creditCardService;

    private String creditCardModuleName;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.workflow.Activity#execute(org.broadleafcommerce.workflow.ProcessContext)
     */
    @Override
    public ProcessContext execute(ProcessContext context) throws Exception {
        CreditCardModule module = creditCardService.getCreditCardModuleByName(creditCardModuleName);
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
                if (seed.getActionType() == ActionType.AUTHORIZE) {
                    module.authorize(info, (CreditCardPaymentInfo) infos.get(info));
                } else if (seed.getActionType() == ActionType.DEBIT) {
                    module.debit(info, (CreditCardPaymentInfo) infos.get(info));
                } else {
                    module.authorizeAndDebit(info, (CreditCardPaymentInfo) infos.get(info));
                }
            }
        }

        return context;
    }

    /**
     * @return the creditCardModuleName
     */
    public String getCreditCardModuleName() {
        return creditCardModuleName;
    }

    /**
     * @param creditCardModuleName the creditCardModuleName to set
     */
    public void setCreditCardModuleName(String creditCardModuleName) {
        this.creditCardModuleName = creditCardModuleName;
    }

}
