package org.broadleafcommerce.workflow;

import java.util.List;


public interface Processor {

    public boolean supports(Activity activity);
    public ProcessContext doActivities() throws WorkflowException;
    public ProcessContext doActivities(Object seedData) throws WorkflowException;
    public void setActivities(List<Activity> activities);
    public void setDefaultErrorHandler(ErrorHandler defaultErrorHandler);
    public void setProcessContextFactory(ProcessContextFactory processContextFactory);

}