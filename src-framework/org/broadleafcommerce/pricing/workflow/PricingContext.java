package org.broadleafcommerce.pricing.workflow;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.workflow.ProcessContext;

public class PricingContext implements ProcessContext {
    public final static long serialVersionUID = 1L;

    private boolean stopEntireProcess = false;
    private Order seedData;

    @Override
    public void setSeedData(Object seedObject) {
        seedData = (Order)seedObject;
    }

    @Override
    public boolean stopProcess() {
        this.stopEntireProcess = true;
        return stopEntireProcess;
    }

    public boolean isStopped() {
        return stopEntireProcess;
    }

    public Order getSeedData(){
        return seedData;
    }

}
