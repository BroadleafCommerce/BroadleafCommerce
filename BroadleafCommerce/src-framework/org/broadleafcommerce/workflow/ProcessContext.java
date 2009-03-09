package org.broadleafcommerce.workflow;

import java.io.Serializable;

public interface ProcessContext extends Serializable {
    /**
     * Activly informs the workflow process to stop processing
     * no further activities will be exeecuted 
     * @return
     */
    public boolean stopProcess();
    
    /**
     * Provide seed information to this ProcessContext, usually 
     * provided at time of workflow kickoff by the containing 
     * workflow processor.
     * 
     * @param seedObject - initial seed data for the workflow
     */
    public void setSeedData(Object seedObject);

}
