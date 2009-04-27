package org.broadleafcommerce.payment.service.workflow;

import java.util.Iterator;
import java.util.Map;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.payment.service.module.GiftCardModule;
import org.broadleafcommerce.payment.service.type.PaymentInfoType;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class GiftCardActivity extends BaseActivity {

    private GiftCardModule giftCardModule;

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
            if (info.getType().equals(PaymentInfoType.GIFT_CARD)) {
                if (seed.getActionType() == PaymentActionType.AUTHORIZE) {
                    giftCardModule.authorize(info);
                } else if (seed.getActionType() == PaymentActionType.DEBIT) {
                    giftCardModule.debit(info);
                } else {
                    giftCardModule.authorizeAndDebit(info);
                }
            }
        }

        return context;
    }

    public GiftCardModule getGiftCardModule() {
        return giftCardModule;
    }

    public void setGiftCardModule(GiftCardModule giftCardModule) {
        this.giftCardModule = giftCardModule;
    }

}
