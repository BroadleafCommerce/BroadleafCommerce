package org.broadleafcommerce.workflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component("defaultErrorHandler")
public class DefaultErrorHandler implements ErrorHandler {

    private static final Log LOG = LogFactory.getLog(DefaultErrorHandler.class);
    @SuppressWarnings("unused")
    private String name;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.workflow.ErrorHandler#handleError(org.broadleafcommerce.workflow.ProcessContext, java.lang.Throwable)
     */
    @Override
    public void handleError(ProcessContext context, Throwable th) {
        context.stopProcess();
        LOG.error("An error occurred during the workflow", th);
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    @Override
    public void setBeanName(String name) {
        this.name = name;
    }

}
