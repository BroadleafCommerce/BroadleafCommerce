package org.broadleafcommerce.workflow;

import java.util.List;


public interface Processor {
    public boolean supports(Activity activity);
    public void doActivities();
    public void doActivities(Object seedData);
    public void setActivities(List<Activity> activities);
    public void setDefaultErrorHandler(ErrorHandler defaultErrorHandler);
 }