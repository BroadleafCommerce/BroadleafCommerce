package org.broadleafcommerce.workflow;

public abstract class BaseActivity implements Activity {
    
    private ErrorHandler errorHandler;
    private String beanName;



    public ErrorHandler getErrorHandler() {
        
        return errorHandler;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName; 

    }

    /**
     * Set the fine grained error handler
     * @param errorHandler
     */
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
    
    public String getBeanName() {
        return beanName;
    }
}