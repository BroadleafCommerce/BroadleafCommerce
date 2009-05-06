package org.broadleafcommerce.payment.service.workflow;

import java.util.Iterator;
import java.util.Map;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.payment.service.GiftCardService;
import org.broadleafcommerce.payment.service.exception.PaymentException;
import org.broadleafcommerce.payment.service.type.BLCPaymentInfoType;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class GiftCardActivity extends BaseActivity {

    protected GiftCardService giftCardService;

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
            if (info.getType().equals(BLCPaymentInfoType.GIFT_CARD)) {
                switch(seed.getActionType()) {
                case AUTHORIZE:
                    giftCardService.authorize(info);
                    break;
                case AUTHORIZEANDDEBIT:
                    giftCardService.authorizeAndDebit(info);
                    break;
                case BALANCE:
                    giftCardService.balance(info);
                    break;
                case CREDIT:
                    giftCardService.credit(info);
                    break;
                case DEBIT:
                    giftCardService.debit(info);
                    break;
                case VOID:
                    giftCardService.voidPayment(info);
                    break;
                default:
                    throw new PaymentException("Module does not support payment type of: " + seed.getActionType().toString());
                }
            }
        }

        return context;
    }

    public GiftCardService getGiftCardService() {
        return giftCardService;
    }

    public void setGiftCardService(GiftCardService giftCardService) {
        this.giftCardService = giftCardService;
    }

}
