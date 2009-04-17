package org.broadleafcommerce.payment.order.workflow;

import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.order.module.GiftCardModule;
import org.broadleafcommerce.payment.order.service.GiftCardService;
import org.broadleafcommerce.payment.secure.domain.Referenced;
import org.broadleafcommerce.type.PaymentInfoType;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class PayWithGiftCard extends BaseActivity {

    @Resource
    private GiftCardService giftCardService;

    private String giftCardModuleName;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.workflow.Activity#execute(org.broadleafcommerce.workflow.ProcessContext)
     */
    @Override
    public ProcessContext execute(ProcessContext context) throws Exception {
        GiftCardModule module = giftCardService.getGiftCardModuleByName(giftCardModuleName);
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
                    module.authorize(info);
                } else if (seed.getActionType() == PaymentActionType.DEBIT) {
                    module.debit(info);
                } else {
                    module.authorizeAndDebit(info);
                }
            }
        }

        return context;
    }

    public String getGiftCardModuleName() {
        return giftCardModuleName;
    }

    public void setGiftCardModuleName(String giftCardModuleName) {
        this.giftCardModuleName = giftCardModuleName;
    }

}
