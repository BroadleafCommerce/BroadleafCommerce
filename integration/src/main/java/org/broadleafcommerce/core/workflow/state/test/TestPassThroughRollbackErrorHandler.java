/*
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.workflow.state.test;

import org.broadleafcommerce.core.workflow.ErrorHandler;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.WorkflowException;

/**
 * Add an ErrorHandler that does nothing and does not stop the workflow
 *
 * @author Jeff Fischer
 */
public class TestPassThroughRollbackErrorHandler implements ErrorHandler {

    @Override
    public void handleError(ProcessContext context, Throwable th) throws WorkflowException {
        //do nothing
        //could get programmatic access to the ActivityStateManager for explicit rollbacks here
    }

    @Override
    public void setBeanName(String name) {
        //do nothing
    }
}
