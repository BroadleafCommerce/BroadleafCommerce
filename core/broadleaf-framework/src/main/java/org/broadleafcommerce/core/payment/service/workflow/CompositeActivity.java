/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
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
 * #L%
 */
package org.broadleafcommerce.core.payment.service.workflow;

import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.Processor;

public class CompositeActivity extends BaseActivity<ProcessContext<PaymentSeed>> {

    private Processor workflow;

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.workflow.Activity#execute(org.broadleafcommerce
     * .workflow.ProcessContext)
     */

    @Override
    public ProcessContext<PaymentSeed> execute(ProcessContext<PaymentSeed> context) throws Exception {
        ProcessContext<PaymentSeed> subContext = (ProcessContext<PaymentSeed>) workflow.doActivities(context.getSeedData());
        if (subContext.isStopped()) {
            context.stopProcess();
        }

        return context;
    }

    public Processor getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Processor workflow) {
        this.workflow = workflow;
    }
}
