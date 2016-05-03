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

import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;

/**
 * Create an activity that simply throws an exception in order to engage the rollback behavior for the workflow
 *
 * @author Jeff Fischer
 */
public class TestRollbackActivity extends BaseActivity<ProcessContext<? extends Object>> {

    @Override
    public ProcessContext<? extends Object> execute(ProcessContext<? extends Object> context) throws Exception {
        throw new IllegalArgumentException("TestRollbackActivity Accessed...");
    }

}
