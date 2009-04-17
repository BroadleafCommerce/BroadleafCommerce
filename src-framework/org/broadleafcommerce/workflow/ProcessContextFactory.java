package org.broadleafcommerce.workflow;

public interface ProcessContextFactory {

    public ProcessContext createContext(Object preSeedData) throws WorkflowException;

}
