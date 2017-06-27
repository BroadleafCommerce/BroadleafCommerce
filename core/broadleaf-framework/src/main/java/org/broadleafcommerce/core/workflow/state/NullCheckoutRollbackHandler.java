/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.workflow.state;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutSeed;
import org.broadleafcommerce.core.workflow.Activity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Component("blNullCheckoutRollbackHandler")
public class NullCheckoutRollbackHandler implements RollbackHandler<ProcessContext<CheckoutSeed>> {

    private static final Log LOG = LogFactory.getLog(NullCheckoutRollbackHandler.class);

    @Override
    public void rollbackState(Activity<ProcessContext<CheckoutSeed>> activity, ProcessContext<CheckoutSeed> processContext,
                              Map<String, Object> stateConfiguration) throws RollbackFailureException {

        LOG.warn("NullCheckoutRollbackHandler invoked - Override to provide a " +
                "mechanism to save any compensating transactions when an error occurs during checkout.");
        LOG.warn("******************* Activity: " + activity.getBeanName() + " *********************");
        RollbackStateLocal rollbackStateLocal = RollbackStateLocal.getRollbackStateLocal();
        LOG.warn("******************* Workflow: " + rollbackStateLocal.getWorkflowId() + " *********************");
        LOG.warn("******************* Thread: " + rollbackStateLocal.getThreadId() + " *********************");

    }
}
