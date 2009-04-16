package org.broadleafcommerce.payment.order.workflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.workflow.ProcessContext;

public class PaymentContext implements ProcessContext {

    public final static long serialVersionUID = 1L;

    private boolean stopEntireProcess;
    private Order seedData;

    private Log log = LogFactory.getLog(PaymentContext.class);

    @Override
    public void setSeedData(Object seedObject) {
        /*if(!(seedObject instanceof List<PaymentInfo>)){
            log.error("STOPPING Workflow Process, seed data instance is incorrect. " +
                    "Required class is "+Order.class.getName()+" " +
                    "but found class: "+seedObject.getClass().getName());
            setStopEntireProcess(true);
        }
        seedData = (Order)seedObject;*/

    }

    @Override
    public boolean stopProcess() {
        return stopEntireProcess;
    }

    public void setStopEntireProcess(boolean stopEntireProcess) {
        this.stopEntireProcess = stopEntireProcess;
    }

    public Order getSeedData(){
        return seedData;
    }


}
