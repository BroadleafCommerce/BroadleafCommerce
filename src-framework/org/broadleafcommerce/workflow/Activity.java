package org.broadleafcommerce.workflow;

import org.springframework.beans.factory.BeanNameAware;

public interface Activity extends BeanNameAware{
    /**
     * Called by the encompassing processor to activate
     * the execution of the Activity
     * 
     * @param context - process context for this workflow
     * @return resulting process context
     * @throws Exception
     */
    public ProcessContext execute(ProcessContext context) throws Exception;
    
    
    /**
     * Get the fine-grained error handler wired up for this Activity
     * @return
     */
    public ErrorHandler getErrorHandler();
    
    public String getBeanName();

}
