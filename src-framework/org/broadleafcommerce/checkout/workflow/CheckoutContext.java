package org.broadleafcommerce.checkout.workflow;

import org.broadleafcommerce.workflow.ProcessContext;

public class CheckoutContext implements ProcessContext {

    public final static long serialVersionUID = 1L;

    private boolean stopEntireProcess = false;
    private CheckoutSeed seedData;

    @Override
    public void setSeedData(Object seedObject) {
        seedData = (CheckoutSeed) seedObject;
    }

    @Override
    public boolean stopProcess() {
        this.stopEntireProcess = true;
        return stopEntireProcess;
    }

    public boolean isStopped() {
        return stopEntireProcess;
    }

    public CheckoutSeed getSeedData(){
        return seedData;
    }


}
