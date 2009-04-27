package org.broadleafcommerce.payment.service.workflow;

import org.broadleafcommerce.workflow.ProcessContext;

public class SimplePaymentContext implements ProcessContext {

    public final static long serialVersionUID = 1L;

    private boolean stopEntireProcess = false;
    private PaymentSeed seedData;

    @Override
    public void setSeedData(Object seedObject) {
        this.seedData = (PaymentSeed) seedObject;
    }

    @Override
    public boolean stopProcess() {
        this.stopEntireProcess = true;
        return stopEntireProcess;
    }

    public boolean isStopped() {
        return stopEntireProcess;
    }

    public PaymentSeed getSeedData(){
        return seedData;
    }

}
