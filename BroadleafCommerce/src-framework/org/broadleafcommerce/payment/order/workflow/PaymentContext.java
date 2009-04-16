package org.broadleafcommerce.payment.order.workflow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.secure.dao.SecurePaymentInfoDao;
import org.broadleafcommerce.payment.secure.domain.BankAccountPaymentInfo;
import org.broadleafcommerce.payment.secure.domain.CreditCardPaymentInfo;
import org.broadleafcommerce.payment.secure.domain.Referenced;
import org.broadleafcommerce.type.PaymentInfoType;
import org.broadleafcommerce.workflow.ProcessContext;
import org.springframework.stereotype.Component;

@Component("paymentContext")
public class PaymentContext implements ProcessContext {

    public final static long serialVersionUID = 1L;
    private static final Log LOG = LogFactory.getLog(PaymentContext.class);

    private boolean stopEntireProcess;
    private CombinedPaymentContextSeed seedData;

    @Resource
    private SecurePaymentInfoDao securePaymentInfoDao;

    @Override
    public void setSeedData(Object seedObject) {
        if(!(seedObject instanceof PaymentContextSeed)){
            LOG.error("STOPPING Workflow Process, seed data instance is incorrect. " +
                    "Required class is "+PaymentContextSeed.class.getName()+" " +
                    "but found class: "+seedObject.getClass().getName());
            setStopEntireProcess(true);
            return;
        }
        PaymentContextSeed temp = (PaymentContextSeed) seedObject;
        Map<PaymentInfo, Referenced> secureMap = new HashMap<PaymentInfo, Referenced>();
        Iterator<PaymentInfo> infos = temp.getInfos().iterator();
        while(infos.hasNext()) {
            PaymentInfo info = infos.next();
            if (info.getType() == PaymentInfoType.CREDIT_CARD) {
                CreditCardPaymentInfo ccinfo = securePaymentInfoDao.findCreditCardInfo(info.getReferenceNumber());
                if (ccinfo == null) {
                    LOG.error("STOPPING Workflow Process, no credit card info associated with credit card payment type with reference number: " + info.getReferenceNumber());
                    setStopEntireProcess(true);
                    break;
                }
                secureMap.put(info, ccinfo);
            } else if (info.getType() == PaymentInfoType.BANK_ACCOUNT) {
                BankAccountPaymentInfo bankinfo = securePaymentInfoDao.findBankAccountInfo(info.getReferenceNumber());
                if (bankinfo == null) {
                    LOG.error("STOPPING Workflow Process, no bank account info associated with bank account payment type with reference number: " + info.getReferenceNumber());
                    setStopEntireProcess(true);
                    break;
                }
                secureMap.put(info, bankinfo);
            } else if (info.getType() == PaymentInfoType.GIFT_CARD) {
                secureMap.put(info, null);
            } else {
                LOG.error("STOPPING Workflow Process, payment info type ['" + info.getType() +  "'] not recognized with reference number: " + info.getReferenceNumber());
                setStopEntireProcess(true);
                break;
            }
        }
        seedData = new CombinedPaymentContextSeed(secureMap, temp.getActionType());
    }

    @Override
    public boolean stopProcess() {
        return stopEntireProcess;
    }

    public void setStopEntireProcess(boolean stopEntireProcess) {
        this.stopEntireProcess = stopEntireProcess;
    }

    public CombinedPaymentContextSeed getSeedData(){
        return seedData;
    }

    public class PaymentContextSeed {

        private List<PaymentInfo> infos;
        private ActionType actionType;

        public PaymentContextSeed(List<PaymentInfo> infos, ActionType actionType) {
            this.infos = infos;
            this.actionType = actionType;
        }

        public List<PaymentInfo> getInfos() {
            return infos;
        }

        public ActionType getActionType() {
            return actionType;
        }

    }

    public class CombinedPaymentContextSeed {

        private Map<PaymentInfo, Referenced> infos;
        private ActionType actionType;

        public CombinedPaymentContextSeed(Map<PaymentInfo, Referenced> infos, ActionType actionType) {
            this.infos = infos;
            this.actionType = actionType;
        }

        public Map<PaymentInfo, Referenced> getInfos() {
            return infos;
        }

        public ActionType getActionType() {
            return actionType;
        }

    }

    public enum ActionType {
        AUTHORIZE,
        DEBIT,
        AUTHORIZEANDDEBIT
    }
}
