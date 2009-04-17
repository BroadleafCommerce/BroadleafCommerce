package org.broadleafcommerce.payment.order.workflow;

import org.broadleafcommerce.workflow.ProcessContext;

public class PaymentContext implements ProcessContext {

    public final static long serialVersionUID = 1L;

    private boolean stopEntireProcess = false;
    private CombinedPaymentContextSeed seedData;

    @Override
    public void setSeedData(Object seedObject) {
        this.seedData = (CombinedPaymentContextSeed) seedObject;
    }

    @Override
    public boolean stopProcess() {
        this.stopEntireProcess = true;
        return stopEntireProcess;
    }

    public boolean isStopped() {
        return stopEntireProcess;
    }

    public CombinedPaymentContextSeed getSeedData(){
        return seedData;
    }

}
