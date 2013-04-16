/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.checkout.service.workflow;

import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.SequenceProcessor;

public class CompositeActivity extends BaseActivity<CheckoutContext> {

    private SequenceProcessor workflow;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.workflow.Activity#execute(org.broadleafcommerce.core.workflow.ProcessContext)
     */
    @Override
    public CheckoutContext execute(CheckoutContext context) throws Exception {
        ProcessContext subContext = workflow.doActivities(context.getSeedData());
        if (subContext.isStopped()) {
            context.stopProcess();
        }

        return context;
    }

    public SequenceProcessor getWorkflow() {
        return workflow;
    }

    public void setWorkflow(SequenceProcessor workflow) {
        this.workflow = workflow;
    }
}
